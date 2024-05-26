package com.github.redreaperlp.cdpusher.api.get;

import com.github.redreaperlp.cdpusher.user.UserManager;
import com.github.redreaperlp.cdpusher.util.enums.responses.CacheControl;
import com.github.redreaperlp.cdpusher.util.enums.responses.ContentTypes;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import io.javalin.Javalin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class GetUser {
    public GetUser(Javalin app) {
        getUser(app);
    }

    public void getUser(Javalin app) {
        app.get("/api/user/{username}/", ctx -> {
            String username = ctx.pathParam("username");
            var user = UserManager.getInstance().getUser(username);
            user.ifPresentOrElse(u -> {
                try {
                    JSONObject response;
                    try {
                        var songs = u.allSongsJSON();
                        var discInfo = u.getDisc().toJSON();
                        response = new JSONObject().put("songs", songs).put("disc", discInfo);
                    } catch (Exception e) {
                        response = new JSONObject().put("songs", new JSONArray()).put("disc", new JSONObject());
                    }


                    CacheControl.NO_CACHE.setCacheControl(ctx);
                    ContentTypes.JSON.setContentType(ctx, response.toString());
                } catch (IOException e) {
                    new ErrorPrinter().appendException(e).print();
                }
            }, () -> {
                ctx.result(new JSONArray().toString());
            });
        });
    }
}
