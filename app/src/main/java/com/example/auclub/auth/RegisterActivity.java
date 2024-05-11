package com.example.auclub.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.auclub.R;
import com.example.auclub.databinding.ActivityRegisterBinding;
import com.example.auclub.model.HelperClass;
import com.example.auclub.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    Animation rotate;
    String name, email, phone, password;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        binding.ivLogo.setAnimation(rotate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        binding.llBottom.setOnClickListener(view ->
                finish());

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()){
                    registerUser();
                }
            }
        });

    }

    private void registerUser() {
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            UserModel model = new UserModel(auth.getCurrentUser().getUid(), name, email, phone, password, "");
            dbRefUsers.child(auth.getCurrentUser().getUid()).setValue(model).addOnCompleteListener(task -> {
                HelperClass.users = model;
                progressDialog.dismiss();
                showMessage("Registered Successfully");
                Intent intent = new Intent(RegisterActivity.this, PhoneVerificationActivity.class);
                startActivity(intent);
                finishAffinity();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getLocalizedMessage());
        });
    }
    private Boolean isValidated() {
        name = binding.userNameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
        password = binding.passET.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Please enter userName");
            return false;
        }
        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (phone.isEmpty()) {
            showMessage("Please enter phone");
            return false;
        }
        if (password.isEmpty()) {
            showMessage("Please enter password");
            return false;
        }

        return true;
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}