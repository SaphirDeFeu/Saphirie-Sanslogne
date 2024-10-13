package io.github.saphirdefeu.minigamelolcow.phones.api;

import io.github.saphirdefeu.minigamelolcow.phones.PhoneGUI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ItemStackWrapper extends ItemStack {
    private PhoneGUI gui;
    private String appName;

    public ItemStackWrapper(PhoneGUI phoneGUI) {
        this.gui = phoneGUI;
        this.appName = "tmp";
    }

    public ItemStack createItem(String material) {
        Material mat = Material.matchMaterial(material);
        if (mat == null) {
            throw new IllegalArgumentException("Invalid material: " + material);
        }
        return new ItemStack(mat);
    }

    public ItemStack createButton(@NotNull String material, @NotNull String name, @NotNull Runnable execute) {
        ItemStack item = this.createItem(material);
        ItemMeta mm = item.getItemMeta();
        mm.getPersistentDataContainer().set(new NamespacedKey("mlc-pl", appName), PersistentDataType.STRING, name);
        item.setItemMeta(mm);
        return item;
    }
}
