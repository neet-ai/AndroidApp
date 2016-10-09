package info.neet_ai.machi_kiku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class PlaylistEditAct extends CommonAct{
    private PlaylistFile plf = new PlaylistFile();
    private ArrayList<MusicFile> list = new ArrayList<>();
    private ListView lv;
    private MusicListAdapter adapter;
    private EditText playlistname;

    public static String getSuffix(String fileName) {
        if (fileName == null) return "null";
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return "null";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.playlist_edit_layout);
        super.onCreate(savedInstanceState);

        lv = (ListView) findViewById(R.id.listView);
         adapter = new MusicListAdapter(this);

        //ファイルダイアログ
        final OFDFrag ofdfrag = new OFDFrag();
        ofdfrag.addListChgListener(new ListChgListener() {
            @Override
            public void ArrayChg(MusicFile mf) {
                list.add(mf);
                adapter.setMusicList(list);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //ofdfrag.list.remove(0);
            }
        });

        //プレイリスト名
        playlistname = (EditText)findViewById(R.id.playlist_editname_top);

        //＋ボタン押下
        final ImageButton add_m_b = (ImageButton)findViewById(R.id.add_music_button);
        add_m_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ofdfrag.show(getFragmentManager(), "file_opener");
            }
        });

        //決定ボタン押下
        final ImageButton finish_e_b = (ImageButton)findViewById(R.id.finish_edit_button);
        finish_e_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PlaylistData = "#EXTM3U";
                for(MusicFile i : list){
                    PlaylistData += i.makePlayList();
                }
                plf = new PlaylistFile();
                plf.setName(playlistname.getText().toString());
                plf.setCreator("アカウント名");
                FileOutputStream fileOutputstream = null;
                try {
                    String file =  plf.makeFileName();
                    fileOutputstream = openFileOutput(file, Context.MODE_PRIVATE);
                    fileOutputstream.write(PlaylistData.getBytes());
                    fileOutputstream.close();
                    Log.v("clear_PlaylistData", PlaylistData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected void onResume() {
        super.onResume();
        String filepath = getIntent().getStringExtra("playlist_filepath");
        getExistPlaylist(filepath);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void getExistPlaylist(String playlistpath){
        this.plf = new PlaylistFile();
        this.list = new ArrayList();

        File file = new File(playlistpath);
        this.plf.extractFileName(file.getName());
        this.plf.setPath(playlistpath);
        try {
            playlistname.setText(this.plf.getName());
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
                    this.list.add(mf);
                }
                str1 = br.readLine();
            }
            this.adapter.setMusicList(this.list);
            this.lv.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
            br.close();
        } catch (FileNotFoundException e) {
            Log.v("error", "指定パスにファイルがありません。");
        } catch (IOException e) {
            Log.v("error", "ファイルが読み込めません。");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface ListChgListener{
        void ArrayChg(MusicFile mf);
    }

    public static class OFDFrag extends DialogFragment{
        //public ArrayList<MusicFile> list = new ArrayList<>();
        ListChgListener listener;

        public void addListChgListener(ListChgListener listener){
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.openFileAction = new OpenFileDialog.OpenFileAction() {
                @Override
                public File write(File file) {
                    return null;
                }
                @Override
                public File append(File file) {
                    return null;
                }
                @Override
                public void read(File file) {
                }
                @Override
                public void getpath(String filepath) {
                    Log.v("clear_PEA_filepath", filepath);
                    String suffix = getSuffix(filepath);
                    //Log.v("clear", suffix);
                    if(suffix.equals("mp3") || suffix.equals("m4a") || suffix.equals("rc")){
                        MusicFile mf = new MusicFile();
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(filepath);
                        mf.setTitle(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                        mf.setArtist(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                        mf.setPath(filepath);
                        mf.setLength(0);
                        //Log.v("clear", mf.getTitle());
                        //mf.setTitle(filepath);
                        //mf.setArtist("dummy");
                        //list.add(mf);
                        listener.ArrayChg(mf);
                    }
                }
            };
            AlertDialog.Builder builder = null;
            try {
                builder = ofd.createOpenFileDialog(getActivity(), OpenFileDialog.OpenMode.Getpath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.create();
        }
    }
}
