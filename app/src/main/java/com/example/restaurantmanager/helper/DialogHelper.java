package com.example.restaurantmanager.helper;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantmanager.R;

public class DialogHelper {
    public static void showSuccessDialog(String message, Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_success);
        TextView textViewMessage = dialog.findViewById(R.id.textViewMessage);

        textViewMessage.setText(message);

        dialog.show();
    }
    public static void showFailDialog(String message, Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_success);

        ImageView imageViewTick = dialog.findViewById(R.id.imageViewTick);
        TextView textViewMessage = dialog.findViewById(R.id.textViewMessage);
        imageViewTick.setImageResource(R.drawable.close);
        textViewMessage.setText(message);

        dialog.show();
    }
}
