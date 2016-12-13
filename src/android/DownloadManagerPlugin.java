package io.cozy.downloadmanager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.CursorLoader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.net.Uri;
import android.content.Context;
import android.app.DownloadManager;

public class DownloadManagerPlugin extends CordovaPlugin {

    /**
     * Constructor.
     */
    public DownloadManagerPlugin() {
    }

    private static final class RequestContext {
        String source;
        CallbackContext callbackContext;
        boolean aborted;
        RequestContext(String source, CallbackContext callbackContext) {
            this.source = source;
            this.callbackContext = callbackContext;
        }
        void sendPluginResult(PluginResult pluginResult) {
            synchronized (this) {
                if (!aborted) {
                    callbackContext.sendPluginResult(pluginResult);
                }
            }
        }
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArray of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("download")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }

                    try {
                        // :TODO: path seems to be of no use, could be removed from JS API.
                        // :TODO: Do something with the headers
                        String source = args.getString(0);
                        JSONObject headers = args.optJSONObject(2);
                        RequestContext rcontext = new RequestContext(source, callbackContext);
                        // :TODO: report id back to JS
                        startDownload(rcontext);
                    } catch (JSONException e) {
                        // :TODO: repor error
                        return false;
                    }
                }
            });
        }
        else {
            return false;
        }
        return true;
    }


    @SuppressLint("NewApi")
    public long startDownload(RequestContext rcontext){
        Context context = this.cordova.getActivity().getApplicationContext();
        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(rcontext.source);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        return downloadmanager.enqueue(request);
    }

}
