package me.siketyan.silicagel.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.entity.Attachment;
import com.sys1yagi.mastodon4j.api.method.Media;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.enumeration.MastodonPrivacy;
import me.siketyan.silicagel.service.NotificationService;
import me.siketyan.silicagel.util.MastodonUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.List;

public class TootTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private SharedPreferences pref;
    private String text;
    private byte[] bitmap;

    public TootTask(Context context, SharedPreferences pref, String text, byte[] bitmap) {
        this.context = context;
        this.pref = pref;
        this.text = text;
        this.bitmap = bitmap;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            MastodonPrivacy privacy = MastodonPrivacy.getByValue(pref.getString("mastodon_privacy", "public"));
            if (privacy == null) {
                return false;
            }

            MastodonClient client = MastodonUtil.getClient(context, true);
            Statuses statuses = new Statuses(client);
            Media media = new Media(client);

            List<Long> mediaIds = new ArrayList<>();
            if (bitmap != null) {
                Attachment attachment = media.postMedia(
                    MultipartBody.Part.createFormData(
                        "file",
                        "cover.png",
                        RequestBody.create(MediaType.parse("image/jpeg"), bitmap)
                    )
                ).execute();
                mediaIds.add(0, attachment.getId());
            }

            statuses.postStatus(
                text,
                null,
                mediaIds,
                false,
                null,
                privacy.getVisibility()
            ).execute();

            Log.d(NotificationService.LOG_TAG, "[Tooted] " + params[0]);
            return true;
        } catch (Exception e) {
            NotificationService.notifyException(context, e);
            e.printStackTrace();

            Log.d(NotificationService.LOG_TAG, "[Error] Failed to toot.");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (pref.getBoolean("notify_posted", true) && result) {
            Toast.makeText(context, R.string.tooted, Toast.LENGTH_SHORT)
                .show();
        }
    }
}