package info.neet_ai.machi_kiku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MyPlaylistAct extends CommonAct {

    private ListView lv;
    private PlaylistListAdapter adapter;
    private ArrayList<PlaylistFile> list = new ArrayList<>();
    private TextView plname_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.myplaylist_layout);
        super.onCreate(savedInstanceState);


        //プレイリスト名など
        plname_tv = (TextView)findViewById(R.id.playlist_name);

        //リストのタップ
        lv = (ListView) findViewById(R.id.listView);
        // アイテムクリック時ののイベントを追加
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Log.v("clear2", String.valueOf(list));
                String playlistpath = list.get(pos).getPath();
                setPlaylist(playlistpath);
                plname_tv.setText(plf.getName());
            }
        });

        //＋ボタン押下
        final ImageButton add_pl_b = (ImageButton)findViewById(R.id.add_playlist_button);
        add_pl_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("info.neet_ai.machi_kiku", "info.neet_ai.machi_kiku.PlaylistEditAct");
                startActivity(intent);
            }
        });
    }

    //起動時　リスト表示
    @Override
    protected void onResume() {
        super.onResume();
        //FileInputStream fileInputStream;
        lv = (ListView)findViewById(R.id.listView);
        adapter = new PlaylistListAdapter(this);
        list = new ArrayList<>();
        File path = this.getFilesDir();
        Log.v("clear", path.toString());
        File[] filelist = path.listFiles();
        for (File i : filelist){
            int extension = i.getName().lastIndexOf(".M3U");
            if (extension != -1){
                PlaylistFile pf = new PlaylistFile();
                pf.extractFileName(i.getName());
                pf.setPath(i.getPath());
                list.add(pf);
            }

            /*try {
                String str1 = null;
                int point = i.getName().lastIndexOf(".");
                if (point != -1) {
                    str1 = i.getName().substring(0, point);
                    Log.v("clear", str1);
                    String[] str2 = str1.split("( - made by )", 0);
                    PlaylistFile pf = new PlaylistFile();
                    pf.setNameAndCreator(str2[0], str2[1]);
                    list.add(pf);
                }
            }catch(ArrayIndexOutOfBoundsException aioobe){
                Log.v("error + " + i.getName(), aioobe.getMessage());
            };*/
        }
        adapter.setPlaylistList(list);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
