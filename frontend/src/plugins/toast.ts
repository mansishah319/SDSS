/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// toast.js
import { createApp } from 'vue'
import VueToastify from 'vue3-toastify'

const app = createApp({})
app.use(VueToastify, {
  position: 'top-center',
  duration: 2000,
  singleton: true
})

export default app
