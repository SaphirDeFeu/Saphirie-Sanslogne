package io.github.saphirdefeu.minigamelolcow.discordimpl.eventlisteners;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.discordimpl.DiscordImplementation;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MessageReceived extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        // Checks if the bot was mentioned
        if(!msg.getMentions().isMentioned(DiscordImplementation.getJda().getSelfUser(), Message.MentionType.USER)) return;

        String type = "random";
        String raw = msg.getContentRaw();

        Logger.debug(String.format("Received mention message: %s", raw));

        // START ALGORITHM SELECTION MODEL
        String[] types = {
                "finance",
                "lgbt",
                "saphirie",
                "sanslogne"
        };

        HashMap<String, String[]> messages = new HashMap<>();
        messages.put("random", new String[]{
                "Union fédérative de Saphirie-Sanslogne N°1 !!!!!!!! <:union:1268165799328223283> <:union:1268165799328223283>",
                "Vérifies blogpopulaire.sa y a totalement un nouveau poste",
                "# FROM THE SCREEN :tv: TO THE RING :boxing_glove: TO THE PEN :pen: TO THE KING :crown: WASSSSHHHHHHIIIIINNGGGGGGG :crossed_swords:",
                "Comme KSI* le dit:\n> BUT IM COOOLLLDLDDDDDDLDLDLDL :cold_face: :cold_face:"
        });
        messages.put(types[0], new String[]{
                "Je suis actuellement en train d'étudier comment je pourrais augmenter mon rendement",
                "Fuck you je te donne pas ma thune",
                "Calcul des comptes du pays en cours......................\n<:sanslogne:1268165829011439679> Sanslogne: +2%\n<:saphirie:1268165815782608946> Saphirie: -500000%"
        });
        messages.put(types[1], new String[]{
                "Si vous avez des questions à propos du fait d'être gay, demandez à <@548922318852849671>",
                "Il faut comprendre, je ne suis pas homosexuel. Je suis un pays. Je suis un bot. Je ne suis pas un humain.",
                "Safŏr Dẽfẽ maintenant que le SAM est au pouvoir : :rainbow_flag::x:"
        });
        messages.put(types[2], new String[]{
                "Imagine habiter en Saphirie (common saphiriq W)",
                "Pov la saphirie quand ils ont plus de thune au gouvernement : :speaking_head:",
                "Pov la saphirie quand ils se rendent compte qu'ils peuvent juste pomper de la thune dans l'industrie : :fire: :fire:",
                "Je propose qu'on refait la frontière : donnez B-131 aux Sanslogniens et donnez Colonz aux saphiriqs",
                "J'ai juré j'ai jamais vu un jour en saphirie sans qu'il y ait de la propagande électorale",
                "Les gars on a pas fait Lone pour qu'on se retrouve À NOUVEAU avec des tyrans au pouvoir!"
        });
        messages.put(types[3], new String[]{
                "Imagine habiter en Sanslogne (common sanslogne W)",
                "Pov la sanslogne après avoir construit une banque : :100: $$$",
                "Pov la sanslogne quand ils ont du pétrole dans le sol (ils vont plus pouvoir respirer) : :money_mouth:",
                "Je propose Libération du n°14",
                "Les théocrates ont déjà théoriquement gagnés... (opinion polémique ici)",
                "Venez on va à la plage d'Athena ce soir!... Attends... t'as pas oublié la protection anti-théocrates?"
        });

        int[] counts = {
                countOccurrences(raw, "thune", "finance", "argent", "monnaie", "money", "$", "€", "£", "₽"),
                countOccurrences(raw, "lgbt", "gay", "lesbienne", "bisexuel", "trans", "sexualité", "aro", "ace", "asexuel"),
                countOccurrences(raw, "saphirie", "saphiriq", "saforga", "safŏrga", "safiorga"),
                countOccurrences(raw, "sanslogne", "sanslognien", "athena")
        };
        // END ALGORITHM SELECTION MODEL

        double total = Arrays.stream(counts).sum();

        ArrayList<Double> probs = new ArrayList<>();

        for(int i = 0; i < counts.length; i++) {
            probs.add(counts[i] / total);
        }

        double[] probabilities = probs.stream().mapToDouble(d -> d).toArray();

        for(int i = 0; i < probabilities.length; i++) {
            double prob = probabilities[i];
            String _t = types[i];
            Logger.debug(String.format("[DISCORD] Probability of message type %s: %f", _t.toUpperCase(), prob));
        }

        OptionalDouble optionalMax = Arrays.stream(probabilities).max();
        double max = 0;
        if(optionalMax.isPresent()) max = optionalMax.getAsDouble();
        Logger.debug(String.format("[DISCORD] Max probability: %f", max));
        if(max != 0) {
            List<Double> list = Arrays.stream(probabilities).boxed().toList();
            int index = list.indexOf(max);
            if(index != -1) type = types[index];
            Logger.debug(String.format("[DISCORD] Index of max probability: %d - Selected type of response: %s", index, type.toUpperCase()));
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

        origin = origin.toLowerCase();

        int totalCount = 0;

        for (String word : words) {
            if (word == null || word.isEmpty()) {
                continue;
            }

            word = word.toLowerCase();

            int index = 0;
            while ((index = origin.indexOf(word, index)) != -1) {
                totalCount++;
                index += word.length();
            }
        }

        return totalCount;
    }
}
