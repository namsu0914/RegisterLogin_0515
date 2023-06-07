package com.example.registerlogin;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class MainActivity extends AppCompatActivity {
    private TextView tv_id, tv_pass;
    private Button btn_info;
    private Button btn_buy;
    private static final String KEY_NAME = "my_key1";
    private KeyStore keyStore;
    PrivateKey privateKey;
    private DBHelper dbHelper;
    String SignString;

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_id = findViewById(R.id.tv_id);
        //tv_pass=findViewById(R.id.tv_pass);
        btn_info = findViewById(R.id.btn_info);
        btn_buy = findViewById(R.id.btn_buy);

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

        //dbHelper객체 초기화
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        //구매하기 버튼
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("challenge")) {
                                String challenge = jsonObject.getString("challenge");

//                                //개인키 갖고와서 서명생성
//                                keyStore = KeyStore.getInstance("AndroidKeyStore");
//                                keyStore.load(null);
//                                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_NAME, null);
//                                privateKey = privateKeyEntry.getPrivateKey();
//                                SignatureActivity signatureActivity= new SignatureActivity(challenge, privateKey, entry);
//                                String signedChallenge = signatureActivity.getEncodedSignature();

                                //키 삭제
//                                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
//                                keyStore.load(null, null);
//                                keyStore.deleteEntry(KEY_NAME);

//                                //개인키 길이 확인
//                                byte[] privateKeyBytes = privateKey.getEncoded();
//                                int privateKeySize = privateKeyBytes.length;
                                Intent intent = getIntent();
                                String userID = intent.getStringExtra("userID");

                                //String이던 개인키를 PrivateKey형식으로 다시 변환
                                String privateKeyString = getUserSecretKey(userID);
                                byte[] privateKeyBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
                                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                                PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

                                SignatureActivity signatureActivity = new SignatureActivity(challenge, privateKey);
                                byte[] encodedSignature = signatureActivity.getEncodedSignature();
                                SignString = Base64.encodeToString(encodedSignature, Base64.DEFAULT);

                                Log.d(TAG, "signature: " + SignString);

                                sendSignatureToServer(SignString,userID);
                            } else {
                                Log.e(TAG, "Challenge key not found in JSON response");
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "오류가 발생하였습니다. ", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeySpecException e) {
                            throw new RuntimeException(e);
                        }

                    }
                };
                BuyRequest buyRequest = new BuyRequest(responseListner);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(buyRequest);

            }

            //생성된 서명 서버로 전송하는 함수
            private void sendSignatureToServer (String signString, String userID){
                Response.Listener<String> verifyResponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses) {
                        try {
                            JSONObject jsonObject = new JSONObject(responses);
                            if (jsonObject.has("StringpublicKey")) {
                                Log.d(TAG, "검증 되었습니다");
                            }else{
                                Log.d(TAG, "검증실패");
                            }


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                tv_id.setText(userID);
                RequestQueue queue_v = Volley.newRequestQueue(MainActivity.this);
                VerifyRequest verifyRequest = new VerifyRequest(signString, userID, verifyResponseListener);
                queue_v.add(verifyRequest);

            }
        });
    }

    //db에서 조회하는 함수
    private String getUserSecretKey(String userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"sk"};
        String selection = "userID = ?";
        String[] selectionArgs = {userID};

        Cursor cursor = db.query("sk", projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int skColumnIndex = cursor.getColumnIndex("sk");
            String sk = cursor.getString(skColumnIndex);
            cursor.close();
            return sk;
        } else {
            cursor.close();
            return null;
        }
    }

}