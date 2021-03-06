package net.somethingdreadful.MAL.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import net.somethingdreadful.MAL.ContentManager;
import net.somethingdreadful.MAL.Theme;
import net.somethingdreadful.MAL.account.AccountService;
import net.somethingdreadful.MAL.api.APIHelper;
import net.somethingdreadful.MAL.api.BaseModels.History;
import net.somethingdreadful.MAL.api.BaseModels.Profile;

import java.util.ArrayList;

public class UserNetworkTask extends AsyncTask<String, Void, Profile> {
    private final boolean forcesync;
    private final UserNetworkTaskListener callback;
    private final Activity activity;

    public UserNetworkTask(boolean forcesync, UserNetworkTaskListener callback, Activity activity) {
        this.forcesync = forcesync;
        this.callback = callback;
        this.activity = activity;
    }

    @Override
    protected Profile doInBackground(String... params) {
        boolean isNetworkAvailable = APIHelper.isNetworkAvailable(activity);
        Profile result = null;
        if (params == null) {
            Crashlytics.log(Log.ERROR, "Atarashii", "UserNetworkTask.doInBackground(): No username to fetch profile");
            return null;
        }
        ContentManager cManager = new ContentManager(activity);

        try {
            if (!AccountService.isMAL() && isNetworkAvailable)
                cManager.verifyAuthentication();

            if (forcesync && isNetworkAvailable) {
                result = cManager.getProfile(params[0]);
            } else if (params[0].equalsIgnoreCase(AccountService.getUsername())) {
                result = cManager.getProfileFromDB();
                if (result == null && isNetworkAvailable)
                    result = cManager.getProfile(params[0]);
            } else if (isNetworkAvailable) {
                result = cManager.getProfile(params[0]);
            }

            if (result != null && isNetworkAvailable && params.length == 2) {
                ArrayList<History> activities = cManager.getActivity(params[0], Integer.parseInt(params[1]));
                result.setActivity(activities);
            }
        } catch (Exception e) {
            Theme.logTaskCrash("UserNetworkTask", "doInBackground(5): task unknown API error (?)", e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(Profile result) {
        if (callback != null)
            callback.onUserNetworkTaskFinished(result);
    }

    public interface UserNetworkTaskListener {
        void onUserNetworkTaskFinished(Profile result);
    }
}
