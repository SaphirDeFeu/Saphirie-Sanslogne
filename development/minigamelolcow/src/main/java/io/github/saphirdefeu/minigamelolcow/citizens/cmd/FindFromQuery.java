package io.github.saphirdefeu.minigamelolcow.citizens.cmd;

import io.github.saphirdefeu.minigamelolcow.citizens.Database;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FindFromQuery implements BasicCommand {

    public static final String name = "findfromquery";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Retrieves all citizen ids that match a given query";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        Database.CitizenSearchQuery data = new Database.CitizenSearchQuery();

        if(args[0].equals("help")) {
            stack.getSender().sendRichMessage("<rainbow>[MLC-PL]</rainbow> Utilisation de <yellow>/findfromquery</yellow>:\n" +
                    "/findfromquery <red>[*firstname] [*lastname] [*birthplace] [*birthdate] [*state] [*gender] [*height] [*job] [*livingplace] [*notes] [*cidmid] [*skinmid]</red>\n" +
                    "- <red>*</red>: champ facultatif (remplacer par * si vous ne voulez pas le modifier)\n" +
                    "- <red>cidmid</red>: Lien discord du message contenant l'image de la carte d'identité du citoyen\n" +
                    "- <red>skinmid</red>: Lien discord du message contenant le skin du citoyen\n" +
                    "Utilisez des tirets du 8 _ comme espaces dans un champ donné.");
            return;
        }

        if(args.length < 12) {
            stack.getSender().sendRichMessage("<red>Il me faut au moins 12 arguments. Utilisez <bold>/findfromquery help</bold> pour plus d'aide.");
            return;
        }

        if(!args[0].equals("*")) data.firstname = args[0].replace("_", " ");
        if(!args[1].equals("*")) data.lastname = args[1].replace("_", " ");
        if(!args[2].equals("*")) data.birthplace = args[2].replace("_", " ");
        if(!args[3].equals("*")) data.birthdate = args[3].replace("_", " ");
        if(!args[4].equals("*")) data.state = args[4].replace("_", " ");
        if(!args[5].equals("*")) data.gender = args[5].replace("_", " ");
        if(!args[6].equals("*")) data.height = args[6].replace("_", " ");
        if(!args[7].equals("*")) data.job = args[7].replace("_", " ");
        if(!args[8].equals("*")) data.livingplace = args[8].replace("_", " ");
        if(!args[9].equals("*")) data.notes = args[9].replace("_", " ");
        if(!args[10].equals("*")) data.cidmid = args[10].replace("_", " ");
        if(!args[11].equals("*")) data.skinmid = args[11].replace("_", " ");

        LinkedList<String> ids = Database.getIdsFromSearchQuery(data);

        stack.getSender().sendRichMessage("<rainbow>[MLC-PL]</rainbow> Résultats de la recherche:");
        for(String id : ids) {
            Database.CitizenSearchQuery result = Database.getDataFromId(id);
            stack.getSender().sendRichMessage(String.format("- [<yellow>%s</yellow>] <yellow>%s %s</yellow> (<yellow>%s</yellow>)", id, result.firstname, result.lastname, result.job));
        }
    }

}