const globalScriptPromises = new Map<string, Promise<void>>()

export const loadBrowserGlobal = async <T>(url: string, globalName: string): Promise<T> => {
  const globalScope = globalThis as typeof globalThis & Record<string, unknown>
  const existingGlobal = globalScope[globalName]
  if (existingGlobal) {
    return existingGlobal as T
  }

  let loadPromise = globalScriptPromises.get(globalName)

  if (!loadPromise) {
    loadPromise = new Promise<void>((resolve, reject) => {
      const selector = `script[data-browser-global="${globalName}"]`
      const existingScript = document.querySelector<HTMLScriptElement>(selector)

      if (existingScript) {
        existingScript.addEventListener('load', () => resolve(), { once: true })
        existingScript.addEventListener(
          'error',
          () => reject(new Error(`Failed to load browser global "${globalName}"`)),
          { once: true }
        )
        return
      }

      const script = document.createElement('script')
      script.src = url
      script.async = true
      script.dataset.browserGlobal = globalName
      script.onload = () => resolve()
      script.onerror = () => reject(new Error(`Failed to load browser global "${globalName}"`))
      document.head.appendChild(script)
    })

    globalScriptPromises.set(globalName, loadPromise)
  }

  await loadPromise

  const loadedGlobal = globalScope[globalName]
  if (!loadedGlobal) {
    globalScriptPromises.delete(globalName)
    throw new Error(`Browser global "${globalName}" was not initialized`)
  }

  return loadedGlobal as T
}
