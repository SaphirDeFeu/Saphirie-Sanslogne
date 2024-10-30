package io.github.saphirdefeu.minigamelolcow.phones.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Server {
    private Player player;

    public Server(Player player) {
        this.player = player;
    }

    public void dispatchCommand(@NotNull String cmd) {
        Bukkit.dispatchCommand(this.player, cmd);
    }

}
