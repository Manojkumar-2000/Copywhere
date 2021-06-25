package com.miniproject.copywhere;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Image_Fragment extends Fragment {
    Uri image_uri;
    ImageView Result_image;

    public Image_Fragment(Uri image_uri) {
        this.image_uri = image_uri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root_view = inflater.inflate(R.layout.imagefragment, container, false);
        Result_image = root_view.findViewById(R.id.processed_image);
        Result_image.setImageURI(image_uri);
        return root_view;


    }
}
