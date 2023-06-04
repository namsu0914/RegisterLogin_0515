package com.example.registerlogin;

import android.content.Intent;
import android.os.Bundle;
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

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

public class MainActivity extends AppCompatActivity{
    private TextView tv_id;
    private Button btn_info;
    private Button btn_buy;
    private static final String KEY_NAME = "my_key";
    private KeyStore keyStore;
    PrivateKey privateKey;
    private static final String ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore";
    //private static final String KEY_ALIAS = "my_key_alias";
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_id=findViewById(R.id.tv_id);
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
                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject= new JSONObject(response);

                            if(jsonObject.has("challenge")){
                                String challenge = jsonObject.getString("challenge");
                                Log.d(TAG, "챌린지값: "+challenge);

                                keyStore = KeyStore.getInstance("AndroidKeyStore");
                                keyStore.load(null);


                                boolean hasAccess = checkPrivateKeyAccess(KEY_NAME);
                                if (hasAccess) {
                                    // 개인 키에 액세스할 수 있는 권한이 있음
                                    Log.d(TAG, "개인 키에 액세스할 수 있는 권한이 있음");
                                } else {
                                    // 개인 키에 액세스할 수 있는 권한이 없음
                                    Log.d(TAG, "개인 키에 액세스할 수 있는 권한이 없음");

                                }


                                if (keyStore.containsAlias(KEY_NAME)) {
                                    // 키 쌍이 존재함

                                    // 공개 키와 개인 키 출력
                                    Log.d(TAG, "키스토어 저장됨");

                                } else {
                                    // 키 쌍이 존재하지 않음
                                    Log.d(TAG, "Key pair not found");
                                }

                                KeyStore.Entry entry = keyStore.getEntry(KEY_NAME, null);

                                if (entry instanceof KeyStore.PrivateKeyEntry) {
                                    // 개인키가 초기화되었으므로 접근 가능
                                    Log.d(TAG, "개인키 초기화됨");
                                } else {
                                    // 개인키가 초기화되지 않았으므로 접근 불가능
                                    Log.d(TAG, "개인키 초기화 안됨");
                                }




                                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_NAME, null);

                                privateKey = privateKeyEntry.getPrivateKey();
//                                byte[] privateKeyBytes = privateKey.getEncoded();
//                                int privateKeySize = privateKeyBytes.length;
//                                Log.e(TAG, "Keysize : " + privateKeySize);


                                //Log.e(TAG, "개인키" + privateKey);
                                if (privateKey == null) {
                                    // 개인 키를 가져오는 데 실패한 경우 처리
                                    Log.e(TAG, "Failed to retrieve private key from KeyStore");
                                    return;
                                }



                                SignatureActivity signatureActivity= new SignatureActivity(challenge, privateKey);
                                String signedChallenge = signatureActivity.signChallenge();
                                Toast.makeText(getApplicationContext(), "signed된 메시지2 : " + signedChallenge, Toast.LENGTH_SHORT).show();


                            }else {
                                Log.e(TAG, "Challenge key not found in JSON response");
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "오류가 발생하였습니다. ", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        } catch (CertificateException | KeyStoreException | IOException |
                                 NoSuchAlgorithmException | UnrecoverableEntryException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                BuyRequest buyRequest = new BuyRequest(responseListner);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(buyRequest);
            }
        });


        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        tv_id.setText(userID);
    }
    private boolean checkPrivateKeyAccess(String keyAlias) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            return keyStore.entryInstanceOf(keyAlias, KeyStore.PrivateKeyEntry.class);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}