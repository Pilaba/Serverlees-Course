package com.cyberpunk2077.cursochat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Interfaz extends AppCompatActivity {

    private usuariosPOJO usuarioClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz);

        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if(usuario == null){ return; }

        updateToken(usuario);

        final ListView usuarios = findViewById(R.id.Usuarios);
        final ListView mensajes = findViewById(R.id.mensajes);
        final Button submit = findViewById(R.id.submit);
        final EditText texto = findViewById(R.id.cuerpo);
        final TextView room = findViewById(R.id.room);

        DatabaseReference refUsurios = FirebaseDatabase.getInstance().getReference("usuarios");
        refUsurios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<usuariosPOJO> users = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    users.add(child.getValue(usuariosPOJO.class));
                }
                usuariosAdapter adapter = new usuariosAdapter(Interfaz.this, users);

                usuarios.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usuarioClick = (usuariosPOJO) view.getTag();
                room.setText(usuarioClick.Nombre);

                DatabaseReference refMensajes = FirebaseDatabase.getInstance().getReference("mensajes");
                refMensajes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<mensajePOJO> dataMSJ = new ArrayList<>();

                        for (DataSnapshot mensajito : dataSnapshot.getChildren()) {
                            mensajePOJO msj = mensajito.getValue(mensajePOJO.class);
                            if(msj != null && (msj.UID_PARA.equals(usuarioClick.UID) && msj.UID_DE.equals(usuario.getUid())
                                    || msj.UID_PARA.equals(usuario.getUid()) && msj.UID_DE.equals(usuarioClick.UID))){
                                dataMSJ.add(msj);
                            }
                        }
                        mensajes.setAdapter(new mensajesAdapter(Interfaz.this, dataMSJ, usuarioClick));
                        mensajes.smoothScrollToPosition(dataMSJ.size() - 1);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(texto.getText() == null || usuarioClick == null){ return; }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                mensajePOJO mensaje = new mensajePOJO(
                        usuario.getUid(), usuarioClick.UID, texto.getText().toString(), df.format(new Date())
                );
                DatabaseReference remoteMessage = FirebaseDatabase.getInstance().getReference("mensajes");
                remoteMessage.push().setValue(mensaje);
                texto.setText("");
            }
        });

    }

    public void updateToken(final FirebaseUser user){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
                ref.child(user.getUid()).child("token").setValue(instanceIdResult.getToken());
            }
        });
    }


    //Menucito
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucito, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.salir){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(null)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseAuth AUTH = FirebaseAuth.getInstance();
                            AUTH.signOut();
                            startActivity(new Intent(Interfaz.this, MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Interfaz.this,"No es posible cerrar sesion", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return true;
    }
}
