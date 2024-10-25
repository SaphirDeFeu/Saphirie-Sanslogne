package io.github.saphirdefeu.minigamelolcow.phones.api;

import io.github.saphirdefeu.minigamelolcow.phones.PhoneGUI;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class Sounds {

    private PhoneGUI gui;
    private Player player;

    public Sounds(PhoneGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    public Sound use(String id) {
        return Sound.valueOf(id);
    }

    public void play(Sound id, float volume, float pitch) {
        this.player.playSound(this.player, id, SoundCategory.MASTER, volume, pitch);
    }

    public void play(Sound id) {
        play(id, 1.0f, 1.0f);
    }
}
