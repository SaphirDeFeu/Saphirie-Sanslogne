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

import java.util.HashMap;

public class EconomyAddon {
    public static final String RESOURCE_LOCATION = "mlc:econ";

    public static ConsoleCommandSender consoleSender;

    public EconomyAddon(JavaPlugin plugin) {
        consoleSender = Bukkit.getServer().getConsoleSender();

        int databaseReturnValue = Database.connect();
        if(databaseReturnValue == 1) {
            throw new RuntimeException("cannot connect to database");
        } else {
            Logger.debug(RESOURCE_LOCATION + " - Economy addon initialized");
        }

        registerCommands(plugin);
    }

    private void registerCommands(JavaPlugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(EconomyManager.name, EconomyManager.description, EconomyManager.aliases, new EconomyManager());
            commands.register(AdminPay.name, AdminPay.description, AdminPay.aliases, new AdminPay());
            commands.register(Pay.name, Pay.description, Pay.aliases, new Pay());
            commands.register(Withdraw.name, Withdraw.description, Withdraw.aliases, new Withdraw());
            commands.register(Balance.name, Balance.description, Balance.aliases, new Balance());
            commands.register(Deposit.name, Deposit.description, Deposit.aliases, new Deposit());
            commands.register(Salary.name, Salary.description, Salary.aliases, new Salary());
        });
    }

    public static boolean accountExists(String username) {
        HashMap<String, Double> accounts = Database.getAccounts();
        if(accounts.containsKey(username)) {
            return true;
        }
        return false;
    }

}
