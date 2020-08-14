package com.example.machinelearningkit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public final int TEXT_CODE=100;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        imageView=findViewById( R.id.imageView );
    }


    public void textDetect(View view) {

        //Intent photo=new Intent( MediaStore.ACTION_IMAGE_CAPTURE );

        Intent photo=new Intent(Intent.EXTRA_ASSIST_CONTEXT);
        photo.setType( "image/*" ) ;
        startActivityForResult(photo,TEXT_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode==TEXT_CODE) {
            if (requestCode==RESULT_OK){
                Bitmap photo=(Bitmap)data.getExtras().get( "data" );
                Uri inageUri=data.getData();
                try {
                    InputStream inputStream=getContentResolver().openInputStream(inageUri);
                    photo = BitmapFactory.decodeStream( inputStream );
                    texRecognizeResult(photo);
                    imageView.setImageBitmap(photo);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }else {
            Toast.makeText( MainActivity.this,"Cancel",Toast.LENGTH_SHORT).show();


    }
}

    private void texRecognizeResult(Bitmap photo) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        final Task< FirebaseVisionText > firebaseVisionTextTask = textRecognizer.processImage( image )
                .addOnSuccessListener( new OnSuccessListener< FirebaseVisionText >() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        FirebaseVisionText result = null;
                        String resultText = result.getText();
                        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
                            String blockText = block.getText();
                            Float blockConfidence = block.getConfidence();
                            List< RecognizedLanguage > blockLanguages = block.getRecognizedLanguages();
                            Point[] blockCornerPoints = block.getCornerPoints();
                            Rect blockFrame = block.getBoundingBox();
                            Toast.makeText( MainActivity.this,blockText,Toast.LENGTH_LONG).show();
                            for (FirebaseVisionText.Line line: block.getLines()) {
                                String lineText = line.getText();
                                Float lineConfidence = line.getConfidence();
                                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                Point[] lineCornerPoints = line.getCornerPoints();
                                Rect lineFrame = line.getBoundingBox();
                                Toast.makeText( MainActivity.this,lineText,Toast.LENGTH_LONG).show();
                                for (FirebaseVisionText.Element element: line.getElements()) {
                                    String elementText = element.getText();
                                    Float elementConfidence = element.getConfidence();
                                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                    Point[] elementCornerPoints = element.getCornerPoints();
                                    Rect elementFrame = element.getBoundingBox();
                                    Toast.makeText( MainActivity.this,elementText,Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    }
                } )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText( MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();



                            }

                        });
    }

}
