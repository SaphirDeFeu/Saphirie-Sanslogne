package io.github.saphirdefeu.minigamelolcow;

import io.github.saphirdefeu.minigamelolcow.calculator.CalculatorAddon;
import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.EconomyAddon;
import io.github.saphirdefeu.minigamelolcow.listeners.Listeners;
import io.github.saphirdefeu.minigamelolcow.nbtedit.NBTAddon;
import io.github.saphirdefeu.minigamelolcow.phones.PhonesAddon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

public final class Main extends JavaPlugin {
    public static final String PREFIX = "[MLC-PL] ";
    private EconomyAddon econModule;
    private CalculatorAddon calcAddon;
    private NBTAddon nbtAddon;
    private PhonesAddon phonesAddon;

    @Override
    public void onEnable() {
        // Récupérer la configuration, ou la créer si elle n'existe pas
        this.saveDefaultConfig(); // création d'une config défaut

        Logger.debug("Enabling MLC");

        FileConfiguration config = this.getConfig(); // Config

        if(config.getBoolean("addons.economy.enabled")) {
            Logger.debug("Initializing ECONOMY addon");
            try {
                econModule = new EconomyAddon(this);
            } catch(SQLException e) {
                Logger.err("Something went wrong while initializing ECONOMY addon!");
            }
        }

        if(config.getBoolean("addons.calculator.enabled")) {
            Logger.debug("Initializing CALCULATOR addon");
            calcAddon = new CalculatorAddon(this);
        }

        if(config.getBoolean("addons.nbt.enabled")) {
            Logger.debug("Initializing NBT addon");
            Logger.warn("NBT ADDON IS EXPERIMENTAL - THINGS MAY BREAK");
            nbtAddon = new NBTAddon(this);
        }

        if(config.getBoolean("addons.listeners.enabled")) {
            Logger.debug("Registering global events");
            Listeners.register(this);
        }

        if(config.getBoolean("addons.phones.enabled")) {
            Logger.debug("Initializing Phones addon");
            try {
                Class.forName("org.python.util.PythonInterpreter");
                phonesAddon = new PhonesAddon(this);
            } catch(ClassNotFoundException e) {
                Logger.err("Cannot initialize Phones Addon: missing dependency 'org.python.util.PythonInterpreter'");
            }
        }

        Logger.debug("MLC enabled");
    }

    public void onLoad() {}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger.debug("Disabling MLC");
        Database.close();
        if(phonesAddon != null) phonesAddon.destroy();
        Logger.debug("MLC disabled");
    }

    public static boolean playerExists(String username) {
        // Itérer sur tous les joueurs et stocker leur nom
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        LinkedList<String> usernames = new LinkedList<>();
        for(Player player : onlinePlayers) {
            usernames.add(player.getName());
        }

        return usernames.contains(username);
    }
}
