
package com.example.registerlogin;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class SignatureActivity extends BiometricActivity{
    private String challenge;
    //private PrivateKey privateKey;
    private PrivateKey pk;
    private static final String TAG = SignatureActivity.class.getSimpleName();

    public SignatureActivity(String challenge, PrivateKey privateKey) {
        this.challenge = challenge;
        //this.privateKey = privateKey;
        this.pk = privateKey;

    }

    public String signChallenge() {
        String signatureBytes="서명전";
        Log.e(TAG, "Sign메시지 : " + signatureBytes);

        try {
            // Signature 객체 생성
            Signature signature = Signature.getInstance("SHA256withRSA");
            // 개인키로 초기화
            signature.initSign(pk);

            // challenge를 바이트 배열로 변환하여 서명
            signature.update(challenge.getBytes());

            if (pk == null) {
                // 개인 키를 가져오는 데 실패한 경우 처리
                Log.e(TAG, "signatureactivity안에 개인키 못들어옴ㅜ ");
            }
            // 서명 생성
            signatureBytes = Base64.encodeToString(signature.sign(),Base64.DEFAULT);
            //byte[] signatureBytes = signature.sign();
            Log.e(TAG, "Sign메시지" + signatureBytes);
            //Toast.makeText(getApplicationContext(), "signed된 메시지1 : " + signatureBytes, Toast.LENGTH_SHORT).show();
            return signatureBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e(TAG, "NoSuchAlgorithmException");
            //Toast.makeText(getApplicationContext(), "Signature은 들어와지는데 안만들어짐", Toast.LENGTH_SHORT).show();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Log.e(TAG, "InvalidKeyException");
            //Toast.makeText(getApplicationContext(), "Signature은 들어와지는데 안만들어짐", Toast.LENGTH_SHORT).show();
            return null;
        }
        catch (SignatureException e) {
            e.printStackTrace();
            Log.e(TAG, "SignatureException");
            //Toast.makeText(getApplicationContext(), "Signature은 들어와지는데 안만들어짐", Toast.LENGTH_SHORT).show();
            return null;
        }


    }
    /*
    private KeyStore keyStore;
    private String encodedSignature;
    public SignatureActivity(String challenge, PrivateKey privateKey){
        try {

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(challenge.getBytes(StandardCharsets.UTF_8));
            byte[] signedData = signature.sign();
            encodedSignature = Base64.encodeToString(signedData, Base64.DEFAULT);
            Toast.makeText(getApplicationContext(), "signed된 메시지 : " + encodedSignature, Toast.LENGTH_SHORT).show();

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }
    public String getEncodedSignature() {
        return encodedSignature;
    }
    */

}

