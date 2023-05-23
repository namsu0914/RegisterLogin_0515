package com.example.registerlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private TextView tv_id, tv_pass;
    private Button btn_info;
    private Button btn_buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_id=findViewById(R.id.tv_id);
        //tv_pass=findViewById(R.id.tv_pass);
        btn_info=findViewById(R.id.btn_info);
        btn_buy=findViewById(R.id.btn_buy);

        //회원정보 버튼
        btn_info.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String userID = intent.getStringExtra("userID");

                intent = new Intent(MainActivity.this, BiometricActivity.class);

                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        //구매하기 버튼
        btn_buy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });


        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        tv_id.setText(userID);
        //tv_pass.setText(userPass);

    }

}