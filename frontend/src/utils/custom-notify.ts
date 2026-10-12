/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { notify } from '@kyvg/vue3-notification'
// To style notifications, set the 'classes' prop on the <notifications> component in App.vue, e.g.:
// <notifications classes="sdss-notification" position="top right" />
// Then style .sdss-notification in your global CSS.
export const customNotify = (type: string, text: string, title?: string) => {
  notify({
    type: type,
    text: text,
    title: title,
    duration: 3000
  })
}
