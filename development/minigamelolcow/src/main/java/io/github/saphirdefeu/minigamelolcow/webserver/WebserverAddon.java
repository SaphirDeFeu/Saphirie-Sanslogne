package io.github.saphirdefeu.minigamelolcow.webserver;

import io.github.saphirdefeu.minigamelolcow.Logger;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class WebserverAddon {
    Thread thread;

    public WebserverAddon(String url) {
        this.thread = new Thread(() -> {
            try {
                URI _u = new URI(url + "/update");
                URI _e = new URI(url + "/exec");
                URL update = _u.toURL();
                URL exec = _e.toURL();

                HttpURLConnection con = (HttpURLConnection) update.openConnection();
                con.setRequestMethod("GET");
                Logger.debug("dnpa: " + con.getResponseCode());
                con.disconnect();

                // I'm too tired to make an actual fix on the webserver so making the plugin WAIT for the server to finish
                // the /update call is the best I can do.
                Thread.sleep(5000);

                con = (HttpURLConnection) exec.openConnection();
                con.setRequestMethod("GET");
                Logger.debug("dnpa: " + con.getResponseCode());
                con.disconnect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        this.thread.start();
    }

    public void destroy() {
    }

}
