package me.siketyan.silicagel.enumeration;

import com.sys1yagi.mastodon4j.api.entity.Status;
import me.siketyan.silicagel.R;

public enum MastodonPrivacy {
    PUBLIC(R.string.privacy_public, "public", Status.Visibility.Public),
    UNLISTED(R.string.privacy_unlisted, "unlisted", Status.Visibility.Unlisted),
    PRIVATE(R.string.privacy_private, "private", Status.Visibility.Private),
    DIRECT(R.string.privacy_direct, "direct", Status.Visibility.Direct);

    private int summaryId;
    private String value;
    private Status.Visibility visibility;

    MastodonPrivacy(int summaryId, String value, Status.Visibility visibility) {
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

    public Status.Visibility getVisibility() {
        return visibility;
    }

    public static MastodonPrivacy getByValue(String value)
    {
        for (MastodonPrivacy privacy : values())
        {
            if (privacy.getValue().equals(value)) {
                return privacy;
            }
        }

        return null;
    }
}