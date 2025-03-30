package com.transformacao.Utilities;

import java.util.ArrayList;
import java.util.List;

public class DataProcessor {
    public static List<String[]> processTextData(String text) {
        List<String[]> data = new ArrayList<>();

        String[] lines = text.split("\n");

        for (String line : lines) {
            String[] columns = line.split("\\s{2,}");

            if (columns.length > 1) {
                for (int i = 0; i < columns.length; i++) {
                    columns[i] = replaceAbbreviations(columns[i]);
                }
                data.add(columns);
            }
        }

        return data;
    }

    private static String replaceAbbreviations(String text) {
        return text.replace("OD", "Odontológico")
                .replace("AMB", "Ambulatorial");
    }
}
