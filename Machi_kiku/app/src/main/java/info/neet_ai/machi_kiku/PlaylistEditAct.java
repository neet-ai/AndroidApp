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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class PlaylistEditAct extends CommonAct{
    public ArrayList<MusicFile> list = new ArrayList<>();

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

        final ListView lv = (ListView) findViewById(R.id.listView);
        final MusicListAdapter adapter = new MusicListAdapter(this);

        //ファイルダイアログ
        final OFDFrag ofdfrag = new OFDFrag();
        ofdfrag.addListChgListener(new ListChgListener() {
            @Override
            public void ArrayChg(ArrayList<MusicFile> list) {
                list.add(ofdfrag.list.get(0));
                adapter.setMusicList(list);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                ofdfrag.list.remove(0);
            }
        });

        //プレイリスト名
        final EditText playlistname = (EditText)findViewById(R.id.playlist_editname_top);

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
                FileOutputStream fileOutputstream = null;
                try {
                    String file = playlistname.getText() + " - made by " + "アカウント名" + ".M3U";
                    fileOutputstream = openFileOutput(file, Context.MODE_PRIVATE);
                    fileOutputstream.write(PlaylistData.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    public interface ListChgListener{
        void ArrayChg(ArrayList<MusicFile> list);
    }

    public static class OFDFrag extends DialogFragment{
        public ArrayList<MusicFile> list = new ArrayList<>();
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
                    Log.v("clear", filepath);
                    String suffix = getSuffix(filepath);
                    Log.v("clear", suffix);
                    if(suffix.equals("mp3") || suffix.equals("m4a") || suffix.equals("rc")){
                        MusicFile mf = new MusicFile();
                        /*MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(filepath);
                        mf.setTitle(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                        mf.setArtist(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                        Log.v("clear", mf.getTitle());*/
                        mf.setTitle(filepath);
                        mf.setArtist("dummy");
                        list.add(mf);
                        listener.ArrayChg(list);
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
