package io.github.saphirdefeu.minigamelolcow.citizens.cmd;

import io.github.saphirdefeu.minigamelolcow.citizens.Database;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class UpdateCitizen implements BasicCommand {

    public static final String name = "updatecitizen";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Updates the data of a citizen";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        Database.CitizenSearchQuery data = new Database.CitizenSearchQuery();

        if(args[0].equals("help")) {
            stack.getSender().sendRichMessage("<rainbow>[MLC-PL]</rainbow> Utilisation de <yellow>/updatecitizen</yellow>:\n" +
                    "/updatecitizen <red>[id] [*firstname] [*lastname] [*birthplace] [*birthdate] [*state] [*gender] [*height] [*job] [*livingplace] [*notes] [*cidmid] [*skinmid]</red>\n" +
                    "- <red>*</red>: champ facultatif (remplacer par * si vous ne voulez pas le modifier)\n" +
                    "- <red>cidmid</red>: Lien discord du message contenant l'image de la carte d'identité du citoyen\n" +
                    "- <red>skinmid</red>: Lien discord du message contenant le skin du citoyen\n" +
                    "Utilisez des tirets du 8 _ comme espaces dans un champ donné.");
            return;
        }

        if(args[0].equals("*")) {
            stack.getSender().sendRichMessage("<red>Le premier argument doit être une ID citoyenne! Utilisez <bold>/updatecitizen help</bold> pour plus de détails</red>");
            return;
        }

        if(args.length < 13) {
            stack.getSender().sendRichMessage("<red>Il me faut au moins 13 arguments. Utilisez <bold>/updatecitizen help</bold> pour plus d'aide.");
            return;
        }

        data.id = args[0];

        if(!Database.isValidID(data.id)) {
            stack.getSender().sendRichMessage("<red>Cet identifiant n'est pas valide! Êtes-vous sûr de l'avoir correctement copié?</red>");
            return;
        }

        if(!args[1].equals("*")) data.firstname = args[1].replace("_", " ");
        if(!args[2].equals("*")) data.lastname = args[2].replace("_", " ");
        if(!args[3].equals("*")) data.birthplace = args[3].replace("_", " ");
        if(!args[4].equals("*")) data.birthdate = args[4].replace("_", " ");
        if(!args[5].equals("*")) data.state = args[5].replace("_", " ");
        if(!args[6].equals("*")) data.gender = args[6].replace("_", " ");
        if(!args[7].equals("*")) data.height = args[7].replace("_", " ");
        if(!args[8].equals("*")) data.job = args[8].replace("_", " ");
        if(!args[9].equals("*")) data.livingplace = args[9].replace("_", " ");
        if(!args[10].equals("*")) data.notes = args[10].replace("_", " ");
        if(!args[11].equals("*")) data.cidmid = args[11].replace("_", " ");
        if(!args[12].equals("*")) data.skinmid = args[12].replace("_", " ");

        int v = Database.setDataFromId(data.id, data);

        if(v == 0) {
            stack.getSender().sendRichMessage("<rainbow>[MLC-PL]</rainbow> Base de données citoyenne mise à jour!");
        }  else {
            stack.getSender().sendRichMessage("<red>An error has occurred.</red>");
        }
    }

}
