package com.uniovi.informaticamovil.cid.Facilities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.uniovi.informaticamovil.cid.R;

/**
 * Created by Luis on 28/4/16.
 */
public class FacilitieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facilitie_description);

        Intent intent = getIntent();

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = (ImageView)findViewById(R.id.descriptionView);
        image.setImageBitmap(bmp);

        TextView name  = (TextView)findViewById(R.id.descriptionName);
        String n = intent.getStringExtra("name");
        name.setText(n);

        TextView direccion  = (TextView)findViewById(R.id.direction_content);
        String direc = intent.getStringExtra("direccion");
        direccion.setText(Html.fromHtml(direc));


        TextView horario  = (TextView)findViewById(R.id.schedule_content);
        String h= intent.getStringExtra("horario");
        horario.setText(Html.fromHtml(h));

        TextView descripcion  = (TextView)findViewById(R.id.description_content);
        String des = intent.getStringExtra("descripcion");
        descripcion.setText(Html.fromHtml(des));


    }
}
