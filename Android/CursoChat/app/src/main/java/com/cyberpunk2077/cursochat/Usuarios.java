package com.cyberpunk2077.cursochat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class Usuarios extends AppCompatActivity {

    private usuariosPOJO usuarioClick;
    private FirebaseUser yo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        yo = FirebaseAuth.getInstance().getCurrentUser();
        if(yo == null){ return; }

        new updateToken().execute(yo.getUid());

        final ListView usuarios = findViewById(R.id.Usuarios);

        DatabaseReference refUsurios = FirebaseDatabase.getInstance().getReference("usuarios");
        refUsurios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<usuariosPOJO> users = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    users.add(child.getValue(usuariosPOJO.class));
                }
                usuariosAdapter adapter = new usuariosAdapter(Usuarios.this, users);

                usuarios.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usuarioClick = (usuariosPOJO) view.getTag();

                Intent intencion = new Intent(Usuarios.this, Chat.class);
                intencion.putExtra("usuarioID", usuarioClick.UID);
                intencion.putExtra("usuarioNombre", usuarioClick.Nombre);
                startActivity(intencion);
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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios/"+yo.getUid());
            ref.child("token").setValue(null)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseAuth AUTH = FirebaseAuth.getInstance();
                        AUTH.signOut();
                        startActivity(new Intent(Usuarios.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Usuarios.this,"No es posible cerrar sesion", Toast.LENGTH_SHORT).show();
                    }
                });
        }
        return true;
    }

    //Clase para actualizar el token en firebase
    private static class updateToken extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(final String... userID) {
            try {
                Thread.sleep(5000);
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
                        ref.child(userID[0]).child("token").setValue( instanceIdResult.getToken());
                    }
                });
            }catch (Exception e){}
           return null;
        }
    }


}
