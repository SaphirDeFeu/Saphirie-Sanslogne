package io.github.saphirdefeu.minigamelolcow.phones.api;

import io.github.saphirdefeu.minigamelolcow.phones.PhoneGUI;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InteractiveHandlers {
    final PhoneGUI gui;

    public InteractiveHandlers(@NotNull PhoneGUI gui) {
        this.gui = gui;
    }

    public String createInteractiveButton(@NotNull String appName, @NotNull String id, @NotNull Runnable runnable) {
        this.gui.addInteractiveHandler("button", appName, id, runnable);
        return String.format("event:%s:%s", appName, id);
    }
}
