package io.github.saphirdefeu.minigamelolcow.economy.cmd;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.EconomyAddon;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Deposit implements BasicCommand {

    public static final String name = "deposit";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Usage: /deposit [account]";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.withdraw") && sender instanceof Player;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        String accountHolder = stack.getSender().getName();
        if(args.length >= 1 && stack.getSender().hasPermission("mlc.economy.admin")) { // Si le joueur est un admin, il peut withdraw d'autres comptes
            accountHolder = args[0];
        } else if(!stack.getSender().hasPermission("mlc.economy.admin")) {
            stack.getSender().sendRichMessage("<red>You cannot withdraw from an account without permission</red>");
            return;
        }

        if(!EconomyAddon.accountExists(accountHolder)) {
            stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: Account '%s' does not exist</red>", accountHolder));
            return;
        }

        PlayerInventory inv = ((Player) stack.getSender()).getInventory();
        int slot = inv.getHeldItemSlot(); // On récupère l'objet en main
        ItemStack item = inv.getItem(slot);
        if(item == null) {
            stack.getSender().sendRichMessage(String.format("<red>You are not holding a currency note!</red>"));
            return;
        }

        ItemMeta mm = item.getItemMeta();
        PersistentDataContainer itemData = mm.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey("mlc-pl", "amount");

        // On vérifie si l'objet tenu a les tags nécessaires
        if(!itemData.has(namespacedKey)) {
            stack.getSender().sendRichMessage(String.format("<red>The item you are holding is not a valid currency note!</red>"));
            return;
        }

        double amount; // On récupère le montant du billet
        try {
            amount = itemData.get(namespacedKey, PersistentDataType.DOUBLE);
        } catch(NullPointerException e) {
            Logger.err(String.format("NullPointerException:\n%s", e));
            return;
        }

        // On modifie la balance du joueur
        double balance = EconomyManager.getBalance(null, accountHolder);
        EconomyManager.setBalance(null, accountHolder, balance + amount);

        inv.setItem(slot, new ItemStack(Material.AIR)); // On retire le billet qu'il tient en main
        stack.getSender().sendRichMessage(String.format("<rainbow>[MLC-PL]</rainbow> You have deposited <yellow>%.2f$</yellow> into <yellow>%s</yellow>'s account", amount, accountHolder));
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