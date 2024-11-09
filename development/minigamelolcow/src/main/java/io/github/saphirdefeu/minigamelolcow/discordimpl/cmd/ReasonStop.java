package io.github.saphirdefeu.minigamelolcow.discordimpl.cmd;

import club.minnced.discord.webhook.WebhookClient;
import io.github.saphirdefeu.minigamelolcow.Main;
import io.github.saphirdefeu.minigamelolcow.discordimpl.DiscordImplementation;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ReasonStop implements BasicCommand {

    public static final String name = "com";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Communicates a message to the discord #logs channel";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        WebhookClient client = DiscordImplementation.getWebhookClient();
        String[] date = Main.getCurrentDate();
        String type = args[0].toUpperCase();
        StringBuilder all_args = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            String arg = args[i];
            all_args.append(arg);
            all_args.append(" ");
        }

        String msg = String.format(":grey_question: Message de type **%s** pour cause: %s", type, all_args);
    }
}