package com.cyberpunk2077.cursochat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Chat extends AppCompatActivity {

    private String userIDclick, userNombreClick;
    private FirebaseUser yo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Tú
        yo = FirebaseAuth.getInstance().getCurrentUser();
        if (yo == null) { return; }

        //Retrieve userID
        userIDclick = getIntent().getStringExtra("usuarioID");
        userNombreClick = getIntent().getStringExtra("usuarioNombre");
        if(userIDclick == null){
            userIDclick = savedInstanceState.getString("usuarioID");
            userNombreClick = savedInstanceState.getString("usuarioNombre");
        }

        //Botoncito para atras
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(userNombreClick);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Views
        final ListView listaMensajes = findViewById(R.id.mensajitos);
        final Button submit = findViewById(R.id.submit);
        final EditText texto = findViewById(R.id.cuerpo);

        DatabaseReference refMensajes = FirebaseDatabase.getInstance().getReference("mensajes");
        refMensajes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<mensajePOJO> dataMSJ = new ArrayList<>();

                for (DataSnapshot mensajito : dataSnapshot.getChildren()) {
                    mensajePOJO msj = mensajito.getValue(mensajePOJO.class);
                    if(msj != null && (msj.UID_PARA.equals(userIDclick) && msj.UID_DE.equals(yo.getUid())
                            || msj.UID_PARA.equals(yo.getUid()) && msj.UID_DE.equals(userIDclick))){
                        dataMSJ.add(msj);
                    }
                }

                listaMensajes.setAdapter(new mensajesAdapter(Chat.this, dataMSJ, userIDclick));
                listaMensajes.setSelection(dataMSJ.size() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(texto.getText().toString().equals("")){ return; }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                mensajePOJO mensaje = new mensajePOJO(
                        yo.getUid(), userIDclick, texto.getText().toString(), df.format(new Date()), null
                );
                DatabaseReference remoteMessage = FirebaseDatabase.getInstance().getReference("mensajes");
                remoteMessage.push().setValue(mensaje);
                texto.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("usuarioID", userIDclick);
        outState.putString("usuarioNombre", userIDclick);
    }
}
