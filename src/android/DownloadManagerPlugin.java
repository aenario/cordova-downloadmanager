package io.cozy.imagesbrowser;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class DownloadManagerPlugin extends CordovaPlugin {

    /**
     * Constructor.
     */
    public DownloadManagerPlugin() {
    }

    private static final class RequestContext {
        String source;
        String target;
        File targetFile;
        Long referece;
        CallbackContext callbackContext;
        boolean aborted;
        RequestContext(String source, String target, CallbackContext callbackContext) {
            this.source = source;
            this.target = target;
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
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("startDownload")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }

                    Url source = args.getString(0)
                    JSONObject headers = args.optJSONObject(2);
                    RequestContext rcontext = new RequestContext(source)

                    startDownload(rcontext);
                }
            });
        }
        else {
            return false;
        }
        return true;
    }


    @SuppressLint("NewApi")
    public JSONArray startDownload(RequestContext rcontext){
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(rcontext.source);
        DownloadManager.Request request = new Request(uri);
        RequestContext.reference = downloadmanager.enqueue(request);

    }

}
