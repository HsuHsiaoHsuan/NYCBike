package idv.funnybrain.bike.nyc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by KuoPiHua on 2014/5/4.
 */
public class StationsFavorAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    Context mContext;

    public StationsFavorAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        mContext = layoutInflater.getContext();
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
