package io.github.saphirdefeu.minigamelolcow.economy.cmd;

import io.github.saphirdefeu.minigamelolcow.economy.Database;
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

public class Pay implements BasicCommand {
    public static final String name = "pay";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Usage: /pay <account> <amount>";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.pay") && sender instanceof Player;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if(args.length < 2) {
            stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: expected 2 arguments, got %d</red>", args.length));
            return;
        }

        double amount = 0.0;

        try {
            amount = Double.parseDouble(args[1]);
        } catch(NumberFormatException e) {
            stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: '%s' is not a valid number</red>", args[1]));
            return;
        }

        if(amount < 0.0) {
            stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: 'amount' cannot be negative (found %.2f)</red>", amount));
            return;
        }

        AdminPay.pay(stack, stack.getSender().getName(), args[0], amount);

        String str = String.format("<rainbow>[MLC-PL]</rainbow> You sent <yellow>%.2f</yellow> to <yellow>%s</yellow>", amount, args[0]);
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
