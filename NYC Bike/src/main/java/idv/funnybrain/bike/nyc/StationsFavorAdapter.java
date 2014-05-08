package idv.funnybrain.bike.nyc;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import idv.funnybrain.bike.nyc.data.StationBeanList;
import idv.funnybrain.bike.nyc.databases.DBHelper;

/**
 * Created by KuoPiHua on 2014/5/4.
 */
public class StationsFavorAdapter extends CursorAdapter {
    // ---- constant variable START ----
    // ---- constant variable END ----

    // ---- local variable START ----
    private LayoutInflater layoutInflater;
    //private Context mContext;
    //private Cursor mCursor;
    // ---- local variable END ----

    public StationsFavorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.layoutInflater = LayoutInflater.from(context);
        //this.mContext = context;
        //this.mCursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.cell_stations, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String idx = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.DB_COL_STATION_ID));
        StationBeanList tmpStation = FunnyActivity.stations_list.get(idx);

        TextView id = (TextView) view.findViewById(R.id._id);
        id.setText(idx);

        TextView name = (TextView) view.findViewById(R.id._name);
        name.setText(tmpStation.getStationName());

        TextView address2 = (TextView) view.findViewById(R.id._address2);
        address2.setText(tmpStation.getStAddress2());

        TextView bike = (TextView) view.findViewById(R.id._bike);
        bike.setText(String.valueOf(tmpStation.getAvailableBikes()));

        TextView dock = (TextView) view.findViewById(R.id._dock);
        dock.setText(String.valueOf(tmpStation.getAvailableDocks()));
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        String idx = cursor.getString(mCursor.getColumnIndexOrThrow(DBHelper.DB_COL_STATION_ID));
//        return super.getItemId(position);
        return Long.valueOf(idx);
    }
}
