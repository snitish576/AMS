package com.example.ams;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecondFragment extends Fragment {

    EditText e1;
    EditText e2;

    Button bt;

    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container, false);
        e1 = view.findViewById(R.id.classname);
        e2 = view.findViewById(R.id.roll_number);

        bt = view.findViewById(R.id.btn2);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String class_name = e1.getText().toString();
                String roll_no = e2.getText().toString().trim();

                Pattern p = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$", Pattern.CASE_INSENSITIVE);

                Matcher m = p.matcher(class_name);

                String roll_number[] = roll_no.split(" ");
                HashSet<Integer> set = new HashSet<>();

                boolean flag = true;

                for (int i = 0; i < roll_number.length; i++) {

                    if (!roll_number[i].trim().isEmpty()) {

                        int rollnumber = Integer.parseInt(roll_number[i].trim());

                        if (set.add(rollnumber)) {
                            // Adding the values in HashSet

                        } else {
                            flag = false;
                            break;
                        }

                    }
                }

                if (!flag) {
                    e2.requestFocus();
                    e2.setFocusableInTouchMode(true);
                    e2.setError("Roll Numbers should be unique");
                }
                else if(set.size()<=2){
                    e2.requestFocus();
                    e2.setFocusableInTouchMode(true);
                    e2.setError("Class should have more than 2 roll numbers");

                }
                else if (class_name.isEmpty()) {

                    e1.requestFocus();
                    e1.setFocusableInTouchMode(true);
                    e1.setError("Class Name is required");

                } else if (roll_no.isEmpty()) {

                    e2.requestFocus();
                    e2.setFocusableInTouchMode(true);
                    e2.setError("Roll Numbers are required");

                } else if (class_name.contains(" ")) {

                    e1.requestFocus();
                    e1.setFocusableInTouchMode(true);
                    e1.setError("Spaces are not allowed, Use underscore(_)");

                } else if (!m.find()) {

                    e1.requestFocus();
                    e1.setFocusableInTouchMode(true);
                    e1.setError("Class Name should begins with english alphabet and contains only digits, alphabets and underscore(_)");

                } else {

                  //  Log.d("mytag",set.toString().substring(1, set.toString().length() - 1));

                    sendPostRequest(set.toString().substring(1, set.toString().length() - 1), class_name);


                }


            }
        });


        return view;
    }

    private void sendPostRequest(String     roll_no, String class_name) {

        try {
            String url = "http://" + MainActivity.IP + "/Project/createclass.php";
            JSONObject map = new JSONObject();
            map.put("roll_no", roll_no);
            map.put("class_name", class_name.toUpperCase());
            map.put("id", First_Page.ID);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject result) {
                    try {

                        String code = (String) result.get("code");

                        if (code.equals("500")) {

                            Toast.makeText(getActivity(), "This Class already exist with the same name", Toast.LENGTH_LONG).show();

                        } else if (code.equals("success")) {

                            Toast.makeText(getActivity(), "Class Created Succesfully", Toast.LENGTH_LONG).show();
                            getActivity().finish();

                        } else {

                            Toast.makeText(getActivity(), "IN ELSE" + result.toString(), Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException ex) {

                        Toast.makeText(getActivity(), "Error" + ex, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getActivity(), "Error" + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception ex) {

            Toast.makeText(getActivity(), "" + ex, Toast.LENGTH_LONG).show();

        }


    }
}