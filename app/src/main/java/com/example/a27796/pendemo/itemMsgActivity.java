package com.example.a27796.pendemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class itemMsgActivity extends AppCompatActivity {
    TextView msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_msg);

        msg = findViewById(R.id.msg);
        Intent i = getIntent();
        String s = i.getStringExtra("bluetooth");
        msg.setText(s);
    }
}
