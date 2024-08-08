package io.github.saphirdefeu.minigamelolcow.calculator;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.saphirdefeu.minigamelolcow.calculator.cmd.*;

public class CalculatorAddon {
    public static final String RESOURCE_LOCATION = "mlc:calc";

    public static ConsoleCommandSender consoleSender;

    public CalculatorAddon(JavaPlugin plugin) {
        consoleSender = Bukkit.getServer().getConsoleSender();
        Logger.debug(RESOURCE_LOCATION + " - Calculator addon initialized");

        registerCommands(plugin);
    }

    /**
     * Enregistre les commandes de l'add-on Calculator
     * @param plugin L'instance du plugin actuel
     */
    private void registerCommands(JavaPlugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            // ! Commandes
            commands.register(Calculate.name, Calculate.description, Calculate.aliases, new Calculate());
            // !
        });
    }
}
