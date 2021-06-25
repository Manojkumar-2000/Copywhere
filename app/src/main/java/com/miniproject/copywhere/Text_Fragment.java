package com.miniproject.copywhere;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.IOException;
import java.util.List;

public class Text_Fragment extends Fragment {
    Uri image_uri;
    EditText result_text_view;
    ImageButton copy_button;
   public Text_Fragment(Uri image_uri){
        this.image_uri= image_uri;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root_view = inflater.inflate(R.layout.fragment_text_, container, false);
        result_text_view = root_view.findViewById(R.id.result_text_view);
        copy_button = root_view.findViewById(R.id.copy_Button);
        Context this_context=getActivity();
        extractimagetext(image_uri,this_context);
       //when copy button is clicked the text in the text field is copied to clipboard
        copy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) this_context.getSystemService(Context.CLIPBOARD_SERVICE);
                String cliptext= result_text_view.getText().toString();
                //if text field is empty then show a toast to tell the user that the field is empty
                if(cliptext.matches("")){
                    Toast.makeText(this_context, "There is no text to Copy", Toast.LENGTH_SHORT).show();
                }
                //else copy the text from the text field to clipboard
                else {
                    ClipData clip = ClipData.newPlainText("CLIPBOARD", cliptext);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this_context, "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root_view;
    }
    //This method extracts the text from the image and calls the DisplayTextfromImage()method to display the text in the textfield
    private void extractimagetext(Uri image_uri,Context this_context) {
        FirebaseVisionImage firebaseVisionImage = null;
        try {
            //creating firebase vision image from the given Uri
            firebaseVisionImage = FirebaseVisionImage.fromFilePath(this_context,image_uri);
        } catch (IOException e) {
            Toast.makeText(this_context, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //creating an instance of the firebase vision text detector
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        //this detects the text from the image.If the detection is successful then it extracts the text and passes firebasevision object to  onSuccess() method
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                DisplaytextfromImage(firebaseVisionText,this_context);//this method is used to display the text in the text field
                Toast.makeText(this_context,"successful",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(this_context,"ERROR: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("ERROR",e.getMessage());
            }
        });
    }
    //This methods gets the firebasevisiontext object from the extractimage() method and process it to display in the text field
    private void DisplaytextfromImage(FirebaseVisionText firebaseVisionText,Context this_context) {
        List<FirebaseVisionText.Block> blockList= firebaseVisionText.getBlocks();
        if (blockList.size()==0){
            Toast.makeText(this_context, "no text found on the picture", Toast.LENGTH_SHORT).show();
        }
        else {
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                String text = block.getText();
                result_text_view.append(text + "\n");

            }
        }
    }


}

