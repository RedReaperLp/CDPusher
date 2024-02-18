package com.github.redreaperlp.cdpusher.api;

import com.github.redreaperlp.cdpusher.user.User;
import com.github.redreaperlp.cdpusher.user.UserManager;
import com.github.redreaperlp.cdpusher.util.enums.responses.ContentTypes;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import io.javalin.Javalin;

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
                ctx.status(410);
                return;
            }
            ContentTypes.JSON.setContentType(ctx, user.allSongsJSON().toString());
        });
    }
}
