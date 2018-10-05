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
    private FirebaseUser mFUSER;
    private usuariosPOJO amigoClicked;

    public mensajesAdapter(@NonNull Context context, ArrayList<mensajePOJO> data, usuariosPOJO amigoClicked) {
        super(context, 0, data);
        this.mensajes = data;
        this.amigoClicked = amigoClicked;
        this.mFUSER = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mensajeadapter, parent, false);
        }

        final mensajePOJO mensaje = mensajes.get(position);
        final ImageView foto = convertView.findViewById(R.id.photo);
        TextView mono = convertView.findViewById(R.id.nombre);
        TextView cuerpo = convertView.findViewById(R.id.mensaje);
        TextView fecha = convertView.findViewById(R.id.fecha);

        if(mensaje.UID_DE.equals(mFUSER.getUid())) {
            mono.setText("tú");
        }else{
            mono.setText(amigoClicked.Nombre);
        }

        cuerpo.setText(mensaje.cuerpo);
        fecha.setText(mensaje.fechaMSJ);
        if( mFUSER!= null && mFUSER.getPhotoUrl() != null && mensaje.UID_DE.equals(mFUSER.getUid()) ){
            Picasso.with(getContext()).load(mFUSER.getPhotoUrl()).into(foto);
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        usuariosPOJO USER = child.getValue(usuariosPOJO.class);
                        if(USER != null && USER.PhotoURL != null && mensaje.UID_DE.equals(USER.UID)){
                            Picasso.with(getContext()).load(USER.PhotoURL).into(foto);
                            break;
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
