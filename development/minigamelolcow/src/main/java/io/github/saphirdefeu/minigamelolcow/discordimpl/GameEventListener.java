package io.github.saphirdefeu.minigamelolcow.discordimpl;

import club.minnced.discord.webhook.WebhookClient;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameEventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        WebhookClient discord = DiscordImplementation.getWebhookClient();
        Player player = event.getPlayer();
        String s = ":arrow_right: ";
        switch(player.getName()) {
            case "SaphirDeFeu":
                s += ":small_orange_diamond: **SaphirDeFeu** a rejoint";
                break;
            case "sans_psaudo":
                s += ":small_blue_diamond: **sans_psaudo** a rejoint";
                break;
            default:
                s += String.format("**%s** a rejoint", player.getName());
                break;
        }
        discord.send(s);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        WebhookClient discord = DiscordImplementation.getWebhookClient();
        Player player = event.getPlayer();
        String s = ":arrow_left: ";
        switch(player.getName()) {
            case "SaphirDeFeu":
                s += ":small_orange_diamond: **SaphirDeFeu** a quitté";
                break;
            case "sans_psaudo":
                s += ":small_blue_diamond: **sans_psaudo** a quitté";
                break;
            default:
                s += String.format("**%s** a quitté", player.getName());
                break;
        }
        discord.send(s);
    }
}
