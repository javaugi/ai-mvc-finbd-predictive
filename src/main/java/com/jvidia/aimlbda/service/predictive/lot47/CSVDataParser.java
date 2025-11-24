/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service.predictive.lot47;

import com.jvidia.aimlbda.utils.ResourceAccessUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVDataParser {

    //private static final String SRC_MAIN_RESOURCE_DOCS_LOT47 = "src/main/resources";
    private static final String DOCS_LOT47 = "/lottdocs/mil4701012020to10252025.csv";
    private static final String DOCS_FANTACY5 = "/lottdocs/mif5 01012020To10302025.csv";

    public static List<LottoDraw> getLott47Data() {
        List<LottoDraw> draws = new ArrayList<>();
        try {
            File file = ResourceAccessUtils.getResourceFile(DOCS_LOT47);
            if (file != null) {
                try (CSVReader reader = new CSVReader(new FileReader(file))) {
                    List<String[]> r = reader.readAll();
                    for (String[] ln : r) {
                        if (ln.length > 1 && !ln[1].contains("Winning")) {
                            String line = ln[1];
                            LottoDraw draw = parseDrawLine(line, 6);
                            draws.add(draw);
                        }
                    }
                }
            }
        } catch (CsvException | IOException ex) {
            log.error("Error getLott47Data for file {}", DOCS_LOT47);
        }
        
        log.info("File {} processed with {} records extracted", DOCS_LOT47, draws.size());

        return draws;
    }

    public static List<LottoDraw> getFantacy5Data() {
        List<LottoDraw> draws = new ArrayList<>();
        try {
            File file = ResourceAccessUtils.getResourceFile(DOCS_FANTACY5);
            if (file != null) {
                try (CSVReader reader = new CSVReader(new FileReader(file))) {
                    List<String[]> r = reader.readAll();
                    for (String[] ln : r) {
                        if (ln.length > 1 && !ln[1].contains("Winning")) {
                            String line = ln[1];
                            LottoDraw draw = parseDrawLine(line, 5);
                            draws.add(draw);
                        }
                    }
                }
            }
        } catch (CsvException | IOException ex) {
            log.error("Error getFantacy5Data for file {}", DOCS_FANTACY5);
        }

        log.info("getFantacy5Data File {} processed with {} records extracted", DOCS_FANTACY5, draws.size());

        return draws;
    }

    private static LottoDraw parseDrawLine(String line, int number) {
        try {
            String[] numberArray = line.split(",");

            if (numberArray.length == number) {
                int[] numbers = new int[number];
                for (int i = 0; i < number; i++) {
                    numbers[i] = Integer.parseInt(numberArray[i].trim());
                }
                Arrays.sort(numbers); // Sort for consistency
                return new LottoDraw(numbers);
            }
        } catch (NumberFormatException e) {
            log.error("Error parsing line: {}", line);
        }
        return null;
    }

}
