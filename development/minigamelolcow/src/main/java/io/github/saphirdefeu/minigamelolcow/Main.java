package io.github.saphirdefeu.minigamelolcow;

import io.github.saphirdefeu.minigamelolcow.calculator.CalculatorAddon;
import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.EconomyAddon;
import io.github.saphirdefeu.minigamelolcow.nbtedit.NBTAddon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.LinkedList;

public final class Main extends JavaPlugin {
    public static final String PREFIX = "[MLC-PL] ";
    protected EconomyAddon econModule;
    protected CalculatorAddon calcAddon;
    protected NBTAddon nbtAddon;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();

        Logger.debug("Enabling MLC");

        FileConfiguration config = this.getConfig();



        if(config.getBoolean("addons.economy.enabled")) {
            Logger.debug("Initializing ECONOMY addon");
            try {
                econModule = new EconomyAddon(this);
            } catch(RuntimeException e) {
                Logger.err("Something went wrong!");
                // getServer().getPluginManager().disablePlugin(this);
            }
        }

        if(config.getBoolean("addons.calculator.enabled")) {
            Logger.debug("Initializing CALCULATOR addon");
            calcAddon = new CalculatorAddon(this);
        }

        if(config.getBoolean("addons.nbt.enabled")) {
            Logger.debug("Initializing NBT addon");
            nbtAddon = new NBTAddon(this);
        }

        Logger.debug("MLC enabled");
    }

    public void onLoad() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger.debug("Disabling MLC");
        Database.close();
        Logger.debug("MLC disabled");
    }

    public static boolean playerExists(String username) {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        LinkedList<String> usernames = new LinkedList<>();
        for(Player player : onlinePlayers) {
            usernames.add(player.getName());
        }
        if(usernames.contains(username)) {
            return true;
        }
        return false;
    }
}
