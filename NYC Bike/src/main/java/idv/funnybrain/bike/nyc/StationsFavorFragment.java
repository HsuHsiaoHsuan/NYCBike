package idv.funnybrain.bike.nyc;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import idv.funnybrain.bike.nyc.databases.DBHelper;

/**
 * Created by KuoPiHua on 2014/5/4.
 */
public class StationsFavorFragment extends ListFragment {
    // ---- constant variable START ----
    private static final boolean D = true;
    private static final String TAG = "StationsFavorFragment";
    // ---- constant variable END ----

    // ---- private variable START ----
    DBHelper dbHelper;
    StationsFavorAdapter adapter;
    // ---- private variable END ----

    public static StationsFavorFragment newInstance() {
        if(D) { Log.d(TAG, "newInstance"); }
        StationsFavorFragment f = new StationsFavorFragment();
        Bundle bundle = f.getArguments();
        if(bundle == null) {
            bundle = new Bundle();
        }
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_stations, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper = new DBHelper(this.getActivity());
        Cursor cursor = dbHelper.queryAll();
        int idx = cursor.getColumnIndexOrThrow("station_id");
        cursor.moveToFirst();

        adapter = new StationsFavorAdapter(getActivity(), dbHelper.queryAll(), true);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ((FunnyActivity) getActivity()).updateMap(String.valueOf(id));
    }

    public void dataChanged() {
        adapter.changeCursor(dbHelper.queryAll());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dbHelper != null) {
            dbHelper.close();
        }
    }
}
