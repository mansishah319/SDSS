/**
 * Represents the different status states of a survey.
 *
 * Available statuses:
 * - 'NOT_STARTED': The survey has not yet started
 * - 'STOPPED': The survey has been stopped
 * - 'PAUSE': The survey is paused
 * - 'COMPLETED': The survey has been completed
 * - 'CANCELED': The survey has been canceled
 * - 'START': The survey has started
 */
export type SurveyStatus = 'NOT_STARTED' | 'STOPPED' | 'PAUSE' | 'COMPLETED' | 'CANCELED' | 'START'

/**
 * Represents the statuses that an SDSS (Software Development Status System) task can have.
 * The possible statuses are:
 *
 * - 'PENDING': The task is newly created and is awaiting further action.
 * - 'IN_PROGRESS': The task is currently being worked on.
 * - 'COMPLETED': The task has been finished successfully.
 * - 'DONE': The task is done, synonymous with 'COMPLETED'.
 * - 'CANCELED': The task has been canceled and will not be completed.
 * - 'FAILED': The task has failed and could not be completed.
 * - 'ON_HOLD': The task is on hold and not currently being worked on.
 * - 'REVIEW': The task is under review before being marked as completed.
 */
export type SdssStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'DONE' | 'CANCELED' | 'FAILED' | 'ON_HOLD' | 'REVIEW'
