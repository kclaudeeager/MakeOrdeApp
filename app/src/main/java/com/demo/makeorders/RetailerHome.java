package com.demo.makeorders;

import static com.demo.makeorders.MainActivity.toMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
ArrayList<Order> orderArrayList=new ArrayList<Order>();
TextView userName;
String all_names="";
Button sendBtn;
User user;
static int nextOrderId;
String selectedSupplier;
ArrayAdapter user_adapter;
public  ArrayList<User> userArrayList;
public static  View.OnClickListener onClickListener;
Product_List_Adapter product_list_adapter;
SelectedAdapter selectedProduct_list_adapter;
ArrayList<Product> productArrayList;
RecyclerView selectedProductRecyclerView;
ArrayList<Product> selectedProductArrayList;
public static boolean isSelectedList=false;
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
        selectedProductArrayList=new ArrayList<>();

        onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOrder();
                isSelectedList=true;
                int position = productRecyclerView.getChildLayoutPosition(view);
                Product product = productArrayList.get(position);
                TextView productName, productPrice;
                AlertDialog.Builder builder;
                if (product != null) {
                    String product_name = product.getpName();
                    Double price = product.getPrice();
                    LayoutInflater inflater = LayoutInflater.from(RetailerHome.this);
                    View productListView =  inflater.inflate(R.layout.selected_recyclerview, frameLayout, true);
                    selectedProductRecyclerView=productListView.findViewById(R.id.selected_recyclerview);
                   builder=new AlertDialog.Builder(RetailerHome.this);
                    ViewGroup viewGroup=RetailerHome.this.findViewById(R.id.retailer);
                    View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.number_of_items, viewGroup, false);
                    Button okbtn=dialogView.findViewById(R.id.ok);
                    Button cancelbtn=dialogView.findViewById(R.id.cancel);
                    EditText editText=dialogView.findViewById(R.id.qtty);
                    builder.setView(dialogView);
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();

                            okbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Integer qtty = Integer.parseInt(editText.getText().toString());
                                        product.setNumReq(qtty);

                                        if (!selectedProductArrayList.contains(product)) {
                                            selectedProductArrayList.add(product);
                                        }
                                        Log.d("productArrayList  >>>", selectedProductArrayList.toString());
                                        selectedProduct_list_adapter = new SelectedAdapter(selectedProductArrayList, RetailerHome.this);
                                        selectedProductRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        selectedProductRecyclerView.setVerticalScrollBarEnabled(true);
                                        selectedProductRecyclerView.setAdapter(selectedProduct_list_adapter);
                                        alertDialog.dismiss();
                                    } catch (NumberFormatException exception) {
                                        exception.printStackTrace();
                                    }
                                }

                            });

                   cancelbtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           alertDialog.dismiss();
                       }
                   });
                }

            }
        };
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             User supplier=new User();
            String supplierName= supplierSpinner.getSelectedItem().toString();
                for(User user:userArrayList){
                 if(user.getCompany().equalsIgnoreCase(supplierName)){
                     supplier=user;
                 }
                }
             Log.i("SupplierName:>>>",supplierName.toString());
                Log.i("Supplier:>>>",supplier.toString());
                String subject="Looking for order..";
                String body ="Order:"+"'\n\n'";
                ArrayList<Order> orders=new ArrayList<>();
                for (int i=0;i<selectedProductArrayList.size();i++){
                    body+=(i+1)+">> Product: "+selectedProductArrayList.get(i).getpName()+", Quantity:"+selectedProductArrayList.get(i).getNumReq()+", Price:"+selectedProductArrayList.get(i).getPrice()+"'\n'";
               Order order=new Order();
               order.setProduct(selectedProductArrayList.get(i));
               if(!orders.contains(order))
                   orders.add(order);
                }
                Email email=new Email(supplier.getEmail(),body,subject);
                sendSimpleEmail(email);
                for (Order order:orders) {
                    order.setOrderId(nextOrderId);
                    order.setSupplier(supplier.getCompany());
                    createOrder(order);
                }

            }
        });

    }
    synchronized   private void sendSimpleEmail(Email email) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait ....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(RetailerHome.this);
                    int method=1;
                    String url = "https://orderproductsapi.herokuapp.com/api/v1/order/sendemail";

                    JSONObject jsonObject= new JSONObject();
                    try {
                        jsonObject.put("toEmail",email.getToEmail());
                        jsonObject.put("body",email.getBody());
                        jsonObject.put("subject",email.getSubject());
                     Log.i("object>> ",""+jsonObject);
                     if(jsonObject instanceof JSONObject)
                        Log.i("I jso>> ","Trueee");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Make request for JSONObject
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                            method, url, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Response: ",response.toString());
                                 Toast.makeText(getApplicationContext(),"ORDER SENT SUCCESSFULLY!",Toast.LENGTH_LONG).show();
                                    //Toast.makeText(AdminHomePage.this,"User role is set to "+userDet.getRole(),Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error!=null)
                              Log.e("Error:>>",error.toString());
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


    synchronized private void loadOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

        progressDialog.setMessage("loading orders....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(RetailerHome.this);
                    int method = 0;
                    String url = "https://orderproductsapi.herokuapp.com/api/v1/orders";

                    JSONObject js = new JSONObject();

                    // Make request for JSONObject
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                            method, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Response obj>>>>: ", response.toString());
                                    // Map UserInfo=new HashMap<>();
                                    try {
                                        JSONArray jsonArray = new JSONArray(response.get("Orders").toString());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject object = new JSONObject(jsonArray.getJSONObject(i).toString());
                                            Log.d("Order>>>", object.toString());
                                            Product product=new Product();
                                            String retailer;
                                            String supplier;
                                            int ordernum;
                                            String pName = object.get("pname").toString();
                                            product.setpName(pName);
                                            product.setNumReq(Integer.parseInt(object.get("quantity").toString()));
                                             supplier=object.get("suplier").toString();
                                             retailer=object.getString("client");
                                             ordernum=object.getInt("ordernum");
                                            Order order=new Order(ordernum,product,retailer,supplier);
                                            Log.i("Order:>>", order.toString());

                                            if (!orderArrayList.contains(order))
                                                orderArrayList.add(order);
                                        }
                                        Log.d("orderArrayList>>>", orderArrayList.toString());
                                        int orderToCompare=0;
                                        for (Order order:orderArrayList) {
                                            if(order.getOrderId()>orderToCompare){
                                                orderToCompare=order.getOrderId();
                                            }
                                        }
                                        nextOrderId=orderToCompare+1;
                                        Log.d("NextID ",""+nextOrderId);

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
                            Log.i("error: ", "" + error);
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
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }

                    };

                    // Adding request to request queue
                    Volley.newRequestQueue(RetailerHome.this).add(jsonObjReq).setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                            .setShouldCache(false);

                } catch (Exception e) {
                    Log.e("tag", "" + e.getMessage());
                }
                // dismiss the progress dialog


                // finish();
            }
        }.start();

    }
    synchronized private void createOrder(Order order) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

        progressDialog.setMessage("creating order....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(RetailerHome.this);
                    int method = 1;
                    String url = "https://orderproductsapi.herokuapp.com/api/v1/orders";

                    JSONObject js = new JSONObject();
                     try{
                         js.put("ordernum",order.getOrderId());
                         js.put("pname",order.getProduct().getpName());
                         js.put("suplier",order.getSupplier());
                         js.put( "quantity",order.getProduct().getNumReq());
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                    // Make request for JSONObject
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                            method, url, js,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Response obj>>>>: ", response.toString());
                                    // Map UserInfo=new HashMap<>();
                                    Toast.makeText(getApplicationContext(),"Order is successfully created!",Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }

                            , new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //  parseVollyError(error);
                            Log.i("error: ", "" + error);
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
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }

                    };

                    // Adding request to request queue
                    Volley.newRequestQueue(RetailerHome.this).add(jsonObjReq).setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                            .setShouldCache(false);

                } catch (Exception e) {
                    Log.e("tag", "" + e.getMessage());
                }
                // dismiss the progress dialog


                // finish();
            }
        }.start();

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