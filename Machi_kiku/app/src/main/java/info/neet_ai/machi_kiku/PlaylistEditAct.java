package info.neet_ai.machi_kiku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class PlaylistEditAct extends CommonAct{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.playlist_edit_layout);
        super.onCreate(savedInstanceState);

        //＋ボタン押下
        final ImageButton add_m_b = (ImageButton)findViewById(R.id.add_music_button);
        add_m_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "pushed", Toast.LENGTH_LONG).show();
                OFDFrag ofdfrag = new OFDFrag();
                ofdfrag.show(getFragmentManager(), "file_opener");
            }
        });
    }

    public static class OFDFrag extends DialogFragment implements OpenFileDialog.OpenFileAction {

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
            Log.v("clear", "readin");
            String filename = file.toString();
            Toast.makeText(getActivity(), filename, Toast.LENGTH_LONG).show();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            OpenFileDialog ofd = new OpenFileDialog();
            AlertDialog.Builder builder = null;
            try {
                builder = ofd.createOpenFileDialog(getActivity(), OpenFileDialog.OpenMode.Read);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.create();
        }
    }
}
