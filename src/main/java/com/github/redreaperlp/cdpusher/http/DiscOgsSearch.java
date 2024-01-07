package com.github.redreaperlp.cdpusher.http;

import com.github.redreaperlp.cdpusher.DiscInformation;
import com.github.redreaperlp.cdpusher.TrackInformation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

public class DiscOgsSearch {
    private String consumerKey = "nlWRgBrgZnFeOuwDqXbu";
    private String consumerSecret = "HxyzwBoNWUhcZeZLsvqFHVTrYoaVkQhN";

    private String tokenURL = "https://api.discogs.com/oauth/request_token=oauth_callback=http://localhost:8080";
    private String accessTokenURL = "https://api.discogs.com/oauth/access_token";
    private String authorizeURL = "https://www.discogs.com/oauth/authorize";


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
    public DiscInformation searchBarcode(String barcode) {
        String requestURL = String.format("https://api.discogs.com/database/search?q=%s&key=%s&secret=%s", barcode, consumerKey, consumerSecret);
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(requestURL)).GET().build();
            JSONArray resJSON = new JSONObject(
                    CliManager.send(request)
            ).getJSONArray("results");
            if (resJSON.isEmpty()) {
                return null;
            }
            return new DiscInformation(resJSON.getJSONObject(0));
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TrackInformation lookupTrack(TrackInformation information) {

        return null;
    }

    public JSONArray searchDiscTracks(DiscInformation discInformation) {
        URI uri = URI.create(discInformation.getResourceURL());
        try {
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            JSONObject res = new JSONObject(CliManager.send(request));
            if (!res.has("tracklist")) return null;
            return res.getJSONArray("tracklist");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
