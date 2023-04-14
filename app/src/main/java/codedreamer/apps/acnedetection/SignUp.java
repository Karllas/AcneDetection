package codedreamer.apps.acnedetection;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import codedreamer.apps.acnedetection.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity
{
    ActivitySignUpBinding signup;
    FirebaseAuth mAuth;
    FirebaseFirestore mfirebaseFirestore;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        signup = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signup.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mfirebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating Your Account...");
        progressDialog.setCancelable(false);
        signup.idSignupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                createUser();
            }
        });

        //Sign Up Button
        signup.idSignInTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(SignUp.this,SignIn.class));
                finish();
            }
        });
    }

    private void createUser()
    {
        String strName = signup.idEditTextPersonName.getText().toString();
        String strEmail = signup.idEditTextEmailAddress.getText().toString();
        String strPassword = signup.idEditTextPassword.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(strName.isEmpty())
        {
            signup.idEditTextPersonName.setError("Please Enter Your Name");
            signup.idEditTextPersonName.requestFocus();
        }
        else if(strEmail.isEmpty() || !strEmail.matches(emailPattern))
        {
            signup.idEditTextEmailAddress.setError("Please Enter valid Email Address");
            signup.idEditTextEmailAddress.requestFocus();
        }
        else if(strPassword.isEmpty())
        {
            signup.idEditTextPassword.setError("Please Enter Your Password");
            signup.idEditTextPassword.requestFocus();
        }
        else
        {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>()
            {
                @Override
                public void onSuccess(AuthResult authResult)
                {
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void unused)
                        {
                            Toast.makeText(SignUp.this, "Email is send to you (Kindly Verify)", Toast.LENGTH_SHORT).show();
                            Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                            String userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = mfirebaseFirestore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", strName);
                            user.put("email", strEmail);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                    Log.d("eee", userID);
                                }
                            });
                            startActivity(new Intent(SignUp.this,Verify.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(SignUp.this, "Email not send: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(SignUp.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}