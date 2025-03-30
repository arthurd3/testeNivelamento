package com.webscraping.Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PdfScraper {
    private final String targetUrl;
    private final String outputDir;
    private final List<String> requiredKeywords;

    public PdfScraper(String targetUrl, String outputDir, List<String> requiredKeywords) {
        this.targetUrl = targetUrl;
        this.outputDir = outputDir;
        this.requiredKeywords = new ArrayList<>(requiredKeywords);
    }

    public List<File> execute() throws IOException {
        createOutputDirectory();
        List<String> pdfUrls = findPdfLinks();
        return downloadPdfs(pdfUrls);
    }

    private void createOutputDirectory() {
        new File(outputDir).mkdirs();
    }

    private List<String> findPdfLinks() throws IOException {
        List<String> pdfLinks = new ArrayList<>();
        Document doc = Jsoup.connect(targetUrl).get();

        Elements links = doc.select("a[href$=.pdf]");
        for (Element link : links) {
            String linkText = link.text();
            if (requiredKeywords.stream().anyMatch(linkText::contains)) {
                pdfLinks.add(link.attr("abs:href"));
            }
        }

        if (pdfLinks.size() < requiredKeywords.size()) {
            throw new IOException("PDFs não encontrados. Esperados: " + requiredKeywords.size());
        }

        return pdfLinks;
    }

    private List<File> downloadPdfs(List<String> pdfUrls) throws IOException {
        List<File> downloadedFiles = new ArrayList<>();

        for (String pdfUrl : pdfUrls) {
            String fileName = pdfUrl.split("\\?")[0].substring(pdfUrl.lastIndexOf('/') + 1);
            File outputFile = new File(outputDir, fileName);

            FileUtils.copyURLToFile(new URL(pdfUrl), outputFile, 1000, 1000);
            downloadedFiles.add(outputFile);
        }

        return downloadedFiles;
    }
}