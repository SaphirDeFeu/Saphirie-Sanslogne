package io.github.saphirdefeu.minigamelolcow.discordimpl.eventlisteners;

import io.github.saphirdefeu.minigamelolcow.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SlashCommandReceived extends ListenerAdapter {

    private static JavaPlugin plugin;
    public SlashCommandReceived(JavaPlugin _plugin) {
        plugin = _plugin;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch(event.getName()) {
            case "playerpos": {
                playerPosCommand(event);
                break;
            }
            case "time": {
                timeCommand(event);
                break;
            }
        }
    }

    public static void playerPosCommand(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        OptionMapping playerNameOptionMapping = event.getOption("player");
        if(playerNameOptionMapping == null) return;

        String playerName = playerNameOptionMapping.getAsString();

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(playerName);
            if(player == null) {
                event.getHook().sendMessage(String.format("**%s** n'est pas connecté ou n'existe pas.", playerName)).queue();
                return;
            }

            Location loc = player.getLocation();

            String discordMessage = String.format("**%s** se situe à X:%d Y:%d Z:%d",
                    playerName,
                    loc.getBlockX(),
                    loc.getBlockY(),
                    loc.getBlockZ()
            );

            String minecraftMessage = String.format("<blue>[Discord] <bold>%s</bold> a récupéré votre position.</blue>",
                    event.getUser().getName()
            );

            player.sendRichMessage(minecraftMessage);
            event.getHook().sendMessage(discordMessage).queue();
        });


    }

    public static void timeCommand(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Bukkit.getScheduler().runTask(plugin, () -> {
            String[] date = Main.getCurrentDate();
            String time = Main.getCurrentTime();

            String msg = String.format("Il est **%s** le **%s %s %s**", time, date[0], date[1], date[2]);

            event.getHook().sendMessage(msg).queue();
        });
    }

}
