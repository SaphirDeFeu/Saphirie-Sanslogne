package io.github.saphirdefeu.minigamelolcow.economy;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DepositListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        if(!event.hasItem()) return;

        ItemStack item = event.getItem();
        if(item == null) return;

        ItemMeta mm = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("mlc-pl", "amount");
        if(mm.getPersistentDataContainer().has(key))
            Bukkit.dispatchCommand(event.getPlayer(), "deposit");
    }
}
