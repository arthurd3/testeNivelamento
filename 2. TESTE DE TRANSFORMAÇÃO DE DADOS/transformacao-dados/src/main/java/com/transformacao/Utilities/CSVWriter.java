package com.transformacao.Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVWriter {
    public static String saveToCSV(List<String[]> data, String csvPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath, StandardCharsets.UTF_8))) {
            for (String[] row : data) {
                writer.write(String.join(";", row));
                writer.newLine();
            }
        }
        return csvPath;
    }
}
