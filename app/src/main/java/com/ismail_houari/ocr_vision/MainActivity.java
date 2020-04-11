package com.ismail_houari.ocr_vision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {


    private Button btn_scan;
    private TextView affich;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    private Uri photoURI;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }




    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast erreur = Toast.makeText(getApplicationContext(),"Erreur creation de fichier",Toast.LENGTH_SHORT);
                erreur.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                System.out.println("Erreur avec URI de la photo!");
                 photoURI = FileProvider.getUriForFile(this,
                        "com.ismail_houari.ocr_vision.fileprovider",
                        photoFile);
                System.out.println("photoURI passee");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }




    protected String doOCR(Bitmap image){
        String final_result = "OCR ERROR !";

        return final_result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.affich = findViewById(R.id.affichage);
        this.btn_scan = findViewById(R.id.btn_scan);



        this.btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("photo taken");
                FirebaseVisionImage image;
                try{
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(),photoURI);
                     FirebaseVisionTextRecognizer textRecongnizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                     Task<FirebaseVisionText> resultat = textRecongnizer.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {

                                            String text = block.getText();
                                            affich.setText("");
                                            affich.setText(affich.getText()+text);

                                            for (FirebaseVisionText.Line line: block.getLines()) {
                                                // ...
                                                for (FirebaseVisionText.Element element: line.getElements()) {
                                                    // ...
                                                }
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        affich.setText("Erreur au niveau de la reconnaissance de la carte !");
                                    }
                                });

                } catch (IOException e){
                    e.printStackTrace();
                }





            }
        }

        );
    }
}
