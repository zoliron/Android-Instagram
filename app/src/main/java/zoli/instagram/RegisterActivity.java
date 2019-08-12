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

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        // Listens when login is clicked and changes view from RegisterActivity to LoginActivity
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // Register button listener - performing registration upon clicking
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                // Checks if the parameters are not empty and password is bigger than 6 characters
                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password must have atleast 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    UserApi.register(pd, str_username, str_fullname, str_email, str_password, RegisterActivity.this, RegisterActivity.this);
                }
            }
        });
    }
}
