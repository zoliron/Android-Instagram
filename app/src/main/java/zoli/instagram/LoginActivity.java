package zoli.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import zoli.instagram.Api.UserApi;


public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView txt_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);

        // Listens when signup is clicked and changes view from LoginActivity to RegisterActivity
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Login button listener - performing login upon clicking
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Pleas wait...");
                pd.show();

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                // Checks if the email/password is empty
                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    UserApi.loginUser(pd, str_email, str_password, LoginActivity.this, LoginActivity.this);
                }
            }
        });
    }
}
