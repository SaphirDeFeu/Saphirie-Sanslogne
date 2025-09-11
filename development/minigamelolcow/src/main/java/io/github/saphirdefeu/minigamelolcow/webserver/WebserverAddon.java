package io.github.saphirdefeu.minigamelolcow.webserver;

import java.util.Objects;

public class WebserverAddon {

    String url = null;
    String api_url = null;
    String raw_content_url = null;

    Thread thread;

    public WebserverAddon(String url) {
        String[] items = url.split("/");
        if(!Objects.equals(items[2], "github.com")) return;

        this.url = url;
        // Set API url
        items[2] = "api.github.com";
        // Make place for the /repos/ between domain & user
        items[5] = items[4];
        items[4] = items[3];
        items[3] = "repos";
        items[6] = "contents";

        this.api_url = String.join("/", items);

        items = url.split("/");
        items[2] = "raw.githubusercontent.com";
        for(int i = 5; i < items.length - 1; i++) {
            items[i] = items[i + 1];
        }
        items[items.length - 1] = "";

        this.raw_content_url = String.join("/", items);
        RepoDownloader.downloadFiles(this.api_url, this.raw_content_url);

        this.thread = new Thread(new ServerExecutor());
        this.thread.start();
    }

    public void destroy() {
    }

}
