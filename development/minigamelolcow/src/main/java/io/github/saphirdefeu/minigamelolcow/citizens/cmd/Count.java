package io.github.saphirdefeu.minigamelolcow.citizens.cmd;

import io.github.saphirdefeu.minigamelolcow.citizens.Database;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class Count implements BasicCommand {

    public static final String name = "count";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Counts the number of citizen entries";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        int count = Database.getCount();
        stack.getExecutor().sendRichMessage(String.format("<rainbow>[MLC-PL]</rainbow> <yellow>%d</yellow> citoyens inscrits", count));
    }

}