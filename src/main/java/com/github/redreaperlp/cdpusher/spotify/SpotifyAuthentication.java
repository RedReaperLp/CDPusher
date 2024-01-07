package com.github.redreaperlp.cdpusher.spotify;

import com.github.redreaperlp.cdpusher.http.CliManager;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;

public class SpotifyAuthentication {
    private String clientID = "81152a04ae1845d1b73a7c55488ec12d";
    private String clientSecret = "494dc1db61b3459cb64d9a397ca0b22e";

    private String bearerToken;
    private LocalDateTime tokenExpiration;

    private String tokenURL = "https://accounts.spotify.com/api/token";

    private static SpotifyAuthentication instance;

    public static SpotifyAuthentication getInstance() {
        return instance == null ? instance = new SpotifyAuthentication() : instance;
    }


    public void receiveToken() {
        String requestBody = "grant_type=client_credentials&client_id=" + clientID + "&client_secret=" + clientSecret;
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(tokenURL)).POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/x-www-form-urlencoded").build();
            JSONObject response = new JSONObject(CliManager.send(request));
            bearerToken = response.getString("access_token");
            tokenExpiration = LocalDateTime.now().plusSeconds(response.getInt("expires_in") - 10);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBearerToken() {
        if (tokenExpiration == null || tokenExpiration.isBefore(LocalDateTime.now())) {
            receiveToken();
        }
        return bearerToken;
    }
}
