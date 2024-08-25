package io.github.saphirdefeu.minigamelolcow.phones.cmd;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class Phone implements BasicCommand {
    public static final String name = "phone";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Summons the specified Pineapple:tm: phone into your inventory";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        String identifier = "mlc:";
        if(args.length == 0) {
            identifier += stack.getSender().getName().toLowerCase();
        } else {
            identifier += args[0].toLowerCase();
        }


    }
}
