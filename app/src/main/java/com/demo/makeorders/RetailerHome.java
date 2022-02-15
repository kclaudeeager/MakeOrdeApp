package com.demo.makeorders;

import static com.demo.makeorders.MainActivity.toMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RetailerHome extends AppCompatActivity {
TextView companyName;
EditText search_product;
RecyclerView productRecyclerView;
Spinner supplierSpinner;
FrameLayout frameLayout;
private String token;
TextView userName;
String all_names="";
Button sendBtn;
User user;
    ArrayAdapter user_adapter;
public  ArrayList<User> userArrayList;
public static  View.OnClickListener onClickListener;
Product_List_Adapter product_list_adapter;

ArrayList<Product> productArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_home);
        companyName=findViewById(R.id.company);
        search_product=findViewById(R.id.search_product);
        userName=(TextView) findViewById(R.id.retailerComp);
        productRecyclerView=findViewById(R.id.productList);
        supplierSpinner=findViewById(R.id.spinner_supplier);
        frameLayout=findViewById(R.id.frameLayout_picked_products);
        sendBtn=findViewById(R.id.sendOrder);
        userArrayList=new ArrayList<>();
        Intent intent=getIntent();
        productArrayList=new ArrayList<>();
        String response=intent.getStringExtra("UserInfo");
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
        loadProducts();
        loadSuppliers();
    }
    synchronized private void loadProducts(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait for loading....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(RetailerHome.this);
                    int method=0;
                    String url = "https://orderproductsapi.herokuapp.com/api/v1/products";

                    JSONObject js = new JSONObject();

                    // Make request for JSONObject
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                            method, url,null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Response obj>>>>: ", response.toString());
                                    // Map UserInfo=new HashMap<>();
                                    try {
                                        JSONArray jsonArray = new JSONArray(response.get("Products").toString());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject object = new JSONObject(jsonArray.getJSONObject(i).toString());
                                            Log.d("PP>>>",object.toString());
                                            String pName=object.get("pname").toString();
                                            double price=Double.parseDouble(object.get("price").toString());
                                            Log.d("PName>>>",pName);
                                            Product product = new Product();
                                            product.setpName(pName);
                                            product.setPrice(price);
                                            Log.i("Product:>>", product.toString());
                                            if (!productArrayList.contains(product))
                                                productArrayList.add(product);
                                        }
                                        Log.d("productArrayList>>>",productArrayList.toString());
                                        product_list_adapter=new Product_List_Adapter(productArrayList,RetailerHome.this);
                                        productRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        productRecyclerView.setVerticalScrollBarEnabled(true);
                                        productRecyclerView.setAdapter(product_list_adapter);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                }
                            }

                                , new Response.ErrorListener() {
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
                            headers.put("Authorization","Bearer "+token);
                            return headers;
                        }

                    };

                    // Adding request to request queue
                    Volley.newRequestQueue(RetailerHome.this).add(jsonObjReq).setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
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

    synchronized private void loadSuppliers(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait for loading....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(RetailerHome.this);
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
                                            if(!userArrayList.contains(user)&& user.role==1)
                                                userArrayList.add(user);
                                        }
                                        ArrayList<String> users=new ArrayList<>();
                                        for (User user:userArrayList) {
                                            users.add(user.getCompany());
                                        }
                                       user_adapter
                                                = new ArrayAdapter(
                                                RetailerHome.this,
                                                android.R.layout.simple_spinner_item,
                                               users);
                                        supplierSpinner.setAdapter( user_adapter);
                                        user_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
                    Volley.newRequestQueue(RetailerHome.this).add(jsonObjReq).setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
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