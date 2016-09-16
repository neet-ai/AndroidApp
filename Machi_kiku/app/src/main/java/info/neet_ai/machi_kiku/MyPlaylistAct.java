package info.neet_ai.machi_kiku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MyPlaylistAct extends CommonAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.myplaylist_layout);
        super.onCreate(savedInstanceState);

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
}
