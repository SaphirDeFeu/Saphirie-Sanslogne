package io.github.saphirdefeu.minigamelolcow.calculator.cmd;

import io.github.saphirdefeu.minigamelolcow.Logger;
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

    public static final String name = "weathercast";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Usage: /weathercast";


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

        for(int i = 0; i < 6; i++) {
            Logger.debug(String.format("%d: %d, %d", i, modes[i], times[i]));
        }

        // Agglutinate similar modes
        int lastMode = -1;
        int lastIndex = -1;
        for(int i = 0; i < 6; i++) {
            if(modes[i] != lastMode) {
                lastIndex++;
                lastMode = modes[i];
                finalModes[lastIndex] = lastMode;
            }

            finalTimes[lastIndex] += times[i];
        }

        for(int i = 0; i < 6; i++) {
            Logger.debug(String.format("%d: %d, %d", i, finalModes[i], finalTimes[i]));
        }

        // Display
        String[] correspondance = {"<yellow>Ensoleillé</yellow>", "<blue>Pluvieux</blue>", "<red>Orageux</red>"};

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

            int hour = ticks / 1000;
            int tick_minute = ticks % 1000;
            float minutes_with_sec = ((float) tick_minute) / 1000f * 60f;

            str[i + 1] = String.format("- %s pendant <yellow>%d heures, %d minutes</yellow>", mode, hour, (int) minutes_with_sec);
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
