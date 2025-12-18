package com.example.electricitybillcalc;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        View rootView = findViewById(android.R.id.content);
        rootView.setPadding(0, getActionBarHeight(), 0, 0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("⚡ Electricity Bill Calculator");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_app_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
        }

        TextView studentName = findViewById(R.id.studentName);
        TextView studentId = findViewById(R.id.studentId);
        TextView courseCode = findViewById(R.id.courseCode);
        TextView courseName = findViewById(R.id.courseName);
        TextView githubLink = findViewById(R.id.githubLink);
        Button aboutBackButton = findViewById(R.id.aboutBackButton);

        //my details
        studentName.setText("Muhammad Syuhairy \nbin Mohd Hatta");
        studentId.setText("2023492222");
        courseCode.setText("ICT602");
        courseName.setText("Mobile Technology \nand Development");

        //github link
        githubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String githubUrl = "https://github.com/yiesumsin/ElectricityBillCalculator";
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
    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }
}