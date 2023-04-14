package codedreamer.apps.acnedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Verify extends AppCompatActivity
{
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        mAuth = FirebaseAuth.getInstance();
        onResume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        onResume();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(mAuth.getCurrentUser() != null)
        {
            Objects.requireNonNull(mAuth.getCurrentUser()).reload().addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void unused)
                {
                    if(mAuth.getCurrentUser().isEmailVerified())
                    {
                        startActivity(new Intent(Verify.this,TreatmentCenter.class));
                        finish();
                    }
                }
            });
        }
    }
}