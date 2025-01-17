package io.github.saphirdefeu.minigamelolcow.phones;

import io.github.saphirdefeu.minigamelolcow.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Appstore {
    private static Path appstoreDirectory;

    public static void init() {
        Path directoryPath = Paths.get("plugins/minigamelolcow/appstore");
        appstoreDirectory = directoryPath;

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            Path appsFile = directoryPath.resolve("./apps").normalize();
            if(!Files.exists(appsFile)) {
                Files.createFile(appsFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fillItems(@NotNull Inventory inventory, final int PAGE_SIZE, final int PAGE_INDEX) {
        LinkedList<Path> apps = new LinkedList<>(readApps());
        int startIndex = PAGE_INDEX * PAGE_SIZE;
        int endIndex = (PAGE_INDEX + 1) * PAGE_SIZE;
        int maxToGo = Math.min(endIndex, apps.size());

        for(int i = startIndex; i < maxToGo; i++) {
            Path appRootFolder = apps.get(i);

            Path appPropertiesPath = appRootFolder.resolve("./app.properties").normalize();
            Properties appProperties = Data.readPropertiesFile(appPropertiesPath);

            String icon = appProperties.getProperty("icon");
            Material mat = null;
            if(icon != null) mat = Material.matchMaterial(icon);
            if(mat == null) {
                Logger.err(String.format("appstore encountered an error: material '%s' could not match", icon));
                return;
            }

            String name = appProperties.getProperty("styled_name");
            MiniMessage miniMessageParser = MiniMessage.miniMessage();
            Component component = miniMessageParser.deserialize(name);

            ItemStack app = PhoneGUI.newItem(
                    mat,
                    component
            );
            ItemMeta appMeta = app.getItemMeta();
            appMeta.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    String.format("app:%s", appRootFolder)
            );
            app.setItemMeta(appMeta);

            inventory.setItem(i, app);
        };
    }

    public static @NotNull Collection<Path> readApps() {
        List<String> apps = List.of();
        LinkedList<Path> allApps = new LinkedList<>();
        try {
            apps = Files.readAllLines(appstoreDirectory.resolve("./apps").normalize());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String app : apps) {
            String fullPath = String.format("./%s", app);
            Path absolutePath = appstoreDirectory.resolve(fullPath).normalize();
            allApps.add(absolutePath);
        }

        return allApps;
    }
}
