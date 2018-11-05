package com.orion.synevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAgendaActivity extends AppCompatActivity {
    private Button mCreateAgenda;
    private Button mCancelAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_agenda);

        mCancelAgenda = findViewById(R.id.btn_cancel_agenda_create);
        mCreateAgenda = findViewById(R.id.btn_create_agenda);

        mCancelAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void cancelAgenda(View view){

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveAgenda(View view){
        Toast.makeText(this,"Saved!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

        finish();
    }
}
