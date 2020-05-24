package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.dropofhope2.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FullImageActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        imageView = findViewById(R.id.image);
        Picasso.get()
                .load(Objects.requireNonNull(getIntent().getExtras()).getString("Image uri", null))
                .centerInside()
                .fit()
                .into(imageView);
    }
}
