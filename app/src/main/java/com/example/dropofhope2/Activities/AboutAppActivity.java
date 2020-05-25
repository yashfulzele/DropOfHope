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
        aboutTv.setText("    The main aim of this project is to save lives of people by providing blood. My app  is developed so that users can view the information of nearby blood banks and hospitals. This project is developed by three perspective i.e. blood bank, patient/donor and hospital. We have provided security for authenticated user as new user have to register according to their type of perspective and existing user have to login.  This application reduces the time to a greater extent that is searching for the required blood through blood banks and hospitals. Thus this application provides the required information in less time and also helps in quicker decision making. The task of blood bank is to receive blood from various donors, to monitor the blood groups database and to send the required blood during the need to the hospital in case of emergencies. The problem is not insufficient number of donors, but finding a willing donor at the right time. The application is developed to built a network of people who can help each other during an emergency. This application timely updates the information regarding the donors, where the administrator accesses the whole information about the blood bank system. The aim of the application that in a very short span it provides user with many facilities.");
    }
}
