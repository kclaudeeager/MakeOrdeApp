package com.demo.makeorders;

import static com.demo.makeorders.MainActivity.toMap;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminHomePage extends AppCompatActivity {
 public  ArrayList<User> userArrayList;
    public static  View.OnClickListener onClickListener;
  UserAdapter userAdapter;
  TextView userName;
  User user;
  EditText search;
  Button logoutBtn;
  public static RecyclerView recyclerView;
  FrameLayout frameLayout;
  String all_names="";
  private String token;

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

      onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=recyclerView.getChildLayoutPosition(view);
                User userDet=userArrayList.get(position);
                if (userDet!=null) {
                    String company = userDet.getCompany();
                    LayoutInflater inflater = LayoutInflater.from(AdminHomePage.this);
                    FrameLayout frameLayout = findViewById(R.id.details);
                    View userDetailView = inflater.inflate(R.layout.user_detail, frameLayout, true);
                    TextView companyV = userDetailView.findViewById(R.id.comp);
                    TextView emailV = userDetailView.findViewById(R.id.email_det);
                    Spinner roleSpiner = userDetailView.findViewById(R.id.role_spiner);

                    Button update = userDetailView.findViewById(R.id.update);
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String selectedRole=roleSpiner.getSelectedItem().toString();
                           // Toast.makeText(AdminHomePage.this,"Role: >"+selectedRole,Toast.LENGTH_LONG).show();
                              if (selectedRole.equalsIgnoreCase("supplier"))
                                  userDet.setRole(1);
                              else
                                  userDet.setRole(0);

                            updateUser(userDet);
                        }
                    });
                    companyV.setText(company);
                    emailV.setText(userDet.getEmail());
                    // Toast.makeText(context,"Role: "+ emailV.getText().toString(),Toast.LENGTH_SHORT).show();
                    String[] roles = {"retailler", "supplier"};
                    for (int i = 0; i < roles.length; i++) {
                        if (userDet.getRole() == i) {
                            roleSpiner.setSelection(i);
                           // Toast.makeText(AdminHomePage.this,"Role: "+roles[i],Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

            }
        };


        search.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        searchUsers(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
});
        try {
            Log.i("Admin details:", ""+response);
            JSONObject responseJs=new JSONObject(response);
            token=responseJs.get("token").toString();
            Log.i("Admin token:", token);
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

  synchronized   private void updateUser(User userDet) {
      final ProgressDialog progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(true);
      progressDialog.setMessage("Please wait ....");
      progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progressDialog.show();
      new Thread() {
          public void run() {
              try {
                  RequestQueue requestQueue = Volley.newRequestQueue(AdminHomePage.this);
                  int method=2;
                  String url = "https://orderproductsapi.herokuapp.com/api/v1/User/setrole/"+userDet.getEmail();

                  JSONObject js = new JSONObject();
                  try {
                      js.put("role",userDet.getRole());

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
                                  String[] roles = {"retailler", "supplier"};
                                  for (int i = 0; i < roles.length; i++) {
                                      if (userDet.getRole() == i) {

                                           Toast.makeText(AdminHomePage.this,"User role is set to: "+roles[i],Toast.LENGTH_SHORT).show();
                                          break;
                                      }
                                  }
                                  //Toast.makeText(AdminHomePage.this,"User role is set to "+userDet.getRole(),Toast.LENGTH_LONG).show();
                                      progressDialog.dismiss();


                              }
                          }, new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          if(error!=null)
                              parseVollyError(error);
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
                          headers.put("Authorization","Bearer "+token);
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

    public void parseVollyError(VolleyError error){
        try {
            String responseBody=new String(error.networkResponse.data,"utf-8");
            JSONObject data=new JSONObject(responseBody);
            Log.i("ErrorRe: ",responseBody);
            String message=data.getString("message");
            if(data.getString("error").equalsIgnoreCase("Forbidden")){
               message="Only system administrator can set user role ::";
            }

            Log.d("Status: ",data.getString("status"));
            if(data.getString("status").equalsIgnoreCase("403") && message==""){
                message="Only system administrator can set user role ::";
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
                                          if(!userArrayList.contains(user)&& user.role!=4)
                                          userArrayList.add(user);
                                        }
                                        userAdapter=new UserAdapter(userArrayList,AdminHomePage.this);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        recyclerView.setVerticalScrollBarEnabled(true);
                                        recyclerView.setAdapter(userAdapter);

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
            case R.id.retailers:
                filter(0);
                return true;
            case R.id.suppliers:
                filter(1);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void filter(int role) {
        ArrayList<User> temp=new ArrayList<>();

        for (User user:userArrayList) {

            if (user.getRole()==role){
                temp.add(user);
            }
        }
        userAdapter.updateList(temp);
    }

    private void showProfile() {
    }

    private void logout() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait ....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(AdminHomePage.this);
                    int method = 1;
                    String url = "https://orderproductsapi.herokuapp.com/api/v1/User/logout";

                    // JSONObject js = new JSONObject();

                    // Make request for JSONObject
                    StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Logout>>", response);
                            progressDialog.dismiss();
                            Toast.makeText(AdminHomePage.this, response.toString(), Toast.LENGTH_LONG).show();
                            AdminHomePage.this.finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("error", error.toString());
                            Toast.makeText(getApplicationContext(),"No user logged in",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    })
                   {

                        /**
                         * Passing some request headers
                         */
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            headers.put("Authorization","Bearer "+token);
                            return headers;
                        }

                    };

                    // Adding request to request queue
                    requestQueue.add(request).setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
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
    private void searchUsers(String text){
        ArrayList<User> temp=new ArrayList<>();
        for (User user:userArrayList) {

            if (user.getCompany().toLowerCase().contains(text.toLowerCase())){
                temp.add(user);
            }
        }
        userAdapter.updateList(temp);
    }
}