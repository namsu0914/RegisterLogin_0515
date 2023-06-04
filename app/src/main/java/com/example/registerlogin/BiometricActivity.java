package com.example.registerlogin;


import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

public class BiometricActivity extends AppCompatActivity {

    private CancellationSignal cancellationSignal = null;
    private BiometricPrompt.AuthenticationCallback authenticationCallback;
    private Button start_authentication;

    private static final String KEY_NAME = "my_key";
    private KeyStore keyStore;
    private PublicKey publicKey;
    public KeyPair pair;
    private static final String TAG = "BiometricActivity";

    @TargetApi(Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);

        authenticationCallback = new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error: " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("인증에 성공하였습니다");
                // 처리를 진행하세요
                //generateKeyPair();
                // 키 쌍 생성 확인 코드 추가
                checkKeyPairExistence();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed");
            }
        };

        start_authentication = findViewById(R.id.start_authentication);
        start_authentication.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                if (checkBiometricSupport()) {
                    BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(BiometricActivity.this)
                            .setTitle("Title of Prompt")
                            .setSubtitle("Subtitle")
                            .setDescription("Uses FP")
                            .setNegativeButton("Cancel", getMainExecutor(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    notifyUser("Authentication Cancelled");
                                }
                            }).build();

                    biometricPrompt.authenticate(getCancellationSignal(), getMainExecutor(), authenticationCallback);
                }
            }
        });

    }
    private void checkKeyPairExistence() {

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        Response.Listener<String> responseListner= new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    //JSONObject jsonObject= new JSONObject(response);
                    //boolean success= jsonObject.getBoolean("success"); // 서버통신 잘 됐냐?
                    //String publicKey = jsonObject.getString("publicKey");
                    String publicKey = response;
                    if ((publicKey.length() > 5)) {
                        // 공개 키와 개인 키 출력
                        //String publicKey = jsonObject.getString("publicKey");
                        Log.d(TAG, "Public Key: " + publicKey);
                        Toast.makeText(getApplicationContext(), "이미 저장된 생체정보입니다. ", Toast.LENGTH_SHORT).show();
                        //generateKeyPair();
                    }else{
                        Log.d(TAG, "Key pair not found");
                        generateKeyPair();
                    }
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        CheckPKRequest checkpkrequest = new CheckPKRequest(userID, responseListner);
        RequestQueue queue = Volley.newRequestQueue(BiometricActivity.this);
        queue.add(checkpkrequest);

//            keyStore = KeyStore.getInstance("AndroidKeyStore");
//            keyStore.load(null);
//
//            if (keyStore.containsAlias(KEY_NAME)) {
//                // 키 쌍이 존재함
//                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_NAME, null);
//                publicKey = privateKeyEntry.getCertificate().getPublicKey();
//                keyStore.deleteEntry(KEY_NAME);
//                // 공개 키와 개인 키 출력
//                Log.d(TAG, "Public Key: " + Base64.encodeToString(publicKey.getEncoded(),Base64.DEFAULT));
//                Toast.makeText(getApplicationContext(), "이미 저장된 생체정보입니다. ", Toast.LENGTH_SHORT).show();
//                //generateKeyPair();
//            } else {
//                // 키 쌍이 존재하지 않음
//                Log.d(TAG, "Key pair not found");
//                generateKeyPair();
//            }
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom();
        // Random 키를 이용해서 키페어 생성 준비
        keyPairGenerator.initialize(2048,secureRandom);

        // 키페어에 암호화된 내용을 담기
        pair = keyPairGenerator.generateKeyPair();

        // 공개키 획득
        PublicKey publicKey = pair.getPublic();

        // 공개키를 문자열로 출력
        String publicKeyString = Base64.encodeToString(publicKey.getEncoded(),Base64.DEFAULT);

        //System.out.println("public key = "+ publicKeyString);
        Log.d(TAG, "공개키: "+publicKeyString);
        // 개인키 획득
        PrivateKey privateKey = pair.getPrivate();
        // 개인키를 문자열로 출력
        String privateKeyString = Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);

        //System.out.println("private key = "+ privateKeyString);
        Log.d(TAG, "비밀키: "+ privateKeyString);

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    boolean success= jsonObject.getBoolean("success"); // 서버통신 잘 됐냐?
                    if (success) {
                        Toast.makeText(getApplicationContext(), "서버통신성공", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(BiometricActivity.this, BiometricActivity.class); //성공 후 열 페이지
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "서버 실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "오류 발생", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        };
            //db로 공개키 전송
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        Log.d(TAG, "ID: "+ userID);
        SavePKRequest savepkRequest = new SavePKRequest(publicKeyString,userID, responseListner);
        RequestQueue queue = Volley.newRequestQueue(BiometricActivity.this);
        queue.add(savepkRequest);
        Toast.makeText(getApplicationContext(), "공개키 저장이 완료되었습니다. ", Toast.LENGTH_SHORT).show();

    }

    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> notifyUser("Authentication was Cancelled by the user"));
        return cancellationSignal;
    }

    @TargetApi(Build.VERSION_CODES.P)
    private Boolean checkBiometricSupport() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isDeviceSecure()) {
            notifyUser("Fingerprint authentication has not been enabled in settings");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint Authentication Permission is not enabled");
            return false;
        }
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true;
        } else {
            return false;
        }
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}