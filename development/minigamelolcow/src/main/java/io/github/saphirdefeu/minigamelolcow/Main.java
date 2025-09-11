package io.github.saphirdefeu.minigamelolcow;

import io.github.saphirdefeu.minigamelolcow.calculator.CalculatorAddon;
import io.github.saphirdefeu.minigamelolcow.discordimpl.DiscordImplementation;
import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.EconomyAddon;
import io.github.saphirdefeu.minigamelolcow.nbtedit.NBTAddon;
import io.github.saphirdefeu.minigamelolcow.phones.PhonesAddon;
import io.github.saphirdefeu.minigamelolcow.webserver.WebserverAddon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

public final class Main extends JavaPlugin {
    public static final String PREFIX = "[MLC-PL] ";
    private static File pluginDataFolder;
    private EconomyAddon econModule;
    private CalculatorAddon calcAddon;
    private NBTAddon nbtAddon;
    private PhonesAddon phonesAddon;
    private DiscordImplementation discord;
    private WebserverAddon webserver;

    @Override
    public void onEnable() {
        // Récupérer la configuration, ou la créer si elle n'existe pas
        this.saveDefaultConfig(); // création d'une config défaut
        pluginDataFolder = this.getDataFolder();

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

        if(config.getBoolean("addons.phones.enabled")) {
            Logger.debug("Initializing PHONES addon");
            try {
                Class.forName("org.python.util.PythonInterpreter");
                phonesAddon = new PhonesAddon(this);
            } catch(ClassNotFoundException e) {
                Logger.err("Cannot initialize PHONES Addon: missing dependency 'org.python.util.PythonInterpreter'");
            }
        }

        if(config.getBoolean("addons.discord.enabled")) {
            Logger.debug("Initializing Discord Implementation");
            try {
                Class.forName("net.dv8tion.jda.api.JDA");
                String token = config.getString("addons.discord.token");
                String url = config.getString("addons.discord.webhook_url");
                if(token != null && url != null) discord = new DiscordImplementation(token, url, this);
            } catch (ClassNotFoundException e) {
                Logger.err("Cannot initialize Discord implementation: missing dependency 'net.dv8tion.jda.api.JDA'");
            }
        }

        if(config.getBoolean("addons.webserver.enabled")) {
            Logger.debug("Initializing Web Server");
            String url = config.getString("addons.webserver.repo");
            if(url != null) webserver = new WebserverAddon(url);
        }

        if(config.getBoolean("addons.listeners.enabled")) {
            Logger.debug("Registering global events");
            PluginManager pluginManager = this.getServer().getPluginManager();
            pluginManager.registerEvents(new Listeners(), this);
        }

        Bukkit.getServer().getConsoleSender().sendRichMessage("<br><green>---------------------------------</green><br>" +
        "<#ff7000>#<white>#############<blue>###################<br>" +
        "<#ff7000>####<white>#############<blue>################<br>" +
        "<#ff7000>#######<white>#############<blue>#############<br>" +
        "<#ff7000>##########<white>#############<blue>##########<br>" +
        "<#ff7000>#############<white>#############<blue>#######<br>" +
        "<#ff7000>################<white>#############<blue>####<br>" +
        "<#ff7000>###################<white>#############<blue>#<br>" +
        "<green>---------------------------------</green>");

        Bukkit.getServer().getConsoleSender().sendRichMessage("<rainbow>MLC enabled</rainbow><br>" +
                "<blue>Gloire a la Sanslogne.</blue> " +
                "<white>Longue vie a l'Union.</white> " +
                "<#ff7000>Gloire a la Saphirie.<white></white>");
    }

    public void onLoad() {}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger.debug("Disabling MLC");
        Database.close();
        if(phonesAddon != null) phonesAddon.destroy();
        if(discord != null) discord.destroy();
        if(webserver != null) webserver.destroy();
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

    public static LinkedList<String> getPlayerUsernames() {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        LinkedList<String> usernames = new LinkedList<>();
        for(Player player : onlinePlayers) {
            usernames.add(player.getName());
        }

        return usernames;
    }

    public static File getPluginDataFolder() {
        return pluginDataFolder;
    }

    public static void runTaskSync(JavaPlugin plugin, Runnable callback) {
        Bukkit.getScheduler().runTask(plugin, callback);
    }

    public static String getComponentText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String[] getCurrentDate() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        Objective obj = scoreboard.getObjective("Timesave");

        int days = obj.getScore("dayOfMonth").getScore();
        int month = obj.getScore("month").getScore() - 1;
        int year = obj.getScore("year").getScore();

        String[] months = {
                "Janvier",
                "Fevrier",
                "Mars",
                "Avril",
                "Mai",
                "Juin",
                "Juillet",
                "Aout",
                "Septembre",
                "Octobre",
                "Novembre",
                "Décembre",
        };

        return new String[]{ Integer.toString(days), months[month], Integer.toString(year) };
    }

    public static String getCurrentTime() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        Objective obj = scoreboard.getObjective("Timesave");

        Score daytime = obj.getScore("daytime");
        int ticks = daytime.getScore();

        float tick_hour = ((float)ticks) / 1000.0f;
        int hour = (6 + ((int)tick_hour)) % 24;

        int tick_minute = ticks % 1000;
        float minutes_with_sec = ((float) tick_minute) / (16.0f + 2.0f / 3.0f);

        float seconds = (minutes_with_sec % 1.0f) * 60.0f;

        return String.format("%d:%d:%.3f", hour, (int) minutes_with_sec, seconds);
    }
}
