package io.github.saphirdefeu.minigamelolcow.economy.cmd;

import io.github.saphirdefeu.minigamelolcow.economy.Database;
import io.github.saphirdefeu.minigamelolcow.economy.EconomyAddon;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class EconomyManager implements BasicCommand {

    public static final String name = "economymanager";
    public static final Collection<String> aliases = List.of("economy", "econ");
    public static final String description = "Main economy management command";

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("mlc.economy.admin");
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if(args.length == 0) {
            displayHelp(stack);
            return;
        }

        Collection<String> cmds = List.of("adminpay", "pay", "balance", "deposit", "withdraw");

        switch(args[0]) {
            case "get": {
                if(args.length < 2) {
                    stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: found %d arguments when 1 is needed</red>", args.length - 1));
                    break;
                }
                try {
                    double balance = getBalance(stack, args[1]);
                    String msg = String.format("<rainbow>[MLC-PL]</rainbow> %s's balance: <gold>%.2f</gold>$", args[1], balance);
                    stack.getSender().sendRichMessage(msg);
                } catch(IllegalArgumentException e) {
                    stack.getSender().sendRichMessage(String.format("<red>Account '%s' does not exist</red>", args[1]));
                    break;
                }

                break;
            }
            case "set": {
                if(args.length < 3) {
                    stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: found %d arguments when 2 are needed</red>", args.length - 1));
                    break;
                }

                double amount = 0.0;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch(NumberFormatException e) {
                    stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: '%s' is not a numeric type</red>", args[2]));
                }

                setBalance(stack, args[1], amount);
                break;
            }
            case "add":
            case "remove": {
                if(args.length < 3) {
                    stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: found %d arguments when 2 are needed</red>", args.length - 1));
                    break;
                }

                double amount = 0.0;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch(NumberFormatException e) {
                    stack.getSender().sendRichMessage(String.format("<red>Illegal Argument: '%s' is not a numeric type</red>", args[2]));
                }

                try {
                    double balance = getBalance(stack, args[1]);
                    amount *= args[0].equals("remove") ? -1 : 1;
                    setBalance(stack, args[1], balance + amount);
                } catch(IllegalArgumentException e) {
                    stack.getSender().sendRichMessage(String.format("<red>Account '%s' does not exist</red>", args[1]));
                    break;
                }

                break;
            }
            case "create": {
                if(args.length < 2) {
                    stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: found %d arguments when 1 are needed</red>", args.length - 1));
                    break;
                }

                int res = Database.createAccountHolder(args[1]);
                if(res == 0) {
                    stack.getSender().sendRichMessage(String.format("<rainbow>[MLC-PL]</rainbow> Created account '%s'", args[1]));
                }
                break;
            }
            case "delete": {
                if(args.length < 2) {
                    stack.getSender().sendRichMessage(String.format("<red>Missing Arguments: found %d arguments when 1 are needed</red>", args.length - 1));
                    break;
                }
                int res = Database.deleteAccount(args[1]);
                if(res == 0) {
                    stack.getSender().sendRichMessage(String.format("<rainbow>[MLC-PL]</rainbow> Successfully deleted account '%s'", args[1]));
                }
                break;
            }
            case "list": {
                HashMap<String, Double> accounts = Database.getAccounts();
                LinkedHashMap<String, Double> sortedAccounts = accounts.entrySet()
                        .stream()
                        .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue,
                                LinkedHashMap::new
                        ));

                String str = "<rainbow>[MLC-PL]</rainbow> All account holders:";
                for(String account : sortedAccounts.keySet()) {
                    if(accounts.get(account) >= 0.0) {
                        str += String.format("<br>| <gold>%s</gold> : <yellow>%.2f$</yellow>", account, accounts.get(account));
                    } else {
                        str += String.format("<br>| <gold>%s</gold> : <red>%.2f$</red>", account, accounts.get(account));
                    }
                }
                stack.getSender().sendRichMessage(str);
                break;
            }
            default: {
                if(args.length < 2) {
                    displayHelp(stack);
                } else {
                    if(cmds.contains(args[1])) displayCommandHelp(stack, args[1]);
                    else displayHelp(stack);
                }
            }
        }
    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        Collection<String> firstParams = List.of("help", "add", "remove", "set", "get", "create", "delete", "list");
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

    /**
     * Spéficies la balance d'un compte à une nouvelle
     * @param stack Le stack utilisé pour envoyer un message de finalisation. Utilisation de 'null' déconseillée
     * @param username Le nom du compte
     * @param balance La nouvelle balance
     */
    public static void setBalance(CommandSourceStack stack, @NotNull String username, @NotNull double balance) {
        if(!EconomyAddon.accountExists(username)) {
            stack.getSender().sendRichMessage(String.format("<red>Account '%s' does not exist</red>", username));
            return;
        }

        Database.setAccountBalance(username, balance);
        String msg = String.format("<rainbow>[MLC-PL]</rainbow> Set %s's balance to <gold>%.2f</gold>$", username, balance);
        try {
            stack.getSender().sendRichMessage(msg);
        } catch(NullPointerException e) {}
    }

    /**
     * Récupères la balance d'un compte
     * @param stack Le stack utilisé pour des messages d'erreur. Utilisation de 'null' déconseillée.
     * @param username Le nom du compte
     * @return La balance du compte
     */
    public static double getBalance(CommandSourceStack stack, @NotNull String username) {
        if(!EconomyAddon.accountExists(username)) {
            stack.getSender().sendRichMessage(String.format("<red>Account '%s' does not exist</red>", username));
            return 0.0;
        }

        return Database.getAccountBalance(username);
    }

    /**
     * Renvois un message d'aide pour une commande au stack
     * @param stack Le stack
     * @param cmd Une commande de l'add-on
     */
    public void displayCommandHelp(@NotNull CommandSourceStack stack, @NotNull String cmd) {
        String helpMessage = String.format("<rainbow>MINIGAMELOLCOW ECONOMY MANAGER</rainbow> - by <gold>SaphirDeFeu</gold><br>");
        switch(cmd) {
            case "adminpay": {
                helpMessage += "<gold>Usage:</gold> <yellow>/adminpay <account1> <account2> <amount></yellow><br>" +
                        "<gold>Description:</gold> Transfers <amount> of money from <account1> to <account2><br>" +
                        "<gold>Restrictions:</gold> <amount> can be any real number<br>" +
                        "<gold>Permissions:</gold> mlc.economy.admin<br>" +
                        "<gold>Aliases:</gold> <yellow>/apay</yellow>";
                break;
            }
            case "balance": {
                helpMessage += "<gold>Usage:</gold> <yellow>/balance [account]</yellow><br>" +
                        "<gold>Description:</gold> Displays the balance of [account] or your personal account if unspecified<br>" +
                        "<gold>Permissions:</gold> mlc.economy.balance<br>" +
                        "<gold>Aliases:</gold> <yellow>/bal</yellow>";
                break;
            }
            case "deposit": {
                helpMessage += "<gold>Usage:</gold> <yellow>/deposit [account]</yellow><br>" +
                        "<gold>Description:</gold> Deposits the held currency note into [account] or your personal account if unspecified<br>" +
                        "<gold>Permissions:</gold> mlc.economy.withdraw";
                break;
            }
            case "withdraw": {
                helpMessage += "<gold>Usage:</gold> <yellow>/withdraw [account]</yellow><br>" +
                        "<gold>Description:</gold> Withdraws a currency note from [account] or your personal account if unspecified<br>" +
                        "<gold>Permissions:</gold> mlc.economy.withdraw";
                break;
            }
            case "pay": {
                helpMessage += "<gold>Usage:</gold> <yellow>/pay <account> <amount></yellow><br>" +
                        "<gold>Description:</gold> Transfers <amount> of money from your account to <account><br>" +
                        "<gold>Restrictions:</gold> <amount> can be any number strictly superior to 0.00<br>" +
                        "<gold>Permissions:</gold> mlc.economy.pay";
                break;
            }
        }
        stack.getSender().sendRichMessage(helpMessage);
    }

    /**
     * Envois un message d'aide au stack
     * @param stack Le stack
     */
    public void displayHelp(@NotNull CommandSourceStack stack) {
        String helpMessage = "<rainbow>MINIGAMELOLCOW ECONOMY MANAGER</rainbow> - by <gold>SaphirDeFeu</gold><br>" +
        "<gold>Aliases:</gold> /economy, /econ<br>" +
        " - <gold>/economymanager help [command]</gold> : <yellow>Displays the command's help message, or this help message</yellow><br>" +
        " - <gold>/economymanager add <account> <amount></gold> : <yellow>Adds <amount> to <account>\'s balance</yellow><br>" +
        " - <gold>/economymanager remove <account> <amount></gold> : <yellow>Removes <amount> from <account>\'s balance</yellow><br>" +
        " - <gold>/economymanager set <account> <amount></gold> : <yellow>Sets <account>\'s balance to <amount></yellow><br>" +
        " - <gold>/economymanager get <account></gold> : <yellow>Retrieves <account>\'s balance</yellow><br>" +
        " - <gold>/economymanager create <account></gold> : <yellow>Creates account holder of name <account></yellow><br>" +
        " - <gold>/economymanager delete <account></gold> : <yellow>Deletes account holder of name <account></yellow><br>" +
        " - <gold>/economymanager list</gold> : <yellow>Lists all accounts and their balance</yellow>";
        stack.getSender().sendRichMessage(helpMessage);
    }
}
