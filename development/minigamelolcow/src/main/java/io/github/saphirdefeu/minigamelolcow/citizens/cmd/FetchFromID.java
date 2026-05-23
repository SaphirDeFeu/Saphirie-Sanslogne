package io.github.saphirdefeu.minigamelolcow.citizens.cmd;

import io.github.saphirdefeu.minigamelolcow.citizens.Database;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class FetchFromID implements BasicCommand {

    public static final String name = "fetchfromid";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Retrieves a given citizen from their id";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if(args.length < 1) {
            stack.getSender().sendRichMessage("<red>Il me faut au moins un argument.</red>");
            return;
        }

        String id = args[0];

        if(!Database.isValidID(id)) {
            stack.getSender().sendRichMessage("<red>Cet identifiant n'est pas valide! Êtes-vous sûr de l'avoir correctement copié?</red>");
            return;
        }

        Database.CitizenSearchQuery result = Database.getDataFromId(id);

        if(result.id.equals("*")) {
            stack.getSender().sendRichMessage("<red>Cet identifiant est introuvable dans la base de données!</red>");
            return;
        }

        stack.getSender().sendRichMessage(String.format("<rainbow>[MLC-PL]</rainbow> Citoyen <yellow>%s</yellow>\n" +
                        "Nom: <yellow>%s %s</yellow>\n" +
                        "Né: le <yellow>%s</yellow> à <yellow>%s</yellow>\n" +
                        "Genre: <yellow>%s</yellow>\n" +
                        "Taille: <yellow>%s</yellow>\n" +
                        "Enregistré en <yellow>%s</yellow>\n" +
                        "Emploi: <yellow>%s</yellow>\n" +
                        "Vit à <yellow>%s</yellow>\n" +
                        "Notes: <yellow>%s</yellow>",
                id,
                result.firstname, result.lastname,
                result.birthdate, result.birthplace,
                result.gender,
                result.height,
                result.state,
                result.job,
                result.livingplace,
                result.notes
        ));
    }

}