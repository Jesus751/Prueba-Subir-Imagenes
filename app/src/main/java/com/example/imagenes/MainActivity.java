package com.example.imagenes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    EditText et;
    Button btnSubir, btnCamera;
    Bitmap bitmap;

    String UPLOAD_URL = "http://localhost/pruebas/imagen.php";
    String KEY_IMAGE = "foto";
    String KEY_DESCRIPCION = "descripcion";


    private static final int REQUEST_IMAGE_CAMERA = 101;
    private  static final  int REQUEST_PERMISSION_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv =  findViewById(R.id.imagenView);
        et = findViewById(R.id.editText);
        btnSubir = findViewById(R.id.btnSubir);
        btnCamera = findViewById(R.id.btnCamera);


        // BOTON TOMAR FOTO
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        abrirCamara();
                    }else{
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION_CAMERA);
                    }
                }else {
                    abrirCamara();
                }
            }
        });

        // BOTON SUBIR-IMAGEN
        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv.getDrawable() == null || et.length() < 1){
                    Toast.makeText(MainActivity.this, "Todos Los Campos Son Obligatorios", Toast.LENGTH_SHORT).show();
                }else{
                    if(iv.getDrawable()  != null && et.length() > 20){
                        uploadImage();
                    }else {
                        Toast.makeText(MainActivity.this, "Tu Descripción Debe Ser Mayor A 20  Caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    //Comvertir la imagen a un string
    public  String getStringImagen(Bitmap bmp){
        ByteArrayOutputStream baos =  new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos. toByteArray();
        String encodedImage = MediaStore.Images.Media.insertImage(getContentResolver(),bmp,"Title",null);
        return encodedImage;
    }


    // SUBIR IMAGEN AL SERVIDOR
    public void uploadImage() {
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String imagen = getStringImagen(bitmap);
                String descripcion = et.getText().toString().trim().toUpperCase();

                Map<String, String> params = new Hashtable<String, String>();
                params.put(KEY_IMAGE, imagen);
                params.put(KEY_DESCRIPCION, descripcion);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    // FUNCION PARA ABRIR LA CAMARA

    private void abrirCamara(){
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null){
           startActivityForResult(intent,REQUEST_IMAGE_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CAMERA){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abrirCamara();
            }else {
                Toast.makeText(this, "Se Necesita Habilitar los Permisos", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAMERA){
            if (resultCode == Activity.RESULT_OK){

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                iv.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}





















