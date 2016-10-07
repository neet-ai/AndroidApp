package info.neet_ai.machi_kiku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MyPlaylistAct extends CommonAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.myplaylist_layout);
        super.onCreate(savedInstanceState);

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
        final ListView lv = (ListView)findViewById(R.id.listView);
        final PlaylistListAdapter adapter = new PlaylistListAdapter(this);
        FileInputStream fileInputStream;
        ArrayList<PlaylistFile> list = new ArrayList<>();
        File path = this.getFilesDir();
        File[] filelist = path.listFiles();
        for (File i : filelist){
            int extension = i.getName().lastIndexOf(".M3U");
            if (extension != -1){
                PlaylistFile pf = new PlaylistFile();
                pf.extractFileName(i.getName());
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
