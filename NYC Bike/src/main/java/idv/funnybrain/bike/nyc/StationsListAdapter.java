package idv.funnybrain.bike.nyc;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Iterator;

/**
 * Created by freeman on 2014/4/28.
 */
public class StationsListAdapter extends BaseAdapter {
    String[] idx;

    public StationsListAdapter() {
        Iterator<String> iterator = FunnyActivity.stations_list.keySet().iterator();
        while(iterator.hasNext()) {
            System.out.println("~~~>"+iterator.next());
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
