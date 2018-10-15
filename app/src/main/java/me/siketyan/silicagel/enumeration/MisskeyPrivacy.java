package me.siketyan.silicagel.enumeration;

import me.siketyan.silicagel.R;
import me.siketyan.silicagel.misskeyapi.api.entities.Note;

public enum MisskeyPrivacy {
    PUBLIC(R.string.misskey_privacy_public, "public", Note.Visibility.Public),
    HOME(R.string.misskey_privacy_home, "home", Note.Visibility.Home),
    FOLLOWERS(R.string.misskey_privacy_followers,"followers", Note.Visibility.Followers),
    //SPECIFIED(R.string.misskey_privacy_specified,"specified", Note.Visibility.Specified),
    PRIVATE(R.string.misskey_privacy_private, "private", Note.Visibility.Private);

    private int summaryId;
    private String value;
    private Note.Visibility visibility;

    MisskeyPrivacy(int summaryId, String value, Note.Visibility visibility) {
        this.summaryId = summaryId;
        this.value = value;
        this.visibility = visibility;
    }

    public int getSummaryId() {
        return summaryId;
    }

    public String getValue() {
        return value;
    }

    public Note.Visibility getVisibility() {
        return visibility;
    }

    public static MisskeyPrivacy getByValue(String value)
    {
        for (MisskeyPrivacy privacy : values())
        {
            if (privacy.getValue().equals(value)) {
                return privacy;
            }
        }

        return null;
    }
}