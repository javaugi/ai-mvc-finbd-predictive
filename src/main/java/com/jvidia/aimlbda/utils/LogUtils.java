/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {
    public static void logMap(String from, Map<String, Object> map) {
        log.debug("\n## START LOGGING {} ", from);
        for (String key : map.keySet()) {
            log.debug(" key {} value {}", key, map.get(key));
        }
        log.debug("## End LOGGING");
    }
}
