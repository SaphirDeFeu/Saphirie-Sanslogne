package io.github.saphirdefeu.minigamelolcow.discordimpl.eventlisteners;

import io.github.saphirdefeu.minigamelolcow.Main;
import io.github.saphirdefeu.minigamelolcow.discordimpl.Landmark;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SlashCommandReceived extends ListenerAdapter {

    private static JavaPlugin plugin;
    public SlashCommandReceived(JavaPlugin _plugin) {
        plugin = _plugin;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch(event.getName()) {
            case "playerpos": {
                playerPosCommand(event);
                break;
            }
            case "time": {
                timeCommand(event);
                break;
            }
        }
    }

    public static void playerPosCommand(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        OptionMapping playerNameOptionMapping = event.getOption("player");
        if(playerNameOptionMapping == null) return;

        String playerName = playerNameOptionMapping.getAsString();

        Landmark[] landmarks = {
                new Landmark("ZISO, Ckulsol, Saphirie", -957, -277),
                new Landmark("Centre de la Grande Forêt Occidentale", -1237, 152),
                new Landmark("Quartier Desperer, Ckulsol, Saphirie", -421, -472),
                new Landmark("Banque nationale, Ckulsol, Sanslogne", -77, -554),
                new Landmark("Quartier résidentiel, Ckulsol, Sanslogne", 249, -379),
                new Landmark("Assemblée Nationale de l'Union, Ckulsol, Saphirie", -196, -260),
                new Landmark("Assemblée Régionale saphiriq, Ckulsol, Saphirie", -177, -124),
                new Landmark("Centre de Colonz, Ckulsol, Sanslogne", -170, 135),
                new Landmark("El Pozo, Ckulsol, Sanslogne", 214, -72),
                new Landmark("Feniks, Ckulsol, Saphirie", -246, -410),
                new Landmark("Assemblée Régionale sanslognienne, Ckulsol, Sanslogne", -76, -697),
                new Landmark("Centre-ville, Athena, Sanslogne", 919, -202),
                new Landmark("Palais d'Athena, Sanslogne", 621, 68),
                new Landmark("Océan sur les côtés d'Athena, Sanslogne", 982, 54),
                new Landmark("HouseWants, Ckulsol, Sanslogne", 105, 182)
        };

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(playerName);
            if(player == null) {
                event.getHook().sendMessage(String.format("**%s** n'est pas connecté ou n'existe pas.", playerName)).queue();
                return;
            }

            Location loc = player.getLocation();

            int x = loc.getBlockX();
            int z = loc.getBlockZ();
            double[] distances = new double[landmarks.length];
            for(int i = 0; i < landmarks.length; i++) {
                Landmark landmark = landmarks[i];
                int differenceX = Math.abs(landmark.x - x);
                int differenceZ = Math.abs(landmark.z - z);
                double distance = Math.sqrt(differenceX * differenceX + differenceZ * differenceZ);
                distances[i] = distance;
            }

            Integer[] indices = new Integer[distances.length];
            for (int i = 0; i < distances.length; i++) {
                indices[i] = i;
            }

            Arrays.sort(indices, Comparator.comparingDouble(i -> distances[i]));

            StringBuilder discordMessage = new StringBuilder(String.format("**%s** se situe à X:%d Y:%d Z:%d",
                    playerName,
                    x,
                    loc.getBlockY(),
                    z
            ));

            for(int i = 0; i < 3; i++) {
                int index = indices[i];

                String toAdd = String.format("Le joueur se situe à ~**%d** blocs de **%s**",
                        (int) distances[index],
                        landmarks[index].name
                );

                discordMessage.append("\n").append(toAdd);
            }

            String minecraftMessage = String.format("<blue>[Discord] <bold>%s</bold> a récupéré votre position.</blue>",
                    event.getUser().getName()
            );

            player.sendRichMessage(minecraftMessage);
            event.getHook().sendMessage(discordMessage.toString()).queue();
        });


    }

    public static void timeCommand(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Bukkit.getScheduler().runTask(plugin, () -> {
            String[] date = Main.getCurrentDate();
            String time = Main.getCurrentTime();

            String msg = String.format("Il est **%s** le **%s %s %s**", time, date[0], date[1], date[2]);

            event.getHook().sendMessage(msg).queue();
        });
    }


}
