package com.cyberpunk2077.cursochat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class mensajesAdapter extends ArrayAdapter<mensajePOJO> {

    private ArrayList<mensajePOJO> mensajes;
    private FirebaseUser yo;
    private String amigoClickedID;

    public mensajesAdapter(@NonNull Context context, ArrayList<mensajePOJO> data, String amigoIDClicked) {
        super(context, 0, data);
        this.mensajes = data;
        this.yo = FirebaseAuth.getInstance().getCurrentUser();
        this.amigoClickedID = amigoIDClicked;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mensajeadapter, parent, false);
        }

        final mensajePOJO mensaje = mensajes.get(position);
        final ImageView foto = convertView.findViewById(R.id.photo);
        final TextView mono = convertView.findViewById(R.id.nombre);
        TextView cuerpo = convertView.findViewById(R.id.mensaje);
        TextView fecha = convertView.findViewById(R.id.fecha);

        if(mensaje.UID_DE.equals(yo.getUid())) {
            mono.setText("tú");
        }

        cuerpo.setText(mensaje.cuerpo);
        fecha.setText(mensaje.fechaMSJ);
        if( yo!= null && yo.getPhotoUrl() != null && mensaje.UID_DE.equals(yo.getUid()) ){
            Picasso.with(getContext()).load(yo.getPhotoUrl()).into(foto);
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios/"+amigoClickedID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usuariosPOJO USER = dataSnapshot.getValue(usuariosPOJO.class);
                    if(USER != null){
                        if(mensaje.UID_DE.equals(USER.UID)){
                            mono.setText(USER.Nombre);
                        }
                        if(USER.PhotoURL != null && mensaje.UID_DE.equals(USER.UID)){
                            Picasso.with(getContext()).load(USER.PhotoURL).into(foto);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mensajes.size();
    }
}
