package com.github.redreaperlp.cdpusher.data.disc;

import com.github.redreaperlp.cdpusher.data.song.Song;
import com.github.redreaperlp.cdpusher.data.song.SongData;
import com.github.redreaperlp.cdpusher.data.song.SongMismatch;
import com.github.redreaperlp.cdpusher.http.SpotifySearch;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;

public class DiscOGsSong extends SongData {
    public DiscOGsSong(JSONObject data, long songID, String artist) {
        this.songID = songID;
        long timeInSeconds = 0;
        try {
            String[] duration = data.getString("duration").split(":");
            timeInSeconds = (Integer.parseInt(duration[0]) * 60L + Integer.parseInt(duration[1]));
        } catch (Exception e) {
        }

        try {
            String[] position = data.getString("position").split("-");
            if (position.length > 1) {
                this.discNo = Integer.parseInt(position[0]);
                this.trackNo = Integer.parseInt(position[1]);
            } else if (position.length == 1) {
                this.trackNo = Integer.parseInt(position[0]);
            }
        } catch (Exception e) {
            this.discNo = -1;
            this.trackNo = -1;
        }


        this.title = data.getString("title");
        try {
            var arr = data.getJSONArray("artists");
            this.artist = arr.getJSONObject(0).getString("name");
        } catch (Exception e) {
            if (!artist.isEmpty()) {
                this.artist = artist;
            } else this.spotifySearch = false;
        }
        this.timeInSeconds = timeInSeconds;
    }

    public SongData spotifySearch() {
        if (this.spotifySearch) {
            JSONObject res = SpotifySearch.getInstance().finalizeSearch(this);
            if (res == null) {
                return new SongMismatch(this, null);
            }
            var track = res.getJSONArray("items").getJSONObject(0);
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
            JSONArray images = album.getJSONArray("images");
            String cover = null;
            if (!images.isEmpty())
                cover = images.getJSONObject(0).getString("url");

            var song = new Song(this);
            song.setTitle(title);
            song.setArtist(artists[0]);
            song.setAlbum(albumName);
            song.setYear(Integer.parseInt(albumReleaseDate.split("-")[0]));
            song.setTimeInSeconds(duration);
            song.setImageURI(cover);
            if (titlesMatch(this.title, normalizedTitle) || images.isEmpty()) {
                return song;
            } else {
                var mismatch = new SongMismatch(this, song);
                return mismatch;
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
