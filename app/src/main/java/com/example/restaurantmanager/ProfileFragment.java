package com.example.restaurantmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.restaurantmanager.Activity.AddFood;
import com.example.restaurantmanager.Activity.EditProfile;
import com.example.restaurantmanager.Activity.Setting;
import com.example.restaurantmanager.manager.PreferencesManager;

import java.io.InputStream;
import java.util.List;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView setAvt = view.findViewById(R.id.avt);
        ImageView btnSetting = view.findViewById(R.id.btnSetting);
        LinearLayout editProfile = view.findViewById(R.id.edit_profile);

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });

        setAvt.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });


        btnSetting.setOnClickListener(v -> {
            // Open the Setting activity
            Intent intent = new Intent(getActivity(), Setting.class);
            startActivity(intent);
        });
        if(PreferencesManager.getInstance().getAvt()!=null){
            ImageView im = view.findViewById(R.id.avt);
//            new ProfileFragment.DownloadImageTask(im).execute(PreferencesManager.getInstance().getAvt());
            Glide.with(this)
                    .load(PreferencesManager.getInstance().getAvt())
                    .into(im);
        }
        if(PreferencesManager.getInstance().getBirthday()!=null){
            TextView birthday = view.findViewById(R.id.birtday);
            birthday.setText(PreferencesManager.getInstance().getBirthday());
        }
        TextView mail = view.findViewById(R.id.email);
        mail.setText(PreferencesManager.getInstance().getUserName());
        TextView phone = view.findViewById(R.id.phone);
        phone.setText(PreferencesManager.getInstance().getPhone());
        TextView name = view.findViewById(R.id.name);
        name.setText(PreferencesManager.getInstance().getName());
        TextView idtxt = view.findViewById(R.id.id);
        idtxt.setText(PreferencesManager.getInstance().getId());

        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}