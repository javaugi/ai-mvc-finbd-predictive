/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

@Slf4j
public class CommonUtil {
    
    public static String listToString(List<String> list) {
        return listToString(list, ",");
    }
    
    public static String listToString(List<String> list, String delim) {
        return String.join(delim, list);
    }

    public static List<String> stringToList(String str) {
        return stringToList(str, ",");
    }
    
    public static List<String> stringToList(String str, String delim) {
        return List.of(str.split(delim));
    }
    
    public static int stringTokensize(String str) {
        return stringToList(str).size();
    }
    
    public static int stringTokensize(String str, String delim) {
        return stringToList(str, delim).size();
    }
    
    public static String convertMapToJson(Map<String, Object> map) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(map);
        }catch(JsonProcessingException ex) {
            Gson gson = new Gson();
            return gson.toJson(map);
        }
    }
    
    public static Map<String, Object> convertJsonToMap(String json) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        }catch(JsonProcessingException ex) {
            Gson gson = new Gson();
            java.lang.reflect.Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            return gson.fromJson(json, mapType);
        }
    }

    public static Date parseDate(String strDate, String pattern) {
        try {
            DateUtils.parseDate(strDate, pattern);
        } catch (ParseException ex) {
            log.error("Error parseDate {}", strDate, pattern);
        }
        return new Date();
    }

    public static LocalDate parseLocalDate(String strDate, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate.parse(strDate, formatter);
        } catch (Exception ex) {
            log.error("Error parseDate {}", strDate, pattern);
        }
        return LocalDate.now();
    }
}
