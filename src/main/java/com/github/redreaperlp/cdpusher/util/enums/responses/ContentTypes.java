package com.github.redreaperlp.cdpusher.util.enums.responses;

import io.javalin.http.Context;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public enum ContentTypes {
    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("text/javascript"),
    JSON("application/json"),
    XML("application/xml"),
    TEXT("text/plain"),
    PNG("image/png"),
    JPG("image/jpeg"),
    WEBP("image/webp"),
    GIF("image/gif"),
    ICO("image/x-icon"),
    SVG("image/svg+xml"),
    MP4("video/mp4"),
    MP3("audio/mpeg"),
    OGG("audio/ogg"),
    WEBM("audio/webm"),
    TTF("font/ttf"),
    PDF("application/pdf"),
    ZIP("application/zip"),
    GZIP("application/gzip"),
    TAR("application/x-tar"),
    RAR("application/vnd.rar"),
    ;
    private String contentType;

    private ContentTypes(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type of the response.
     *
     * @param ctx The context of the request. (Context)
     */
    public void setContentType(Context ctx, String toSubmit) throws IOException {
        ctx.res().setContentType(contentType);
        ctx.res().setContentLength(toSubmit.getBytes(StandardCharsets.UTF_8).length);
        ctx.res().setCharacterEncoding("UTF-8");
        PrintWriter writer = ctx.res().getWriter();
        writer.write(toSubmit);
    }

    /**
     * Sets the content type of the response.
     * @param ctx The context of the request. (Context)
     * @param toSubmit The byte array to submit.
     * @throws IOException If an error occurs while writing to the response.
     */
    public void setContentType(Context ctx, byte[] toSubmit) throws IOException {
        ctx.res().setContentType(contentType);
        ctx.res().setContentLength(toSubmit.length);
        ctx.res().setCharacterEncoding("UTF-8");
        ctx.res().getOutputStream().write(toSubmit);
    }

    /**
     * Sets the content type of the response.
     *
     * @param res The response of the request. (HttpServletResponse)
     */
    public void setContentType(HttpServletResponse res) {
        res.setContentType(contentType);
    }
}
