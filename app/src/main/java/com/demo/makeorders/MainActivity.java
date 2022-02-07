package com.demo.makeorders;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
String email,password;
EditText emailText,passwordeText;
TextView link;
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
         link=(TextView)findViewById(R.id.newuser);
         link.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent regiterIntent=new Intent(MainActivity.this,RegistrationActivity.class);
                 startActivity(regiterIntent);
             }
         });
         loginBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 email=emailText.getText().toString();
                 password=passwordeText.getText().toString();
                 if(!email.isEmpty()&& !password.isEmpty())
                 Login(email,password);
                 else {
                     if(email.isEmpty())
                         emailText.setError("Email is required!");
                     if(password.isEmpty())
                         passwordeText.setError("Password is required!");
                 }
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
        public void parseVollyError(VolleyError error){
            try {
                String responseBody=new String(error.networkResponse.data,"utf-8");
                JSONObject data=new JSONObject(responseBody);
                Log.i("ErrorRe: ",responseBody);
                String message=data.getString("message");
                Log.d("Status: ",data.getString("status"));
                if(data.getString("status").equalsIgnoreCase("500") && message.equalsIgnoreCase("")){
                    message="Invalid email or password";
                }
                Log.i("Error: ",message);
                Toast.makeText(getApplicationContext(),"Error: "+message,Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException | JSONException  e) {
                e.printStackTrace();
            }
            catch (NullPointerException nullPointerException){
               nullPointerException.printStackTrace();
            }
        }
    synchronized private void Login(String email,String password){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait for loading....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    int method=1;
                    String url = "https://orderproductsapi.herokuapp.com/api/v1/User/login";

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

                                    Log.i("Response: ",response.toString());
                                    UserInfo=new HashMap<>();
                                    try {
                                       if(!response.has("error")) {
                                           Toast.makeText(MainActivity.this, " Well Logedin",Toast.LENGTH_LONG).show();
                                           UserInfo = toMap(response);
                                           token = UserInfo.get("token").toString();
                                           Log.i("User details:", UserInfo.get("User").toString());
                                           JSONObject userJson = new JSONObject(response.get("User").toString());
                                           Log.i("userJson:", userJson.toString());
                                           // Object user=(UserInfo.get("User")).getClass();
                                           Log.d("token: ", token);
                                           emailText.setText("");
                                           passwordeText.setText("");
                                           String role = userJson.get("role").toString();
                                           Log.i("Role ", role);
                                           switch (role) {
                                               case "4": {
                                                   Intent intent = new Intent(MainActivity.this, AdminHomePage.class);
                                                   intent.putExtra("UserInfo", response.toString());

                                                   startActivity(intent);
                                               }
                                               break;

                                           }
                                       }
                                       else {
                                           String message=response.getString("error");
                                           Toast.makeText(getApplicationContext(),"Error: "+message,Toast.LENGTH_LONG).show();
                                           emailText.setText("");
                                           passwordeText.setText("");
                                           progressDialog.dismiss();
                                       }
                                        progressDialog.dismiss();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error!=null)
                           parseVollyError(error);
                            emailText.setText("");
                            passwordeText.setText("");
                            progressDialog.dismiss();
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
                    Volley.newRequestQueue(MainActivity.this).add(jsonObjReq).setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                    .setShouldCache(false);

                }

                catch (Exception e) {
                    Log.e("tag", ""+e.getMessage());
                }
                // dismiss the progress dialog


                // finish();
            }
        }.start();

    }
}