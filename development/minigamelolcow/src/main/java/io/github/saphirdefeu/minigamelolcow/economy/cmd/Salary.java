package io.github.saphirdefeu.minigamelolcow.economy.cmd;

import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.EconomyAddon;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Salary implements BasicCommand {

    public static final String name = "salary";
    public static final Collection<String> aliases = List.of("sal");
    public static final String description = "Sets and gets the salary of an account. Can be used to deposit the salary of every account at once";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.admin");
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        // Structure : /salary <get|set|all> [<args>]
        if(args.length == 0) {
            displayHelp(stack);
            return;
        }

        switch(args[0]) {
            case "get": {
                if(args.length < 2) {
                    stack.getSender().sendRichMessage(String.format("<red>Expected 2 arguments, got %d</red>", args.length));
                    return;
                }

                String account = args[1];
                if(!EconomyAddon.accountExists(account)) {
                    stack.getSender().sendRichMessage(String.format("<red>Account '%s' does not exist</red>", account));
                    return;
                }

                double salary = Database.getAccountSalary(account);
                stack.getSender().sendRichMessage(String.format(
                        "<yellow>%s</yellow> : <yellow>%.2f$</yellow>", account, salary
                ));
                break;
            }

            case "set": {
                if(args.length < 3) {
                    stack.getSender().sendRichMessage(String.format("<red>Expected 3 arguments, got %d</red>", args.length));
                    return;
                }

                String account = args[1];
                if(!EconomyAddon.accountExists(account)) {
                    stack.getSender().sendRichMessage(String.format("<red>Account '%s' does not exist</red>", account));
                    return;
                }

                double salary;
                try {
                    salary = Double.parseDouble(args[2]);
                } catch(NumberFormatException e) {
                    stack.getSender().sendRichMessage(String.format("<red>'%s' is not a valid number</red>", args[2]));
                    return;
                }

                Database.setAccountSalary(account, salary);
                stack.getSender().sendRichMessage(String.format(
                        "Set <yellow>%s</yellow>'s salary to <yellow>%.2f$</yellow>", account, salary
                ));
                break;
            }

            case "all": {
                Set<String> accounts = Database.getAccounts().keySet();
                for(String account : accounts) {
                    double salary = Database.getAccountSalary(account);
                    double balance = EconomyManager.getBalance(null, account);
                    EconomyManager.setBalance(null, account, balance + salary);
                }
                stack.getSender().sendRichMessage("<yellow>Deposited salaries into all accounts</yellow>");
                break;
            }
        }
    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        Collection<String> firstParams = List.of("get", "set", "all");
        switch(args.length) {
            case 0: {
                return firstParams;
            }
        }

        if(!firstParams.contains(args[0])) {
            return firstParams;
        }

        HashMap<String, Double> accounts = Database.getAccounts();
        LinkedList<String> accountUsernames = new LinkedList<>();

        for(String accountUsername : accounts.keySet()) {
            accountUsernames.add(accountUsername);
        }

        return accountUsernames;
    }

    public void displayHelp(@NotNull CommandSourceStack stack) {
        final String msg = "<rainbow>MINIGAMELOLCOW ECONOMY MANAGER</rainbow> - by <gold>SaphirDeFeu</gold><br>" +
                "<gold>Aliases:</gold> /sal<br>" +
                " - <gold>/salary set <account> <salary></gold> : <yellow>Sets the salary of an account</yellow><br>" +
                " - <gold>/salary get <account></gold> : <yellow>Gets the salary of an account</yellow><br>" +
                " - <gold>/salary all</gold> : <yellow>Deposits salaries into every account";
        stack.getSender().sendRichMessage(msg);
    }
}