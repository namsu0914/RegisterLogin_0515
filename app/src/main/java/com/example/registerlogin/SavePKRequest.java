package com.example.registerlogin;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class SavePKRequest extends StringRequest{
    final static private String URL = "http://192.168.0.5:80/SavePK.php";
    final private Map<String , String> map;
    private static final String TAG = "SavePKRequest";

    public SavePKRequest(String userID, String publicKey, Response.Listener<String> listener,Response.ErrorListener errorListener){
        super(Request.Method.POST, URL, listener, errorListener);
        Log.d(TAG, "리퀘스트 id :  "+ userID);
        Log.d(TAG, "리퀘스트 공개키 : "+publicKey);
        map= new HashMap<>();
        map.put("userID", userID);
        map.put("publicKey", publicKey);

    }
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
