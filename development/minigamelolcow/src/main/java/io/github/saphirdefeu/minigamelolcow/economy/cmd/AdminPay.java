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
        if(args.length < 3) {
            stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: expected 3 arguments, got %d</red>", args.length));
            return;
        }

        double amount = 0.0;

        try {
            amount = Double.parseDouble(args[2]);
        } catch(NumberFormatException e) {
            stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: '%s' is not a valid number</red>", args[2]));
            return;
        }

        pay(stack, args[0], args[1], amount);

        String str = String.format("<rainbow>[MLC-PL]</rainbow> <yellow>%s</yellow> paid <yellow>%.2f$</yellow> to <yellow>%s</yellow>", args[0], amount, args[1]);
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendRichMessage(str);
        }
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
