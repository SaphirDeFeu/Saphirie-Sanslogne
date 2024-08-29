package io.github.saphirdefeu.minigamelolcow.phones;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public class PhoneReceive implements Listener {
    private final JavaPlugin plugin;
    public PhoneReceive(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        if(!event.hasItem()) return;

        ItemStack item = event.getItem();
        PersistentDataContainer mm = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey("mlc-pl", "phoneowner");

        if(!mm.has(key)) return;

        @Nullable String owner = mm.get(key, PersistentDataType.STRING);
        if(owner == null) return;

        PhoneGUI phoneGUI = new PhoneGUI(this.plugin, owner);
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(phoneGUI, plugin);

        phoneGUI.openInventory(event.getPlayer());
    }
}
