package com.github.redreaperlp.cdpusher.http;

import org.json.JSONObject;

public class Topic {
    public static enum Disc {
        ALREADY_EXISTS("already_exists", "Disc already exists"),
        STILL_MISMATCHES("still_mismatches", "There are still mismatches"),
        NOT_FOUND("not_found", "Disc not found"),
        PUSHED_TO_DB("pushed_to_db", "Disc pushed to database"),
        SUCCESS("success", "Disc found"),
        STILL_INDEXING("still_indexing", "Disc is still indexing"),
        FAILED("failed", "Internal Server Error");

        private final String value;
        private final String description;

        Disc(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public JSONObject fillResponse(JSONObject response) {
            return response.put("topic", "disc")
                    .put("type", value)
                    .put("description", description);
        }
    }
}
