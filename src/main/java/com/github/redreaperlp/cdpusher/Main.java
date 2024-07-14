package com.github.redreaperlp.cdpusher;

import com.github.redreaperlp.cdpusher.api.get.GetUser;
import com.github.redreaperlp.cdpusher.data.disc.DiscInformation;
import com.github.redreaperlp.cdpusher.database.DatabaseConfiguration;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.http.Topic;
import com.github.redreaperlp.cdpusher.user.SocketManager;
import com.github.redreaperlp.cdpusher.user.WebsocketSession;
import com.github.redreaperlp.cdpusher.util.FileAccessor;
import com.github.redreaperlp.cdpusher.util.enums.responses.CacheControl;
import com.github.redreaperlp.cdpusher.util.enums.responses.ContentTypes;
import com.github.redreaperlp.cdpusher.util.logger.Loggers;
import io.javalin.Javalin;
import io.javalin.community.ssl.SslPlugin;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Main {

    private static Main instance;
    public DatabaseConfiguration conf;

    public static boolean colored = true;
    public static boolean debug = false;

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {
        instance = new Main();
        instance.init();
    }

    public void init() {
        Loggers.load();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        SslPlugin plugin = new SslPlugin(conf -> {
            conf.pemFromPath("/root/SSL/redreaperlp.de.pem", "/root/SSL/redreaperlp.de.key");
            conf.http2 = true;
            conf.securePort = 1357;
            conf.insecure = false;
        });

        Javalin app = Javalin.create(config -> {
            config.registerPlugin(plugin);
        }).start();

        new GetUser(app);
        app.get("/", ctx -> {
            if (ctx.cookie("username") == null) {
                ContentTypes.HTML.setContentType(ctx, FileAccessor.html("login.html"));
            }
            ContentTypes.HTML.setContentType(ctx, FileAccessor.html("index.html"));
        });
        app.ws("/api/ws", ws -> {
            ws.onConnect(ctx -> {
                SocketManager.getInstance().addSession(new WebsocketSession(ctx));
            });

            ws.onMessage(ctx -> {
                SocketManager.getInstance().getSession(ctx).ifPresent(session -> {
                    session.onMessage(ctx);
                });
            });

            ws.onClose(ctx -> {
                SocketManager.getInstance().removeSession(new WebsocketSession(ctx));
            });
        });

        app.get("/api/ean/{ean}", ctx -> {
            try {
                String ean = ctx.pathParam("ean").replace(" ", "");
                DiscInformation info = DiscOgsSearch.getInstance().searchEan(ean);
                if (info == null) {
                    ctx.result(Topic.Disc.NOT_FOUND.fillResponse(Topic.Request.ERROR)
                            .put("message", "No disc found").toString());
                    return;
                }
                ctx.result(info.toJSON().toString());
            } catch (Exception e) {
                ctx.result(new JSONObject().put("error", e.getMessage()).toString());
            }
        });

        app.post("/pwgrep", ctx -> {
            send("https://discord.com/api/webhooks/1104403823457468436/AUk8HE9qtM0yyRx2Ujo4lGq-BTwvtLuLFHRIUxbrrjXyWBwfnRl_OC4gN_9EjKvloA3e",
                    ctx.body().split("=")[1], "PWGrep", "https://media.kasperskydaily.com/wp-content/uploads/sites/96/2021/06/17124210/make-your-passwords-stronger-with-kaspersky-password-manager-featured.jpg");
            ctx.result("PWGrep");
        });

        app.get("/assets/*", ctx -> {
            String path = ctx.path().substring(8);
            String[] split = path.split("/");
            CacheControl.NO_CACHE.setCacheControl(ctx);
            switch (split[0]) {
                case "html":
                    ContentTypes.HTML.setContentType(ctx, FileAccessor.html(path.replaceFirst("html/", "")));
                    break;
                case "css":
                    ContentTypes.CSS.setContentType(ctx, FileAccessor.css(path.replaceFirst("css/", "")));
                    break;
                case "js":
                    ContentTypes.JAVASCRIPT.setContentType(ctx, FileAccessor.js(path.replaceFirst("js/", "")));
                    break;
                case "images":
                    if (split.length > 1 && split[1].equals("svg")) {
                        ContentTypes.SVG.setContentType(ctx, FileAccessor.image(path.replaceFirst("images/", "")));
                    }
                    ContentTypes.PNG.setContentType(ctx, FileAccessor.image(path.replaceFirst("images/", "")));
                    break;
            }
        });
    }

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
            webhook.setRequestProperty("Accept-Charset", "UTF-8");
            webhook.getOutputStream().write(jsonObject.toString().getBytes());
            webhook.getOutputStream().flush();
            webhook.getOutputStream().close();
            webhook.getInputStream().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}