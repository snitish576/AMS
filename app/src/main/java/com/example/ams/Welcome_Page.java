package com.example.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.zip.Inflater;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Welcome_Page extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    TextView t1, t2, t3;

    SweetAlertDialog progressDialog;
    Button b1;
    ImageView img;

    String id = First_Page.ID;
    static JSONArray class_name;

    static JSONArray ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome__page);

        if (getSupportActionBar() != null) {

            getSupportActionBar().hide();
        }
        t1 = findViewById(R.id.tv1);

        t1.setText("Hello \n" + First_Page.NAME);
        t3 = findViewById(R.id.tv3);
        b1 = findViewById(R.id.bt1);

        img = findViewById(R.id.logout);

        id = First_Page.ID;

        getdata(id);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Welcome_Page.this, Dialog_Activity.class);
                startActivity(intent);


            }
        });


//        img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Welcome_Page.this, SweetAlertDialog.WARNING_TYPE);
//                sweetAlertDialog.setTitleText("Signout").setContentText("Are your sure you want\n to sign out");
//                sweetAlertDialog.showCancelButton(true);
//                sweetAlertDialog.setCancelText("No");
//
//                sweetAlertDialog.setConfirmText("Yes");
//                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        SharedPreferences preferences = getSharedPreferences("logindetails", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.clear();
//                        editor.apply();
//                        Intent intent = new Intent(getApplicationContext(), First_Page.class);
//                        sweetAlertDialog.dismiss();
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//                sweetAlertDialog.show();
//
//            }
//        });

    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();



    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){

            case R.id.item1:

                Intent intent = new Intent(Welcome_Page.this,Dialog_Activity.class);

                startActivity(intent);

                return true;

            case R.id.item2:

                Intent intent1 = new Intent(Welcome_Page.this,change_password.class);
                intent1.putExtra("currentpassword","1");
                startActivity(intent1);


                return true;

            case R.id.item3:

                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Welcome_Page.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Signout").setContentText("Are your sure you want\n to sign out");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelText("No");

                sweetAlertDialog.setConfirmText("Yes");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SharedPreferences preferences = getSharedPreferences("logindetails", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(getApplicationContext(), First_Page.class);
                        sweetAlertDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
                sweetAlertDialog.show();

                return true;

            default:
                return false;


        }

    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        getdata(id);
        Log.d("mytag", "Resumed");

    }

    private void getdata(String id) {

        JSONObject map = new JSONObject();
        String url = "http://" + MainActivity.IP + "/Project/getdata.php";

        try {
            map.put("id", id);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        int code = (int) response.get("code");


                        if (code == 100) {

                            class_name = response.getJSONArray("data");
                            ID = response.getJSONArray("id");
                            if (class_name.length() == 0) {


                                t3.setVisibility(View.VISIBLE);
                            } else {

                                t3.setVisibility(View.GONE);
                                init(class_name, ID);

                            }


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error = " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("mytag", error.getMessage());


                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void init(JSONArray classs, JSONArray ID) {

        TableLayout tableLayout = findViewById(R.id.table);

        tableLayout.removeAllViews();

        TableRow.LayoutParams p1 = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        p1.weight = 2;

        p1.setMargins(10, 0, 40, 30);



        TableRow tr1 = new TableRow(this);


        TextView tv = new TextView(this);
        tv.setText("SR. No");
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(18);
        tv.setLayoutParams(p1);
        tv.setTypeface(null, Typeface.BOLD);

        tr1.addView(tv);


        TextView b = new TextView(this);
        b.setText("Class Name");
        b.setTextColor(Color.WHITE);
        b.setGravity(Gravity.CENTER);
        b.setLayoutParams(p1);
        b.setTextSize(18);
        b.setTypeface(null, Typeface.BOLD);
        tr1.addView(b);

        TextView tvv = new TextView(this);
        tvv.setText("Action");
        tvv.setTextColor(Color.WHITE);
        tvv.setGravity(Gravity.CENTER);
        tvv.setLayoutParams(p1);
        tvv.setTextSize(18);
        tvv.setTypeface(null, Typeface.BOLD);
        tr1.addView(tvv);

        tableLayout.addView(tr1);


        ImageView imageView[] = new ImageView[classs.length()];

        ImageView imageView1[] = new ImageView[classs.length()];

        for (int i = 0; i < classs.length(); i++) {

            TableRow tr = new TableRow(this);


            TextView tv1 = new TextView(this);
            tv1.setText("" + (i + 1));
            tv1.setTextColor(Color.WHITE);
            tv1.setGravity(Gravity.CENTER);
            tv1.setTextSize(18);
            tv1.setLayoutParams(p1);
            tv1.setTypeface(null, Typeface.BOLD);
            tr.addView(tv1);


            Button b1 = new Button(this);
            try {
                b1.setText("" + classs.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            b1.setBackgroundColor(Color.CYAN);
            b1.setTextColor(Color.WHITE);
            b1.setGravity(Gravity.CENTER);
            b1.setLayoutParams(p1);
            b1.setTextSize(18);
            b1.setTypeface(null, Typeface.BOLD);
            tr.addView(b1);

            TableRow.LayoutParams p2 = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p2.weight = 0.05f;
            p2.gravity = Gravity.CENTER_HORIZONTAL;

            p2.setMargins(0, 0, 0, 30);

            imageView[i] = new ImageView(this);
            imageView[i].setImageResource(R.drawable.writing);
            imageView[i].setLayoutParams(p2);
            imageView[i].setId(i);
            try {
                imageView[i].setTag("" + ID.get(i));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            imageView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), attendance.class);
                    intent.putExtra("id", "" + view.getTag());

                    startActivity(intent);
                }
            });
            tr.addView(imageView[i]);

            imageView1[i] = new ImageView(this);
            imageView1[i].setImageResource(R.drawable.delte);
            imageView1[i].setLayoutParams(p2);

            imageView1[i].setId(i);
            try {
                imageView1[i].setTag("" + ID.get(i));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            imageView1[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SweetAlertDialog deleteDialog = new SweetAlertDialog(Welcome_Page.this, SweetAlertDialog.WARNING_TYPE);
                    deleteDialog.setTitle("Delete Class");
                    deleteDialog.setContentText("Sure You Want to Remove\n this class?");
                    deleteDialog.showCancelButton(true);
                    deleteDialog.setCancelText("No");
                    deleteDialog.setConfirmText("Yes");
                    deleteDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            deleteDialog.dismiss();

                            progressDialog = new SweetAlertDialog(Welcome_Page.this, SweetAlertDialog.PROGRESS_TYPE);
                            progressDialog.getProgressHelper().setBarColor(Color.BLUE);
                            progressDialog.setTitle("Loading...");
                            progressDialog.show();

                            boolean result = deleteClass(v.getTag().toString());

                            progressDialog.dismiss();


                        }
                    });
                    deleteDialog.show();

                }
            });


            tr.addView(imageView1[i]);


            tableLayout.addView(tr);

        }


    }

    private boolean deleteClass(String id) {

        JSONObject map = new JSONObject();

        try {
            map.put("id",id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String url = "http://" + MainActivity.IP + "/Project/deleteclass.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    String code = response.getString("code");

                    if(code.equals("500")){

                        Toast.makeText(getApplicationContext(),"Class Removed",Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(getIntent());

                    }
                    else{

                        Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_LONG).show();

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

        return true;
    }


}