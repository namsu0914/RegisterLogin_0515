
package com.example.registerlogin;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class SignatureActivity extends AppCompatActivity {

    public byte[] encodedSignature;
    private static final String TAG = "SignatureActivity";

    //전자서명 생성하는 함수
    public SignatureActivity(String challenge, PrivateKey privateKey){
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);

            Log.d(TAG, "initSign까진 됨");
            signature.update(challenge.getBytes());
            encodedSignature = signature.sign();
            if(encodedSignature == null){
                Toast.makeText(getApplicationContext(), "encodedSignature is null", Toast.LENGTH_SHORT).show();
            }else{
                Log.d(TAG, "Signature생성 완료");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }

    public byte[] getEncodedSignature() {
        return encodedSignature;
    }


}

