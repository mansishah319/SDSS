import { Client, IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { Paths } from '@/Paths'
import Employee from '@/dto/Employee.ts'
import { Fields } from '@/constants/DpConstants.ts'

type TimeoutHandle = ReturnType<typeof setTimeout>

export class WebsocketClient {
  private static instance: WebsocketClient
  private stompClient: Client | null = null
  private readonly onConnectCallbacks = new Set<() => void>()
  private isActivating = false
  private readonly subscriptions = new Map<number, { unsubscribe: () => void }>()
  private nextSubscriptionId = 0
  private lastStatusUpdate = 0
  private statusUpdateTimeout: TimeoutHandle | null = null
  private connectionPromise: Promise<void> | null = null
  private cachedEmployee: Employee | null = null
  private employeeCacheTime = 0
  private readonly CACHE_DURATION = 5000 // 5 seconds
  private readonly CONNECTION_TIMEOUT = 10000
  private readonly STATUS_THROTTLE_MS = 1000

  private constructor() {
    this.initializeWebSocket()
  }

  public static getInstance(): WebsocketClient {
    if (!WebsocketClient.instance) {
      WebsocketClient.instance = new WebsocketClient()
    }
    return WebsocketClient.instance
  }

  public getStompClient(): Client | null {
    return this.stompClient
  }

  public setOnConnectCallback(callback: () => void) {
    this.onConnectCallbacks.add(callback)

    if (this.stompClient?.connected) {
      callback()
    }
  }

  public removeOnConnectCallback(callback: () => void) {
    this.onConnectCallbacks.delete(callback)
  }

  public isConnected(): boolean {
    return this.stompClient?.connected === true
  }

  public async ensureConnection(): Promise<void> {
    if (this.stompClient?.connected) {
      return
    }

    if (this.connectionPromise) {
      return this.connectionPromise
    }

    if (!this.stompClient || (!this.stompClient.active && !this.isActivating)) {
      this.initializeWebSocket()
    }

    this.connectionPromise = new Promise((resolve, reject) => {
      const handleConnect = () => {
        clearTimeout(timeout)
        this.removeOnConnectCallback(handleConnect)
        this.connectionPromise = null
        resolve()
      }

      const timeout = setTimeout(() => {
        this.removeOnConnectCallback(handleConnect)
        this.connectionPromise = null
        reject(new Error('Connection timeout'))
      }, this.CONNECTION_TIMEOUT)

      this.setOnConnectCallback(handleConnect)
    })

    return this.connectionPromise
  }

  private initializeWebSocket(): void {
    if (this.isActivating || this.stompClient?.active) {
      return
    }

    this.isActivating = true
    const socketUrl = this.toSockJsUrl(Paths.WEBSOCKET_URL)
    const isDev = import.meta.env.DEV

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      connectHeaders: {},
      debug: isDev ? (message: string) => console.log('STOMP:', message) : () => {},
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onDisconnect: () => {
        this.isActivating = false
      },
      onWebSocketClose: (event) => {
        console.log('ws closed', event)
        this.isActivating = false
      }
    })

    this.stompClient.onConnect = () => {
      this.isActivating = false
      this.onConnectCallbacks.forEach((callback) => callback())
    }

    this.stompClient.onStompError = (frame) => {
      this.isActivating = false
      console.error('Broker reported error: ' + frame.headers['message'])
      console.error('Additional details: ' + frame.body)
    }

    this.stompClient.onWebSocketError = (error) => {
      console.error('WebSocket error:', error)
      this.isActivating = false
    }

    this.stompClient.activate()
  }

  private toSockJsUrl(url: string): string {
    try {
      const parsedUrl = new URL(url)

      if (parsedUrl.protocol === 'ws:') {
        parsedUrl.protocol = 'http:'
      } else if (parsedUrl.protocol === 'wss:') {
        parsedUrl.protocol = 'https:'
      }

      return parsedUrl.toString()
    } catch {
      return url.replace(/^ws:/, 'http:').replace(/^wss:/, 'https:')
    }
  }

  private getSelectedEmployee(): Employee | null {
    const now = Date.now()
    if (this.cachedEmployee && (now - this.employeeCacheTime) < this.CACHE_DURATION) {
      return this.cachedEmployee
    }

    try {
      const employeeJson = localStorage.getItem(Fields.EMPLOYEE)
      if (employeeJson) {
        this.cachedEmployee = JSON.parse(employeeJson)
        this.employeeCacheTime = now
        return this.cachedEmployee
      }
    } catch (error) {
      console.error('Error parsing employee from localStorage:', error)
    }

    return null
  }

  public subscribe(destination: string, callback: (message: IMessage) => void): () => void {
    const subscription = this.stompClient?.subscribe(destination, (message: IMessage) => {
      this.scheduleTask(() => {
        try {
          callback(message)
        } catch (error) {
          console.error('Error in message callback:', error)
        }
      })
    })

    if (subscription) {
      const subscriptionId = ++this.nextSubscriptionId
      this.subscriptions.set(subscriptionId, subscription)

      return () => {
        subscription.unsubscribe()
        this.subscriptions.delete(subscriptionId)
      }
    }

    return () => undefined
  }

  private sendUserStatusImmediate(status: string, challengeId: number): void {
    const selectedEmployee = this.getSelectedEmployee()

    if (this.stompClient?.connected && selectedEmployee) {
      const message = {
        type: 'CHAT',
        content: status,
        sender: selectedEmployee.id,
        challengeId: challengeId
      }

      const publish = () => {
        this.stompClient?.publish({
          destination: status === 'online' ? '/app/chat.addUser' : '/app/chat.removeUser',
          body: JSON.stringify(message)
        })
      }

      if (status === 'offline') {
        publish()
        return
      }

      this.scheduleTask(publish, { timeout: 100 })
    }
  }

  public sendUserStatus(status: string, challengeId: number): void {
    if (status === 'offline') {
      if (this.statusUpdateTimeout) {
        clearTimeout(this.statusUpdateTimeout)
        this.statusUpdateTimeout = null
      }
      this.lastStatusUpdate = Date.now()
      this.sendUserStatusImmediate(status, challengeId)
      return
    }

    const now = Date.now()
    if (now - this.lastStatusUpdate < this.STATUS_THROTTLE_MS) {
      if (this.statusUpdateTimeout) {
        clearTimeout(this.statusUpdateTimeout)
      }
      this.statusUpdateTimeout = setTimeout(() => {
        this.sendUserStatusImmediate(status, challengeId)
        this.statusUpdateTimeout = null
        this.lastStatusUpdate = Date.now()
      }, this.STATUS_THROTTLE_MS)
      return
    }

    this.lastStatusUpdate = now
    this.sendUserStatusImmediate(status, challengeId)
  }

  public async sendChallengeStatus(status: string, challengeId: number): Promise<boolean> {
    await this.ensureConnection()

    const selectedEmployee = this.getSelectedEmployee()

    if (!this.stompClient?.connected || !selectedEmployee || !challengeId || !status) {
      return false
    }

    return new Promise((resolve) => {
      this.scheduleTask(() => {
        try {
          this.stompClient?.publish({
            destination: '/app/challenge.status',
            body: JSON.stringify({
              challengeId,
              status,
              sender: String(selectedEmployee.id || '')
            })
          })
          resolve(true)
        } catch (error) {
          console.error('Error sending challenge status:', error)
          resolve(false)
        }
      })
    })
  }

  public async disconnect(): Promise<void> {
    this.subscriptions.forEach((subscription) => {
      if (subscription && typeof subscription.unsubscribe === 'function') {
        subscription.unsubscribe()
      }
    })
    this.subscriptions.clear()

    // Clear any pending timeouts
    if (this.statusUpdateTimeout) {
      clearTimeout(this.statusUpdateTimeout)
      this.statusUpdateTimeout = null
    }

    this.connectionPromise = null

    this.cachedEmployee = null
    this.employeeCacheTime = 0
    this.isActivating = false

    if (this.stompClient) {
      await this.stompClient.deactivate()
      this.stompClient = null
    }
  }

  public clearEmployeeCache(): void {
    this.cachedEmployee = null
    this.employeeCacheTime = 0
  }

  public async reconnect(): Promise<void> {
    await this.disconnect()
    this.initializeWebSocket()
  }

  private scheduleTask(task: () => void, options?: { timeout?: number }) {
    if (typeof requestIdleCallback !== 'undefined') {
      requestIdleCallback(task, { timeout: options?.timeout ?? 50 })
      return
    }

    if (typeof queueMicrotask !== 'undefined') {
      queueMicrotask(task)
      return
    }

    setTimeout(task, 0)
  }
}
