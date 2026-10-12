/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface BaseResponse<I extends number | string> {
  id?: I
  createdBy?: string
  createdAt?: Date
  updatedBy?: string
  updatedAt?: Date
}

export interface ChallengesMajorDetailsResponse extends BaseResponse<number> {
  status?: string
  surveyStatus?: string
  titleAr?: string
  titleEn?: string
  visibility?: string
  descriptionAr?: string
  descriptionEn?: string
  allowImmediateAccess?: boolean
  localDescription?: string
  localTitle?: string
}

export interface VideosResponse extends BaseResponse<number> {
  link?: string
  challenges?: ChallengesMajorDetailsResponse
}

export interface VideosDashboardResponse {
  challengeId?: number
  videosResponseList?: VideosResponse[]
}
