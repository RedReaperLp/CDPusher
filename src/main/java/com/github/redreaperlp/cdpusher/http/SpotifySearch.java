package com.github.redreaperlp.cdpusher.http;

import com.github.redreaperlp.cdpusher.TrackInformation;
import com.github.redreaperlp.cdpusher.spotify.SpotifyAuthentication;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;

public class SpotifySearch {
    private String searchURL = "https://api.spotify.com/v1/search?q=%s&type=track&limit=1";

    private static SpotifySearch instance;

    private SpotifySearch() {}

    public static SpotifySearch getInstance() {
        return instance == null ? instance = new SpotifySearch() : instance;
    }

    public JSONObject finalizeSearch(TrackInformation information) {
        try {
            String prepared = URLEncoder.encode((information.getTitle() + " - " +  information.getArtist(0)));
            String req = String.format(searchURL, prepared);

            HttpRequest request = HttpRequest.newBuilder(new URI(req)).GET()
                    .header("Authorization","Bearer " + SpotifyAuthentication.getInstance().getBearerToken()).build();
            JSONObject response = new JSONObject(CliManager.send(request)).getJSONObject("tracks");
            JSONObject track = response.getJSONArray("items").getJSONObject(0);
            return response;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
