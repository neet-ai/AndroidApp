package info.neet_ai.machi_kiku;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CommonAct extends ActionBarActivity{
    private DrawerLayout vDrawerLayout;
    private ActionBarDrawerToggle vDrawerToggle;
    private ListView vListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

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
}