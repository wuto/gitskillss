package com.example.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.bean.Festival;
import com.example.myapplication.bean.FestivalLab;
import com.example.myapplication.bean.Msg;
import com.example.myapplication.bean.SendedMsg;
import com.example.myapplication.biz.SmsBiz;
import com.example.myapplication.view.FlowLayout;

import java.util.HashSet;

public class SendMsgActivity extends AppCompatActivity {

    public static final String KEY_FESTIVALID = "festivalId";
    public static final String KEY_MSGID = "msgId";
    private static final int CODE_RESULT = 1;
    private int mFestivalId;
    private int mMsgId;

    private EditText mEdMsg;
    private Button mBtnAdd;
    private FlowLayout mFlContacts;
    private FloatingActionButton mFabSend;
    private View mLayoutLoading;
    private Msg mMsg;
    private Festival mFestival;


    private HashSet<String> mContactNames = new HashSet<>();
    private HashSet<String> mContactNums = new HashSet<>();

    private LayoutInflater mInflater;

    private static final String ACTION_SEND_MSG = "ACTION_SEND_MSG";
    private static final String ACTION_DELIVER_MSG = "ACTION_DELIVER_MSG";

    private PendingIntent mSendPi;
    private PendingIntent mDeliverPi;

    private BroadcastReceiver mSendBroadcastReceiver;
    private BroadcastReceiver mDeliverBroadcastReceiver;

    private SmsBiz mSmsBiz;


    private int mMsgSendCount = 0;//记录发送的短信数量；
    private int mTotalCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);
        mInflater = LayoutInflater.from(this);
        mSmsBiz = new SmsBiz(this);
        initData();
        initViews();
        initEvent();
        initReceivers();

    }

    private void initReceivers() {
        Intent sendIntent = new Intent(ACTION_SEND_MSG);
        mSendPi = PendingIntent.getBroadcast(this, 0, sendIntent, 0);
        Intent deliverIntent = new Intent(ACTION_DELIVER_MSG);
        mDeliverPi = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);

        registerReceiver(mSendBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mMsgSendCount++;
                if (getResultCode() == RESULT_OK) {

                    Log.d("TAG", "短信发送成功" + (mMsgSendCount + "/" + mTotalCount));
                } else {
                    Log.d("TAG", "短信发送失败");
                }

                Toast.makeText(SendMsgActivity.this, (mMsgSendCount + "/" + mTotalCount) + "短信发送成功", Toast.LENGTH_SHORT).show();

                if (mMsgSendCount == mTotalCount) {
                    finish();
                }


            }
        }, new IntentFilter(ACTION_SEND_MSG));

        registerReceiver(mDeliverBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == RESULT_OK) {
                    Log.d("TAG", "短信接受成功" + (mMsgSendCount + "/" + mTotalCount));
                } else {
                    Log.d("TAG", "短信接受失败");
                }

            }
        }, new IntentFilter(ACTION_DELIVER_MSG));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSendBroadcastReceiver);
        unregisterReceiver(mDeliverBroadcastReceiver);


    }

    private void initEvent() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CODE_RESULT);
            }
        });

        mFabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContactNums.size() == 0) {
                    Toast.makeText(SendMsgActivity.this, "请先选择联系人", Toast.LENGTH_SHORT).show();
                    return;
                }

                String msg = mEdMsg.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(SendMsgActivity.this, "短信内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLayoutLoading.setVisibility(View.VISIBLE);

                mTotalCount = mSmsBiz.sendMsg(mContactNums, buildSendMsg(msg), mSendPi, mDeliverPi);
                mMsgSendCount = 0;


            }
        });

    }

    private SendedMsg buildSendMsg(String msg) {
        SendedMsg sendmsg = new SendedMsg();

        sendmsg.setMsg(msg);
        sendmsg.setFestivalName(mFestival.getName());
        String names = "";
        for (String name : mContactNames) {
            names += name + ":";
        }
        sendmsg.setNames(names.substring(0, names.length() - 1));

        String numbers = "";
        for (String number : mContactNums) {
            numbers += number + ":";
        }
        sendmsg.setNumber(numbers.substring(0, numbers.length() - 1));

        return sendmsg;
    }

    private void initViews() {
        mEdMsg = (EditText) findViewById(R.id.id_et_content);
        mBtnAdd = (Button) findViewById(R.id.id_btn_add);
        mFlContacts = (FlowLayout) findViewById(R.id.id_fl_contacts);
        mFabSend = (FloatingActionButton) findViewById(R.id.id_fab_send);
        mLayoutLoading = findViewById(R.id.id_layout_loading);
        mLayoutLoading.setVisibility(View.GONE);

        if (mMsgId != -1) {
            mMsg = FestivalLab.getInstance().getMsgByMsgId(mMsgId);
            mEdMsg.setText(mMsg.getContent());
        }


    }

    private void initData() {
        mFestivalId = getIntent().getIntExtra(KEY_FESTIVALID, -1);
        mMsgId = getIntent().getIntExtra(KEY_MSGID, -1);
        mFestival = FestivalLab.getInstance().getFestivalById(mFestivalId);
        setTitle(mFestival.getName());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_RESULT) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                cursor.moveToFirst();
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = getContactNumber(cursor);
                if (!TextUtils.isEmpty(number)) {
                    mContactNames.add(contactName);
                    mContactNums.add(number);
                    addTag(contactName);
                }
            }

        }
    }

    private void addTag(String contactName) {
        TextView view = (TextView) mInflater.inflate(R.layout.tag, mFlContacts, false);
        view.setText(contactName);
        mFlContacts.addView(view);

    }

    private String getContactNumber(Cursor cursor) {
        int numberCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        String number = null;
        if (numberCount > 0) {
            String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID, null, null);
            phoneCursor.moveToFirst();
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneCursor.close();
        }
        cursor.close();
        return number;
    }

    public static void toActivity(Context context, int festivalId, int msgId) {
        Intent intent = new Intent(context, SendMsgActivity.class);
        intent.putExtra(KEY_FESTIVALID, festivalId);
        intent.putExtra(KEY_MSGID, msgId);
        context.startActivity(intent);
    }


}
