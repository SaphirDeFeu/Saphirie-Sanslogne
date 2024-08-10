package io.github.saphirdefeu.minigamelolcow.listeners;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Listeners {

    public static void register(JavaPlugin plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new io.github.saphirdefeu.minigamelolcow.listeners.events.Time(), plugin);
        pluginManager.registerEvents(new io.github.saphirdefeu.minigamelolcow.listeners.events.Alerts(), plugin);

    }

}
