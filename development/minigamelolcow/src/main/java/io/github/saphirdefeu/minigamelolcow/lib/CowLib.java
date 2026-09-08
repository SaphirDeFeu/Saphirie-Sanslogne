package io.github.saphirdefeu.minigamelolcow.lib;

import io.github.saphirdefeu.minigamelolcow.Main;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Provides an intuitive access point for other plugins to use these controls
 */
public class CowLib {
    private final JavaPlugin plugin;

    /**
     * Constructs a new interface with a specified plugin
     * @param plugin The plugin that needs access to the library
     */
    public CowLib(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract static class Time {
        public static double[] getTime() {
            return Main.getCurrentTime();
        }

        public static @NotNull String[] getDate() {
            return Main.getCurrentDate();
        }
    }
}
