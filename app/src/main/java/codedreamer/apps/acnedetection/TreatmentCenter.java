package codedreamer.apps.acnedetection;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import codedreamer.apps.acnedetection.databinding.ActivityTreatmentCenterBinding;


public class TreatmentCenter extends AppCompatActivity
{
    ActivityTreatmentCenterBinding treatmentCenter;
    FirebaseAuth mAuth;
    FirebaseFirestore mfirebaseFirestore;
    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        treatmentCenter = ActivityTreatmentCenterBinding.inflate(getLayoutInflater());
        setContentView(treatmentCenter.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mfirebaseFirestore = FirebaseFirestore.getInstance();
        if(mAuth.getCurrentUser() != null)
        {
            if(mAuth.getCurrentUser().getPhotoUrl() != null)
            {
                Glide.with(this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .into(treatmentCenter.idImage);
            }
        }
        treatmentCenter.idShoot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(TreatmentCenter.this,Shooting.class));
            }
        });
        treatmentCenter.idHistory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(TreatmentCenter.this,History.class));
            }
        });
        if (!checkPermission())
            requestPermission();

        //Get image(Profile) from gallery and save in firebase
        ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->
                {
                    Log.d("eee", "10");
                    int resultCode = result.getResultCode();
                    @Nullable Intent data = result.getData();
                    if (resultCode == RESULT_OK)
                    {
                        Log.d("eee", "11");
                        final Uri imageUri = Objects.requireNonNull(data).getData();
                        InputStream imageStream = null;
                        try
                        {
                            imageStream = getContentResolver().openInputStream(imageUri);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                        treatmentCenter.idImage.setImageBitmap(bitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileimages").child(uid + ".jpeg");
                        storageReference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        Log.d("eee", "12");
                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                        {
                                            @Override
                                            public void onSuccess(Uri uri)
                                            {
                                                Log.d("eee", "13");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                        .setPhotoUri(uri)
                                                        .build();

                                                user.updateProfile(request)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>()
                                                        {
                                                            @Override
                                                            public void onSuccess(Void unused)
                                                            {
                                                                Toast.makeText(TreatmentCenter.this, "Profile image updated successfully ", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener()
                                                        {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e)
                                                            {
                                                                Toast.makeText(TreatmentCenter.this, "Profile image not Updated: " + e.getCause(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Log.d("eee", e.getMessage());
                                    }
                                });
                    }

                });

        treatmentCenter.idImage.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                if(intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityIntent.launch(Intent.createChooser(intent, "Select Picture"));
                }
            }
        });

        //Update Email and Name in Database
        treatmentCenter.idEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater inflater = LayoutInflater.from(TreatmentCenter.this);
                View subView = inflater.inflate(R.layout.edit_data, null);
                final EditText enterName = subView.findViewById(R.id.enterName);
                final EditText enterEmail = subView.findViewById(R.id.enterPhoneNum);
                AlertDialog.Builder builder = new AlertDialog.Builder(TreatmentCenter.this);
                builder.setView(subView);
                builder.create();
                mfirebaseFirestore.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        enterName.setText(documentSnapshot.getString("name"));
                        enterEmail.setText(documentSnapshot.getString("email"));
                    }
                });
                enterName.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {
                        final String name = enterName.getText().toString();
                        if (TextUtils.isEmpty(name))
                        {
                            enterName.setError("Please Enter your Name");
                            enterName.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable)
                    {

                    }
                });
                enterEmail.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {
                        final String email = enterEmail.getText().toString();
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                        if (TextUtils.isEmpty(email) || !email.matches(emailPattern))
                    {
                        enterEmail.setError("Please Enter a Valid Email");
                        enterEmail.requestFocus();
                    }
                    }

                    @Override
                    public void afterTextChanged(Editable editable)
                    {

                    }
                });
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        final String name = enterName.getText().toString();
                        final String email = enterEmail.getText().toString();
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                        if (TextUtils.isEmpty(name))
                        {
                            Toast.makeText(TreatmentCenter.this, "Name is Empty", Toast.LENGTH_SHORT).show();
                        }
                        else if (email.isEmpty() || !email.matches(emailPattern))
                        {
                            Toast.makeText(TreatmentCenter.this, "Email is not Valid", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", enterName.getText().toString());
                            user.put("email", enterEmail.getText().toString());
                            mfirebaseFirestore.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).update(user).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                    Toast.makeText(TreatmentCenter.this, "Personal Information Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(TreatmentCenter.this, "Information not Updated: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(TreatmentCenter.this, "Task cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        onResume();
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0)
            {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage)
                {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
                    if(!mAuth.getCurrentUser().isEmailVerified())
                    {
                        startActivity(new Intent(TreatmentCenter.this,Verify.class));
                        finish();
                    }
                }
            });
        }
        String userID = mAuth.getCurrentUser().getUid();
        mfirebaseFirestore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                treatmentCenter.idName.setText("Name: " + documentSnapshot.getString("name"));
                treatmentCenter.idEmail.setText("Email: " + documentSnapshot.getString("email"));
            }
        });
    }
}