package idv.funnybrain.bike.nyc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import idv.funnybrain.bike.nyc.data.StationBeanList;

import java.util.Iterator;

/**
 * Created by freeman on 2014/4/28.
 */
public class StationsListAdapter extends BaseAdapter {
    String[] idx;
    LayoutInflater layoutInflater;

    public StationsListAdapter(LayoutInflater inflater) {
        layoutInflater = inflater;

        idx = new String[FunnyActivity.stations_list.size()];
        FunnyActivity.stations_list.keySet().toArray(idx);
        for(int x=0; x<idx.length;x++) {
            System.out.println("~~~~>" + idx[x]);
        }

//        Iterator<String> iterator = FunnyActivity.stations_list.keySet().iterator();
//        while(iterator.hasNext()) {
//            System.out.println("~~~>"+iterator.next());
//        }
    }

    @Override
    public int getCount() {
        return idx.length;
    }

    @Override
    public Object getItem(int position) {
        return FunnyActivity.stations_list.get(idx[position]);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(idx[position]);
    }

    static class ViewHolder {
        public TextView _id;
        public TextView _name;
        public TextView _address2;
        public TextView _bike;
        public TextView _dock;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.cell_stations, null);
            ViewHolder holder = new ViewHolder();
            holder._id = (TextView) rowView.findViewById(R.id._id);
            holder._name = (TextView) rowView.findViewById(R.id._name);
            holder._address2 = (TextView) rowView.findViewById(R.id._address2);
            holder._bike = (TextView) rowView.findViewById(R.id._bike);
            holder._dock = (TextView) rowView.findViewById(R.id._dock);

            rowView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        StationBeanList station = FunnyActivity.stations_list.get(idx[position]);
        holder._id.setText(station.getId());
        holder._name.setText(station.getStationName());
        holder._address2.setText(station.getStAddress2().toString());
        holder._bike.setText(String.valueOf(station.getAvailableBikes()));
        holder._dock.setText(String.valueOf(station.getAvailableDocks()));
        return rowView;
    }
}
