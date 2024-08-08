package io.github.saphirdefeu.minigamelolcow.economy.cmd;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class Salary implements BasicCommand {

    public static final String name = "salary";
    public static final Collection<String> aliases = List.of("sal");
    public static final String description = "Sets and gets the salary of an account. Can be used to deposit the salary of every account at once";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.salary");
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {

    }
}