package info.neet_ai.machi_kiku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nhaarman.listviewanimations.appearance.StickyListHeadersAdapterDecorator;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.nhaarman.listviewanimations.util.StickyListHeadersListViewWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class PlaylistEditAct extends CommonAct {
    private PlaylistFile plf = new PlaylistFile();
    private ArrayList<MusicFile> list = new ArrayList<>();
    private DynamicListView lv;
    private MusicListAdapter adapter;
    private AlphaInAnimationAdapter animAdapter;
    private EditText playlistname;
    private long itemid = -1;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        Log.v("clear", "A");
        lv = (DynamicListView) findViewById(R.id.listView);
        Log.v("clear", "B");
        adapter = new MusicListAdapter(this);
        Log.v("clear", "C");
        SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(adapter,
                this, new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull ViewGroup lv, @NonNull int[] reverseSortedPositions) {
            }
        });
        Log.v("clear", "D");
        AdapterChg();

        ///////////////////////////////////////////////////////////////////////////////////////////

        //ファイルダイアログ
        final OFDFrag ofdfrag = new OFDFrag();
        ofdfrag.addListChgListener(new ListChgListener() {
            @Override
            public void ArrayChg(MusicFile mf) {
                mf.setId(id_count());
                list.add(mf);
                adapter.setMusicList(list);
                lv.setAdapter(animAdapter);
                animAdapter.notifyDataSetChanged();
                //AdapterChg();
                //ofdfrag.list.remove(0);
            }
        });

        //削除確認ダイアログ
        final RemoveMusicFrag rmfrag = new RemoveMusicFrag();
        rmfrag.addListTapListener(new ListTapListener() {
            @Override
            public void RemoveMusic(int pos) {
                list.remove(pos);
                adapter.setMusicList(list);
                lv.setAdapter(animAdapter);
                animAdapter.notifyDataSetChanged();
            }
        });

        //アダプターリスナー
        adapter.addListRefreshListener(new MusicListAdapter.ListRefreshListener() {
            @Override
            public void ListChg() {
                lv.setAdapter(animAdapter);
                animAdapter.notifyDataSetChanged();
            }
        });

        /////////////////////////////////////////////////////////////////////////////////////////////

        /* Enable drag and drop functionality */
        //リストビューの操作
        lv.enableDragAndDrop();
        lv.setDraggableManager(new TouchViewDraggableManager(R.id.back_button));
        lv.setOnItemMovedListener(new OnItemMovedListener() {
            @Override
            public void onItemMoved(int originalPosition, int newPosition) {
                // リストの並び替え後に実行される
                Log.v("clear_B", String.valueOf(originalPosition) + "→" + String.valueOf(newPosition));
                list = adapter.musicList;
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (lv != null) {
                    lv.startDragging(position);
                    return true;
                }
                return false;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                rmfrag.listpos = pos;
                rmfrag.show(getFragmentManager(), "remove_music");
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////

        //プレイリスト名
        playlistname = (EditText) findViewById(R.id.playlist_editname_top);

        //＋ボタン押下
        final ImageButton add_m_b = (ImageButton) findViewById(R.id.add_music_button);
        add_m_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ofdfrag.show(getFragmentManager(), "file_opener");
            }
        });

        //決定ボタン押下
        final ImageButton finish_e_b = (ImageButton) findViewById(R.id.finish_edit_button);
        finish_e_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("clear_A", plf.getPath());
                if (!plf.getPath().equals("")) {
                    //編集とは別に複製コマンドをつくるなら有効にする
                    //deleteFile(plf.makeFileName());
                }
                String PlaylistData = "#EXTM3U";
                for (MusicFile i : list) {
                    PlaylistData += i.makePlayList();
                }
                //plf = new PlaylistFile();
                plf.setName(playlistname.getText().toString());
                plf.setCreator("アカウント名");
                FileOutputStream fileOutputstream = null;
                try {
                    String file = plf.makeFileName();
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected void onResume() {
        super.onResume();
        String filepath = getIntent().getStringExtra("playlist_filepath");
        getExistPlaylist(filepath);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void getExistPlaylist(String playlistpath) {
        if (playlistpath.equals("")) {
            this.plf.setPath("");
            return;
        }

        this.list = new ArrayList();
        File file = new File(playlistpath);
        this.plf.extractFileName(file.getName());
        this.plf.setPath(playlistpath);
        try {
            playlistname.setText(this.plf.getName());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str1 = br.readLine();
            String str2 = null;
            while (str1 != null) {
                Log.v("clear_str1", str1);
                if (str1.indexOf("#EXTINF") == 0) {
                    str2 = br.readLine();
                    Log.v("clear_str2", str2);
                    MusicFile mf = new MusicFile();
                    mf.extractPlayList(str1, str2);
                    mf.setId(id_count());
                    this.list.add(mf);
                }
                str1 = br.readLine();
            }
            //AdapterChg();
            adapter.setMusicList(list);
            lv.setAdapter(animAdapter);
            animAdapter.notifyDataSetChanged();
            br.close();
        } catch (FileNotFoundException e) {
            Log.v("error", "指定パスにファイルがありません。");
        } catch (IOException e) {
            Log.v("error", "ファイルが読み込めません。");
        }
    }

    private void AdapterChg() {
        this.adapter.setMusicList(this.list);   //更新時やや必須
        this.animAdapter = new AlphaInAnimationAdapter(adapter);
        animAdapter.setAbsListView(lv);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(400);
        this.lv.setAdapter(this.animAdapter);   //更新時必須
        this.animAdapter.notifyDataSetChanged();    //更新時必須
    }

    private long id_count(){
        itemid++;
        Log.v("clear", String.valueOf(itemid));
        return itemid;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PlaylistEditAct Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://info.neet_ai.machi_kiku/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PlaylistEditAct Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://info.neet_ai.machi_kiku/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface ListChgListener {
        void ArrayChg(MusicFile mf);
    }

    public static class OFDFrag extends DialogFragment {
        //public ArrayList<MusicFile> list = new ArrayList<>();
        ListChgListener listener;

        public void addListChgListener(ListChgListener listener) {
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
                    if (suffix.equals("mp3") || suffix.equals("m4a") || suffix.equals("rc")) {
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface ListTapListener{
        void RemoveMusic(int pos);
    }

    public static class RemoveMusicFrag extends DialogFragment {
        //public ArrayList<MusicFile> list = new ArrayList<>();
        ListTapListener listener;
        public int listpos = 0;

        public void addListTapListener(ListTapListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return  new AlertDialog.Builder(getActivity())
                    .setTitle("確認")
                    .setMessage("この曲をプレイリストから削除しますか？")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // OK button pressed
                            listener.RemoveMusic(listpos);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        @Override
        public void onPause() {
            super.onPause();
            // onPause でダイアログを閉じる場合
            dismiss();
        }
    }


}
