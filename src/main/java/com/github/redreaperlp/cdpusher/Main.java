package com.github.redreaperlp.cdpusher;

import com.github.redreaperlp.cdpusher.api.get.GetUser;
import com.github.redreaperlp.cdpusher.database.DatabaseConfiguration;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.user.User;
import com.github.redreaperlp.cdpusher.util.FileAccessor;
import com.github.redreaperlp.cdpusher.util.enums.responses.CacheControl;
import com.github.redreaperlp.cdpusher.util.enums.responses.ContentTypes;
import com.github.redreaperlp.cdpusher.util.logger.Loggers;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Main instance;
    public DatabaseConfiguration conf;

    public boolean colored = true;
    public boolean debug = false;

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {
        instance = new Main();
        instance.init();
    }

    List<List<String>> data = new ArrayList<>();

    JProgressBar progressBar = new JProgressBar();
    JTable table = new JTable();
    JPanel panel = new JPanel();

    public void init() {
        Loggers.load();
        new DatabaseConfiguration("45.81.234.99", "root", "CD-PUSHER", "cd_pusher").initDatabase();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(CorsPluginConfig::anyHost);
            });
        }).start(80);
        new GetUser(app);
        app.get("/", ctx -> ContentTypes.HTML.setContentType(ctx, FileAccessor.html("index.html")));
        app.ws("/api/ws", new User().getHandler());

        app.get("/api/ean/{ean}", ctx -> {
            try {
                String ean = ctx.pathParam("ean").replace(" ", "");
                DiscInformation info = DiscOgsSearch.getInstance().searchEan(ean);
                if (info == null) {
                    ctx.result(new JSONObject().put("error", "No CD found").toString());
                    return;
                }
                ctx.result(info.toJSON().toString());
            } catch (Exception e) {
                ctx.result(new JSONObject().put("error", e.getMessage()).toString());
            }
        });

        app.get("/assets/*", ctx -> {
            String path = ctx.path().substring(8);
            String[] split = path.split("/");
            System.out.println(ctx.path());
            CacheControl.MAX_AGE.setCacheControl(ctx, 0);
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
}