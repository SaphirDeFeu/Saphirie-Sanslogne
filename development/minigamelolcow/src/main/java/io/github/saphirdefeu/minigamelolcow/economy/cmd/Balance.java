package io.github.saphirdefeu.minigamelolcow.economy.cmd;

import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.EconomyAddon;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Balance implements BasicCommand {

    public static final String name = "balance";
    public static final Collection<String> aliases = List.of("bal");
    public static final String description = "Usage: /balance [account]";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.balance");
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        String accountHolder = stack.getSender().getName();
        if(args.length >= 1) { // Check si il y a au moins 1 argument
            accountHolder = args[0]; // Si oui, le compte en question sera l'argument 1
        }

        if(!EconomyAddon.accountExists(accountHolder)) {
            stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: Account '%s' does not exist", accountHolder));
            return;
        }

        double balance = EconomyManager.getBalance(null, accountHolder);
        String str = String.format("<rainbow>[MLC-PL]</rainbow> <yellow>%s</yellow>'s balance: ", accountHolder);
        if(balance < 0.0) { // Mets en rouge les balances négatives
            str += String.format("<red>%.2f$</red>", balance);
        } else {
            str += String.format("<yellow>%.2f$</yellow>", balance);
        }
        stack.getSender().sendRichMessage(str);
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
