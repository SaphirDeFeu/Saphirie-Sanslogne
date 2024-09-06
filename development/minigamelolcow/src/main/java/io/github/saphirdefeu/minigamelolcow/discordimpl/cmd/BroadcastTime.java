package io.github.saphirdefeu.minigamelolcow.discordimpl.cmd;

import club.minnced.discord.webhook.WebhookClient;
import io.github.saphirdefeu.minigamelolcow.Main;
import io.github.saphirdefeu.minigamelolcow.discordimpl.DiscordImplementation;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class BroadcastTime implements BasicCommand {

    public static final String name = "broadcasttime";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Broadcasts the day to Discord";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        WebhookClient client = DiscordImplementation.getWebhookClient();
        String[] date = Main.getCurrentDate();
        String msg = String.format("Nous sommes le **%s %s %s**", date[0], date[1], date[2]);
        client.send(msg);
    }
}
