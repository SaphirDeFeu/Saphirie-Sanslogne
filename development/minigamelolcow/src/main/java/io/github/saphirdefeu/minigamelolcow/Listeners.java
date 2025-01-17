package io.github.saphirdefeu.minigamelolcow;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;
import java.util.function.Consumer;

public class Listeners implements Listener {
    private static final Map<UUID, Consumer<String>> inputCallbacks = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Time
        {
            String[] strings = Main.getCurrentDate();

            event.getPlayer().sendRichMessage(
                    String.format(" >> <rainbow>[MinigameLolCow]</rainbow> <gold>Vous vous connectez le %s %s %s</gold>",
                            strings[0],
                            strings[1],
                            strings[2]
                    )
            );
        }
        // Alerts
        {
            Collection<String> recurrentPlayers = List.of("SaphirDeFeu", "sans_psaudo");

            if(recurrentPlayers.contains(event.getPlayer().getName())) {
                return;
            }

            event.getPlayer().sendRichMessage(" >> <rainbow>[MinigameLolCow]</rainbow> To experience the full effect of this server, please use CIT and CEM mod" +
                    " such as OptiFine for CIT+CEM, cit-resewn for CIT, and entity model features + entity texture features for CEM<br>" +
                    "If you do not use those mods, some items may appear with their normal textures instead of the custom textures and models we used on this server"
            );
        }
    }

    public static void getStringFromUser(Player player, String msg, Consumer<String> callback) {
        inputCallbacks.put(player.getUniqueId(), callback);
        player.sendRichMessage(msg);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if the player is waiting for input
        if (inputCallbacks.containsKey(playerUUID)) {
            // Cancel the event to prevent it from being broadcasted
            event.setCancelled(true);

            // Get the callback and execute it with the player's input
            Consumer<String> callback = inputCallbacks.remove(playerUUID);
            callback.accept(Main.getComponentText(event.originalMessage()));
        }
    }

}
