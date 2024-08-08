package io.github.saphirdefeu.minigamelolcow.economy.cmd;

import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Withdraw implements BasicCommand {

    public static final String name = "withdraw";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Withdraws a currency note of a specified amount to your inventory";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.withdraw") && sender instanceof Player;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if(!(stack.getSender() instanceof Player)) {
            stack.getSender().sendRichMessage(String.format("<red>You are not supposed to be here</red>"));
        }
        if(args.length == 0) {
            stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: expected 1 or 2 arguments, got %d</red>", args.length));
            return;
        }

        String accountHolder = stack.getSender().getName();
        if(args.length >= 2 && stack.getSender().hasPermission("mlc.economy.admin")) {
            accountHolder = args[1];
        } else if(!stack.getSender().hasPermission("mlc.economy.admin")) {
            stack.getSender().sendRichMessage("<red>You cannot withdraw from an account without permission</red>");
            return;
        }

        double amount = 0.0;
        try {
            amount = Double.parseDouble(args[0]);
            if(amount < 0.01) throw new NumberFormatException();
        } catch(NumberFormatException e) {
            stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: '%s' is not a valid withdraw number</red>", args[0]));
            return;
        }

        stack.getSender().sendRichMessage(String.format("<rainbow>[MLC-PL]</rainbow> Withdrawn <yellow>%.2f$</yellow> from <yellow>%s</yellow>", amount, accountHolder));

        ItemStack note = new ItemStack(Material.PAPER);
        ItemMeta mm = note.getItemMeta();
        mm.itemName(
                Component.text("Billet", NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
        );
        mm.lore(List.of(
                Component.text("Amount: ", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(
                                Component.text(String.format("$%.2f SAD", amount), NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
        ));
        mm.getPersistentDataContainer().set(new NamespacedKey("mlc-pl", "amount"), PersistentDataType.DOUBLE, amount);
        note.setItemMeta(mm);

        PlayerInventory inv = ((Player) stack.getSender()).getInventory();
        inv.addItem(note);

        double balance = EconomyManager.getBalance(null, accountHolder);
        EconomyManager.setBalance(null, accountHolder, balance - amount);
    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        HashMap<String, Double> accounts = Database.getAccounts();
        LinkedList<String> accountUsernames = new LinkedList<>();

        for(String accountUsername : accounts.keySet()) {
            accountUsernames.add(accountUsername);
        }

        return accountUsernames;
    }
}