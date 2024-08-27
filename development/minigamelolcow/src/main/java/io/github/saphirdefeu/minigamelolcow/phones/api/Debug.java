package io.github.saphirdefeu.minigamelolcow.phones.api;

import io.github.saphirdefeu.minigamelolcow.Logger;

import java.io.IOException;
import java.io.OutputStream;

public class Debug extends OutputStream {
    private StringBuilder buffer = new StringBuilder();

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        if(c == '\n') {
            Logger.debug(buffer.toString());
            buffer.setLength(0);
        } else {
            buffer.append(c);
        }
    }

}
