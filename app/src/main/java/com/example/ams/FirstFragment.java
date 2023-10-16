package com.example.ams;

import android.location.GnssAntennaInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FirstFragment extends Fragment {

     EditText e1,e2,e3;
     Button b1;

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_first, container, false);
        e1 = view.findViewById(R.id.first_roll);
        e2 = view.findViewById(R.id.last_roll);
        e3 = view.findViewById(R.id.class_name);

        b1 = view.findViewById(R.id.createclass);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String first_roll = String.valueOf(e1.getText());
                String last_roll = String.valueOf(e2.getText());

                String class_name = e3.getText().toString();

                Pattern p = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$",Pattern.CASE_INSENSITIVE);

                Matcher m = p.matcher(class_name);

                if(first_roll.isEmpty()){

                    e1.requestFocus();
                    e1.setFocusableInTouchMode(true);
                    e1.setError("This field is required");

                }
                else if(last_roll.isEmpty()){
                    e2.requestFocus();
                    e2.setFocusableInTouchMode(true);
                    e2.setError("This field is required");

                }

                else if(Integer.parseInt(first_roll) == Integer.parseInt(last_roll) ){

                    e1.requestFocus();
                    e1.setFocusableInTouchMode(true);;
                    e1.setError("Both Roll Numbers cant be same");

                }
                else if(Integer.parseInt(first_roll) > Integer.parseInt(last_roll)){

                    e2.requestFocus();
                    e2.setFocusableInTouchMode(true);
                    e2.setError("This field needs a higher value than the above field");

                }
                else if(class_name.isEmpty()){

                    e3.requestFocus();
                    e3.setFocusableInTouchMode(true);
                    e3.setError("This field is required");
                }
                else if(class_name.contains(" ")){
                    e3.requestFocus();
                    e3.setFocusableInTouchMode(true);
                    e3.setError("Spaces are not allowed, Use underscore(_)");

                }
                else if(!m.find()){
                    e3.requestFocus();
                    e3.setFocusableInTouchMode(true);
                    e3.setError("Class Name should begins with english alphabet and contains only digits, alphabets and underscore(_)");

                }
                else{

                 sendPostRequest(first_roll,last_roll,class_name);

                }


            }
        });

        return view;
    }

    private void sendPostRequest(String first_roll, String last_roll, String class_name) {

        try{
           String url = "http://"+MainActivity.IP+"/Project/createclass.php";
            JSONObject map = new JSONObject();
            map.put("first_roll",first_roll);
            map.put("last_roll",last_roll);
            map.put("class_name",class_name.toUpperCase());
            map.put("id",First_Page.ID);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject result) {
                        try {

                            String code = (String) result.get("code");

                            if(code.equals("500")){

                                Toast.makeText(getActivity(),"This Class already exist with the same name",Toast.LENGTH_LONG).show();

                            }
                            else if(code.equals("success")){

                                Toast.makeText(getActivity(),"Class Created Succesfully",Toast.LENGTH_LONG).show();
                                getActivity().finish();

                            }
                            else{

                                Toast.makeText(getActivity(),"In else"+result.toString(),Toast.LENGTH_LONG).show();
                            }


                        }
                        catch (JSONException ex){

                            Toast.makeText(getActivity(),"Error"+ex,Toast.LENGTH_LONG).show();
                        }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getActivity(),"Error"+error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        }
        catch (Exception ex){

            Toast.makeText(getActivity(),""+ex,Toast.LENGTH_LONG).show();

        }
    }
}