package com.github.redreaperlp.cdpusher.util;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class WebhookManager {
    public static void send(String url, String content, String username, String avatarURL) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", content);
        jsonObject.put("username", username);
        jsonObject.put("avatar_url", avatarURL);

        try {
            URL webhookURL = new URL(url);
            URLConnection webhook = webhookURL.openConnection();
            webhook.setDoOutput(true);
            webhook.setRequestProperty("Content-Type", "application/json");
            webhook.setRequestProperty("Accept", "application/json");
            webhook.getOutputStream().write(jsonObject.toString().getBytes());
            webhook.getOutputStream().flush();
            webhook.getOutputStream().close();
            webhook.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void error(String string) {
        send("https://discord.com/api/webhooks/1249978066613047306/cDu-6mGhyL2PTCqrkhzENzAkzQ5RLAoQFqHF-QwOMZqb281BrgTt7fDULoopeY1EQixf",
                "```" + string + "```", "CDPusher", "https://cdn.discordapp.com/attachments/1084909692037373992/1249979314422677514/UXuAMVaScQAAAABJRU5ErkJggg.png?ex=66694574&is=6667f3f4&hm=63c85e97e33792179e169482059e697074a08502e8f18fec7fe375c3360d0c1d&");
    }
}
