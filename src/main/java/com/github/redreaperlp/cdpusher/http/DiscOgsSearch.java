package com.github.redreaperlp.cdpusher.http;

import com.github.redreaperlp.cdpusher.data.disc.DiscInformation;
import com.github.redreaperlp.cdpusher.data.disc.DiscOGsSong;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class DiscOgsSearch {
    private final String consumerKey = "nlWRgBrgZnFeOuwDqXbu";
    private final String consumerSecret = "HxyzwBoNWUhcZeZLsvqFHVTrYoaVkQhN";

    private final String tokenURL = "https://api.discogs.com/oauth/request_token=oauth_callback=http://localhost:8080";
    private final String accessTokenURL = "https://api.discogs.com/oauth/access_token";
    private final String authorizeURL = "https://www.discogs.com/oauth/authorize";
    private final String discOGsURL = "https://api.discogs.com/database/search?q=%s&key=%s&secret=%s";


    private static DiscOgsSearch instance;

    private DiscOgsSearch() {
    }


    public static DiscOgsSearch getInstance() {
        return instance == null ? instance = new DiscOgsSearch() : instance;
    }

    /**
     * Searches for a barcode and returns the first result
     *
     * @param barcode the barcode to search for
     * @return the first result or null if no result was found
     */
    public DiscInformation searchEan(String barcode) {
        String requestURL = String.format(discOGsURL, barcode, consumerKey, consumerSecret);
        String response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(requestURL)).GET().build();
            response = CliManager.send(request);
            JSONArray resJSON = new JSONObject(response).getJSONArray("results");
            if (resJSON.isEmpty()) {
                return null;
            }
            return new DiscInformation(resJSON.getJSONObject(0));
        } catch (URISyntaxException | InterruptedException | IOException e) {
            new ErrorPrinter().appendException(e).appendNewLine("\nFailed to search for barcode " + barcode)
                    .appendNewLine("Received response: " + response).print();
            return null;
        }
    }

    public @Nullable List<DiscOGsSong> searchDiscTracks(DiscInformation discInformation) {
        URI uri = URI.create(discInformation.getResourceURL());
        JSONObject res = null;
        try {
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            res = new JSONObject(CliManager.send(request));
            List<DiscOGsSong> songs = new ArrayList<>();
            JSONArray artists = new JSONArray();
            if (res.has("artists")) {
                artists = res.getJSONArray("artists");
            }
            String artist = "";
            if (!artists.isEmpty()) {
                artist = artists.getJSONObject(0).getString("name");
            }

            if (!res.has("tracklist")) return songs;

            var trackList = res.getJSONArray("tracklist");
            for (int i = 0; i < trackList.length(); i++) {
                DiscOGsSong t = new DiscOGsSong((JSONObject) trackList.get(i), i, artist);
                songs.add(t);
            }
            return songs;
        } catch (IOException | InterruptedException e) {
            new ErrorPrinter().appendException(e).appendNewLine("Failed to search for tracks of " + discInformation.getTitle())
                    .appendNewLine("Received response: ")
                    .appendNewLine(res == null ? "null" : res.toString(2))
                    .print();
        }
        return null;
    }
}
