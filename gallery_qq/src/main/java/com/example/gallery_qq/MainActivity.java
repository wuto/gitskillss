package com.example.gallery_qq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private CustomerGallery gvMain;
    private MyBaseAdapter myBaseAdapter;
    private List<CustomerBean> mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gvMain = (CustomerGallery) findViewById(R.id.gv_main);
        initData();

    }

    private void initData() {
        mDatas = new ArrayList<>();
        CustomerBean c1 = new CustomerBean();
        c1.setNumber("0.01%");
        c1.setTitle("天天贏");

        CustomerBean c2 = new CustomerBean();
        c2.setNumber("0.05%");
        c2.setTitle("周周贏");

        CustomerBean c3 = new CustomerBean();
        c3.setNumber("0.08%");
        c3.setTitle("半月贏");

        CustomerBean c4 = new CustomerBean();
        c4.setNumber("1.11%");
        c4.setTitle("月月贏");

        CustomerBean c5 = new CustomerBean();
        c5.setNumber("2.05%");
        c5.setTitle("三月贏");

        CustomerBean c6 = new CustomerBean();
        c6.setNumber("4.05%");
        c6.setTitle("季度贏");

        CustomerBean c7 = new CustomerBean();
        c7.setNumber("4.25%");
        c7.setTitle("半年贏");

        CustomerBean c8 = new CustomerBean();
        c8.setNumber("4.55%");
        c8.setTitle("年年贏");

        CustomerBean c9 = new CustomerBean();
        c9.setNumber("5.25%");
        c9.setTitle("两年贏");

        CustomerBean c10 = new CustomerBean();
        c10.setNumber("5.55%");
        c10.setTitle("三年贏");

        CustomerBean c11 = new CustomerBean();
        c11.setNumber("5.85%");
        c11.setTitle("四年贏");

        CustomerBean c12 = new CustomerBean();
        c12.setNumber("6.00%");
        c12.setTitle("五年贏");

        CustomerBean c13 = new CustomerBean();
        c13.setNumber("6.50%");
        c13.setTitle("八年贏");

        CustomerBean c14 = new CustomerBean();
        c14.setNumber("100%");
        c14.setTitle("终身贏");
        mDatas.add(c1);
        mDatas.add(c2);
        mDatas.add(c3);
        mDatas.add(c4);
        mDatas.add(c5);
        mDatas.add(c6);
        mDatas.add(c7);
        mDatas.add(c8);
        mDatas.add(c9);
        mDatas.add(c10);
        mDatas.add(c11);
        mDatas.add(c12);
        mDatas.add(c13);
        mDatas.add(c14);
        myBaseAdapter = new MyBaseAdapter();
        gvMain.setAdapter(myBaseAdapter);
        gvMain.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.e("onItemSelected"+position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        LogUtil.e("onNothingSelected");
    }

    private class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {
               holder= (ViewHolder) convertView.getTag();
            }
            LogUtil.e("getView" + position);
            holder.tvNumber.setText(mDatas.get(position).getNumber()+"");
            holder.tvTitle.setText(mDatas.get(position).getTitle());
            return convertView;
        }
    }


    private class ViewHolder {
        private TextView tvNumber;
        private TextView tvTitle;
        public ViewHolder(View view) {
            tvNumber = (TextView) view.findViewById(R.id.tv_number);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }

}
