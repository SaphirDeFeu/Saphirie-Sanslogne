package io.github.saphirdefeu.minigamelolcow.phones;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public abstract class Data {
    public static File dataFolder;

    public static void init(@NotNull JavaPlugin plugin) {
        Path directoryPath = Paths.get("plugins/MinigameLolCow/phones");
        dataFolder = plugin.getDataFolder();

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

    public static @NotNull Path resolvePath(@NotNull String id, @NotNull String path) {
        String formattedString = String.format("./phones/%s/", id);
        if(path.charAt(0) == '/') formattedString = String.format("./phones/%s%s", id, path);

        Path resolvedPath = Path.of(formattedString);
        Path absolutePath = Path.of(dataFolder.getAbsolutePath());
        return absolutePath.resolve(resolvedPath).normalize();
    }

    public static void saveToFile(@NotNull String path, @NotNull String lines) {
        byte[] bytes = lines.getBytes();
        try {
            Files.write(Path.of(path), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static @NotNull Collection<String> scanFile(@NotNull Path file) {
        List<String> lines = List.of();
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static Properties readPropertiesFile(Path path) {
        // Create a Properties object to hold the properties
        Properties properties = new Properties();

        // Use a try-with-resources statement to ensure the InputStream is closed automatically
        try (var inputStream = Files.newInputStream(path)) {
            // Load properties from the input stream
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
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

                try (var outputStream = Files.newOutputStream(appsPropertiesPath)) {
                    properties.store(outputStream, "Application Properties");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions
        }
    }
}
