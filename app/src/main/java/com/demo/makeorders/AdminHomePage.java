package com.demo.makeorders;

import static com.demo.makeorders.MainActivity.toMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminHomePage extends AppCompatActivity {
  ArrayList<User> userArrayList;
  UserAdapter userAdapter;
  TextView userName;
  User user;
  EditText search;
  Button logoutBtn;
  RecyclerView recyclerView;
  FrameLayout frameLayout;
  String all_names="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);
        userArrayList=new ArrayList<>();
        userName=(TextView) findViewById(R.id.username);
        search=(EditText) findViewById(R.id.search_user);
        recyclerView=(RecyclerView) findViewById(R.id.userList);
        frameLayout=(FrameLayout) findViewById(R.id.details);
        Intent intent=getIntent();
        String response=intent.getStringExtra("UserInfo");

        try {
            Log.i("Admin details:", ""+response);
            JSONObject responseJs=new JSONObject(response);
            String token=responseJs.get("token").toString();
            Log.i("Admin details:", responseJs.get("User").toString());
            JSONObject userJson=new JSONObject(responseJs.get("User").toString());
            all_names=userJson.get("firstName").toString()+" "+userJson.get("lastName").toString();
            userName.setText(userJson.get("company").toString());
            Log.i("AdminJson :",userJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadUsers();
    }
    public void parseVollyError(VolleyError error){
        try {
            String responseBody=new String(error.networkResponse.data,"utf-8");
            JSONObject data=new JSONObject(responseBody);
            Log.i("ErrorRe: ",responseBody);
            String message=data.getString("message");
            Log.d("Status: ",data.getString("status"));
            if(data.getString("status").equalsIgnoreCase("500") && message==""){
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
    synchronized private void loadUsers(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait for loading....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(AdminHomePage.this);
                    int method=0;
                    String url = "https://orderproductsapi.herokuapp.com/api/v1/User/";

                    JSONObject js = new JSONObject();

                    // Make request for JSONObject
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                            method, url,null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Response obj: ",response.toString());
                                  Map  UserInfo=new HashMap<>();
                                    try {

                                        UserInfo=toMap(response);
                                        Log.i("User details:",UserInfo.toString());
                                        JSONArray jsonArray=new JSONArray(response.get("Users").toString());
                                        for(int i=0;i<jsonArray.length();i++){
                                            user=new User();
                                          JSONObject object=new JSONObject(jsonArray.getJSONObject(i).toString());
                                          user.setCompany(object.getString("company"));
                                          user.setEmail(object.getString("email"));
                                          user.setRole(object.getInt("role"));
                                          Log.i("User:>>",user.toString());
                                          if(!userArrayList.contains(user))
                                          userArrayList.add(user);
                                        }

                                        progressDialog.dismiss();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                         //  parseVollyError(error);
                            Log.i("error: ",""+error);
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
                    Volley.newRequestQueue(AdminHomePage.this).add(jsonObjReq).setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
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

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }
@SuppressLint("RestrictedApi")
@Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
      menu.findItem(R.id.user).setTitle(all_names);
      if(menu instanceof MenuBuilder){
          ((MenuBuilder) menu).setOptionalIconsVisible(true);
      }
        return  super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
            logout();
                return true;
            case R.id.user:
               showProfile();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showProfile() {
    }

    private void logout() {
    }
}