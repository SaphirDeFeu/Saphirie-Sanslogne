package io.github.saphirdefeu.minigamelolcow.calculator.cmd;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Collection;
import java.util.List;

public class Weather implements BasicCommand {

    public static final String name = "weather";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Usage: /weather";


    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        Objective obj = scoreboard.getObjective("weather");

        int[] modes = {0, 0, 0, 0, 0, 0};
        int[] times = {0, 0, 0, 0, 0, 0};

        int[] finalTimes = {0, 0, 0, 0, 0, 0};
        int[] finalModes = {-1, -1, -1, -1, -1, -1};

        // Fetch all data
        modes[0] = obj.getScore("mode").getScore();
        times[0] = obj.getScore("time").getScore();

        for(int i = 1; i <= 5; i++) {
            modes[i] = obj.getScore(String.format("mode%d", i)).getScore();
            times[i] = obj.getScore(String.format("time%d", i)).getScore();
        }

        // Agglutinate similar modes
        int lastMode = -1;
        int lastIndex = 0;
        for(int i = 0; i < 6; i++) {
            if(modes[i] != lastMode) {
                lastIndex++;
                lastMode = modes[i];
                finalModes[lastIndex] = lastMode;
            }

            finalTimes[lastIndex] += times[i];
        }

        // Display
        String[] correspondance = {"Ensoleillé", "Pluvieux", "Orageux"};

        String[] str = {
                "<rainbow>[MLC-PL]</rainbow> <yellow>Prévisions météorologiques</yellow>",
                "",
                "",
                "",
                "",
                "",
                "",
        };

        for(int i = 0; i < 6; i++) {
            if(finalModes[i] == -1) {
                break;
            }

            String mode = correspondance[finalModes[i]];
            int ticks = finalTimes[i];

            float tick_hour = ((float)ticks) / 1000.0f;
            int hour = (6 + ((int)tick_hour)) % 24;

            int tick_minute = ticks % 1000;
            float minutes_with_sec = ((float) tick_minute) / (16.0f + 2.0f / 3.0f);

            float seconds = (minutes_with_sec % 1.0f) * 60.0f;

            str[i + 1] = String.format("\\> <yellow>%s</yellow> pendant <yellow>%d:%d:%.3f</yellow>", mode, hour, (int) minutes_with_sec, seconds);
        }

        for(int i = 0; i < 7; i++) {
            if(str[i].isEmpty()) break;

            stack.getSender().sendRichMessage(str[i]);
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return BasicCommand.super.suggest(commandSourceStack, args);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("mlc.calculator.weather");
    }
}
