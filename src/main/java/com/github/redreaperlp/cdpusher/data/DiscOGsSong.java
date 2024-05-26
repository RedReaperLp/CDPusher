package com.github.redreaperlp.cdpusher.data;

import com.github.redreaperlp.cdpusher.hibernate.Song;
import com.github.redreaperlp.cdpusher.http.SpotifySearch;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import org.json.JSONObject;

import java.text.Normalizer;

public class DiscOGsSong extends SongData {
    public DiscOGsSong(JSONObject data, long songID) {
        this.songID = songID;
        long timeInSeconds = 0;
        new TestPrinter().append(data.toString(2)).print();
        try {
            String[] duration = data.getString("duration").split(":");
            timeInSeconds = (Integer.parseInt(duration[0]) * 60L + Integer.parseInt(duration[1]));
        } catch (Exception e) {
        }

        String[] position = data.getString("position").split("-");
        if (position.length > 1) {
            this.discNo = Integer.parseInt(position[0]);
            this.trackNo = Integer.parseInt(position[1]);
        } else if (position.length == 1) {
            this.trackNo = Integer.parseInt(position[0]);
        }

        this.title = data.getString("title");
        try {
            var arr = data.getJSONArray("artists");
            this.artist = arr.getJSONObject(0).getString("name");
        } catch (Exception e) {
            this.spotifySearch = false;
        }
        this.timeInSeconds = timeInSeconds;
    }

    public SongData spotifySearch() {
        if (this.spotifySearch) {
            var track = SpotifySearch.getInstance().finalizeSearch(this).getJSONArray("items").getJSONObject(0);
            JSONObject album = track.getJSONObject("album");

            String title = track.getString("name").trim();
            String normalizedTitle = normalizeAndCleanTitle(title);
            String[] artists = new String[track.getJSONArray("artists").length()];
            for (int i = 0; i < artists.length; i++) {
                artists[i] = track.getJSONArray("artists").getJSONObject(i).getString("name");
            }

            int duration = track.getInt("duration_ms") / 1000;
            String albumName = album.getString("name");
            String albumReleaseDate = album.getString("release_date");
            String cover = album.getJSONArray("images").getJSONObject(0).getString("url");

            var song = new Song(this);
            song.setTitle(title);
            song.setArtist(artists[0]);
            song.setAlbum(albumName);
            song.setYear(Integer.parseInt(albumReleaseDate.split("-")[0]));
            song.setTimeInSeconds(duration);
            song.setImageURI(cover);
            if (titlesMatch(this.title, normalizedTitle)) {
                return song;
            } else {
                var missmatch = new SongMissmatch(this, song);
                return missmatch;
            }
        }
        return new Song(this);
    }

    public boolean titlesMatch(String originalTitle, String updatedTitle) {
        String cleanedOriginal = normalizeAndCleanTitle(originalTitle);
        String cleanedUpdated = normalizeAndCleanTitle(updatedTitle);

        if (cleanedOriginal.length() > cleanedUpdated.length()) {
            String bigger = cleanedOriginal;
            cleanedOriginal = cleanedUpdated;
            cleanedUpdated = bigger;
        }

        return cleanedUpdated.startsWith(cleanedOriginal);
    }

    private String normalizeAndCleanTitle(String title) {
        return Normalizer.normalize(title.replaceAll("\\([^)]+\\)([^()]*)$", ""), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .trim()
                .toLowerCase();
    }

}
