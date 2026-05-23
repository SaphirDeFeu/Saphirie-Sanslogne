package io.github.saphirdefeu.minigamelolcow.citizens;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.citizens.cmd.*;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Add-on ECONOMY du plugin, classe principale qui gère l'entièreté de la section économique
 */
public final class CitizensAddon {
    public static final String RESOURCE_LOCATION = "mlc:citizens";

    /**
     * Setup de l'add-on
     * @param plugin L'instance du plugin actuel
     * @throws SQLException si on ne peut se connecter à la base de données
     */
    public CitizensAddon(JavaPlugin plugin) throws SQLException {
        int databaseReturnValue = io.github.saphirdefeu.minigamelolcow.citizens.Database.connect();
        if(databaseReturnValue == 1) {
            throw new SQLException("cannot connect to database");
        } else {
            Logger.debug(RESOURCE_LOCATION + " - Citizens addon initialized");
        }

        registerCommands(plugin);
    }

    /**
     * Enregistre les commandes de l'add-on
     * @param plugin L'instance du plugin actuel
     */
    private void registerCommands(JavaPlugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            // ! Commandes
            commands.register(UpdateCitizen.name, UpdateCitizen.description, UpdateCitizen.aliases, new UpdateCitizen());
            // !
        });
    }
}
