package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.dropofhope2.R;

public class AboutAppActivity extends AppCompatActivity {
    private TextView aboutTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        aboutTv = findViewById(R.id.about);
        setTextTv();
    }
    private void setTextTv(){
        aboutTv.setText(" The main aim of this project is to save lives of people by providing blood. My app  is developed so that users can view the information of nearby blood banks and hospitals. This project is developed by three perspective i.e. blood bank, patient/donor and hospital. We have provided security for authenticated user as new user have to register according to their type of perspective and existing user have to login.  This application reduces the time to a greater extent that is searching for the required blood through blood banks and hospitals. Thus this application provides the required information in less time and also helps in quicker decision making.");
    }
}
