package idv.funnybrain.bike.nyc;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.capricorn.RayMenu;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import idv.funnybrain.bike.nyc.data.DataDownloader;
import idv.funnybrain.bike.nyc.data.StationBeanList;
import idv.funnybrain.bike.nyc.databases.DBHelper;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

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
    public static HashMap<String, Marker> stations_marker_list;
    // ---- public variable END ----

    // ---- private variable START ----
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private GoogleMap.OnMarkerClickListener markerClickListener;

    private ObjectMapper objectMapper;
    private String lastUpdate;
    private ListFragment listFragment_left;
    private ListFragment listFragment_favor;
    private String preSelectedMarker = "";
    private String nowSelectedMarker = "";

    RayMenu rayMenu;
    private DBHelper dbHelper;
    // ---- private variable END ----


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_funny);
        setBehindContentView(R.layout.menu_frame); // init left sliding menu

        SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT_RIGHT);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setSecondaryMenu(R.layout.menu_frame_favor);

        setSlidingActionBarEnabled(false);

        mLocationClient = new LocationClient(this, this, this);
        objectMapper = new ObjectMapper();
        stations_list = new HashMap<String, StationBeanList>();
        stations_marker_list = new HashMap<String, Marker>();

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
        //getData();

        dbHelper = new DBHelper(this);

        rayMenu = (RayMenu) findViewById(R.id.ray_menu);
        rayMenu.setVisibility(View.GONE);


        if(D) { Log.d(TAG, "----> onCreate"); }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if(D) { Log.d(TAG, "----> onCreateView"); }
        return super.onCreateView(name, context, attrs);
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
        if(dbHelper != null) {
            dbHelper.close();
        }
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
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if(D) Log.d(TAG, "----> setUpMap");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.725925, -74.00322), 12.0f));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        markerClickListener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                FunnyActivity.this.selectMarker(marker.getTitle());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
                return true; // if return true, it wont show marker info window.
            }
        };
        mMap.setOnMarkerClickListener(markerClickListener);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                FunnyActivity.this.selectMarker(""); // when user click empty space on Map, it means click nothing -> ""
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(D) { Log.d(TAG, "----> map onCameraChangeListener, Zoom: " + cameraPosition.zoom); }
            }
        });

        getData();
    }

    private void getData() {
        setProgressBarIndeterminateVisibility(true);
        DataDownloader.post("", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    lastUpdate = response.getString("executionTime");

                    if(D) { Log.d(TAG,"lastUpdate: " + lastUpdate); }
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
                        if(D) { Log.d(TAG, "----> getData, stations_list size: " + stations_list.size()); }
                        setupLeftSlidingMenu();
                        putDataOnMap();
                        setProgressBarIndeterminateVisibility(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // no Connection!  or 500
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                super.onFailure(statusCode, headers, responseBody, error);
                if(D) { Log.e(TAG, "----> getData fail: " + statusCode); }
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if(D) { Log.e(TAG, "----> getData progress, bytesWritten: " + bytesWritten + ", totalSize: " + totalSize); }
            }

            @Override
            public void onRetry() {
                super.onRetry();
                if(D) { Log.e(TAG, "----> getData retry"); }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if(D) { Log.e(TAG, "----> onFinish"); }
            }
        });
    }

    // TODO cache data
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

    // TODO get cached data
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
        TextView tv_lastUpdate = (TextView) findViewById(R.id.lastUpdate);
        tv_lastUpdate.setText(getString(R.string.lastUpdate)+ lastUpdate);
        tv_lastUpdate.setVisibility(View.VISIBLE);

        mMap.clear();
        stations_marker_list.clear();
        Iterator<String> iterator = stations_list.keySet().iterator();
        while(iterator.hasNext()) {
            String idx = iterator.next();
            StationBeanList tmpStation = stations_list.get(idx);
            stations_marker_list.put(idx, mMap.addMarker(
                            new MarkerOptions().position(tmpStation.getLatLng())
                                    .title(tmpStation.getId())
                                    .snippet(tmpStation.getStationName())
                                            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_empty))
                                    .icon(
                                        BitmapDescriptorFactory.fromBitmap(
                                            writeOnDrawable(idx, tmpStation.getAvailableBikes(), tmpStation.getAvailableDocks()).getBitmap())
                                    )
                                    .draggable(false)
                    )
            );
        }
    }

    private void setupLeftSlidingMenu() {
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        listFragment_left = StationsListFragment.newInstance();
        t.replace(R.id.menu_frame, listFragment_left);
        t.commit();

        // after setup left, now start to setup favor list.
        setupRightSlidingMenu();
    }

    private void setupRightSlidingMenu() { // setup favor list
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        listFragment_favor = StationsFavorFragment.newInstance();
        t.replace(R.id.menu_frame_favor, listFragment_favor);
        t.commit();
    }

    protected void updateMap(String idx) {
        getSlidingMenu().showContent(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stations_list.get(idx).getLatLng(), 15.0f));
        selectMarker(idx);
    }

    protected void selectMarker(String idx) {
        if(D) { Log.d(TAG, "selectMarker, preSelectedMarker: " + preSelectedMarker); }
        nowSelectedMarker = idx;
        if(!preSelectedMarker.equals("")) { // reset previous selected Marker
            StationBeanList tmpStation = stations_list.get(preSelectedMarker);
            stations_marker_list.get(preSelectedMarker).setIcon(
                    BitmapDescriptorFactory.fromBitmap(writeOnDrawable(preSelectedMarker, tmpStation.getAvailableBikes(), tmpStation.getAvailableDocks()).getBitmap()));
        }
        if(!idx.equals("")) { // click on another Marker
            if(dbHelper.queryIsFavor(idx)) {
                stations_marker_list.get(idx).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bike_selected_favor));
            } else {
                stations_marker_list.get(idx).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bike_selected));
            }
            updateExtraInfo(true, idx);
        } else { // click on nothing
            updateExtraInfo(false, idx);
        }
        preSelectedMarker = idx;

    }

    protected void updateExtraInfo(boolean toShow, final String idx) {
        RelativeLayout infoLayout = (RelativeLayout) findViewById(R.id.info);
        if(toShow) {
            StationBeanList tmpStation = stations_list.get(idx);
            ((TextView) findViewById(R.id.info_name)).setText(tmpStation.getStationName());
            ((TextView) findViewById(R.id.info_address2)).setText(tmpStation.getStAddress2());
            ((TextView) findViewById(R.id.info_bike)).setText(String.valueOf(tmpStation.getAvailableBikes()));
            ((TextView) findViewById(R.id.info_dock)).setText(String.valueOf(tmpStation.getAvailableDocks()));
            infoLayout.setVisibility(View.VISIBLE);
            rayMenu.setVisibility(View.VISIBLE);
            rayMenu.removeAllChild();

            if(dbHelper.queryIsFavor(idx)) { // it's favor one, so we should show un-favor icon
                ImageView iv_favor_not = new ImageView(this);
                iv_favor_not.setImageResource(R.drawable.bt_nofavor);
                rayMenu.addItem(iv_favor_not, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int result = dbHelper.removeFavor(idx);
                        if(result >= 0) {
                            selectMarker(idx);
                            ((StationsFavorFragment)listFragment_favor).dataChanged();
                        }
                    }
                });

            } else {
                ImageView iv_favor = new ImageView(this);
                iv_favor.setImageResource(R.drawable.bt_favor);
                rayMenu.addItem(iv_favor, new View.OnClickListener() { // item position is 0
                    @Override
                    public void onClick(View v) {
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.DB_COL_STATION_ID, stations_marker_list.get(idx).getTitle());
                        if(D) { Log.d(TAG, "addFavor click: " + stations_list.get(idx).getStationName() + "," +
                                stations_list.get(idx).getId()); }
                        long result = dbHelper.insertFavor(values);
                        if(result > 0) {
                            selectMarker(idx);
                            ((StationsFavorFragment)listFragment_favor).dataChanged();
                        }
                    }
                });
            }
        } else {
            infoLayout.setVisibility(View.GONE);
            rayMenu.setVisibility(View.GONE);
        }
    }

//    private void updateFavorMarker(String idx, boolean isFavor) {
//        StationBeanList tmpStation = stations_list.get(idx);
//        stations_marker_list.get(idx).setIcon(BitmapDescriptorFactory.fromBitmap(writeOnDrawable(idx, tmpStation.getAvailableBikes(), tmpStation.getAvailableDocks()).getBitmap()));
//    }

    private BitmapDrawable writeOnDrawable(String idx, int bikeCount, int dockCount) {
        Paint paint = new Paint();

        Bitmap bitmap;
        if(dbHelper.queryIsFavor(idx)) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_empty_favor).copy(Bitmap.Config.ARGB_8888, true);
            paint.setColor(Color.RED);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_empty).copy(Bitmap.Config.ARGB_8888, true);
            paint.setColor(Color.BLUE);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(23);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        Canvas canvas = new Canvas(bitmap);
        String show = bikeCount + "/" + dockCount;
        canvas.drawText(show, bitmap.getWidth()/2, bitmap.getHeight()/2, paint);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private int getSmallIcon(String idx) {
        if(dbHelper.queryIsFavor(idx)) {
            return R.drawable.marker_empty_favor_small;
        } else {
            return R.drawable.marker_empty_small;
        }
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
