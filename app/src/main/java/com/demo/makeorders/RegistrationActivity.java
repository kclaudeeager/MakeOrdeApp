package com.demo.makeorders;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
EditText firstNameText,lastNameText,companyText,emailText,passwordText;
    String  firstName, lastName,company,email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firstNameText=(EditText) findViewById(R.id.fname);
        lastNameText=(EditText) findViewById(R.id.lname);
        companyText=(EditText) findViewById(R.id.company);
        emailText=(EditText) findViewById(R.id.em);
        passwordText=(EditText)findViewById(R.id.register_password);
        Button register=(Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             firstName=firstNameText.getText().toString();
             lastName=lastNameText.getText().toString();
                password=passwordText.getText().toString();
             email=emailText.getText().toString();
             email=email.toLowerCase();
             emailText.setText(email);
            company=companyText.getText().toString();
             if((firstName.length()>=4)&&(lastName.length()>=2)&&(!company.isEmpty())&& (email.endsWith("@gmail.com"))&&(!password.isEmpty())){
                 register_User(firstName,lastName,company,email,password);
             }
             else {
                 if((firstName.length()<4)){
                     firstNameText.setError("firstName should have at least 4 characters");
                     return;
                 }
                 if((lastName.length()<2)){
                     lastNameText.setError("lastName should have at least 2 characters");
                     return;
                 }
                 if((company.isEmpty())){
                     companyText.setError("Company should not be left empty");
                     return;
                 }
                 if((!email.endsWith("@gmail.com"))){
                     emailText.setError("Correct your email");
                     return;
                 }
                 if((password.isEmpty())){
                     passwordText.setError("Password is required!");
                     return;
                 }

             }

            }
        });
    }

    synchronized private void register_User(String firstName, String lastName, String company, String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait for loading....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivity.this);
                    int method=1;
                    String url = "https://orderproductsapi.herokuapp.com/api/v1/User/signup";

                    JSONObject js = new JSONObject();
                    try {
                        js.put("firstName",firstName);
                        js.put("lastName",lastName);
                        js.put("company",company);
                        js.put("email",email);
                        js.put("password",password);

                        Log.d("Json: ",js.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Make request for JSONObject
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                            method, url, js,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(RegistrationActivity.this, " Well registerd",Toast.LENGTH_LONG).show();
                                    Log.i("Response: ",response.toString());
                                    progressDialog.dismiss();
                                    RegistrationActivity.this.finish();
                                    //UserInfo=new HashMap<>();
//                                    try {
//
//                                        UserInfo=toMap(response);
//                                        token=UserInfo.get("token").toString();
//                                        Log.i("User details:",UserInfo.get("User").toString());
//                                        Log.d("token: ",token);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegistrationActivity.this,"Error occured: "+error.getMessage(),Toast.LENGTH_LONG).show();
                            VolleyLog.d("Error: Tag", "Error: " + "Email already in use");
                            emailText.setText("");
                            passwordText.setText("");
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
                    Volley.newRequestQueue(RegistrationActivity.this).add(jsonObjReq);

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