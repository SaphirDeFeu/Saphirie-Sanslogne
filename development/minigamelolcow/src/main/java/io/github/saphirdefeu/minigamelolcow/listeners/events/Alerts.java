package io.github.saphirdefeu.minigamelolcow.listeners.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collection;
import java.util.List;

public class Alerts implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
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
