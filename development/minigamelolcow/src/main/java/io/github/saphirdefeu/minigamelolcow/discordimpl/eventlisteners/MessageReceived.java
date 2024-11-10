package io.github.saphirdefeu.minigamelolcow.discordimpl.eventlisteners;

import io.github.saphirdefeu.minigamelolcow.discordimpl.DiscordImplementation;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.*;

public class MessageReceived extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        // Checks if the bot was mentioned
        if(!msg.getMentions().isMentioned(DiscordImplementation.getJda().getSelfUser(), Message.MentionType.USER)) return;

        String type = "random";
        String raw = msg.getContentRaw();

        // START ALGORITHM SELECTION MODEL
        HashMap<String, String[]> messages = new HashMap<>();
        messages.put("random", new String[]{
                "Union fédérative de Saphirie-Sanslogne N°1 !!!!!!!! <:union:1268165799328223283> <:union:1268165799328223283>",
                "Vérifies blogpopulaire.sa y a totalement un nouveau poste"
        });
        messages.put("finance", new String[]{
                "Je suis actuellement en train d'étudier comment je pourrais augmenter mon rendement",
                "Fuck you je te donne pas ma thune",
                "Calcul des comptes du pays en cours......................\n<:sanslogne:1268165829011439679> Sanslogne: +2%\n<:saphirie:1268165815782608946> Saphirie: -500000%"
        });
        messages.put("lgbt", new String[]{
                "Si vous avez des questions à propos du fait d'être gay, demandez à <@548922318852849671>",
                "Il faut comprendre, je ne suis pas homosexuel. Je suis un pays. Je suis un bot. Je ne suis pas un humain.",
                "Safŏr Dẽfẽ maintenant que le SAM est au pouvoir : :rainbow_flag::x:"
        });
        messages.put("saphirie", new String[]{
                "Imagine habiter en Saphirie (common saphiriq L)",
                "Pov la saphirie quand ils ont plus de thune au gouvernement : :speaking_head:",
                "Je propose qu'on refait la frontière : donnez B-131 aux Sanslogniens et donnez Colonz aux saphiriqs"
        });
        messages.put("sanslogne", new String[]{
                "Imagine habiter en Sanslogne (common sanslogne W)",
                "Pov la sanslogne quand ils ont du pétrole dans le sol (ils vont plus pouvoir respirer) : :money_mouth:",
                "Je propose Libération au n°14"
        });

        int countFinance = countOccurrences(raw, "thune", "finance", "argent", "monnaie", "money", "$", "€");
        int countLgbt = countOccurrences(raw, "lgbt", "gay", "lesbienne", "bisexuel", "trans", "sexualité", "aro", "ace", "asexuel");
        int countSaphirie = countOccurrences(raw, "saphirie", "saphiriq", "saforga", "safŏrga", "safiorga");
        int countSanslogne = countOccurrences(raw, "sanslogne", "sanslognien", "athena");

        int total = countFinance +
                countLgbt +
                countSaphirie +
                countSanslogne;

        int pFinance = 0;
        int pLgbt = 0;
        int pSaphirie = 0;
        int pSanslogne = 0;

        if(total != 0) {
            pFinance = countFinance / total;
            pLgbt = countLgbt / total;
            pSaphirie = countSaphirie / total;
            pSanslogne = countSanslogne / total;
        }

        int[] probabilities = {
                pFinance,
                pLgbt,
                pSaphirie,
                pSanslogne
        };

        String[] types = {
                "finance",
                "lgbt",
                "saphirie",
                "sanslogne"
        };
        // END ALGORITHM SELECTION MODEL

        int max = Arrays.stream(probabilities).max().getAsInt();
        if(max != 0) {
            int index = Arrays.asList(probabilities).indexOf(max);
            type = types[index];
        }

        Random rng = new Random();
        int SIZE = messages.get(type).length;
        int num = rng.nextInt(SIZE);
        String select = messages.get(type)[num];
        msg.reply(select).queue();
    }

    private int countOccurrences(String origin, String... words) {
        if (origin == null || words == null || words.length == 0) {
            return 0;
        }

        int totalCount = 0;

        for (String word : words) {
            if (word == null || word.isEmpty()) {
                continue;
            }

            int index = 0;
            while ((index = origin.indexOf(word, index)) != -1) {
                totalCount++;
                index += word.length();
            }
        }

        return totalCount;
    }
}
