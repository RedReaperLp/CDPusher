package com.github.redreaperlp.cdpusher.http;

import com.github.redreaperlp.cdpusher.data.disc.DiscInformation;
import com.github.redreaperlp.cdpusher.data.disc.DiscOGsSong;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
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

    public List<DiscOGsSong> searchDiscTracks(DiscInformation discInformation) {
        URI uri = URI.create(discInformation.getResourceURL());
        try {
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            JSONObject res = new JSONObject(CliManager.send(request));
            List<DiscOGsSong> songs = new ArrayList<>();
            if (!res.has("tracklist")) return songs;

            var tracklist = res.getJSONArray("tracklist");
            for (int i = 0; i < tracklist.length(); i++) {
                DiscOGsSong t = new DiscOGsSong((JSONObject) tracklist.get(i), i);
                songs.add(t);
            }
            return songs;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
