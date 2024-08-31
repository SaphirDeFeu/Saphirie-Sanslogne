package io.github.saphirdefeu.minigamelolcow.phones;

import io.github.saphirdefeu.minigamelolcow.Listeners;
import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.Main;
import io.github.saphirdefeu.minigamelolcow.phones.api.Debug;
import io.github.saphirdefeu.minigamelolcow.phones.api.InventoryWrapper;
import io.github.saphirdefeu.minigamelolcow.phones.api.ItemStackWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class PhoneGUI implements Listener {
    private final JavaPlugin plugin;
    private PythonInterpreter jythonInterpreter;
    private Inventory inventory;
    private String ownerID;
    private String currentPath = "home";
    private boolean loadingDone = false;
    private boolean removeMode = false;
    private boolean addMode = false;

    private final int SIZE = 54;
    private final int PAGE_SIZE = SIZE - 18;
    private int pageIndex = 0;

    private final String APPS_LOCATION_FILE = "/apps/apps.properties";
    // Only available when using the explorer
    /**
     * Can only be filled if PhoneGUI.directoryEntries is empty
     */
    private LinkedList<String> lines = new LinkedList<>(); // When editing a file
    private int editLineIndex = 0;
    private boolean pullFromDisk = true;

    private LinkedList<String> appPaths = new LinkedList<>();
    private LinkedList<String> appStoreApps = new LinkedList<>();

    /**
     * Can only be filled if PhoneGUI.lines is empty
     */
    private LinkedList<Path> directoryEntries = new LinkedList<>(); // When exploring a folder

    public PhoneGUI(@NotNull JavaPlugin plugin, @NotNull String owner) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.jythonInterpreter = new PythonInterpreter();
            jythonInterpreter.set("GUI", new InventoryWrapper(this));
            jythonInterpreter.set("Item", ItemStackWrapper.class);
            this.loadingDone = true;
            for(int i = this.SIZE - 9; i < this.SIZE; i++) {
                this.inventory.setItem(i, newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, Component.empty()));
            }
        });
        this.plugin = plugin;

        updateInventory(owner, currentPath, null, false);
    }

    public void updateInventory(@NotNull String owner, @NotNull String title, @Nullable HumanEntity player, boolean reopen) {
        // Prepares inventory
        ownerID = owner;
        if(inventory != null) inventory.close();
        inventory = Bukkit.createInventory(null, this.SIZE, Component.text(title));
        fillItems();
        // If we want to reopen the inventory (after update), supply a player
        if(reopen && player == null) return;
        if(reopen) openInventory(player);
    }

    /**
     * Fills the inventory with the mutable data that is contained in the filesystem, such as folders or items
     */
    public void fillInventoryMutableItems(boolean isFile) {
        int startIndex = pageIndex * PAGE_SIZE;
        int endIndex = (pageIndex + 1) * PAGE_SIZE;

        if(this.currentPath.equals("home")) {
            ItemStack explorer;
            ItemStack appStore;
            {
                explorer = newItem(
                        Material.BOOK,
                        Component.text("Fichiers").color(NamedTextColor.GOLD)
                );
                ItemMeta appMeta = explorer.getItemMeta();
                appMeta.getPersistentDataContainer().set(
                        new NamespacedKey("mlc-pl", "phonebutton"),
                        PersistentDataType.STRING,
                        "app_root:explorer"
                );
                explorer.lore(List.of(
                        Component.text("Explorateur de fichiers").color(NamedTextColor.GRAY)
                ));
                explorer.setItemMeta(appMeta);
            }
            {
                appStore = newItem(
                        Material.EMERALD_BLOCK,
                        Component.text("Pineapp Store").color(NamedTextColor.GREEN)
                );
                ItemMeta appStoreMeta = appStore.getItemMeta();
                appStoreMeta.getPersistentDataContainer().set(
                        new NamespacedKey("mlc-pl", "phonebutton"),
                        PersistentDataType.STRING,
                        "app_root:appstore"
                );
                appStore.setItemMeta(appStoreMeta);
            }

            this.inventory.setItem(9, explorer);
            this.inventory.setItem(10, appStore);

            setHomeApps();
            return;
        }

        if(this.currentPath.equals("home/appstore")) {
            // Add a way to leave
            ItemStack item = newItem(
                    Material.REDSTONE_BLOCK,
                    Component.text("Quitter").color(NamedTextColor.DARK_RED)
            );
            ItemMeta mm = item.getItemMeta();
            mm.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    "quit"
            );
            item.setItemMeta(mm);
            this.inventory.setItem(0, item);



            return;
        }

        // If in explorer, display all directory entries.
        int index = 9;
        if(!isFile) {
            int scanUpTo = Math.min(directoryEntries.size(), endIndex);
            for(int i = startIndex; i < scanUpTo; i++) {
                Path entry = directoryEntries.get(i);
                File file = new File(entry.toString());

                if(file.isDirectory()) {
                    ItemStack folder = newItem(
                            Material.BOOK,
                            Component.text(entry.getFileName().toString()).color(NamedTextColor.GOLD)
                    );
                    ItemMeta mm = folder.getItemMeta();
                    mm.getPersistentDataContainer().set(
                            new NamespacedKey("mlc-pl", "phonebutton"),
                            PersistentDataType.STRING,
                            String.format("move:%s/%s", this.currentPath.equals("/") ? "" : this.currentPath, entry.getFileName().toString())
                    );
                    folder.setItemMeta(mm);
                    this.inventory.setItem(index, folder);
                }

                if(file.isFile()) {
                    ItemStack folder = newItem(
                            Material.PAPER,
                            Component.text(entry.getFileName().toString()).color(NamedTextColor.BLUE)
                    );
                    ItemMeta mm = folder.getItemMeta();
                    mm.getPersistentDataContainer().set(
                            new NamespacedKey("mlc-pl", "phonebutton"),
                            PersistentDataType.STRING,
                            String.format("open:%s/%s", this.currentPath.equals("/") ? "" : this.currentPath, entry.getFileName().toString())
                    );
                    folder.setItemMeta(mm);
                    this.inventory.setItem(index, folder);
                }

                index++;
            }
        } else {
            int scanUpTo = Math.min(lines.size(), endIndex);
            for(int i = startIndex; i < scanUpTo; i++) {
                String line = lines.get(i);
                ItemStack item = newItem(Material.YELLOW_STAINED_GLASS_PANE, Component.text(line));
                ItemMeta mm = item.getItemMeta();
                mm.getPersistentDataContainer().set(
                        new NamespacedKey("mlc-pl", "phonebutton"),
                        PersistentDataType.STRING,
                        String.format("editline:%d", i)
                );
                item.setItemMeta(mm);

                this.inventory.setItem(index, item);
                index++;
            }
        }
    }

    public void setHomeApps() {
        Path rootPath = Data.resolvePath(this.ownerID, " ");
        Path path = rootPath.resolve("./apps/apps").normalize();
        List<String> apps = List.of();
        try {
            apps = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.appPaths.clear();
        for(String appPathString : apps) {
            this.appPaths.add(String.format("/apps/%s", appPathString));
        }

        int startIndex = pageIndex * PAGE_SIZE;
        int endIndex = (pageIndex + 1) * PAGE_SIZE;
        int maxToGo = Math.min(endIndex, this.appPaths.size());
        int currentPosition = pageIndex == 0 ? 11 : 9;

        for(int i = startIndex; i < maxToGo; i++) {
            String value = appPaths.get(i);

            Path appRootFolder = Data.resolvePath(this.ownerID, value);
            Path appPropertiesPath = appRootFolder.resolve("./app.properties").normalize();
            Properties appProperties = Data.readPropertiesFile(appPropertiesPath);

            String icon = appProperties.getProperty("icon");
            Material mat = null;
            if(icon != null) mat = Material.matchMaterial(icon);
            if(mat == null) {
                Logger.err(String.format("phone-%s encountered an error: material '%s' could not match", this.ownerID, icon));
                return;
            }

            String name = appProperties.getProperty("styled_name");
            MiniMessage miniMessageParser = MiniMessage.miniMessage();
            Component component = miniMessageParser.deserialize(name);

            ItemStack app = newItem(
                    mat,
                    component
            );
            ItemMeta appMeta = app.getItemMeta();
            appMeta.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    String.format("app_root:%s", value)
            );
            app.setItemMeta(appMeta);

            this.inventory.setItem(currentPosition, app);
            currentPosition++;
        };
    }

    public void fillItems() {
        Path resolvedPath = Data.resolvePath(this.ownerID, this.currentPath);
        File currentFile = new File(resolvedPath.toString());
        // Scans if we're in explorer mode
        if(this.currentPath.charAt(0) == '/' && this.pullFromDisk) {
            if(currentFile.isDirectory()) {
                directoryEntries = new LinkedList<>(
                        Data.scanDirectory(resolvedPath)
                );
                lines.clear();
            } else if(currentFile.isFile()) {
                lines = new LinkedList<>(
                        Data.scanFile(resolvedPath)
                );
                directoryEntries.clear();
            }
        }

        if(this.currentPath.equals("home")) {
            directoryEntries.clear();
            lines.clear();
        }

        if(!this.currentPath.equals("home") && this.currentPath.charAt(0) != '/' && !this.currentPath.equals("home/appstore")) {
            // Must be an app
            ItemStack item = newItem(
                    Material.REDSTONE_BLOCK,
                    Component.text("Quitter").color(NamedTextColor.DARK_RED)
            );
            ItemMeta mm = item.getItemMeta();
            mm.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    "quit"
            );
            item.setItemMeta(mm);
            this.inventory.setItem(0, item);
            return;
        }

        String filler = "";

        // Filler items for background (gray stained glass for main, blue stained glass for hotbar)
        ItemStack empty = newItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty());
        ItemStack hotbar = newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, Component.empty());
        ItemStack hotbarRemove = newItem(Material.RED_STAINED_GLASS_PANE, Component.empty());
        ItemStack hotbarAdd = newItem(Material.YELLOW_STAINED_GLASS_PANE, Component.empty());
        ItemStack hotbarLoad = newItem(Material.WHITE_STAINED_GLASS_PANE, Component.text("Loading"));

        for(int i = directoryEntries.size() + 9; i < this.SIZE; i++) {
            if(i >= this.SIZE - 9) filler = "hotbar";

            if(filler.isEmpty()) this.inventory.setItem(i, empty);
            if(filler.equals("hotbar")) this.inventory.setItem(i, hotbar);
            if(filler.equals("hotbar") && this.removeMode) this.inventory.setItem(i, hotbarRemove);
            if(filler.equals("hotbar") && this.addMode) this.inventory.setItem(i, hotbarAdd);
            if(filler.equals("hotbar") && !this.loadingDone) this.inventory.setItem(i, hotbarLoad);
        }

        // If using the explorer, add the back button
        if(this.currentPath.charAt(0) == '/') {
            setExplorerButtons();
        }

        setButtons();

        fillInventoryMutableItems(currentFile.isFile());
    }

    public void setButtons() {
        ItemStack nextPage;
        ItemStack lastPage;
        {
            nextPage = newItem(
                    Material.ARROW,
                    Component.text("Prochaine page")
            );
            ItemMeta mm = nextPage.getItemMeta();
            mm.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    "next"
            );
            nextPage.setItemMeta(mm);
        }
        {
            lastPage = newItem(
                    Material.ARROW,
                    Component.text("Dernière page")
            );
            ItemMeta mm = lastPage.getItemMeta();
            mm.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    "last"
            );
            lastPage.setItemMeta(mm);
        }

        this.inventory.setItem(7, lastPage);
        this.inventory.setItem(8, nextPage);
    }

    public void setExplorerButtons() {
        ItemStack backButton;
        ItemStack addButton;
        ItemStack removeButton;
        {
            backButton = newItem(
                    Material.ARROW,
                    Component.text("Retour").color(NamedTextColor.RED)
            );
            ItemMeta backButtonItemMeta = backButton.getItemMeta();
            backButtonItemMeta.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    "back"
            );
            backButton.setItemMeta(backButtonItemMeta);
        }
        {
            addButton = newItem(
                    Material.NETHER_STAR,
                    Component.text("Ajouter").color(NamedTextColor.GREEN)
            );
            ItemMeta addButtonItemMeta = addButton.getItemMeta();
            addButtonItemMeta.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    "add"
            );
            addButton.setItemMeta(addButtonItemMeta);
        }
        {
            removeButton = newItem(
                    Material.BARRIER,
                    Component.text("Supprimer").color(NamedTextColor.RED)
            );
            ItemMeta removeButtonItemMeta = removeButton.getItemMeta();
            removeButtonItemMeta.getPersistentDataContainer().set(
                    new NamespacedKey("mlc-pl", "phonebutton"),
                    PersistentDataType.STRING,
                    "remove"
            );
            removeButton.setItemMeta(removeButtonItemMeta);
        }

        this.inventory.setItem(0, backButton);
        this.inventory.setItem(1, addButton);
        this.inventory.setItem(2, removeButton);
    }

    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inventory);
    }

    public static ItemStack newItem(Material material, Component name) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.itemName(name);
        item.setItemMeta(itemMeta);

        return item;
    }

    /**
     * Modifies the path using a relative path and updates the GUI accordingly
     * @param relativePath Path relative to the current path
     * @param player Player Entity that had the GUI open
     */
    public void modifyPath(@NotNull String relativePath, @NotNull Player player) {
        if(this.currentPath.equals("home") && relativePath.equals("/")) {
            setCurrentPath("/");
            updateInventory(this.ownerID, this.currentPath, player, true);
            return;
        }
        boolean onHomeOrApp = this.currentPath.charAt(0) == 'h' && relativePath.equals("..");
        boolean onRoot = this.currentPath.equals("/") && relativePath.equals("..");
        if(onHomeOrApp || onRoot) {
            setCurrentPath("home");
            updateInventory(this.ownerID, this.currentPath, player, true);
            return;
        }

        Path root = Path.of(this.currentPath);
        Path relative = Path.of(relativePath);
        Path newPath = root.resolve(relative).normalize();
        setCurrentPath(newPath.toString());

        updateInventory(this.ownerID, this.currentPath, player, true);
    }

    /**
     * Setter method for currentPath
     * @param path
     */
    public void setCurrentPath(@NotNull String path) {
        path = path.replace("\\", "/");
        this.currentPath = path;
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();
        if(clickedItem == null) return;
        final String phoneButtonType = clickedItem.getPersistentDataContainer().get(
                new NamespacedKey("mlc-pl", "phonebutton"),
                PersistentDataType.STRING
        );
        final Player p = (Player) e.getWhoClicked();

        if(phoneButtonType == null) return;

        final File file = new File(Data.resolvePath(this.ownerID, this.currentPath).toString());

        switch(phoneButtonType) {
            case "app_root:explorer": {
                this.modifyPath("/", p);
                return;
            }
            case "app_root:appstore": {
                this.modifyPath("appstore", p);
                return;
            }
            case "back": {
                if(file.isFile()) {
                    String concatLines = String.join("\n", lines);
                    Data.saveToFile(file.getAbsolutePath(), concatLines);
                    this.pullFromDisk = true;
                }
                this.removeMode = false;
                this.addMode = false;
                this.modifyPath("..", p);
                return;
            }
            case "add": {
                this.removeMode = false;

                if(file.isFile() && lines.isEmpty()) {
                    lines.add("");
                    this.pullFromDisk = false;
                    this.modifyPath(".", p);
                    return;
                }

                if(file.isFile()) {
                    this.addMode = !this.addMode;
                    this.pullFromDisk = false;
                    this.modifyPath(".", p);
                    return;
                }

                this.addMode = false;
                String msg = "<green>Ajout d'un dossier ou fichier. Préfixez le nom par ':' si vous voulez créer un fichier</green>";
                this.pullFromDisk = true;
                this.inventory.close();
                Listeners.getStringFromUser(p, msg, input -> {

                    Main.runTaskSync(plugin, () -> {
                        boolean isFile = input.charAt(0) == ':';
                        String name = isFile ? input.substring(1) : input;
                        Path path = Data.resolvePath(this.ownerID, this.currentPath);
                        Path resolvedPath = path.resolve(name);

                        try {
                            if (Files.notExists(resolvedPath) && isFile) {
                                Files.createFile(resolvedPath);
                            } else if(Files.notExists(resolvedPath) && !isFile) {
                                Files.createDirectory(resolvedPath);
                            } else if(!Files.notExists(resolvedPath)) {
                                p.sendRichMessage(String.format("<red>Le fichier de nom '%s' existe déjà!</red>", name));
                            }
                        } catch(IOException err) {
                            err.printStackTrace();
                        }

                        this.modifyPath(".", p);
                    });
                });
                return;
            }
            case "remove": {
                this.addMode = false;
                this.removeMode = !this.removeMode;
                this.modifyPath(".", p);
                return;
            }
            case "next": {
                this.pageIndex++;
                this.modifyPath(".", p);
                return;
            }
            case "last": {
                if(this.pageIndex > 0) this.pageIndex--;
                this.modifyPath(".", p);
                return;
            }
            case "quit": {
                this.jythonInterpreter.close();
                this.modifyPath("..", p);
                return;
            }
            default: { break; }
        }

        String[] split = phoneButtonType.split(":");

        if(this.removeMode) { // Deleting mode
            if(file.isDirectory()) {
                Path parentPath = Data.resolvePath(this.ownerID, this.currentPath);
                String name = Main.getComponentText(clickedItem.getItemMeta().itemName());
                Path path = parentPath.resolve("./" + name).normalize();
                try {
                    Files.delete(path);
                } catch (IOException ex) {
                    if(Files.exists(path)) p.sendRichMessage("<red>You cannot remove a non-empty directory!</red>");
                    else p.sendRichMessage("<red>The file you wish to delete does not exist!</red>");
                }
            } else {
                int lineIndex = Integer.parseInt(split[1]);
                this.pullFromDisk = false;
                lines.remove(lineIndex);
            }

            this.modifyPath(".", p);
            return;
        }

        if(this.addMode) {
            if(file.isDirectory()) {
                return;
            }

            int lineIndex = Integer.parseInt(split[1]) + 1;
            lines.add(lineIndex, "");
            this.pullFromDisk = false;
            this.modifyPath(".", p);
            return;
        }

        if(split[0].equals("move")) {
            modifyPath(split[1], p);
        }

        if(split[0].equals("open")) {
            modifyPath(split[1], p);
        }

        if(split[0].equals("editline")) {
            editLineIndex = Integer.parseInt(split[1]);
            this.inventory.close();
            String msg = String.format(
                    "<green>Modification de la ligne %d</green><br>%s",
                    editLineIndex+1,
                    lines.get(editLineIndex)
            );
            Listeners.getStringFromUser(p, msg, input -> {
                Main.runTaskSync(this.plugin, () -> {
                    this.lines.set(editLineIndex, input);
                    this.pullFromDisk = false;
                    this.modifyPath(".", p);
                });
            });
        }

        if(split[0].equals("app_root")) {
            if(!this.loadingDone) return;

            Path appRootFolder = Data.resolvePath(this.ownerID, split[1]);
            Path mainScriptFile = appRootFolder.resolve("./main.py").normalize();
            String appID = appRootFolder.getFileName().toString();
            p.sendRichMessage(String.format("<green>Launching %s</green>", appID));
            if(!Files.exists(mainScriptFile)) {
                Logger.warn(String.format("phone-%s encountered an error: main script file for %s is missing", this.ownerID, split[1]));
            }
            modifyPath(appID, p);
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                jythonInterpreter.exec(String.format(
                        "import sys\nsys.path = ['%s/', '%s/']",
                        appRootFolder.toString().replace("\\", "/"),
                        appRootFolder.toString().replace("\\", "/")
                ));

                try {
                    jythonInterpreter.execfile(mainScriptFile.toString());
                } catch(Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if(jythonInterpreter != null) jythonInterpreter.cleanup();
                }
            });
        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getSize() {
        return SIZE;
    }
}
