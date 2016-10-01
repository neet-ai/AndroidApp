package info.neet_ai.machi_kiku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User_h on 2016/09/29.
 */
public class PlaylistListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<PlaylistFile> PlaylistList;

    public PlaylistListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setPlaylistList(ArrayList<PlaylistFile> PlaylistList) {
        this.PlaylistList = PlaylistList;
    }

    @Override
    public int getCount() {
        return PlaylistList.size();
    }

    @Override
    public Object getItem(int position) {
        return PlaylistList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return PlaylistList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_content, parent, false);

        ((TextView)convertView.findViewById(R.id.MainText)).setText(PlaylistList.get(position).getName());
        ((TextView)convertView.findViewById(R.id.SubText)).setText(PlaylistList.get(position).getCreator());

        return convertView;
    }
}