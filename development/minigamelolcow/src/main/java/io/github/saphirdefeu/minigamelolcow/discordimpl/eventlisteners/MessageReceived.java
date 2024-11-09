package io.github.saphirdefeu.minigamelolcow.discordimpl.eventlisteners;

import io.github.saphirdefeu.minigamelolcow.discordimpl.DiscordImplementation;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Random;

public class MessageReceived extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        // Checks if the bot was mentioned
        if(!msg.getMentions().isMentioned(DiscordImplementation.getJda().getSelfUser(), Message.MentionType.USER)) return;

        String[] messages = {
                "Legalement, je ne sais pas si on peut mettre des accents dans ces messages: éàù?! aussi c'est un test je vérifies si cela fonctionne."
        };
        Random rng = new Random();
        // int num = rng.nextInt(messages.length);
        int num = 0;
        String select = messages[num];
        msg.reply(select).queue();
    }
}
