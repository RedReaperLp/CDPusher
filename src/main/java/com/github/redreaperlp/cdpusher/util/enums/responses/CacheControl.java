package com.github.redreaperlp.cdpusher.util.enums.responses;

import io.javalin.http.Context;

public enum CacheControl {

    /**
     * Means that the response shouldnot be cached.
     */
    NO_CACHE("no-cache", null),
    /**
     * This is the maximum age of the response in seconds.
     */
    MAX_AGE("max-age", Integer.class),
    /**
     * Means that the response must be revalidated when it is stale. (Stale means that the response is not fresh anymore.)
     */
    MUST_REVALIDATE("must-revalidate", null),
    /**
     * Means that the response should not be stored in any cache.
     */
    NO_STORE("no-store", null),
    /**
     * Means that the response should not be transformed by any proxy.
     */
    NO_TRANSFORM("no-transform", null);

    private String cacheControl;
    private Class<?> type;

    private CacheControl(String cacheControl, Class<?> type) {
        this.cacheControl = cacheControl;
        this.type = type;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    /**
     * Sets the cache control of the response. Use this if the cache control does not need a value.
     * @param ctx The context of the request. (Context)
     */
    public void setCacheControl(Context ctx) {
        setCacheControl(ctx, getCacheControl());
    }

    /**
     * Sets the cache control of the response.
     * @param ctx The context of the request. (Context)
     * @param value The value of the cache control. (T) (Integer or Nothing)
     * @param <T> The type of the value.
     */
    public <T> void setCacheControl(Context ctx, T value) {
        if (type == null) {
            ctx.res().setHeader("Cache-Control", cacheControl);
        } else {
            if (type.isInstance(value)) {
                ctx.res().setHeader("Cache-Control", cacheControl + "=" + value);
            } else {
                throw new IllegalArgumentException("The value is not of type " + type.getName() + "! (CacheControl)");
            }
        }
    }
}
