package io.github.saphirdefeu.minigamelolcow.nbtedit;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.nbtedit.cmd.NBT;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class NBTAddon {
    public static final String RESOURCE_LOCATION = "mlc:nbt";

    public static ConsoleCommandSender consoleSender;

    public NBTAddon(JavaPlugin plugin) {
        consoleSender = Bukkit.getServer().getConsoleSender();
        Logger.debug(RESOURCE_LOCATION + " - NBT addon initialized");

        registerCommands(plugin);
    }

    private void registerCommands(JavaPlugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(NBT.name, NBT.description, NBT.aliases, new NBT(plugin));
        });
    }

    public static void registerEvent(JavaPlugin plugin, NBTGUI gui) {
        plugin.getServer().getPluginManager().registerEvents(gui, plugin);
    }
}
