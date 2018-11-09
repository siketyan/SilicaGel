package me.siketyan.silicagel.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import me.siketyan.silicagel.R;
import me.siketyan.silicagel.enumeration.MisskeyPrivacy;
import me.siketyan.silicagel.misskeyapi.MisskeyClient;
import me.siketyan.silicagel.misskeyapi.api.endpoints.drive;
import me.siketyan.silicagel.misskeyapi.api.endpoints.notes;
import me.siketyan.silicagel.misskeyapi.api.entities.DriveFile;
import me.siketyan.silicagel.misskeyapi.api.entities.Geo;
import me.siketyan.silicagel.misskeyapi.api.entities.Poll;
import me.siketyan.silicagel.misskeyapi.api.entities.createdNote;
import me.siketyan.silicagel.misskeyapi.api.exception.MisskeyRequestException;
import me.siketyan.silicagel.service.NotificationService;
import me.siketyan.silicagel.util.MisskeyUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private SharedPreferences pref;
    private String text;
    private byte[] bitmap;
    private String i;
    private notes notes;
    private drive drive;
    private Boolean post = false;
    private JSONArray fileIds;

    public PostTask(Context context, SharedPreferences pref, String text, byte[] bitmap) {
        this.context = context;
        this.pref = pref;
        this.text = text;
        this.bitmap = bitmap;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!MisskeyUtil.hasAccessToken(context)) return false;

        try {
            MisskeyClient client = MisskeyUtil.getClient(context);
            i = MisskeyUtil.getAccessToken(context);
            notes = new notes(client);

            drive = new drive(client);

            if (bitmap != null) {
                createFile();
            } else {
                createNote();
            }
            return true;
        } catch (Exception e) {
            NotificationService.notifyException(context, e);
            e.printStackTrace();

            Log.d(NotificationService.LOG_TAG, "[Error] Failed to toot.");
            return false;
        }
    }

    private void createFile() {
        AsyncTask<Void, Void, DriveFile> task = new AsyncTask<Void, Void, DriveFile>() {
            @Override
            protected DriveFile doInBackground(Void... params) {
                try {
                    return drive.createFiles(
                            i,
                            MultipartBody.Part.createFormData(
                                    "file",
                                    "cover.png",
                                    RequestBody.create(MediaType.parse("image/jpeg"), bitmap)
                            )
                    ).execute();
                } catch (MisskeyRequestException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(DriveFile driveFile) {
                if (driveFile != null) {
                    fileIds = new JSONArray();
                    fileIds.put(driveFile.getId());
                    createNote();
                } else {
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        task.execute();
    }

    private void createNote() {
        AsyncTask<Void, Void, createdNote> task = new AsyncTask<Void, Void, createdNote>() {
            @Override
            protected createdNote doInBackground(Void... params) {
                try {
                    String t = text;
                    String cw = null;
                    Boolean mobile = true;
                    Geo geo = null;
                    String renoteId = null;
                    Poll poll = null;
                    JSONArray files = null;
                    if (bitmap != null) {
                        files = fileIds;
                    }
                    MisskeyPrivacy privacy = MisskeyPrivacy.getByValue(pref.getString("misskey_privacy", "public"));
                    if (privacy == null) {
                        privacy = MisskeyPrivacy.PUBLIC;
                    }
                    return notes.createNote(
                            i,
                            privacy.getVisibility(),
                            t,
                            cw,
                            mobile,
                            geo,
                            files,
                            renoteId,
                            poll
                    ).execute();
                } catch (MisskeyRequestException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(createdNote result) {
                if (result != null) {
                    Log.d(NotificationService.LOG_TAG, "[Posted] " + text);
                    post = true;
                } else {
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        task.execute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (pref.getBoolean("notify_posted", true) && result && post) {
            Toast.makeText(context, R.string.posted, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}