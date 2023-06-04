package com.example.registerlogin;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class SavePKRequest extends StringRequest{
    final static private String URL = "http://192.168.0.4:80/SavePK.php";
    final private Map<String , String> map;

    public SavePKRequest(String publicKey,String userID, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        map= new HashMap<>();
        map.put("userID", userID);
        map.put("publicKey",publicKey);

    }

}
