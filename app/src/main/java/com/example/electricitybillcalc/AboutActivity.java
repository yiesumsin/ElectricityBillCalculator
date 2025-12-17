package com.example.electricitybillcalc;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        TextView studentName = findViewById(R.id.studentName);
        TextView studentId = findViewById(R.id.studentId);
        TextView courseCode = findViewById(R.id.courseCode);
        TextView courseName = findViewById(R.id.courseName);
        TextView githubLink = findViewById(R.id.githubLink);
        Button aboutBackButton = findViewById(R.id.aboutBackButton);

        //my details
        studentName.setText("Muhammad Syuhairy bin Mohd Hatta");
        studentId.setText("2023492222");
        courseCode.setText("CSC584");
        courseName.setText("Mobile Technology");

        //github link
        githubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String githubUrl = "https://github.com/yourusername/ElectricityBillCalculator";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
                startActivity(intent);
            }
        });

        aboutBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}