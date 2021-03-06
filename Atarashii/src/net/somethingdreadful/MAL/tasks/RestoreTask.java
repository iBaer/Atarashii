package net.somethingdreadful.MAL.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import net.somethingdreadful.MAL.ContentManager;
import net.somethingdreadful.MAL.R;
import net.somethingdreadful.MAL.Theme;
import net.somethingdreadful.MAL.account.AccountService;
import net.somethingdreadful.MAL.api.APIHelper;
import net.somethingdreadful.MAL.api.BaseModels.Backup;
import net.somethingdreadful.MAL.database.DatabaseManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class RestoreTask extends AsyncTask<String, Void, Object> {


    private final RestoreTaskListener callback;
    private final Activity activity;
    private final String filename;

    public RestoreTask(RestoreTaskListener callback, Activity activity, String filename) {
        this.callback = callback;
        this.activity = activity;
        this.filename = filename;
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
            ContentManager cManager = new ContentManager(activity);

            // get and read the backup file
            StringBuilder backupJson = new StringBuilder();
            FileInputStream fIn = new FileInputStream(new File(filename));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null)
                backupJson.append(buffer);
            bufferedReader.close();

            // convert string to backup model
            Backup backup = (new Gson()).fromJson(backupJson.toString(), Backup.class);
            if (backup.getAccountType().equals(AccountService.accountType)) {
                DatabaseManager databaseManager = new DatabaseManager(activity);
                databaseManager.restoreLists(backup.getAnimeList(), backup.getMangaList());
            } else {
                Theme.Snackbar(activity, R.string.toast_info_backup_list);
                return null;
            }

            cManager.verifyAuthentication();

            // check if the network is available
            if (APIHelper.isNetworkAvailable(activity)) {
                // clean dirty records to pull all the changes
                cManager.cleanDirtyAnimeRecords();
                cManager.cleanDirtyMangaRecords();
                cManager.downloadAnimeList(AccountService.getUsername());
                cManager.downloadMangaList(AccountService.getUsername());
            }

            // notify user
            Crashlytics.log(Log.INFO, "Atarashii", "RestoreTask.restoreBackup(): Backup has been created");
            if (callback != null)
                callback.onRestoreTaskFinished();
        } catch (Exception e) {
            Theme.logTaskCrash("RestoreTask", "restoreBackup()", e);
            if (callback != null) {
                callback.onRestoreTaskFailed();
            }
        }
        return null;
    }

    public interface RestoreTaskListener {
        void onRestoreTaskFinished();

        void onRestoreTaskFailed();
    }
}
