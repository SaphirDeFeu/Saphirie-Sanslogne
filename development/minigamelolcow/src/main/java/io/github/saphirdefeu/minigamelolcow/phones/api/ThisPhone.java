package io.github.saphirdefeu.minigamelolcow.phones.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ThisPhone {
    private Player player;
    private String owner;

    public ThisPhone(@NotNull Player player, @NotNull String owner) {
        this.player = player;
        this.owner = owner;
    }

    public class GPS {
        public Location where() {
            return player.getLocation();
        }
    }

    public class Data {
        
    }
}
