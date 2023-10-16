package com.example.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class First_Page extends AppCompatActivity {
    TextView t1,t2;
    EditText e1;
    EditText e2;
    CheckBox ch1;
    Button bt;
    static String NAME;
    static String ID;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first__page);

        t2 = findViewById(R.id.forgetpassword);
        
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgetPassword.class);
                startActivity(intent);
            }
        });



        
        preferences = getSharedPreferences("logindetails",MODE_PRIVATE);
        if(!preferences.getString("ID","").equals("") || !preferences.getString("name","").equals("")){
            NAME = preferences.getString("name","");
            ID =  preferences.getString("ID","");
            Intent intent = new Intent(getApplicationContext(),Welcome_Page.class);
            startActivity(intent);
            finish();

        }


        if(getSupportActionBar()!=null){

            getSupportActionBar().hide();

        }

        t1 = findViewById(R.id.loginbt);
        e1 = findViewById(R.id.email1);
        e2 = findViewById(R.id.pass2);
        ch1 = findViewById(R.id.checkBox);
        bt = findViewById(R.id.changepassword);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = e1.getText().toString();
                String pass = e2.getText().toString();

                if(email.isEmpty()){
                    e1.requestFocus();
                    e1.setFocusableInTouchMode(true);
                    e1.setError("Email Cannot be empty");

                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    e1.requestFocus();
                    e1.setFocusableInTouchMode(true);
                    e1.setError("Invalid Email Address");

                }
                else if(pass.isEmpty()){
                    e2.requestFocus();
                    e2.setFocusableInTouchMode(true);
                    e2.setError("Password Cannot be empty");

                }
                else{

                    sendPostRequest(email,pass);


                }

            }
        });



        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();

            }
        });


    }

    private void sendPostRequest(String email,String pass) {

        JSONObject map = new JSONObject();
        try{



            SweetAlertDialog progressDialog = new SweetAlertDialog(First_Page.this,SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.getProgressHelper().setBarColor(Color.BLUE);
            progressDialog.setTitle("Loading...");
            progressDialog.show();

            map.put("email",email);
            map.put("pass",pass);


            String url = "http://"+MainActivity.IP+"/Project/login.php";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        progressDialog.dismiss();
                        int result = (int) response.get("code");
                        String name ="";
                        String id = "";
                        if(result == 100){
                            name =(String) response.get("name");
                            id = (String) response.get("id");
                            NAME = name;
                            ID = id;

                            if(ch1.isChecked()){
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("name",name);
                                editor.putString("ID",id);
                                editor.apply();
                            }

                            Intent i = new Intent(getApplicationContext(),Welcome_Page.class);
                            startActivity(i);
                        }else if (result==101){

                            new SweetAlertDialog(First_Page.this,SweetAlertDialog.ERROR_TYPE).setTitleText("Check Again").setContentText("Invalid Email and Password").show();

                        }
                        else{

                            Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception ex) {

                        Log.d("mytag", "hhh "+ex.getMessage());

                    }
                }



            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();

                    if (error.getMessage()!=null && error.getMessage().contains("Failed to connect")){

                       new SweetAlertDialog(First_Page.this,SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("Check internet connection").show();
                    }
                    else{

                        Toast.makeText(getApplicationContext(),"Something went wrong "+error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(request);
        }
        catch (JSONException ex){

            Toast.makeText(getApplicationContext(),"Something went wrong "+ex.getMessage(),Toast.LENGTH_LONG).show();

        }



    }
}