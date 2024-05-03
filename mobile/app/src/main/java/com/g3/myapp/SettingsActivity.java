
package com.g3.myapp;

import android.os.Bundle;

import com.g3.myapp.internetoptions.ApplicationOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.EditText;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {
    private EditText ipvalue;
    private EditText portvalue;
    private RadioButton radioButton1;
    private RadioButton radioButton2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ipvalue = findViewById(R.id.ipvalue);
        portvalue = findViewById(R.id.portvalue);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String[] options = ApplicationOptions.getOptions();

        ipvalue.setText(options[0]);
        portvalue.setText(options[1]);

        String file = options[3];

        if (file.contains("26")) {
            radioButton1.setChecked(true);
            radioButton2.setChecked(false);
        } else {
            radioButton1.setChecked(false);
            radioButton2.setChecked(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        String[] options = ApplicationOptions.getOptions();

        options[0] = ipvalue.getText().toString();
        options[1] = portvalue.getText().toString();

        if (radioButton1.isChecked()) {
            options[3] = "vehicle_26.csv";
        } else {
            options[3] = "vehicle_27.csv";
        }

        ApplicationOptions.setOptions(options);
    }
}
