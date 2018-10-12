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
import com.squareup.picasso.Picasso;

import java.util.List;

public class usuariosAdapter extends ArrayAdapter<usuariosPOJO> {

    private List<usuariosPOJO> usuariosData;

    public usuariosAdapter(Context context, List<usuariosPOJO> data) {
        super(context, 0, data);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        usuariosData = data;

        for (usuariosPOJO user : usuariosData) {
            if(user.UID.equals(usuario.getUid())){
                usuariosData.remove(user);
                break;
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.usuarioadapter, parent, false);
        }
        ImageView foto = convertView.findViewById(R.id.foto);
        TextView nombre = convertView.findViewById(R.id.nombre);

        usuariosPOJO usr = usuariosData.get(position);
        convertView.setTag(usr);

        if(usr.PhotoURL != null){
            Picasso.with(getContext()).load(usr.PhotoURL).into(foto);
        }else{
            foto.setImageResource(android.R.drawable.ic_menu_view);
        }
        nombre.setText(usr.Nombre);

        return convertView;
    }

    @Override
    public int getCount() {
        return usuariosData.size();
    }
}
