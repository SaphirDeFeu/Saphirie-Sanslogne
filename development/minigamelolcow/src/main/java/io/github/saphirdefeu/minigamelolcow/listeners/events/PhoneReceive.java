package io.github.saphirdefeu.minigamelolcow.listeners.events;

import io.github.saphirdefeu.minigamelolcow.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;

public class PhoneReceive implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        if(!event.hasItem()) return;

        ItemStack item = event.getItem();
        ItemMeta mm = item.getItemMeta();

        if(mm.getPersistentDataContainer().has(new NamespacedKey("mlc-pl", "phoneowner"))) {
            // We have a match, we right clicked on a phone
            Logger.debug("Phone");
        }
    }
}
