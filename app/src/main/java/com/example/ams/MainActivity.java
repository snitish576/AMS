package com.example.ams;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    EditText e1, e2, e3, e4, e5;
    Button b1;
    TextView t1;

    SharedPreferences preferences;

    // 192.168.191.15

    public static String IP = "192.168.1.5";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("logindetails",MODE_PRIVATE);

        if (getSupportActionBar() != null) {

            getSupportActionBar().hide();

        }

        e1 = findViewById(R.id.name);
        e1.requestFocus();
        e2 = findViewById(R.id.email);
        e3 = findViewById(R.id.pass);
        e4 = findViewById(R.id.cpass);
        b1 = findViewById(R.id.b1);
        t1 = findViewById(R.id.tv1);

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), First_Page.class);
                startActivity(i);
                finish();
            }
        });




        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = e1.getText().toString();
                String email = e2.getText().toString();
                String pass = e3.getText().toString();
                String cpass = e4.getText().toString();

                char cpass_array[] = cpass.toCharArray();
                String pass_result = check_pass(cpass_array);


                if (name.isEmpty()) {
                    e1.requestFocus();
                    e1.setFocusableInTouchMode(true);
                    e1.setError("Name filed is required");
                } else if (email.isEmpty()) {
                    e2.requestFocus();
                    e2.setFocusableInTouchMode(true);
                    e2.setError("Email Cannot be empty");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    e2.requestFocus();
                    e2.setFocusableInTouchMode(true);
                    e2.setError("Invalid Email Address");
                } else if (pass.isEmpty()) {
                    e3.requestFocus();
                    e3.setFocusableInTouchMode(true);
                    e3.setError("Password is required");
                } else if (cpass.isEmpty()) {
                    e4.requestFocus();
                    e4.setFocusableInTouchMode(true);
                    e4.setError("Confirm password is required");
                } else if (!pass.equals(cpass)) {
                    e4.requestFocus();
                    e4.setFocusableInTouchMode(true);
                    e4.setError("Password doesnot matches");
                } else if (pass.length() < 8) {
                    e4.requestFocus();
                    e4.setFocusableInTouchMode(true);
                    e4.setError("Password must be greater than 8 character and must contains at-least one upper letter and one symbol");
                } else if (!pass_result.equals("Ok")) {
                    e4.requestFocus();
                    e4.setFocusableInTouchMode(true);
                    e4.setError(pass_result);

                } else {

                    sendPostData(name, email, pass);

                }

            }

            public String check_pass(char array[]) {
                int ascii_code = 0;
                int validation_1 = 0, validation_2 = 0, validation_3 = 0;
                String code = "";
                for (int i = 0; i < array.length; i++) {

                    ascii_code = (int) array[i];
                    code = code + ascii_code + ",";

                    if ((ascii_code >= 48 && ascii_code <= 57)) {
                        validation_1++;
                    } else if ((ascii_code >= 65 && ascii_code <= 90)) {
                        validation_2++;

                    } else if ((ascii_code >= 33 && ascii_code <= 47) || (ascii_code >= 58 && ascii_code <= 64) || (ascii_code >= 91 && ascii_code <= 96) || (ascii_code >= 123 && ascii_code <= 126)) {
                        validation_3++;

                    }
                }

                if (validation_1 == 0) {
                    return "Password must contains atleast one number";
                } else if (validation_2 == 0) {
                    return "Password must contains atleast one upper case letter";
                } else if (validation_3 == 0) {
                    return "Password must contains atleast one special symbol";
                } else {
                    return "Ok";
                }

            }
        });


    }

    public void sendPostData(String name, String email, String pass) {

        SweetAlertDialog progressDialog = new SweetAlertDialog(MainActivity.this,SweetAlertDialog.PROGRESS_TYPE);


        try {


            progressDialog.getProgressHelper().setBarColor(Color.BLUE);
            progressDialog.setTitle("Loading...");
            progressDialog.show();

            JSONObject map = new JSONObject();
            map.put("name",name);
            map.put("email", email);
            map.put("pass", pass);

            String url = "http://"+MainActivity.IP+"/Project/signup.php"; // 192.168.1.9
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject result) {
                    progressDialog.dismiss();
                    try {
                        int code = (int) result.get("code");
                        int id = (int) result.get("id");
                        if(code == 100){

                           SweetAlertDialog dialog =  new SweetAlertDialog(MainActivity.this,SweetAlertDialog.SUCCESS_TYPE);
                           dialog.setCancelable(false);
                           dialog.setTitleText("Successful");
                           dialog.setContentText("SignUp Successful");
                           dialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                               @Override
                               public void onClick(SweetAlertDialog sweetAlertDialog) {
                                   dialog.dismiss();
                                   SharedPreferences.Editor editor = preferences.edit();
                                   editor.putString("name",name);
                                   editor.putString("ID",""+id);
                                   editor.apply();

                                   First_Page.NAME = name;
                                   First_Page.ID =  ""+id;
                                   Intent intent = new Intent(getApplicationContext(),Welcome_Page.class);
                                   startActivity(intent);
                                   finish();


                               }
                           });
                           dialog.show();

                        }
                        else if(code == 101){
                          //  pDialog.hide();
                          SweetAlertDialog dialog =   new SweetAlertDialog(MainActivity.this,SweetAlertDialog.ERROR_TYPE);
                            dialog.setCancelable(false);
                            dialog.setTitleText("Error");
                            dialog.setContentText("Email Already Exist");
                            dialog.show();
                        }
                        

                    } catch (JSONException e) {
                        Log.e("mytag", ""+e.getMessage());

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Error = "+error.toString(),Toast.LENGTH_LONG).show();
                    Log.e("mytag", error.toString());
                }
            });
          RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
          requestQueue.add(objectRequest);




        } catch (Exception ex) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Exception" + ex, Toast.LENGTH_LONG).show();


        }


    }
}