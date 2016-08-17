package com.example.jd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ScaleMoney.MoveScaleInterface {
    private TextView tvValue;
    private ScaleMoney svView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvValue = (TextView) findViewById(R.id.tv_value);
        svView = (ScaleMoney) findViewById(R.id.sm_view);
        svView.setMoveScaleInterface(this);

    }

    @Override
    public void getValue(int value) {
        tvValue.setText(value + "");
    }
}
