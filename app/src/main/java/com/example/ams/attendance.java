package com.example.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class attendance extends AppCompatActivity {
    String id = "";
    String class_name = "";
    Button bt;
    CheckBox checkBox[];
    SweetAlertDialog progressDialog;
    int mYear, mMonth, mDay;
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        if (getSupportActionBar() != null) {

            getSupportActionBar().hide();

        }
        bt = findViewById(R.id.submit);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");


        getdata(id);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String attendance[] = new String[checkBox.length];


                for (int i = 0; i < checkBox.length; i++) {

                    if (checkBox[i].isChecked()) {
                        attendance[i] = "P ";

                    } else {
                        attendance[i] = "A ";

                    }


                }
                if (attendance[checkBox.length - 1].equals("P ")) {
                    attendance[checkBox.length - 1] = "P";

                } else {
                    attendance[checkBox.length - 1] = "A";

                }

                DatePickerDialog dp = new DatePickerDialog(attendance.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d("mytag", Arrays.toString(attendance));

                     //   Toast.makeText(getApplicationContext(),"Attendance = "+Arrays.toString(attendance)+" class_name "+class_name+" id = "+id,Toast.LENGTH_LONG).show();

                      sendPostRequest(Arrays.toString(attendance), "" + dayOfMonth + "-" + (month+1) + "-" + year);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

                dp.getDatePicker().setMaxDate(System.currentTimeMillis());
                dp.setCancelable(false);
                dp.show();


            }
        });


    }

    private void sendPostRequest(String attendance, String date) {

        JSONObject map = new JSONObject();
        try {
            map.put("attendance",attendance);
            map.put("date",date);
            map.put("classname",class_name);
            map.put("id",id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        String url = "http://" + MainActivity.IP + "/Project/attendance.php";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("mytag",response.toString());

                try {
                    String code = (String) response.get("code");

                    if(code.equals("success")){

                        Toast.makeText(getApplicationContext(),"Attendance Marked Succesfully",Toast.LENGTH_LONG).show();
                        finish();

                    }
                    else if(code.equals("Already_exist")){

                        Toast.makeText(getApplicationContext(),"Attendance Already Exist For This Date ",Toast.LENGTH_LONG).show();

                    }
                    else if(code.equals("failed")){
                        Toast.makeText(getApplicationContext(),"Something Went Wrong ",Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(objectRequest);

    }

    private void getdata(String id) {

        progressDialog = new SweetAlertDialog(attendance.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.BLUE);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        JSONObject map = new JSONObject();
        try {
            map.put("id", id);

            String url = "http://" + MainActivity.IP + "/Project/getclass.php";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        String code = (String) response.get("code");

                        if (code.equals("success")) {

                            JSONArray data = response.getJSONArray("data");

                            if(data.length()==2) {

                                int first = Integer.parseInt((String) data.get(0));
                                int last = Integer.parseInt((String) data.get(1));


                                class_name = (String) response.get("class_name");

                                JSONArray attendance = response.getJSONArray("attendance");



                                init(first, last, attendance);
                            }
                            else if(data.length()>2){

                                class_name = (String) response.get("class_name");

                                JSONArray attendance = response.getJSONArray("attendance");



                             init1(data,attendance); // for customized roll numbers;

                            }
                            progressDialog.dismiss();
                        } else {

                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            });

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void init1(JSONArray rollnumbers,JSONArray attendance){


        int attendance_count[] = new int[rollnumbers.length()];

        int k = 0;
        while(k<attendance.length()){

            for(int j=0;j<attendance_count.length;j++){

                try {

                    if(attendance.getJSONArray(k).get(j).equals(" P") || attendance.getJSONArray(k).get(j).equals("P ") || attendance.getJSONArray(k).get(j).equals(" P ")){

                        attendance_count[j] = attendance_count[j]+1;

                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            k++;
        }



        TableLayout tableLayout = findViewById(R.id.table_layout);

        TableRow tableRow1 = new TableRow(this); // create a new TableRow
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);


        TextView tv1 = new TextView(this);
        TableRow.LayoutParams tp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tp1.weight = 4;
        tp1.gravity = Gravity.CENTER;
        tv1.setLayoutParams(tp1);
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextColor(Color.WHITE);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setText("Roll No.");
        tv1.setTextSize(18);


        TextView tv2 = new TextView(this);
        tv2.setText("Absent/Present");
        tv2.setTextColor(Color.WHITE);
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(18);
        tv2.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.weight = 4;
        layoutParams1.gravity = Gravity.CENTER;
        tv2.setLayoutParams(layoutParams1);
        linearLayout.addView(tv2);


        TextView tv3 = new TextView(this);
        TableRow.LayoutParams textParams3 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        textParams3.weight = 4;
        textParams3.gravity = Gravity.CENTER;
        tv3.setLayoutParams(textParams3);
        tv3.setGravity(Gravity.CENTER);
        tv3.setText("Presents/Total");
        tv3.setTextColor(Color.WHITE);
        tv3.setTextSize(18);
        tv3.setTypeface(null, Typeface.BOLD);

        tableRow1.addView(tv1);
        tableRow1.addView(linearLayout);
        tableRow1.addView(tv3);
        tableLayout.addView(tableRow1);


        TableRow.LayoutParams p1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        p1.gravity = Gravity.CENTER;
        p1.weight = 4;

        checkBox = new CheckBox[rollnumbers.length()];

        int ind = 0;

        for (int i = 0; i < rollnumbers.length(); i++) {


            TableRow tableRow = new TableRow(this);
            LinearLayout linearLayout1 = new LinearLayout(this);
            linearLayout1.setOrientation(LinearLayout.VERTICAL);
            linearLayout1.setGravity(Gravity.CENTER);


            checkBox[i] = new CheckBox(this);
            checkBox[i].setGravity(Gravity.CENTER);
            checkBox[i].setId(i);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 4;
            layoutParams.gravity = Gravity.CENTER;
            checkBox[i].setLayoutParams(layoutParams);
            linearLayout1.addView(checkBox[i]);

            TextView textView = new TextView(this);
            TableRow.LayoutParams textParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            textParams.weight = 4;
            textParams.gravity = Gravity.CENTER;
            textView.setLayoutParams(textParams);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(16);
            textView.setTextColor(Color.BLACK);
            try {
                textView.setText(""+rollnumbers.get(i));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            TextView textView1 = new TextView(this);
            TableRow.LayoutParams textParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            textParams1.weight = 4;
            textParams1.gravity = Gravity.CENTER;
            textView1.setLayoutParams(textParams1);
            textView1.setGravity(Gravity.CENTER);
            textView1.setText(""+attendance_count[ind]+"/"+attendance.length());
            textView1.setTextSize(16);
            textView1.setTextColor(Color.BLACK);
            textView1.setTypeface(null, Typeface.BOLD);


            tableRow.addView(textView);
            tableRow.addView(linearLayout1);
            tableRow.addView(textView1);
            tableLayout.addView(tableRow);

        ind++;
        }


    }
    private void init(int first, int last,JSONArray attendance) {

        int attendance_count[] = new int[last-first+1];

        int k = 0;
        while(k<attendance.length()){



            for(int j=0;j<attendance_count.length;j++){

                try {

                    if(attendance.getJSONArray(k).get(j).equals(" P") || attendance.getJSONArray(k).get(j).equals("P ") || attendance.getJSONArray(k).get(j).equals(" P ")){

                        attendance_count[j] = attendance_count[j]+1;

                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

           }

            k++;
        }




        int first_roll = first;
        int last_roll = last;

        TableLayout tableLayout = findViewById(R.id.table_layout);

        TableRow tableRow1 = new TableRow(this); // create a new TableRow
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);


        TextView tv1 = new TextView(this);
        TableRow.LayoutParams tp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tp1.weight = 4;
        tp1.gravity = Gravity.CENTER;
        tv1.setLayoutParams(tp1);
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextColor(Color.WHITE);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setText("Roll No.");
        tv1.setTextSize(18);


        TextView tv2 = new TextView(this);
        tv2.setText("Absent/Present");
        tv2.setTextColor(Color.WHITE);
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(18);
        tv2.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.weight = 4;
        layoutParams1.gravity = Gravity.CENTER;
        tv2.setLayoutParams(layoutParams1);
        linearLayout.addView(tv2);


        TextView tv3 = new TextView(this);
        TableRow.LayoutParams textParams3 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        textParams3.weight = 4;
        textParams3.gravity = Gravity.CENTER;
        tv3.setLayoutParams(textParams3);
        tv3.setGravity(Gravity.CENTER);
        tv3.setText("Presents/Total");
        tv3.setTextColor(Color.WHITE);
        tv3.setTextSize(18);
        tv3.setTypeface(null, Typeface.BOLD);

        tableRow1.addView(tv1);
        tableRow1.addView(linearLayout);
        tableRow1.addView(tv3);
        tableLayout.addView(tableRow1);


        TableRow.LayoutParams p1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        p1.gravity = Gravity.CENTER;
        p1.weight = 4;

        checkBox = new CheckBox[(last_roll - first_roll) + 1];
        int ind = 0;


        for (int i = first_roll; i <= last_roll; i++) {


            TableRow tableRow = new TableRow(this);
            LinearLayout linearLayout1 = new LinearLayout(this);
            linearLayout1.setOrientation(LinearLayout.VERTICAL);
            linearLayout1.setGravity(Gravity.CENTER);


            checkBox[ind] = new CheckBox(this);
            checkBox[ind].setGravity(Gravity.CENTER);
            checkBox[ind].setId(i);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 4;
            layoutParams.gravity = Gravity.CENTER;
            checkBox[ind].setLayoutParams(layoutParams);
            linearLayout1.addView(checkBox[ind]);

            TextView textView = new TextView(this);
            TableRow.LayoutParams textParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            textParams.weight = 4;
            textParams.gravity = Gravity.CENTER;
            textView.setLayoutParams(textParams);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(16);
            textView.setTextColor(Color.BLACK);
            textView.setText("" + i);



            TextView textView1 = new TextView(this);
            TableRow.LayoutParams textParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            textParams1.weight = 4;
            textParams1.gravity = Gravity.CENTER;
            textView1.setLayoutParams(textParams1);
            textView1.setGravity(Gravity.CENTER);
            textView1.setText(""+attendance_count[ind]+"/"+attendance.length());
            textView1.setTextSize(16);
            textView1.setTextColor(Color.BLACK);
            textView1.setTypeface(null, Typeface.BOLD);


            tableRow.addView(textView);
            tableRow.addView(linearLayout1);
            tableRow.addView(textView1);
            tableLayout.addView(tableRow);

            ind++;
        }


    }
}