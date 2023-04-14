package codedreamer.apps.acnedetection;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Results extends AppCompatActivity
{
    ImageView finalAreaImage;
    TextView areaName, diagnosisResult, advice, medication;
    Button back_to_home, save;
    ImageButton download;
    FirebaseAuth mAuth;
    FirebaseFirestore mfirebaseFirestore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        diagnosisResult = findViewById(R.id.diagnosisResults);
        advice = findViewById(R.id.adviceTextView);
        medication = findViewById(R.id.medicineTextView);
        areaName = findViewById(R.id.areaName);
        finalAreaImage = findViewById(R.id.finalImage);
        download = findViewById(R.id.download);
        back_to_home = findViewById(R.id.back_to_home);
        save = findViewById(R.id.save_diagnosis);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Saving Data...");
        progressDialog.setCancelable(false);
        diagnosisResult.setText(Global.acneType);
        advice.setText(Global.Advice);
        medication.setText(Global.Medication);
        areaName.setText(Global.area);
        finalAreaImage.setImageBitmap(getBitmapFromCache());

        mAuth = FirebaseAuth.getInstance();
        mfirebaseFirestore = FirebaseFirestore.getInstance();
        back_to_home.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Results.this, Shooting.class));
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view)
            {
                if(mAuth.getCurrentUser() != null)
                {
                    Map<String, Object> diagnosis = new HashMap<>();
                    diagnosis.put("acneType", Global.acneType);
                    diagnosis.put("area", Global.area);
                    diagnosis.put("age", Global.age);
                    diagnosis.put("sleepingHours", Global.SleepingHours);
                    diagnosis.put("gender", Global.gender);
                    diagnosis.put("skinType", Global.skinType);
                    diagnosis.put("spicyFood", Global.spicyFoodStatus);
                    diagnosis.put("advice", Global.Advice);
                    diagnosis.put("medication", Global.Medication);
                    diagnosis.put("date", new SimpleDateFormat("dd/MMM/yyyy").format(new Date()));
                    diagnosis.put("time", new SimpleDateFormat("hh:mm:ss a").format(new Date()));

                    progressDialog.show();
                    mfirebaseFirestore.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).collection("diagnosis").add(diagnosis).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                    {
                        @Override
                        public void onSuccess(DocumentReference documentReference)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(Results.this, "Results added in History", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(Results.this, "Results can't be added: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        download.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view)
            {
                PdfDocument pdfDocument = new PdfDocument();
                // start page for our PDF file.
                // pageHeight and number of pages
                PdfDocument.Page myPage = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(792, 1120, 1).create());

                //Extra paint object for drawing logo
                Paint drawLogo = new Paint();
                drawLogo.setTextSize(50);
                drawLogo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                Paint headerText = new Paint();
                headerText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                headerText.setTextSize(15);
                headerText.setColor(ContextCompat.getColor(Results.this, R.color.black));

                Paint mainText =   new Paint();
                mainText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                mainText.setTextSize(20);
                mainText.setColor(ContextCompat.getColor(Results.this, R.color.black));

                //Creating canvas for drawing
                Canvas canvas = myPage.getCanvas();

                //Draw Logo
                canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo), 500, 500, false), 180, 60, drawLogo);
                //Draw Header Text
                canvas.drawText(new SimpleDateFormat("hh:mm:ss a").format(new Date()), 40,40,headerText);
                canvas.drawText(new SimpleDateFormat("dd/MMM/yyyy").format(new Date()), 40,60,headerText);
                //Draw Main Text
                canvas.drawText("Acne Detection", 200, 680, drawLogo);
                canvas.drawText("Face Area(s): " + Global.area, 40, 750, mainText);
                canvas.drawText("Advice: " + Global.Advice, 40, 750 + 30, mainText);
                canvas.drawText("Medicine: " + Global.Medication, 40, 750 + 60, mainText);
                canvas.drawText("Acne Type: " + Global.acneType, 40, 750 + 90, mainText);
                canvas.drawText("Gender: " + Global.gender, 40, 750 + 120, mainText);
                canvas.drawText("Sleeping Hours: " + Global.SleepingHours, 40, 750 + 150, mainText);
                canvas.drawText("Age: " + Global.age, 40, 750 + 180, mainText);
                canvas.drawText("Skin Type: " + Global.skinType, 40, 750 + 210, mainText);
                canvas.drawText("Spicy Food: " + Global.spicyFoodStatus, 40, 750 + 240, mainText);
                pdfDocument.finishPage(myPage);

                //Save in Storage
                File file = new File(Environment.getExternalStorageDirectory(), "My Acne Report (" + new SimpleDateFormat("yyyy_MMM_dd_hhmm").format(new Date())+ ").pdf");

                try {
                    // after creating a file name we will
                    // write our PDF file to that location.
//                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    sharingIntent.setType("PDF");
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(Results.this, "111",file));
//                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    pdfDocument.writeTo(new FileOutputStream(file));

                    // below line is to print toast message
                    // on completion of PDF generation.
                    Toast.makeText(Results.this, "PDF file saved successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    // below line is used
                    // to handle error
                    e.printStackTrace();
                }
                // after storing our pdf to that
                // location we are closing our PDF file.
                pdfDocument.close();
            }
        });
    }

    public Bitmap getBitmapFromCache(){
        File cacheFile = new File(getApplicationContext().getCacheDir(), "final_image.jpg");
        Bitmap myBitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        return myBitmap;
    }

    @Override
    public void onBackPressed()
    {
        finish();
        Toast.makeText(Results.this, "Diagnosis Cancelled", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    protected void onResume()
    {
        diagnosisResult.setText(Global.acneType);
        advice.setText(Global.Advice);
        medication.setText(Global.Medication);
        areaName.setText(Global.area);
        finalAreaImage.setImageBitmap(getBitmapFromCache());
        super.onResume();
    }
}