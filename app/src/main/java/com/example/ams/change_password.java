package com.example.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class change_password extends AppCompatActivity {

    EditText e1, e2, e3;

    Button changepassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (getSupportActionBar() != null) {

            getSupportActionBar().hide();

        }

        e1 = findViewById(R.id.currentpassword);
        e2 = findViewById(R.id.newpassword);
        e3 = findViewById(R.id.confirmpassword);

        changepassword = findViewById(R.id.changepassword);

        Intent intent = getIntent();

        String changepass_field = intent.getStringExtra("currentpassword");

     //   Toast.makeText(getApplicationContext(),First_Page.ID,Toast.LENGTH_LONG).show();

        if (changepass_field == null || changepass_field.isEmpty()) {

            e1.setVisibility(View.GONE);
        } else {

            e1.setVisibility(View.VISIBLE);
        }

        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String current_password = "";
                String pass = "";
                String cpass = "";


                if (e1.getVisibility() == View.GONE) {

                    pass = e2.getText().toString();
                    cpass = e3.getText().toString();

                    char cpass_array[] = cpass.toCharArray();
                    String pass_result = check_pass(cpass_array);

                    if (pass.isEmpty()) {
                        e2.requestFocus();
                        e2.setFocusableInTouchMode(true);
                        e2.setError("Password is required");
                    } else if (cpass.isEmpty()) {
                        e3.requestFocus();
                        e3.setFocusableInTouchMode(true);
                        e3.setError("Confirm password is required");
                    } else if (!pass.equals(cpass)) {
                        e3.requestFocus();
                        e3.setFocusableInTouchMode(true);
                        e3.setError("Password doesnot matches");
                    } else if (pass.length() < 8) {
                        e3.requestFocus();
                        e3.setFocusableInTouchMode(true);
                        e3.setError("Password must be greater than 8 character and must contains at-least one upper letter and one symbol");
                    } else if (!pass_result.equals("Ok")) {
                        e3.requestFocus();
                        e3.setFocusableInTouchMode(true);
                        e3.setError(pass_result);

                    } else {

                          sendPostData(null,pass);

                    }


                } else {

                    current_password = e1.getText().toString();
                    pass = e2.getText().toString();
                    cpass = e3.getText().toString();


                    char cpass_array[] = cpass.toCharArray();
                    String pass_result = check_pass(cpass_array);

                    if (current_password.isEmpty()) {

                        e1.requestFocus();
                        e1.setFocusableInTouchMode(true);
                        e1.setError("Current Password is required");

                    } else if (pass.isEmpty()) {
                        e2.requestFocus();
                        e2.setFocusableInTouchMode(true);
                        e2.setError("Password is required");
                    } else if (cpass.isEmpty()) {
                        e3.requestFocus();
                        e3.setFocusableInTouchMode(true);
                        e3.setError("Confirm password is required");
                    } else if (!pass.equals(cpass)) {
                        e3.requestFocus();
                        e3.setFocusableInTouchMode(true);
                        e3.setError("Password doesnot matches");
                    } else if (pass.length() < 8) {
                        e3.requestFocus();
                        e3.setFocusableInTouchMode(true);
                        e3.setError("Password must be greater than 8 character and must contains at-least one upper letter and one symbol");
                    } else if (!pass_result.equals("Ok")) {
                        e3.requestFocus();
                        e3.setFocusableInTouchMode(true);
                        e3.setError(pass_result);

                    } else {

                          sendPostData(current_password,pass);

                    }


                }

            }
        });


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

    private void sendPostData(String currentpass, String newpass){

        SweetAlertDialog progressDialog = new SweetAlertDialog(change_password.this,SweetAlertDialog.PROGRESS_TYPE);


        try {


            progressDialog.getProgressHelper().setBarColor(Color.BLUE);
            progressDialog.setTitle("Loading...");
            progressDialog.show();

            JSONObject map = new JSONObject();
            map.put("currentpassword",currentpass);
            map.put("newpass", newpass);
            map.put("userId",First_Page.ID);

            String url = "http://"+MainActivity.IP+"/Project/changepassword.php"; // 192.168.1.9

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject result) {
                    progressDialog.dismiss();
                    try {

                        Log.d("mytag","myresult = "+result.toString());

                       int code = (int) result.get("code");

                        if(code == 100){

                            SweetAlertDialog dialog =  new SweetAlertDialog(change_password.this,SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setCancelable(false);
                            dialog.setTitleText("Successful");
                            dialog.setContentText("Password Changed Successful");
                            dialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();

                                    Intent intent = new Intent(change_password.this,First_Page.class);
                                    startActivity(intent);
                                   


                                }
                            });
                            dialog.show();

                        }
                        else if(code == 101){
                            SweetAlertDialog dialog =   new SweetAlertDialog(change_password.this,SweetAlertDialog.ERROR_TYPE);
                            dialog.setCancelable(false);
                            dialog.setTitleText("Error");
                            dialog.setContentText("Current Password Doesnot Matches");
                            dialog.show();
                        }


                    } catch (JSONException e) {

                       Toast.makeText(getApplicationContext(),"Error1 = "+e.getMessage(),Toast.LENGTH_LONG).show();

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(),"Error = "+error.getMessage(),Toast.LENGTH_LONG).show();

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(change_password.this);
            requestQueue.add(objectRequest);




        } catch (Exception ex) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Error " + ex, Toast.LENGTH_LONG).show();


        }


    }

}