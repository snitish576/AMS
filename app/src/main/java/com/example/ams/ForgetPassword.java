package com.example.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgetPassword extends AppCompatActivity {

    Button sendotp,verifyotp;

    EditText email,OTP;

    TextView message;

    SweetAlertDialog dialog;

    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);



        if(getSupportActionBar()!=null){

            getSupportActionBar().hide();

        }

        OTP = findViewById(R.id.otp);
        verifyotp = findViewById(R.id.verifyotpbtn);

        OTP.setVisibility(View.INVISIBLE);
        verifyotp.setVisibility(View.INVISIBLE);

        sendotp = findViewById(R.id.changepassword);
        email = findViewById(R.id.email1);
        message = findViewById(R.id.message);






        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getemail = email.getText().toString();

                if(getemail.isEmpty()){

                    email.setError("Email Cannot be empty");

                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(getemail).matches()){

                   email.setError("Invalid Email address");

                }
                else {
                    sendotp.setVisibility(View.INVISIBLE);

                    dialog = new SweetAlertDialog(ForgetPassword.this, SweetAlertDialog.PROGRESS_TYPE);
                    dialog.setTitle("In Progress");
                    dialog.setCancelable(false);
                    dialog.setTitleText("Please Wait Verifying Your Email");
                    dialog.show();

                    sendPostrequest(getemail);

                }

            }
        });


    }

    private void sendPostrequest(String email){

        JSONObject map = new JSONObject();
        try {
            map.put("email",email);

            String url = "http://"+MainActivity.IP+"/Project/sendemail.php";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        String code = response.getString("code");

                       if(code.equals("500")){


                           String OTP_FROM_SERVER = response.getString("OTP");

                      //     Toast.makeText(getApplicationContext(),"OTP :"+OTP_FROM_SERVER,Toast.LENGTH_LONG).show();

                           String UserID = response.getString("userId");

                           message.setTextColor(Color.GREEN);
                           message.setText("An OTP has been sent to your registered email Id ");
                           OTP.setVisibility(View.VISIBLE);
                           verifyotp.setVisibility(View.VISIBLE);

                           verifyotp.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {

                                   String OTP_FROM_USER = OTP.getText().toString();



                                   if(OTP_FROM_SERVER.equals(OTP_FROM_USER)){

                                       Intent intent1 = new Intent(ForgetPassword.this,change_password.class);

                                       First_Page.ID = UserID;

                                       startActivity(intent1);

                                   }
                                   else{

                                       Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_LONG).show();

                                   }

                               }
                           });


                       }
                       else if(code.equals("501")){

                           message.setTextColor(Color.RED);
                           message.setText("This Email ID is not registered in our database");

                           sendotp.setVisibility(View.VISIBLE);

                       }else if(code.equals("502")){

                           Toast.makeText(getApplicationContext(),"Error, Something Went Wrong",Toast.LENGTH_LONG).show();

                       }


                        dialog.dismiss();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });

            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

           RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
           requestQueue.add(request);

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }


    }

}