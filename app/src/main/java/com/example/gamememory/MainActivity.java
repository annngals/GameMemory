package com.example.gamememory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static int selectvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectvalue = 1;
    }

    public void ButtonExit(View view)
    {
        System.exit(0);
    }

    public void ButtonStart(View view)
    {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("size", selectvalue);
        startActivity(intent);
    }

    public void ButtonSettings(View view)
    {
        CustomDialogFragment dialog = new CustomDialogFragment();
        dialog.show(getSupportFragmentManager(), getString(R.string.custom));
    }

    public static class CustomDialogFragment extends DialogFragment {

        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int n = 5; // максимальный размер поля
            int k = 2;
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < n; i++){
                String s = String.valueOf(k) + 'x' + String.valueOf(k);
                k += 2;
                arrayList.add(s);
            }
            String[] array = arrayList.toArray(new String[arrayList.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.choose_size);
            builder.setSingleChoiceItems(array, selectvalue, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectvalue = which;
                    Log.i("test", String.valueOf(selectvalue));
                }
            });
            return builder.create();
        }

    }

}