package io.github.saphirdefeu.minigamelolcow.economy;

import io.papermc.paper.command.brigadier.Commands;
import io.github.saphirdefeu.minigamelolcow.Logger;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.saphirdefeu.minigamelolcow.economy.cmd.*;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Add-on ECONOMY du plugin, classe principale qui gère l'entièreté de la section économique
 */
public final class EconomyAddon {
    public static final String RESOURCE_LOCATION = "mlc:econ";

    /**
     * Setup de l'add-on
     * @param plugin L'instance du plugin actuel
     * @throws SQLException si on ne peut se connecter à la base de données
     */
    public EconomyAddon(JavaPlugin plugin) throws SQLException {
        int databaseReturnValue = Database.connect();
        if(databaseReturnValue == 1) {
            throw new SQLException("cannot connect to database");
        } else {
            Logger.debug(RESOURCE_LOCATION + " - Economy addon initialized");
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
            commands.register(EconomyManager.name, EconomyManager.description, EconomyManager.aliases, new EconomyManager());
            commands.register(AdminPay.name, AdminPay.description, AdminPay.aliases, new AdminPay());
            commands.register(Pay.name, Pay.description, Pay.aliases, new Pay());
            commands.register(Withdraw.name, Withdraw.description, Withdraw.aliases, new Withdraw());
            commands.register(Balance.name, Balance.description, Balance.aliases, new Balance());
            commands.register(Deposit.name, Deposit.description, Deposit.aliases, new Deposit());
            commands.register(Salary.name, Salary.description, Salary.aliases, new Salary());
            // !
        });
    }

    /**
     * Vérifie si un compte existe
     * @param username Le nom du compte
     * @return true - Il existe | false - n'existe pas
     */
    public static boolean accountExists(String username) {
        HashMap<String, Double> accounts = Database.getAccounts();
        return accounts.containsKey(username);
    }

}
