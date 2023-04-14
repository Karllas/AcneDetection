package codedreamer.apps.acnedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MetaData extends AppCompatActivity
{
    Button complete;
    RadioGroup Gender, SkinType, SpicyFood;
    EditText Age, SleepTime;
    ImageView imageView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meta_data);
        Age = findViewById(R.id.id_editTextAge);
        SleepTime = findViewById(R.id.id_editTextSleepTime);
        Gender = findViewById(R.id.radioGroup_Gender);
        SkinType = findViewById(R.id.radioGroup_SkinType);
        SpicyFood = findViewById(R.id.radioGroup_SpicyFood);
        complete = findViewById(R.id.done_button);
        imageView = findViewById(R.id.my_image);
        imageView.setImageBitmap(getBitmapFromCache());

        complete.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view)
            {
                String strAge = Age.getText().toString();
                String strSleepTime = SleepTime.getText().toString();
                if(TextUtils.isEmpty(strAge))
                {
                    Age.setError("Please Enter Your Age");
                    Age.requestFocus();
                }
                else if (Integer.parseInt(strAge) < 13 || Integer.parseInt(strAge) > 30)
                {
                    Age.setError("Please enter age between 13 to 30");
                    Age.requestFocus();
                }
                else if(TextUtils.isEmpty(strSleepTime))
                {
                    Age.setError("Please Enter Your Sleep Hours");
                    Age.requestFocus();
                }
                else if (Integer.parseInt(strSleepTime) < 1 || Integer.parseInt(strSleepTime) > 24)
                {
                    Age.setError("Please Enter Valid Sleep Hours");
                    Age.requestFocus();
                }
                else if(Gender.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(MetaData.this, "Please Select Your Gender", Toast.LENGTH_SHORT).show();
                }
                else if(SkinType.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(MetaData.this, "Please Select Your Skin Type", Toast.LENGTH_SHORT).show();
                }
                else if(SpicyFood.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(MetaData.this, "Please Select Spicy Food Choice", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Global.age = strAge;
                    Global.SleepingHours = strSleepTime;
                    Global.gender = (String)((RadioButton) Gender.findViewById(Gender.getCheckedRadioButtonId())).getText();
                    Global.skinType = (String)((RadioButton) SkinType.findViewById(SkinType.getCheckedRadioButtonId())).getText();
                    Global.spicyFoodStatus = (String)((RadioButton) SpicyFood.findViewById(SpicyFood.getCheckedRadioButtonId())).getText();
                    check(Integer.parseInt(strAge), Global.gender, Global.skinType, Global.spicyFoodStatus);

                    startActivity(new Intent(MetaData.this, Results.class));
                    finish();
                }
            }
        });
    }

    public void check(int userAge, String Gender, String SkinType, String EatSpicy)
    {
        if(userAge < 13)
        {
            Global.Advice = "This age has no acne";
            Global.acneType = "Nil";
            Global.Medication = "Nil";

        }

        else
        {
            if (Objects.equals(SkinType, "Oily"))
            {
                if (userAge <= 18)
                {
                    Global.Advice = "Avoid fast foods,drinks and don’t use any hair oil";
                }
                else
                {
                    Global.Advice = "Avoid fast foods,drinks and don’t use any hair oil,sleep at least 5-8 hours";
                }

                //Gender is Female
                if(Objects.equals(Gender, "Female"))
                {
                    switch (userAge)
                    {
                        case 13: case 14: case 15:
                        Global.Medication = "Isotretinoin Capsules,Clindamycin Lotion,Acnelene Facewash";
                        break;

                        case 16: case 17: case 18:
                        Global.Medication = "Isotretinoin Capsules,Pinsyle Peroxid,Glytone Mild Gel Cleanser";
                        break;

                        case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29: case 30:
                        Global.Medication = "Isotretinoin Capsules, Acnelene Facewash ,Azythromycine Lotion";
                        break;

                    }
                }

                //Gender is Male and EatSpicy
                else if(Objects.equals(Gender, "Male") && Objects.equals(EatSpicy, "Yes"))
                {
                    switch (userAge)
                    {
                        case 13: case 14: case 15:
                        Global.Medication = "Isotretinoin capsules,Clindamycin lotion,Glytone Mild Gel Cleanser";
                        break;

                        case 16: case 17: case 18:
                        Global.Medication = "Isotretinoin capsules, Acnelene facewash ,Clindamycin lotion";
                        break;

                        case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29: case 30:
                        Global.Medication = "isotretinoin capsules, acnelene facewash ,Azythromycine lotion";
                        break;

                    }
                }
                //Gender is Male and do not EatSpicy
                else if(Objects.equals(Gender, "Male") && Objects.equals(EatSpicy, "No"))
                {
                    switch (userAge)
                    {
                        case 13: case 14: case 15:
                        Global.Medication = "Isotretinoin capsules,Pinsyle peroxid,Acnelene facewash";
                        break;

                        case 16: case 17: case 18:
                        Global.Medication = "Isotretinoin capsules, Acnelene facewash ,Clindamycin lotion";
                        break;

                        case 19: case 20: case 21: case 22: case 23: case 24:
                        Global.Medication = "Isotretinoin capsules, Acnelene Facewash ,Azythromycine lotion";
                        break;

                        case 25: case 26: case 27: case 28: case 29: case 30:
                        Global.Medication = "Isotretinoin capsules, Azythromycine lotion,Humane acne facewash";
                        break;
                    }
                }
            }

            else
            {
                Global.Advice = "Dry skin has no acne it is just pimple and spot";
                Global.acneType = "Nil";
                Global.Medication = "Nil";
            }
        }
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
        Toast.makeText(MetaData.this, "Diagnosis Cancelled", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}