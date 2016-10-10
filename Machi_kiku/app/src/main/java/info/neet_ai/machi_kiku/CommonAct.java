package info.neet_ai.machi_kiku;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

public class CommonAct extends ActionBarActivity implements MediaPlayer.OnCompletionListener { //AppCompatActivity {
    private DrawerLayout vDrawerLayout;
    private ActionBarDrawerToggle vDrawerToggle;
    private ListView vListView;
    private ImageButton ssButton;
    private ImageButton prButton;
    private ImageButton bkButton;
    private ImageButton frButton;
    private ImageButton nxButton;
    private TextView plname_tv;
    private TextView title_tv;
    private TextView artist_tv;

    static protected MediaPlayer mediaPlayer;
    static protected PlaylistFile plf = null;
    static protected ArrayList<PlaylistFile> plfarray = new ArrayList();
    static protected ArrayList<MusicFile> mfarray = new ArrayList();
    static protected int playlist_num = 0;
    static protected int music_num = 0;
    static protected int playing = 0;
    static protected int playtime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();

        //ドロワー
        vDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        initDrawer();
        vListView = (ListView) findViewById(R.id.drawer_list);
        vListView.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, new String[]{"アカウント", "自分のプレイリスト", "設定"}));
        // アイテムクリック時ののイベントを追加
        vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos){
                    case 0:
                        // インテントの生成
                        vDrawerLayout.closeDrawers();
                        //Toast.makeText(getBaseContext(), getLocalClassName(), Toast.LENGTH_LONG).show();
                        if(!getLocalClassName().equals("AccountAct")) {
                            Intent intent = new Intent();
                            intent.setClassName("info.neet_ai.machi_kiku", "info.neet_ai.machi_kiku.AccountAct");
                            startActivity(intent);
                        }
                        break;
                    case 1:
                        // インテントの生成
                        vDrawerLayout.closeDrawers();
                        //Toast.makeText(getBaseContext(), getLocalClassName(), Toast.LENGTH_LONG).show();
                        if(!getLocalClassName().equals("MyPlaylistAct")) {
                            Intent intent = new Intent();
                            intent.setClassName("info.neet_ai.machi_kiku", "info.neet_ai.machi_kiku.MyPlaylistAct");
                            startActivity(intent);
                        }
                        break;
                }
            }
        });

        //音楽再生
        ssButton = (ImageButton) findViewById(R.id.start_stop_button);
        ssButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayAndStop();
            }
        });
        prButton = (ImageButton) findViewById(R.id.previous_button);
        prButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioStop();
                if(plfarray.size() > 0) {
                    music_num = 0;
                    int pos = playlist_num - 1;
                    if (pos < 0) pos = plfarray.size() - 1;
                    setPlaylist(plfarray, pos);
                    checkonPlayable();
                }
            }
        });
        nxButton = (ImageButton) findViewById(R.id.next_button);
        nxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioStop();
                if(plfarray.size() > 0) {
                    music_num = 0;
                    int pos = playlist_num + 1;
                    if(pos > plfarray.size() - 1) pos = 0;
                    setPlaylist(plfarray, pos);
                    checkonPlayable();
                }
            }
        });
        bkButton = (ImageButton) findViewById(R.id.back_button);
        bkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioStop();
                music_num--;
                if(music_num < 0) music_num = mfarray.size() - 1;
                checkonPlayable();
            }
        });
        frButton = (ImageButton) findViewById(R.id.forward_button);
        frButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioStop();
                music_num++;
                if(music_num > mfarray.size() - 1) music_num = 0;
                checkonPlayable();
            }
        });


        //プレイリスト名など
        plname_tv = (TextView)findViewById(R.id.playlist_name);
        title_tv = (TextView)findViewById(R.id.music_name);
        artist_tv = (TextView)findViewById(R.id.artist_name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected void onResume() {
        super.onResume();
        if(playing == 1)
            ssButton.setImageResource(android.R.drawable.ic_media_pause);
        else
            ssButton.setImageResource(android.R.drawable.ic_media_play);
        if(plf != null) {
            plname_tv.setText(plf.getName());
            title_tv.setText(mfarray.get(music_num).getTitle());
            artist_tv.setText(mfarray.get(music_num).getArtist());
        } else if (plfarray.size() > 0){
            plname_tv.setText(plfarray.get(playlist_num).getName());
            title_tv.setText("-----");
            artist_tv.setText("-----");
        } else {
            plname_tv.setText("-----");
            title_tv.setText("-----");
            artist_tv.setText("-----");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initDrawer() {
        // （3本線の）切り替えボタンの生成
        vDrawerToggle = new ActionBarDrawerToggle(this, vDrawerLayout, R.string.app_name, R.string.app_name);
        vDrawerToggle.setDrawerIndicatorEnabled(true);
        vDrawerLayout.setDrawerListener(vDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return vDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        vDrawerToggle.onConfigurationChanged(newConfig);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean setPlaylist(String playlistpath){
        ArrayList<PlaylistFile> plflists = new ArrayList();
        return setPlaylistContent(playlistpath, plflists, 0);
    }

    public Boolean setPlaylist(ArrayList<PlaylistFile> plflists, int pos){
        String playlistpath = plflists.get(pos).getPath();
        return setPlaylistContent(playlistpath, plflists, pos);
    }

    private Boolean setPlaylistContent(String playlistpath, ArrayList<PlaylistFile> plflists, int pos){
        plfarray = plflists;
        playlist_num = pos;
        File file = new File(playlistpath);
        if(plflists.size() > 0){
            plf = plfarray.get(pos);
        } else {
            plf = new PlaylistFile();
            plf.extractFileName(file.getName());
            plf.setPath(playlistpath);
            plfarray.add(plf);
        }
        mfarray = new ArrayList();
        music_num = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str1 = br.readLine();
            String str2 = null;
            while(str1 != null){
                Log.v("clear_str1", str1);
                if(str1.indexOf("#EXTINF") == 0){
                    str2 = br.readLine();
                    Log.v("clear_str2", str2);
                    MusicFile mf = new MusicFile();
                    mf.extractPlayList(str1, str2);
                    mfarray.add(mf);
                }
                str1 = br.readLine();
            }
            br.close();
            return setPlayingInfo();
        } catch (FileNotFoundException e) {
            Log.v("error", "指定パスにファイルがありません。");
            plname_tv.setText("-----");
            title_tv.setText("-----");
            artist_tv.setText("-----");
        } catch (IOException e) {
            Log.v("error", "ファイルが読み込めません。");
        }
        return false;
    }

    public Boolean setPlayingInfo(){
        if(mfarray.size() > 0) {
            plname_tv.setText(plf.getName());
            title_tv.setText(mfarray.get(music_num).getTitle());
            artist_tv.setText(mfarray.get(music_num).getArtist());
            return true;
        } else {
            plf = null;
            plname_tv.setText(plfarray.get(playlist_num).getName());
            title_tv.setText("-----");
            artist_tv.setText("-----");
            return false;
        }
    }

    private void checkonPlayable(){
        if(!setPlayingInfo()) {
            ssButton.setImageResource(android.R.drawable.ic_media_play);
            playing = 0;
        } else if(playing == 1)
            audioPlay(mfarray.get(music_num).getPath());
        else
            playing = 0;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    /*public void audioPlayAndStop(){
        audioPlayAndStopContents(playing);
    }
    public void audioPlayAndStop(int chg_playing){
        audioPlayAndStopContents(1 - chg_playing);
    }*/
    public void audioPlayAndStop(){
        if(plf != null) {
            switch (playing){
                case 0:
                    ssButton.setImageResource(android.R.drawable.ic_media_pause);
                    playing = 1;
                    audioPlay(mfarray.get(music_num).getPath());
                    break;
                case 1:
                    ssButton.setImageResource(android.R.drawable.ic_media_play);
                    playing = 2;
                    audioPause();
                    break;
                case 2:
                    ssButton.setImageResource(android.R.drawable.ic_media_pause);
                    playing = 1;
                    audioContinue();
                    break;
            }
        }
    }

    private void audioPlay(String filepath) {
        // 繰り返し再生する場合
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            // リソースの解放
            mediaPlayer.release();
        }
        // インタンスを生成
        mediaPlayer = new MediaPlayer();
        //音楽ファイル名, あるいはパス
        try {
            // assetsから mp3 ファイルを読み込み
                //AssetFileDescriptor afdescripter = getAssets().openFd(filepath);
            //MediaPlayerに読み込んだ音楽ファイルを指定
                //mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                        //afdescripter.getStartOffset(),
                        //afdescripter.getLength());
            mediaPlayer.setDataSource(filepath);
            // 音量調整を端末のボタンに任せる
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // 再生する
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
    }

    private void audioStop() {
        if(mediaPlayer != null) {
            // 再生終了
            mediaPlayer.stop();
            // リセット
            mediaPlayer.reset();
            // リソースの解放
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void audioPause(){
        if(mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void audioContinue(){
        if(mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //再生終了検知
        frButton.callOnClick();
    }
}