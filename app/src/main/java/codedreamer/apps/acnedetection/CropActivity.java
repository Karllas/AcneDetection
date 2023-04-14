package codedreamer.apps.acnedetection;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CropActivity extends AppCompatActivity {
    CropUtils.CropImageView imageview1;
    CheckBox ch1,ch2,ch3,ch4,ch5;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Button button1 = findViewById(R.id.done_button);
        imageview1 = findViewById(R.id.cropimageview);
        imageview1.setImageBitmap(getBitmapFromCache());

        ch1 = findViewById(R.id.radio_leftcheek);
        ch2 = findViewById(R.id.radio_nose);
        ch3 = findViewById(R.id.radio_rightcheek);
        ch4 = findViewById(R.id.radio_chin);
        ch5 = findViewById(R.id.radio_forehead);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(ch1.isChecked()) && !(ch2.isChecked()) && !(ch3.isChecked()) && !(ch4.isChecked()) && !(ch5.isChecked()))
                {
                    Toast.makeText(CropActivity.this, "Please Select one of Area Names", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(ch1.isChecked())
                        Global.area = Global.area + ch1.getText().toString() + ", ";
                    if(ch2.isChecked())
                        Global.area = Global.area + ch2.getText().toString() + ", ";
                    if(ch3.isChecked())
                        Global.area = Global.area + ch3.getText().toString() + ", ";
                    if(ch4.isChecked())
                        Global.area = Global.area + ch4.getText().toString() + ", ";
                    if(ch5.isChecked())
                        Global.area = Global.area + ch5.getText().toString() + ", ";

                    Bitmap bitmap = imageview1.getCroppedImage();
                    try {
                        saveBitmapToCache(bitmap);
                    } catch (IOException e) {
                        Log.e("tag", e.toString());
                    }
                    startActivity(new Intent(CropActivity.this, MetaData.class));
                    finish();
                }
            }
        });
    }

    public void saveBitmapToCache(Bitmap bitmap) throws IOException {
        String filename = "final_image.jpg";
        File cacheFile = new File(getApplicationContext().getCacheDir(), filename);
        OutputStream out = new FileOutputStream(cacheFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, (int)100, out);
        out.flush();
        out.close();
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
        Toast.makeText(CropActivity.this, "Diagnosis Cancelled", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}
