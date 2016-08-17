package com.imooc.wuziqi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    private WuziqiPanel mWuziqiPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWuziqiPanel= (WuziqiPanel) findViewById(R.id.id_wuziqi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.id_menu_start){
            mWuziqiPanel.start();
            return  true;
        }else if (id==R.id.id_menu_regret)
        {
            mWuziqiPanel.regret();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}
