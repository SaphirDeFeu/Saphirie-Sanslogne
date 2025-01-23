package io.github.saphirdefeu.minigamelolcow.fixes.cmd;

import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.cmd.AdminPay;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MVSpawn implements BasicCommand {
    public static final String name = "spawnpoint";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Usage: /spawnpoint [selector] [x] [y] [z]";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.") && sender instanceof Player;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {


    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        
    }
}