package io.github.saphirdefeu.minigamelolcow.phones.api;

import io.github.saphirdefeu.minigamelolcow.phones.PhoneGUI;
import org.bukkit.inventory.ItemStack;

public class InventoryWrapper {
    private final PhoneGUI phoneGUI;

    public InventoryWrapper(PhoneGUI phoneGUI) {
        this.phoneGUI = phoneGUI;
    }

    public void setItem(int index, ItemStack item) {
        if(index >= phoneGUI.getSize()) throw new IllegalArgumentException("index must be less than the size of the gui");
        if(index < 1) throw new IllegalArgumentException("index must be a positive integer, strictly superior to 0");
        phoneGUI.getInventory().setItem(index, item);
    }

}
