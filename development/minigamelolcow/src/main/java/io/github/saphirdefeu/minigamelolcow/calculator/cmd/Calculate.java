package io.github.saphirdefeu.minigamelolcow.calculator.cmd;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.Main;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class Calculate implements BasicCommand {

    public static final String name = "calculate";
    public static final Collection<String> aliases = List.of("calc");
    public static final String description = "Usage: /calculate <time|date>";

    @Override
    public boolean canUse(@NotNull CommandSender sender) { return sender.hasPermission("mlc.calculator.calculate"); }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        boolean doTime = false;
        if(args.length == 0) {
            stack.getSender().sendRichMessage("<red>Usage: /calculate <time|date></red>");
            return;
        }
        if(args[0].equals("time") && args[0].equals("date")) {
            stack.getSender().sendRichMessage("<red>Usage: /calculate <time|date></red>");
            return;
        }
        if(args[0].equals("time")) doTime = true;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        Objective obj = scoreboard.getObjective("Timesave");

        String str;

        if(doTime) {
            str = Main.getCurrentTime();
        } else {
            int days = obj.getScore("day").getScore() + 1;
            if(obj.getScore("daytime").getScore() > 18000) {
                days++;
            }
            long year = 1;
            byte month = 1;
            byte date = 1;

            byte dayNameIndex = 0;

            byte[] daysInMonths = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
            String[] dayNames = { "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche" };
            String[] months = {
                    "Janvier",
                    "Fevrier",
                    "Mars",
                    "Avril",
                    "Mai",
                    "Juin",
                    "Juillet",
                    "Aout",
                    "Septembre",
                    "Octobre",
                    "Novembre",
                    "Décembre",
            };

            while(days > 0) {
                int remainingDaysInMonth = daysInMonths[month - 1] - date + 1;

                if(days > remainingDaysInMonth) {
                    days -= remainingDaysInMonth;
                    dayNameIndex += remainingDaysInMonth;
                    dayNameIndex %= 7;
                    date = 1;
                    month++;

                    if(month > 12) {
                        month = 1;
                        year++;
                    }
                } else {
                    date += days - 1;
                    dayNameIndex += days - 1;
                    dayNameIndex %= 7;
                    days = 0;
                }
            }

            str = String.format("%s %d %s, an %d", dayNames[dayNameIndex], date, months[month - 1], year);
        }

        Logger.debug(String.format("Calculated %s to %s", doTime ? "time" : "date", str));
        stack.getSender().sendRichMessage(String.format("<rainbow>[MLC-PL]</rainbow> <yellow>%s</yellow>", str));
    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        return List.of("time", "date");
    }

}
