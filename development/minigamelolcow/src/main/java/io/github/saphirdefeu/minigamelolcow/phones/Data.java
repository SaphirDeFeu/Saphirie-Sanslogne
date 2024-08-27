package io.github.saphirdefeu.minigamelolcow.phones;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public abstract class Data {
    public static void init() {
        Path directoryPath = Paths.get("plugins/MinigameLolCow/phones");

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static @NotNull Path getRoot(@NotNull String id) {
        Path directoryPath = Paths.get("plugins/MinigameLolCow/phones/" + id.toLowerCase().replaceAll(" ", "_"));
        if (!Files.exists(directoryPath)) {
            initializeRoot(directoryPath);
        }

        return directoryPath;
    }

    public static @NotNull Path resolvePath(@NotNull String id, @NotNull Path path) {
        String formattedString = String.format("/plugins/MinigameLolCow/phones/%s/", id);
        if(path.isAbsolute()) formattedString = String.format("/plugins/MinigameLolCow/phones/%s%s", id, path);

        return Path.of(formattedString);
    }

    public static @NotNull Collection<Path> scanDirectory(@NotNull Path directory) {
        Collection<Path> paths = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                paths.add(path);
            }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
        }

        return paths;
    }

    private static void initializeRoot(@NotNull Path path) {
        try {
            // Ensure the root directory exists, create it if it does not exist
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }

            // Create 'apps/' and 'data/' directories under the root path
            Path appsPath = path.resolve("apps");
            Path dataPath = path.resolve("data");

            if (Files.notExists(appsPath)) {
                Files.createDirectory(appsPath);
            }

            if (Files.notExists(dataPath)) {
                Files.createDirectory(dataPath);
            }

            // Create 'apps.properties' file inside the 'apps/' directory
            Path appsPropertiesPath = appsPath.resolve("apps.properties");
            if (Files.notExists(appsPropertiesPath)) {
                Files.createFile(appsPropertiesPath);

                // Optional: Initialize the 'apps.properties' file with some default properties
                Properties properties = new Properties();
                properties.setProperty("pineapple", "/");

                try (var outputStream = Files.newOutputStream(appsPropertiesPath)) {
                    properties.store(outputStream, "Application Properties");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions
        }
    }
}
