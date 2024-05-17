package com.github.redreaperlp.cdpusher.api.get;

import com.github.redreaperlp.cdpusher.user.UserManager;
import com.github.redreaperlp.cdpusher.util.enums.responses.CacheControl;
import com.github.redreaperlp.cdpusher.util.enums.responses.ContentTypes;
import io.javalin.Javalin;
import org.json.JSONArray;

import java.io.IOException;

public class GetUser {
    public GetUser(Javalin app) {
        getUser(app);
    }
    public void getUser(Javalin app)  {
        app.get("/api/user/{username}/" , ctx -> {
            String username = ctx.pathParam("username");
            var user = UserManager.getInstance().getUser(username);
            user.ifPresentOrElse(u -> {
                try {
                    CacheControl.NO_CACHE.setCacheControl(ctx);
                    ContentTypes.JSON.setContentType(ctx, u.allSongsJSON().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                ctx.result(new JSONArray().toString());
            });
        });
    }
}
