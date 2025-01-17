package io.github.saphirdefeu.minigamelolcow.phones.cmd;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.phones.Data;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Download implements BasicCommand {
    public static final String name = "download";
    public static final Collection<String> aliases = List.of();
    public static final String description = "Downloads a ZIP file from a URL and decompresses it into your Pineapple:tm: phone";

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        // 3 args - [owner] [path] [url]
        if(args.length < 3) {
            stack.getSender().sendRichMessage("<red>Missing arguments, expected 3 arguments</red>");
            return;
        }

        String owner = args[0];
        String __path = args[1];
        String url = args[2];

        Path path = Data.resolvePath(owner, __path);

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            stack.getSender().sendRichMessage("<red>Error creating directories</red>");
            return;
        }

        new Thread(() -> {

        // Cherchez même pas à comprendre
        try(InputStream inputStream = new URL(url).openStream();
            ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(inputStream))) {

            ZipEntry entry;
            while((entry = zipIn.getNextEntry()) != null) {
                String fileName = Paths.get(entry.getName()).getFileName().toString();
                Path filePath = Paths.get(path.toString(), fileName);
                Logger.debug(String.format("Downloading %s from %s", filePath, url));

                if(!entry.isDirectory()) {

                    try(OutputStream outputStream = Files.newOutputStream(filePath)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zipIn.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        } catch (Exception e) {
            stack.getSender().sendRichMessage("<red>An error occurred treating ZIP resource</red>");
            return;
        }

        stack.getSender().sendRichMessage(String.format(
                "<green>Successfully downloaded files to %s from %s</green>",
                __path,
                url
        ));

        }).start();
    }
}
