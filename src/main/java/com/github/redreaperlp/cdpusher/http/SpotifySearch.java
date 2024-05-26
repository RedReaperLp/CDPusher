package com.github.redreaperlp.cdpusher.http;

import com.github.redreaperlp.cdpusher.data.DiscOGsSong;
import com.github.redreaperlp.cdpusher.hibernate.Song;
import com.github.redreaperlp.cdpusher.spotify.SpotifyAuthentication;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifySearch {
    private String searchURL = "https://api.spotify.com/v1/search?q=%s&type=track&limit=1";
    private String tackURL = "https://api.spotify.com/v1/tracks/%s";

    private static SpotifySearch instance;

    private SpotifySearch() {
    }

    public static SpotifySearch getInstance() {
        return instance == null ? instance = new SpotifySearch() : instance;
    }

    public JSONObject finalizeSearch(DiscOGsSong information) {
        String title = information.getTitle();
        Pattern bracketRemover = Pattern.compile("\\(.*?\\)");
        Matcher matcher = bracketRemover.matcher(title);
        title = matcher.replaceAll("");
        try {
            String prepared = URLEncoder.encode((title + (information.getArtists().length > 0 ? " - " + information.getArtists()[0] : "")));
            String req = String.format(searchURL, prepared);

            HttpRequest request = HttpRequest.newBuilder(new URI(req)).GET().header("Authorization", "Bearer " + SpotifyAuthentication.getInstance().getBearerToken()).build();
            JSONObject response = new JSONObject(CliManager.send(request)).getJSONObject("tracks");
            JSONObject track = response.getJSONArray("items").getJSONObject(0);
            return response;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Song searchSong(long songID, String songURL) {
        Pattern pattern = Pattern.compile("(?<protocol>https?|ftp)://(?:(?<subdomain>[^./]+)\\.)?(?<domain>[-A-z0-9.]+\\.[A-z]{2,})(?<file>/[-A-z0-9+&@#/%=~_|!:,.;]*)?(?:\\?)?(?<parameters>[A-z0-9+&@#/%=~_|!:,.;]*)?");
        Matcher matcher = pattern.matcher(songURL);
        if (!matcher.find()) {
            return null;
        }

        String protocol = matcher.group("protocol");
        String subdomain = matcher.group("subdomain");
        String domain = matcher.group("domain");
        String file = matcher.group("file");
        String parameters = matcher.group("parameters");

        String url = String.format(tackURL, file.replace("/intl-de/track/", ""));

        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(url)).GET().header("Authorization", "Bearer " + SpotifyAuthentication.getInstance().getBearerToken()).build();
            JSONObject response = new JSONObject(CliManager.send(request));

            String[] artists = new String[response.getJSONArray("artists").length()];
            for (int i = 0; i < response.getJSONArray("artists").length(); i++) {
                artists[i] = response.getJSONArray("artists").getJSONObject(i).getString("name");
            }

            String album = response.getJSONObject("album").getString("name");
            long durationMs = response.getLong("duration_ms") / 1000;
            int year = Integer.parseInt(response.getJSONObject("album").getString("release_date").split("-")[0]);
            String imageURI = response.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
            String name = response.getString("name");

            return new Song(songID, name, artists, album, 0, 0, durationMs, year, imageURI);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
