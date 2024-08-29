package io.github.saphirdefeu.minigamelolcow.phones.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class ItemStackWrapper extends ItemStack {
    public static ItemStack createItem(String material) {
        Material mat = Material.matchMaterial(material);
        if (mat == null) {
            throw new IllegalArgumentException("Invalid material: " + material);
        }
        return new ItemStack(mat);
    }
}
