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
public class MusicListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<MusicFile> musicList;

    public MusicListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMusicList(ArrayList<MusicFile> musicList) {
        this.musicList = musicList;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return musicList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_content, parent, false);

        ((TextView)convertView.findViewById(R.id.MainText)).setText(musicList.get(position).getTitle());
        ((TextView)convertView.findViewById(R.id.SubText)).setText(musicList.get(position).getArtist());

        return convertView;
    }
}