package codedreamer.apps.acnedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import codedreamer.apps.acnedetection.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity
{
    ActivitySignInBinding signin;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        signin = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signin.getRoot());
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Signing In...");
        progressDialog.setCancelable(false);
        signin.idForgotPasswordTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        signin.idSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                loginUser();
            }
        });

        signin.idSignUpTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(SignIn.this,SignUp.class));
            }
        });
    }

    private void loginUser()
    {
        String strEmail = signin.idEditTextEmailAddress.getText().toString();
        String strPassword = signin.idEditTextPassword.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(strEmail.isEmpty() || !strEmail.matches(emailPattern))
        {
            signin.idEditTextEmailAddress.setError("Please Enter valid Email Address");
            signin.idEditTextEmailAddress.requestFocus();
        }
        else if(strPassword.isEmpty())
        {
            signin.idEditTextPassword.setError("Please Enter Your Password");
            signin.idEditTextPassword.requestFocus();
        }
        else
        {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>()
            {
                @Override
                public void onSuccess(AuthResult authResult)
                {
                    Toast.makeText(SignIn.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignIn.this,TreatmentCenter.class));
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(mAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(SignIn.this,TreatmentCenter.class));
            finish();
        }
    }
}