package com.webscraping;

import com.webscraping.Utilities.Compactor;
import com.webscraping.Utilities.PdfScraper;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Compactor compactor = new Compactor();
            PdfScraper scraper = new PdfScraper(
                    "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos",
                    "meus_pdfs",
                    List.of("Anexo I", "Anexo II")
            );

            List<File> downloadedFiles = scraper.execute();
            System.out.println("Arquivos baixados:");
            downloadedFiles.forEach(file -> System.out.println("- " + file.getName()));
            String zipPath = Paths.get("meus_pdfs", "Pdfs.zip").toString();

            compactor.compressMultipleFiles(downloadedFiles, zipPath);
            System.out.println("Compactação concluída com sucesso!");



        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}