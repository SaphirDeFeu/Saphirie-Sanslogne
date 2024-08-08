package io.github.saphirdefeu.minigamelolcow.economy.cmd;

import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.EconomyAddon;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AdminPay implements BasicCommand {
    public static final String name = "adminpay";
    public static final Collection<String> aliases = List.of("apay");
    public static final String description = "Usage: /adminpay <paying account> <receiving account> <amount>";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.admin");
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if(args.length < 3) { // Check si la commande a reçu au moins 3 arguments
            stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: expected 3 arguments, got %d</red>", args.length));
            return;
        }

        double amount;

        try { // Check si l'argument n°3 est un nombre valide
            amount = Double.parseDouble(args[2]);
        } catch(NumberFormatException e) {
            stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: '%s' is not a valid number</red>", args[2]));
            return;
        }

        // Payes le compte 1 au compte 2
        pay(stack, args[0], args[1], amount);

        // Message de confirmation
        String str = String.format("<rainbow>[MLC-PL]</rainbow> <yellow>%s</yellow> paid <yellow>%.2f$</yellow> to <yellow>%s</yellow>", args[0], amount, args[1]);
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendRichMessage(str);
        }
    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        HashMap<String, Double> accounts = Database.getAccounts(); // Récupères les comptes
        LinkedList<String> accountUsernames = new LinkedList<>();

        for(String accountUsername : accounts.keySet()) { // Stockes les noms de comptes
            accountUsernames.add(accountUsername);
        }

        return accountUsernames;
    }

    /**
     * Transfères `amount` du compte `acc1` à `acc2`
     * @param stack Le stack utilisé pour envoyer des messages d'erreur. Attention - mettez 'null' qu'à votre risque
     * @param acc1 Le nom du compte 1
     * @param acc2 Le nom du compte 2
     * @param amount La somme à transférer
     */
    public static void pay(CommandSourceStack stack, @NotNull String acc1, @NotNull String acc2, @NotNull double amount) {
        if(!EconomyAddon.accountExists(acc1)) {
            stack.getSender().sendRichMessage(String.format("<red>Account '%s' does not exist", acc1));
            return;
        }

        if(!EconomyAddon.accountExists(acc2)) {
            stack.getSender().sendRichMessage(String.format("<red>Account '%s' does not exist", acc2));
            return;
        }

        double balanceAccountOne = EconomyManager.getBalance(null, acc1) - amount;
        double balanceAccountTwo = EconomyManager.getBalance(null, acc2) + amount;

        EconomyManager.setBalance(null, acc1, balanceAccountOne);
        EconomyManager.setBalance(null, acc2, balanceAccountTwo);
    }
}
