package com.menowattge.lightpointscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    final int MIN_PASSWORD_LENGTH = 7; // altrimenti tecnico non funziona e non mi va di cambiarla
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        viewInitializations();
    }

    void viewInitializations() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        // To show back button in actionbar
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Checking if the input in form is valid
    boolean validateInput() {

        if (etEmail.getText().toString().equals("")) {
            etEmail.setError("Please Enter Email");
            return false;
        }
        if (etPassword.getText().toString().equals("")) {
            etPassword.setError("Please Enter Password");
            return false;
        }

        // checking the proper email format
        if (!isEmailValid(etEmail.getText().toString())) {
            etEmail.setError("Please Enter Valid Email");
            return false;
        }

        // checking minimum password Length
        if (etPassword.getText().length() < MIN_PASSWORD_LENGTH) {
            etPassword.setError("Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters");
            return false;
        }

        return true;
    }

    boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    /**
     * Effettua il login al portale
     * @param retrofit
     */
    public void login(Retrofit retrofit,String token){
        JsonApi login = retrofit.create(JsonApi.class);

        Call<Void> call_login = login.loginCitymonitor(new Login(email, password),token);

        call_login.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    try {

                        String rc = String.valueOf(response.code());
                        Log.d("http_rc_login : ",rc);
                        Toast.makeText(getApplicationContext(),"Benvenuto", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), PreQrCodeActivity.class);
                        startActivity(intent);

                        // TODO passare credenziali d'accesso alle altre activity fino a senddata
                        // TODO perche le operazioni vanno effettuate con lo stesso utente che logga

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    try {

                        String rc = String.valueOf(response.code());
                        Log.d("http_rc_ko : ",rc);
                        Toast.makeText(getApplicationContext(),"Username o Password errata ", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // handle error
                Log.d("http_error : ",t.getMessage());
            }
        });

    }

    public void performSignUp (View v) {

        if (validateInput()) {

             email = etEmail.getText().toString();
             password = etPassword.getText().toString();


            // ---------TODO DEBUG -----------------


                // debug log http
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://citymonitor.azurewebsites.net/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                //-------------- end-debug -----------------

                String token = LoginCredentials.getAuthToken(email,password);

                login(retrofit,token);



        }
    }

    public void goToSignup(View v) {

        // Open your SignUp Activity if the user wants to signup
        // Visit this article to get SignupActivity code https://handyopinion.com/signup-activity-in-android-studio-kotlin-java/
      //  Intent intent = new Intent(this, SignupActivity.class);
      //  startActivity(intent);

    }
}
