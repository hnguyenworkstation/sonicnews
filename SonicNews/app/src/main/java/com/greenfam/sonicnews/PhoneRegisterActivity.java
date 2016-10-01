package com.greenfam.sonicnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PhoneRegisterActivity extends SonicNewsActivity {

    private Button phoneVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);

        phoneVerify = (Button) findViewById(R.id.phone_request);

        phoneVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneRegisterActivity.this, MainActivity.class));
            }
        });
    }
}
