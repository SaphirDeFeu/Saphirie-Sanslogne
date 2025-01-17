package io.github.saphirdefeu.minigamelolcow.nbtedit.cmd;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.nbtedit.NBTAddon;
import io.github.saphirdefeu.minigamelolcow.nbtedit.NBTGUI;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class NBT implements BasicCommand {

    public static final String name = "nbtedit";
    public static final Collection<String> aliases = List.of("nbt");
    public static final String description = "";
    private final JavaPlugin plugin;

    public NBT(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.nbt.nbt") && sender instanceof Player;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, String[] args) {
        PlayerInventory pInv = ((Player) stack.getSender()).getInventory();
        int heldItemSlot = pInv.getHeldItemSlot();
        ItemStack heldItem = pInv.getItem(heldItemSlot);

        if(heldItem == null || heldItem.getType().isAir()) {
            stack.getSender().sendRichMessage("<red>You must be holding an item to run this command!</red>");
            return;
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();

        stack.getSender().sendRichMessage("<rainbow>[MLC-NBT]</rainbow> Opening NBT Editor");

        NBTGUI nbtgui = new NBTGUI(heldItem, heldItemMeta);
        NBTAddon.registerEvent(this.plugin, nbtgui);
        nbtgui.openInventory((Player) stack.getSender());
    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack stack, String[] args) {
        return List.of();
    }

}
