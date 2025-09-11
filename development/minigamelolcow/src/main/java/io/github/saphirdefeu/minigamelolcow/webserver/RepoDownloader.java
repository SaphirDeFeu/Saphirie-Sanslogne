package io.github.saphirdefeu.minigamelolcow.webserver;

import io.github.saphirdefeu.minigamelolcow.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RepoDownloader {

    public static void downloadFiles(String apiUrl, String rawContentUrl) {
        try {
            Path localDir = Paths.get("plugins/minigamelolcow/webserver/");
            Files.createDirectories(localDir);

            List<String> fileUrls = listFilesInDirectory(apiUrl, rawContentUrl);

            for(String fileUrl : fileUrls) {
                Logger.debug("Downloading " + fileUrl);
                downloadFile(fileUrl, localDir);
            }

            Logger.debug("Downloaded webserver");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> listFilesInDirectory(String apiUrl, String rawContentUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Parse the JSON response to extract file URLs
            // This is a simplified example. In a real application, you should use a JSON library like Jackson or Gson.
            String jsonResponse = response.body();

            ArrayList<String> fileNames = new ArrayList<>();
            for(int i = 0; i < jsonResponse.length(); i++) {
                if(jsonResponse.charAt(i) == 'n' && jsonResponse.charAt(i + 1) == 'a' && jsonResponse.charAt(i + 2) == 'm' && jsonResponse.charAt(i + 3) == 'e') {
                    i = i + 7; // Set index after the "name"="
                    StringBuilder name = new StringBuilder();
                    for(int j = i; true; j++) {
                        if(jsonResponse.charAt(j) == '"') break;
                        name.append(jsonResponse.charAt(j));
                    }

                    fileNames.add(name.toString());
                }
            }
            return fileNames.stream().map(name -> rawContentUrl + name).toList();
        } else {
            throw new IOException("Failed to list files. HTTP status code: " + response.statusCode());
        }
    }

    private static void downloadFile(String url, Path localDir) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            Path filePath = localDir.resolve(fileName);
            try (BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(response.body()));
                 FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile())) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
        } else {
            throw new IOException("Failed to download file. HTTP status code: " + response.statusCode());
        }
    }

}
