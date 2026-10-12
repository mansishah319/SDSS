/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class DpJsonUtilsService {

    private final ObjectMapper objectMapper;

    public Map<String, Object> parseJsonToMap(String json) {
        try {
            log.info("JSON need to map is : {}", json);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            // Handle the parsing exception
            return Collections.emptyMap();
        }
    }
}
