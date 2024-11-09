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

    public static final String name = "rstop";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Stops and records a reason for stopping to the discord bot";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        WebhookClient client = DiscordImplementation.getWebhookClient();
        String[] date = Main.getCurrentDate();
        StringBuilder all_args = new StringBuilder();
        for(String arg : args) {
            all_args.append(arg);
            all_args.append(" ");
        }

        String msg = String.format(":grey_question: Le serveur s'arrête pour cause: %s", all_args);
        client.send(msg);

        // Minestrator est une p*te du coup /stop est considéré comme un crash et force un redémarrage.
        // Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:stop");
    }
}