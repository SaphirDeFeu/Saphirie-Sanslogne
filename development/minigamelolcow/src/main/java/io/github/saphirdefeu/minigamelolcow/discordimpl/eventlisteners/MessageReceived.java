package io.github.saphirdefeu.minigamelolcow.discordimpl.eventlisteners;

import io.github.saphirdefeu.minigamelolcow.discordimpl.DiscordImplementation;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Random;

public class MessageReceived extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        // Checks if the bot was mentioned
        if(!msg.getMentions().isMentioned(DiscordImplementation.getJda().getSelfUser(), Message.MentionType.USER)) return;

        HashMap<String, String[]> messages = new HashMap<>();
        messages.put("random", new String[]{
                "Aucun message disponible à part celui-ci. GG à <@548922318852849671> pour sa fainéantise",
                "Vérifies blogpopulaire.sa y a totalement un nouveau poste"
        });
        messages.put("finance", new String[]{
                "Je suis actuellement en train d'étudier comment je pourrais augmenter mon rendement",
                "Fuck you je te donne pas ma thune"
        });
        messages.put("lgbt", new String[]{
                "Si vous avez des questions à propos du fait d'être gay, demandez à <@548922318852849671>",
                "Il faut comprendre, je ne suis pas homosexuel. Je suis un pays. Je suis un bot. Je ne suis pas un humain."
        });
        messages.put("saphirie", new String[]{
                "Imagine habiter en Saphirie (common saphiriq L)",
                "Pov la saphirie quand ils ont plus de thune au gouvernement : :speaking_head:"
        });
        messages.put("sanslogne", new String[]{
                "Imagine habiter en Sanslogne (common sanslogne W)",
                "Pov la sanslogne quand ils ont du pétrole dans le sol (ils vont plus pouvoir respirer) : :money_mouth:"
        });

        String type = "random";
        String raw = msg.getContentRaw();
        if(containsWords(raw, "thune", "finance", "argent", "monnaie", "money", "$", "€"))
            type = "finance";
        else if(containsWords(raw, "lgbt", "gay", "lesbienne", "bisexuel", "trans", "sexualité", "aro", "ace", "asexuel"))
            type = "lgbt";
        else if(containsWords(raw, "saphirie", "saphiriq", "saforga", "safŏrga", "safiorga"))
            type = "saphirie";
        else if(containsWords(raw, "sanslogne", "sanslognien", "athena"))
            type = "sanslogne";


        Random rng = new Random();
        int max = messages.get(type).length;
        int num = rng.nextInt(max);
        String select = messages.get(type)[num];
        msg.reply(select).queue();
    }

    boolean containsWords(String raw, String... words) {
        for(String word : words) {
            if(raw.toLowerCase().contains(word.toLowerCase())) return true;
        }
        return false;
    }
}
