/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.config.client;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 09 Jan 2025
 */
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 500) {
            // Log or handle the internal server error
            return new RuntimeException("Internal Server Error while fetching token");
        }
        return new Default().decode(methodKey, response);
    }
}