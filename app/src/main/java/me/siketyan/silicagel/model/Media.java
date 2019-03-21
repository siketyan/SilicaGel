package me.siketyan.silicagel.model;

import android.app.Notification;
import android.content.Context;
import android.media.session.MediaSession;
import android.os.Bundle;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.util.Logger;

public class Media {
    private String title;
    private String artist;
    private String album;

    private Media(Context context, Bundle extras) {
        CharSequence titleSeq = extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence artistSeq = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence albumSeq = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

        String unknown = context.getString(R.string.unknown);
        title = titleSeq != null ? titleSeq.toString() : unknown;
        artist = artistSeq != null ? artistSeq.toString() : unknown;
        album = albumSeq != null ? albumSeq.toString() : unknown;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public boolean equals(Media media) {
        if (media == null) return false;

        return
            title.equals(media.title) &&
            artist.equals(media.artist) &&
            album.equals(media.album);
    }

    public static Media create(Context context, Notification notification) {
        Bundle extras = notification.extras;
        MediaSession.Token token = (MediaSession.Token) extras.get(Notification.EXTRA_MEDIA_SESSION);
        if (token == null) {
            Logger.debug("There is no media session, skipping");
            return null;
        }

        return new Media(context, extras);
    }
}
