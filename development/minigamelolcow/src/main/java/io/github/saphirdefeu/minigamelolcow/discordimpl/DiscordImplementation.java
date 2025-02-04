package io.github.saphirdefeu.minigamelolcow.discordimpl;

import club.minnced.discord.webhook.WebhookClient;
import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.discordimpl.cmd.ReasonStop;
import io.github.saphirdefeu.minigamelolcow.discordimpl.eventlisteners.MessageReceived;
import io.github.saphirdefeu.minigamelolcow.discordimpl.eventlisteners.SlashCommandReceived;
import io.github.saphirdefeu.minigamelolcow.discordimpl.cmd.BroadcastTime;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DiscordImplementation {
    private static String token;
    private static String webhookUrl;
    private static Thread thread;

    private static JDA jda;
    private static WebhookClient webhookClient;

    public DiscordImplementation(@NotNull String _token, @NotNull String url, @NotNull JavaPlugin plugin) {
        token = _token;
        webhookUrl = url;

        thread = new Thread(() -> {
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.watching("🟢 MinigameLolCow"))
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new MessageReceived())
                    .addEventListeners(new SlashCommandReceived(plugin))
                    .build();

            jda.updateCommands().addCommands(
                    Commands.slash("playerpos", "Récupères la position d'un joueur avec une approximation du lieu dans lequel il est")
                            .addOption(OptionType.STRING, "player", "Le nom du joueur")
                            .addOption(OptionType.INTEGER, "locations", "Le nombre maximal de positions de référence à donner"),
                    Commands.slash("time", "Récupères le temps actuel sur le serveur"),
                    Commands.slash("cmd", "Exécutes une commande sur le serveur")
                            .addOption(OptionType.STRING, "command", "La commande à exécuter")
            ).queue();

            try {
                jda.awaitReady();
                Logger.debug("Discord Bot initialized!");
            } catch (InterruptedException | IllegalStateException e) {
                e.printStackTrace();
                Logger.warn("If you were shutting down your server when this message appeared, you can ignore this error");
            }
        });

        thread.start();

        if(webhookUrl.isEmpty()) return;

        webhookClient = WebhookClient.withUrl(webhookUrl);
        webhookClient.send(":green_circle: Le serveur s'allume");

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(new GameEventListener(), plugin);

        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final io.papermc.paper.command.brigadier.Commands commands = event.registrar();

            commands.register(BroadcastTime.name, BroadcastTime.description, BroadcastTime.aliases, new BroadcastTime());
            commands.register(ReasonStop.name, ReasonStop.description, ReasonStop.aliases, new ReasonStop());
        });
    }

    public void destroy() {
        webhookClient.send(":red_circle: Le serveur s'éteint");
        jda.shutdown();
        if(webhookClient != null) webhookClient.close();

        try {
            jda.awaitShutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static WebhookClient getWebhookClient() {
        return webhookClient;
    }

    public static JDA getJda() {
        return jda;
    }
}
