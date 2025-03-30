package com.transformacao.Utilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Compactor {

    public void compressMultipleFiles(List<File> arquivosOrigem, String arquivoDestinoZip) throws IOException {

        if (arquivosOrigem == null || arquivosOrigem.isEmpty()) {
            throw new IllegalArgumentException("Lista de arquivos não pode ser vazia");
        }
        if (arquivoDestinoZip == null || arquivoDestinoZip.trim().isEmpty()) {
            throw new IllegalArgumentException("Destino ZIP não pode ser vazio");
        }

        Map<String, String> zipProperties = new HashMap<>();
        zipProperties.put("create", String.valueOf(Files.notExists(Paths.get(arquivoDestinoZip))));
        zipProperties.put("encoding", "UTF-8");

        URI zipUri = URI.create("jar:" + Paths.get(arquivoDestinoZip).toUri());

        try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, zipProperties)) {
            for (File arquivo : arquivosOrigem) {
                if (!arquivo.exists()) {
                    System.err.println("Arquivo não encontrado: " + arquivo.getPath());
                    continue;
                }

                Path sourcePath = arquivo.toPath();
                Path destPath = zipfs.getPath(sourcePath.getFileName().toString());


                Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
