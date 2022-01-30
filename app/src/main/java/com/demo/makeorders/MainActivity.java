package com.demo.makeorders;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
String email,password;
EditText emailText,passwordeText;
Map<String,Object> UserInfo;
String token;
Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         emailText=(EditText) findViewById(R.id.loginEmail);
         passwordeText=(EditText) findViewById(R.id.password);
         loginBtn=(Button) findViewById(R.id.login);
         loginBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 email=emailText.getText().toString();
                 password=passwordeText.getText().toString();
                 Login(email,password);
             }
         });

    }

    protected void onStart(Bundle savedInstance){
        super.onStart();
    }
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


        public static Map<String, Object> toMap(JSONObject jsonobj)  throws JSONException {
            Map<String, Object> map = new HashMap<String, Object>();
            Iterator<String> keys = jsonobj.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                Object value = jsonobj.get(key);
                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = toMap((JSONObject) value);
                }
                map.put(key, value);
            }   return map;
        }
    synchronized private void Login(String email,String password){
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait for uploading....");
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    int method=1;
                    String url = "http://192.168.43.141:9090/api/v1/User/login";

                    JSONObject js = new JSONObject();
                    try {
                       js.put("email",email);
                       js.put("password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Make request for JSONObject
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                            method, url, js,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(MainActivity.this, " Well posted",Toast.LENGTH_LONG).show();
                                    Log.i("Response: ",response.toString());
                                    UserInfo=new HashMap<>();
                                    try {

                                        UserInfo=toMap(response);
                                        token=UserInfo.get("token").toString();
                                        Log.i("User details:",UserInfo.get("User").toString());
                                        Log.d("token: ",token);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this,"Invvalid email or password",Toast.LENGTH_LONG).show();
                            VolleyLog.d("Error: Tag", "Error: " + error.toString());
                            emailText.setText("");
                            passwordeText.setText("");
                        }
                    }) {

                        /**
                         * Passing some request headers
                         */
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }

                    };

                    // Adding request to request queue
                    Volley.newRequestQueue(MainActivity.this).add(jsonObjReq);

                }

                catch (Exception e) {
                    Log.e("tag", ""+e.getMessage());
                }
                // dismiss the progress dialog
                progressDialog.dismiss();
                emailText.setText("");
                passwordeText.setText("");
                // finish();
            }
        }.start();

    }
}