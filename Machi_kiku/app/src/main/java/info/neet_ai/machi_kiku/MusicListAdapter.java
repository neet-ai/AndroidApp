package info.neet_ai.machi_kiku;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;

/**
 * Created by User_h on 2016/09/29.
 */
public class MusicListAdapter extends BaseAdapter implements UndoAdapter, Swappable{

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<MusicFile> musicList = new ArrayList<>();

    public MusicListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMusicList(ArrayList<MusicFile> musicList) {
        this.musicList = musicList;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        //return musicList.get(position);
        return position;
    }

    @Override
    public long getItemId(int position) {
        return musicList.get(position).getId();
        //return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_content, parent, false);

        ((TextView)convertView.findViewById(R.id.MainText)).setText(musicList.get(position).getTitle());
        ((TextView)convertView.findViewById(R.id.SubText)).setText(musicList.get(position).getArtist());

        return convertView;
    }

    @NonNull
    @Override
    public View getUndoView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return null;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull View view) {
        return null;
    }


    private PlaylistEditAct.ListChgListener listener;

    @Override
    public void swapItems(int originalPosition, int newPosition) {
        MusicFile mf_tmp = musicList.get(newPosition);
        musicList.set(newPosition, musicList.get(originalPosition));
        musicList.set(originalPosition, mf_tmp);
        LRListener.ListChg();

        //this.notifyDataSetChanged();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ListRefreshListener LRListener;

    public interface ListRefreshListener {
        void ListChg();
    }

    public void addListRefreshListener(ListRefreshListener LRlistener){
        this.LRListener = LRlistener;
    }

}