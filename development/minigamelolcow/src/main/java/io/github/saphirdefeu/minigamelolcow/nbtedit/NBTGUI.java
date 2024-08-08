package io.github.saphirdefeu.minigamelolcow.nbtedit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class NBTGUI implements Listener {
    private final Inventory inventory;
    private String currentNBTPath = "/";

    public NBTGUI(ItemStack item, ItemMeta meta) {
        inventory = Bukkit.createInventory(null, 54, "NBT GUI");

        initializeItems(item, meta);
    }

    // You can call this whenever you want to put the items in
    public void initializeItems(ItemStack item, ItemMeta meta) {
        inventory.addItem(item);
    }

    // Nice little method to create a gui item with a custom name, and description
    /* protected ItemStack createEditingItem(final Material material, final byte count) {
        final ItemStack item = new ItemStack(material, count);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    } */

    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inventory);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is the best option for your inventory click's
        p.sendMessage("You clicked at slot " + e.getRawSlot());
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}
