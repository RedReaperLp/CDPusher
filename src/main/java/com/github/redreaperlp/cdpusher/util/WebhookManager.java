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
            throw new RuntimeException(e);
        }
    }

    private static final int REMINDER_HOUR = 11;
    private static final int REMINDER_MINUTE = 15;
    private static final int REMINDER_SECOND = 0;

    public static void meetingReminder() {
//        Thread meetingThread = new Thread(() -> {
//            Date date = new Date(Instant.now().toEpochMilli());
//            LocalDateTime time = LocalDateTime.of(Instant.now().toEpochMilli());
//        });
//                send("https://discord.com/api/webhooks/1174283638372831302/n_Wgjs0z-_6-Yte8w2Lt_hvqLlRg4mu79Hg87uX3b9D745kBT0CDGSfGvQCiA6HU359D", "Reminder für <@&1153029185137279063>: Das Meeting findet nun Statt!!!", "Meeting Reminder", "https://www.stempel-malter.de/wp-content/uploads/2018/02/mini_motivstempel_symbol_wecker.png");
//
//        meetingThread.setName("Meeting Reminder Thread");
//        meetingThread.start();
    }
}
