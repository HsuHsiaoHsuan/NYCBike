package idv.funnybrain.bike.nyc;

import android.content.Context;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import idv.funnybrain.bike.nyc.data.DataDownloader;
import idv.funnybrain.bike.nyc.data.StationBeanList;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Freeman on 2014/4/26.
 */
public class FunnyActivity extends SlidingFragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    // ---- constatt variable START ----
    private static final boolean D = true;
    private static final String TAG = "FunnyActivity";
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0;
    private static final String CACHE_FILE = "data";
    // ---- constant variable END ----

    // ---- public variable START ----
    public static HashMap<String, StationBeanList> stations_list;
    // ---- public variable END ----

    // ---- local variable START ----
    private GoogleMap mMap;
    private LocationClient mLocationClient;

    private ObjectMapper objectMapper;
    private String lastUpdate;
    private ListFragment listFragment_left;
    // ---- local variable END ----


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_funny);
        setBehindContentView(R.layout.menu_frame);

        mLocationClient = new LocationClient(this, this, this);
        objectMapper = new ObjectMapper();
        stations_list = new HashMap<String, StationBeanList>();

//        if (savedInstanceState == null) {
//            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
//            listFragment_left = StationsListFragment.newInstance();
//            t.replace(R.id.menu_frame, listFragment_left);
//            t.commit();
//        } else {
//            listFragment_left = (ListFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
//        }
        if(savedInstanceState != null && stations_list.size() > 0) {
            listFragment_left = (ListFragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
        }
        getData();
        if(D) { Log.d(TAG, "----> onCreate"); }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if(D) { Log.d(TAG, "----> onCreateView"); }
        return super.onCreateView(name, context, attrs);
    }

    private void getData() {
        DataDownloader.post("", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    lastUpdate = response.getString("executionTime");

                    System.out.println(lastUpdate);
                    String data = response.getString("stationBeanList");
                    JsonFactory factory = new JsonFactory();
                    JsonParser parser = factory.createParser(data);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                    StationBeanList[] stationList = mapper.readValue(parser, StationBeanList[].class);

                    stations_list.clear();
                    for(StationBeanList sbl : stationList) {
                        stations_list.put(sbl.getId(), sbl);
                        if(D) { Log.d(TAG, "----> getData: " + sbl.toString()); }
                    }
                    if(stations_list.size() > 0) {
                        if(D) { Log.d(TAG, "----> stations_list size: " + stations_list.size()); }
                        setupLeftSlidingMenu();
                        putDataOnMap();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveDataCache(String data) {
        File file = new File(getFilesDir(), CACHE_FILE);
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(CACHE_FILE, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCacheData() {
        File file = new File(getFilesDir(), CACHE_FILE);
        FileInputStream inputStream;
        StringBuilder result = new StringBuilder();
        byte[] buffer = new byte[1024];

        try {
            inputStream = openFileInput(CACHE_FILE);
            while(inputStream.read(buffer) != -1) {
                result.append(new String(buffer));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private void putDataOnMap() {

    }

    private void setupLeftSlidingMenu() {
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        listFragment_left = StationsListFragment.newInstance();
        t.replace(R.id.menu_frame, listFragment_left);
        t.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(D) { Log.d(TAG, "----> onStart"); }
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        if(D) { Log.d(TAG, "----> onStop"); }
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        if(D) { Log.d(TAG, "----> onResume"); }
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        if(D) { Log.d(TAG, "----> onPause"); }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(D) { Log.d(TAG, "----> onDestroy"); }
        super.onDestroy();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        if(D) { Log.d(TAG, "----> onPostCreate"); }
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(D) { Log.d(TAG, "----> onConfigurationChanged"); }
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        //mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        if(D) { Log.d(TAG, "----> onLowMemory"); }
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(D) { Log.d(TAG, "----> onSaveInstanceState"); }
        super.onSaveInstanceState(outState);
    }

    // ---- local method START ----
    private void setUpMapIfNeeded() {
        if(D) { Log.d(TAG, "----> setUpMapIfNeeded"); }
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.725925, -74.00322), 14.0f));
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if(D) Log.d(TAG, "----> setUpMap");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    // ---- local method END ----

    // ---- get current location START ----
    @Override
    public void onConnected(Bundle bundle) {
        if(D) Log.d(TAG, "----> onConnected");
        Location mCurrentLocation = mLocationClient.getLastLocation();
    }

    @Override
    public void onDisconnected() {
        if(D) Log.d(TAG, "----> onDisconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(D) { Log.d(TAG, "----> onConnectionFailed"); }
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                if(D) Log.e(TAG, e.getMessage());
                //e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            if(D) Log.e(TAG, connectionResult.getErrorCode() + "");
        }
    }
    // ---- get current location END ----
}
