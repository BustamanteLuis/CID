package com.uniovi.informaticamovil.cid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SuggestionFragment extends Fragment {

    public static SuggestionFragment newInstance(){
        SuggestionFragment fragment = new SuggestionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggestion, container, false);

        Button button = (Button)view.findViewById(R.id.sendButton);
        final EditText editText  = (EditText)view.findViewById(R.id.infoText);

        // Ejecuta una aplicación de correo electrónico que decida el usuario para enviar
        // el contenido del edit text al correo del propietario de la aplicación
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();

                if (!text.isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.fromParts("mailto", "uo231405@uniovi.es", null)); // destinatario
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CID");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, text);
                    editText.setText("");
                    startActivity(Intent.createChooser(emailIntent, "CID"));
                }

                // Recordamos al usuario que debe escribir algo para poder enviarlo
                else{
                    String mes = getResources().getString(R.string.suggestion_toast);
                    Toast toast = Toast.makeText(getContext(), mes, Toast.LENGTH_SHORT);
                    toast.show();
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                }
            }
        });

        return view;
    }

}
