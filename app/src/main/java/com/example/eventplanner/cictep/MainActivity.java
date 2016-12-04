package com.example.eventplanner.cictep;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventplanner.cictep.database.DBHelper;
import com.example.eventplanner.cictep.database.DBInfo.TableUser;
import com.example.eventplanner.cictep.database.DBProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText inpUsername, inpPassword;

    DBHelper dbHelper;

    boolean isPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        Cursor cursor =
                DBProvider.fetchUser(dbHelper, new String[] {"*"}, TableUser.ISLOGGEDIN, "1");
        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            String userType = cursor.getString(cursor.getColumnIndex(TableUser.USER_TYPE));
            if ("0".equals(userType)) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            } else if ("1".equals(userType)) {
                startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
            }
        }

        inpUsername = (EditText) findViewById(R.id.inpUsernameL);
        inpPassword = (EditText) findViewById(R.id.inpPasswordL);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(
                        getString(R.string.link_login),
                        inpUsername.getText().toString(),
                        inpPassword.getText().toString()
                );
            }
        });

        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });
    }

    private void login(String url, String username, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Cursor cursor =
                DBProvider.fetchUser(dbHelper, new String[] {"*"}, TableUser.USERNAME, username);
        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            isPresent = true;
        }

        JSONArray arrayParams = new JSONArray();
        JSONObject objectParams = new JSONObject();
        try {
            objectParams.put("username", username);
            objectParams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.POST, url, arrayParams.put(objectParams),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RES", response.toString());
                        try {
                            if (!response.isNull(0)) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String firstname = jsonObject.getString("firstname");
                                    String lastname = jsonObject.getString("lastname");
                                    String username = jsonObject.getString("username");
                                    String password = jsonObject.getString("password");
                                    String user_type = jsonObject.getString("user_type");

                                    if (response.length() > 0) {
                                        if (isPresent) {
                                            DBProvider.updateUser(
                                                    dbHelper,
                                                    TableUser.ISLOGGEDIN, "1",
                                                    TableUser.USERNAME, username);
                                        } else {
                                            DBProvider.insertUser(dbHelper,
                                                    firstname, lastname, username, password,
                                                    user_type, "1");
                                        }

                                        if ("0".equals(user_type)) {
                                            startActivity(new Intent(MainActivity.this,
                                                    HomeActivity.class));
                                        } else if ("1".equals(user_type)) {
                                            startActivity(new Intent(MainActivity.this,
                                                    AdminHomeActivity.class));
                                        }
                                    }
                                }
                            } else {
                                dialogError("Login Error", "Username or password is incorrect!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VERROR", error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void dialogError(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
