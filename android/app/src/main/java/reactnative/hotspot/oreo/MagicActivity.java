package reactnative.hotspot.oreo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Callback;

import reactnative.hotspot.R;

public class MagicActivity extends PermissionsActivity {

    public static void useMagicActivityToTurnOn(Context c){
        Uri uri = new Uri.Builder().scheme(c.getString(R.string.intent_data_scheme)).authority(c.getString(R.string.intent_data_host_turnon)).build();
//        Toast.makeText(c,"Turn on. Uri: "+uri.toString(),Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(uri);
        c.startActivity(i);
    }

    public static void useMagicActivityToTurnOff(Context c){
        Uri uri = new Uri.Builder().scheme(c.getString(R.string.intent_data_scheme)).authority(c.getString(R.string.intent_data_host_turnoff)).build();
//        Toast.makeText(c,"Turn off. Uri: "+uri.toString(),Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(uri);
        c.startActivity(i);
    }

    public static void turnOnWithCb(Context c, Callback cb){
        callback = cb;
        useMagicActivityToTurnOn(c);
    }

    public static void turnOffWithCb(Context c,Callback cb){
        callback = cb;
        useMagicActivityToTurnOff(c);
    }


    private static Callback callback;

    @Override
    protected void onStart() {
        super.onStart();
        if(callback != null){
            callback.invoke();
            callback = null;
        }
    }

    private static final String TAG = MagicActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");

    }

    @Override
    void onPermissionsOkay() {
        carryOnWithHotSpotting();
    }


    /**
     * The whole purpose of this activity - to start {@link HotSpotIntentService}
     * This may be called straright away in {@code onCreate} or after permissions granted.
     */
    private void carryOnWithHotSpotting() {
        Intent intent = getIntent();
        HotSpotIntentService.start(this, intent);
        finish();
    }




}
