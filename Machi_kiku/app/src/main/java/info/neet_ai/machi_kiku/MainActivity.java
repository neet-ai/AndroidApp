package info.neet_ai.machi_kiku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    int counter = 0;
    int names[] = {R.drawable.chika, R.drawable.dia, R.drawable.hanamaru,
            R.drawable.kanan, R.drawable.mari, R.drawable.riko,
            R.drawable.ruby, R.drawable.yoshiko, R.drawable.you};
    long starttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        starttime = System.currentTimeMillis();

        final ImageButton img_b = (ImageButton)findViewById(R.id.imageButton);
        img_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                if(counter > 8) counter = 0;
                img_b.setImageResource(names[counter]);
            }
        });

        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    // トグルがドラッグされると呼ばれる
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        counter = progress;
                        img_b.setImageResource(names[counter]);
                    }

                    // トグルがタッチされた時に呼ばれる
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    // トグルがリリースされた時に呼ばれる
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });
    }
}
