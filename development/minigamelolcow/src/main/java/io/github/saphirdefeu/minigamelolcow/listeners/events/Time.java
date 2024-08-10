package io.github.saphirdefeu.minigamelolcow.listeners.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Time implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        Objective obj = scoreboard.getObjective("Timesave");

        int days = obj.getScore("dayOfMonth").getScore();
        int month = obj.getScore("month").getScore() - 1;
        int year = obj.getScore("year").getScore();

        String months[] = {
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

        event.getPlayer().sendRichMessage(
                String.format(" >> <rainbow>[MinigameLolCow]</rainbow> <gold>Vous vous connectez le %d %s %d</gold>",
                        days,
                        months[month],
                        year
                )
        );
    }

}
