package io.github.saphirdefeu.minigamelolcow.webserver;

import io.github.saphirdefeu.minigamelolcow.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerExecutor implements Runnable {

    @Override
    public void run() {
        try {
            String command = "./plugins/minigamelolcow/libs/nodejs/node.exe ./plugins/minigamelolcow/webserver/index.js 91.197.6.118 31252";

            ProcessBuilder builder = new ProcessBuilder(command.split(" "));
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                Logger.debug("[MLC-PL] [Webserver] " + line);
            }

            int exitCode = process.waitFor();
            Logger.debug("[MLC-PL] [Webserver] Process exited with code " + exitCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
