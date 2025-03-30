package com.transformacao;

import com.transformacao.Utilities.CSVWriter;
import com.transformacao.Utilities.Compactor;
import com.transformacao.Utilities.DataProcessor;
import com.transformacao.Utilities.PDFExtractor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String name = "Arthur";
        String pdfPath = "Anexo_I_Rol_2021RN_465.2021_RN627L.2024.pdf";
        String csvPath = "Teste_"+name+".csv";
        String zipPath = "Teste_"+name+".zip";


        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            System.err.println("Erro: O arquivo PDF não foi encontrado em " + pdfFile.getAbsolutePath());
            return;
        }

        try {

            String extractedText = PDFExtractor.extractTextFromPDF(pdfPath);


            List<String[]> processedData = DataProcessor.processTextData(extractedText);

            String csvFilePath = CSVWriter.saveToCSV(processedData, csvPath);
            System.out.println("CSV gerado com sucesso: " + csvFilePath);


            Compactor compactor = new Compactor();
            compactor.compressMultipleFiles(List.of(new File(csvFilePath)), zipPath);
            System.out.println("Arquivo ZIP gerado com sucesso: " + zipPath);

        } catch (IOException e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}
