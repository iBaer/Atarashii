package net.somethingdreadful.MAL.tasks;

import android.os.Bundle;

import net.somethingdreadful.MAL.api.MALApi;

import java.util.ArrayList;

public interface NetworkTaskCallbackListener {
    public void onNetworkTaskFinished(Object result, TaskJob job, MALApi.ListType type, Bundle data);
}
