package io.github.saphirdefeu.minigamelolcow.citizens.cmd;

import io.github.saphirdefeu.minigamelolcow.citizens.Database;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class AddCitizen implements BasicCommand {

    public static final String name = "addcitizen";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Creates a new citizen entry";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        String id = Database.addEntry();
        if(id.equals("0")) {
            stack.getSender().sendRichMessage("<red>An error has occurred</red>");
        } else {
            stack.getSender().sendRichMessage(String.format("<rainbow>[MLC-PL]</rainbow>: Nouveau citoyen créé avec l'identifiant <yellow>%s</yellow>", id));
        }
    }

}
