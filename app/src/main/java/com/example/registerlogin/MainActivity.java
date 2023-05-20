package com.example.registerlogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private TextView tv_id, tv_pass;
    private Button btn_enroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_id=findViewById(R.id.tv_id);
        //tv_pass=findViewById(R.id.tv_pass);
        btn_enroll=findViewById(R.id.btn_enroll);

        btn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String userID = intent.getStringExtra("userID");

                intent = new Intent(MainActivity.this, BiometricActivity.class);

                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });


        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        //String userPass = intent.getStringExtra("userPass");

        tv_id.setText(userID);
        //tv_pass.setText(userPass);

    }
}