package io.github.saphirdefeu.minigamelolcow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public abstract class Logger {
    public static void debug(String s) {
        ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
        consoleSender.sendMessage(ChatColor.WHITE + Main.PREFIX + s);
    }

    public static void warn(String s) {
        ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
        consoleSender.sendMessage(ChatColor.GOLD + Main.PREFIX + s);
    }

    public static void err(String s) {
        ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
        consoleSender.sendMessage(ChatColor.RED + Main.PREFIX + s);
    }
}
