package com.miniproject.copywhere;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.miniproject.copywhere.MainActivity;
import java.io.IOException;
import java.util.List;

public class resultactivity extends AppCompatActivity{
    ImageView processed_image_view;
    ImageButton copy2clip,back_button;
    Uri image_uri;
    TextInputEditText result_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        processed_image_view=(ImageView)findViewById(R.id.processed_image);
        copy2clip=findViewById(R.id.copy);
        back_button=findViewById(R.id.backtomain);
        result_text=findViewById(R.id.result_text_view);
        image_uri = Uri.parse(getIntent().getStringExtra("imageUri"));
        processed_image_view.setImageURI(image_uri);
        extractimagetext(image_uri);
        copy2clip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String cliptext= result_text.getText().toString();
                ClipData clip = ClipData.newPlainText("CLIPBOARD", cliptext);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(resultactivity.this,"Copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultactivity.this.finish();
                System.exit(0);
            }
        });
    }

   private void extractimagetext(Uri image_uri) {
        FirebaseVisionImage firebaseVisionImage = null;
        try {
            firebaseVisionImage = FirebaseVisionImage.fromFilePath(this, image_uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                DisplaytextfromImage(firebaseVisionText);
                Toast.makeText(resultactivity.this,"successfull",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(resultactivity.this,"ERROR: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("ERROR",e.getMessage());
            }
        });
    }

    private void DisplaytextfromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList= firebaseVisionText.getBlocks();
        if (blockList.size()==0){
            Toast.makeText(this, "NO TEXT FOUND", Toast.LENGTH_SHORT).show();
        }
        else{
            for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()){
                String text= block.getText();
                result_text.append(text);
            }
        }
    }
}
