package com.example.eventplanner.cictep;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventplanner.cictep.database.DBHelper;
import com.example.eventplanner.cictep.database.DBProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    TextInputLayout inpLayFirstName, inpLayLastName, inpLayUsername, inpLayPassword;
    EditText inpFirstName, inpLastName, inpUsername, inpPassword;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DBHelper(this);

        inpLayFirstName = (TextInputLayout) findViewById(R.id.inpLayFirstName);
        inpLayLastName = (TextInputLayout) findViewById(R.id.inpLayLastName);
        inpLayUsername = (TextInputLayout) findViewById(R.id.inpLayUsername);
        inpLayPassword = (TextInputLayout) findViewById(R.id.inpLayPassword);

        inpFirstName = (EditText) findViewById(R.id.inpFirstName);
        inpLastName = (EditText) findViewById(R.id.inpLastName);
        inpUsername = (EditText) findViewById(R.id.inpUsername);
        inpPassword = (EditText) findViewById(R.id.inpPassword);

        inpFirstName.addTextChangedListener(new TxtWatcher(inpFirstName));
        inpLastName.addTextChangedListener(new TxtWatcher(inpLastName));
        inpUsername.addTextChangedListener(new TxtWatcher(inpUsername));
        inpPassword.addTextChangedListener(new TxtWatcher(inpPassword));

        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        if (isNameValid(inpFirstName, inpLayFirstName) &&
                isNameValid(inpLastName, inpLayLastName) &&
                isUsernameValid() && isPasswordValid()) {
            sendUserData(
                    getString(R.string.link_register),
                    inpFirstName.getText().toString(),
                    inpLastName.getText().toString(),
                    inpUsername.getText().toString(),
                    inpPassword.getText().toString());
        }
    }

    private void
    sendUserData(String url, String firstname, String lastname, String username, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject objectParams = new JSONObject();
        try {
            objectParams.put("firstname", firstname);
            objectParams.put("lastname", lastname);
            objectParams.put("username", username);
            objectParams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, objectParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success = response.getString("success");
                            if ("1".equals(success)) {
                                DBProvider.insertUser(
                                        dbHelper,
                                        inpFirstName.getText().toString(),
                                        inpLastName.getText().toString(),
                                        inpUsername.getText().toString(),
                                        inpPassword.getText().toString(),
                                        "0", "0");

                                Toast.makeText(getApplicationContext(),
                                        "Registered successfully!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(
                                        RegistrationActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(RegistrationActivity.this,
                                        "Registration failed!", Toast.LENGTH_SHORT).show();
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

    private boolean isNameValid(EditText editText, TextInputLayout textInputLayout) {
        String input = editText.getText().toString().trim();
        if (input.isEmpty()) {
            textInputLayout.setError(getString(R.string.err_empty));
            focus(editText);
            return false;
        } else if (!validateName(input)) {
            textInputLayout.setError(getString(R.string.err_name));
            focus(editText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean isUsernameValid() {
        String username = inpUsername.getText().toString().trim();
        if (username.isEmpty()) {
            inpLayUsername.setError(getString(R.string.err_empty));
            focus(inpUsername);
            return false;
        } else if (username.length() < 6) {
            inpLayUsername.setError(getString(R.string.err_username_length));
            focus(inpUsername);
            return false;
        } else if (!validateUsername(username)) {
            inpLayUsername.setError(getString(R.string.err_username));
            focus(inpUsername);
            return false;
        } else {
            inpLayUsername.setErrorEnabled(false);
        }

        return true;
    }

    private boolean isPasswordValid() {
        String password = inpPassword.getText().toString().trim();
        if (password.isEmpty()) {
            inpLayPassword.setError(getString(R.string.err_empty));
            focus(inpPassword);
            return false;
        } else if (password.length() < 6) {
            inpLayPassword.setError(getString(R.string.err_password_length));
            focus(inpPassword);
            return false;
        } else {
            inpLayPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName(String name) {
        return !TextUtils.isEmpty(name) && Pattern.matches("^[a-zA-Z]+$", name);
    }

    private boolean validateUsername(String username) {
        return !TextUtils.isEmpty(username) && Pattern.matches("^[a-zA-Z0-9]*$", username);
    }

    private void focus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private String encryptedPassword(String password) {
        byte[] bytes = Base64.encode(password.getBytes(), Base64.DEFAULT);
        String encryptedPass = new String(bytes);
        return encryptedPass.substring(0, encryptedPass.length() - 1);
    }

    class TxtWatcher implements TextWatcher {
        View view;

        TxtWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (this.view.getId()) {
                case R.id.inpFirstName:
                    isNameValid(inpFirstName, inpLayFirstName);
                    break;
                case R.id.inpLastName:
                    isNameValid(inpLastName, inpLayLastName);
                    break;
                case R.id.inpUsername:
                    isUsernameValid();
                    break;
                case R.id.inpPassword:
                    isPasswordValid();
                    break;
            }
        }
    }
}
