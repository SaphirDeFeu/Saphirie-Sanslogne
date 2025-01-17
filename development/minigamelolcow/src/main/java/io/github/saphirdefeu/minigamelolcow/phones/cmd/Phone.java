package io.github.saphirdefeu.minigamelolcow.phones.cmd;

import io.github.saphirdefeu.minigamelolcow.phones.Data;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class Phone implements BasicCommand {
    public static final String name = "phone";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Summons the specified Pineapple:tm: phone into your inventory";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        String owner = "";
        if(args.length == 0) {
            owner += stack.getSender().getName().toLowerCase();
        } else {
            owner += args[0].toLowerCase();
        }

        ItemStack item = new ItemStack(Material.IRON_DOOR);
        ItemMeta mm = item.getItemMeta();
        mm.itemName(
                Component.text("Téléphone Pineapple:tm:", NamedTextColor.YELLOW)
                        .decoration(TextDecoration.BOLD, true)
        );
        mm.lore(List.of(
                Component.text(String.format("Propriétaire : %s", owner), NamedTextColor.GRAY)
        ));
        mm.getPersistentDataContainer().set(new NamespacedKey("mlc-pl", "phoneowner"), PersistentDataType.STRING, owner);

        item.setItemMeta(mm);

        PlayerInventory inv = ((Player) stack.getSender()).getInventory();
        inv.addItem(item);

        Data.getRoot(owner);
    }
}
