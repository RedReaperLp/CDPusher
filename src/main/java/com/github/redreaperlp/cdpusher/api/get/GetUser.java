package com.github.redreaperlp.cdpusher.api.get;

import com.github.redreaperlp.cdpusher.user.User;
import com.github.redreaperlp.cdpusher.user.UserManager;
import com.github.redreaperlp.cdpusher.util.enums.responses.CacheControl;
import com.github.redreaperlp.cdpusher.util.enums.responses.ContentTypes;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import io.javalin.Javalin;
import org.json.JSONArray;

public class GetUser {
    public GetUser(Javalin app) {
        getUser(app);
    }
    public void getUser(Javalin app)  {
        app.get("/api/user/{username}/" , ctx -> {
            String username = ctx.pathParam("username");
            new TestPrinter().append("Getting user: " + username).print();
            User user = UserManager.getInstance().getUser(username);
            if (user == null) {
                new TestPrinter().append("User not found").print();
                ctx.result(new JSONArray().toString());
                return;
            }
            CacheControl.NO_CACHE.setCacheControl(ctx);
            ContentTypes.JSON.setContentType(ctx, user.allSongsJSON().toString());
        });
    }
}
