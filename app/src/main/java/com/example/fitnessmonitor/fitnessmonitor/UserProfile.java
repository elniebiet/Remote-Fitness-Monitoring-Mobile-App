package com.example.fitnessmonitor.fitnessmonitor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    private Spinner spnGender;
    private ArrayAdapter<String> adapter;
    private List<String> lstGender;

    private TextView txtEmail;
    private TextView txtDOB;
    private TextView txtHeight;
    private TextView txtWeight;
    private Calendar cldDOB;
    private ImageView imgProfilePicture;
    private static final int FILE_SELECT_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //set flBack height
        int flBackHeight = (int)(0.05 * height);
        FrameLayout flBack = (FrameLayout) findViewById(R.id.flBack);
        ViewGroup.LayoutParams flBackLayoutParams = flBack.getLayoutParams();
        flBackLayoutParams.height = flBackHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flBack, 0,0,0,0);
        flBack.setLayoutParams(flBackLayoutParams);

        //set flPicture1 height
        int flPicture1Height = (int)(0.25 * height);
        FrameLayout flPicture1 = (FrameLayout) findViewById(R.id.flPicture1);
        ViewGroup.LayoutParams flPicture1LayoutParams = flPicture1.getLayoutParams();
        flPicture1LayoutParams.height = flPicture1Height;//(int)(grdMainHeight * 0.9);
        setMargins(flPicture1, 0,0,0,0);
        flPicture1.setLayoutParams(flPicture1LayoutParams);

        //set imgPicture1 height
        int imgPicture1Height = (int)(0.80 * flPicture1Height);
        ImageView imgPicture1 = (ImageView)findViewById(R.id.imgPicture1);
        ViewGroup.LayoutParams imgPicture1LayoutParams = imgPicture1.getLayoutParams();
        imgPicture1LayoutParams.height = imgPicture1Height;//(int)(grdMainHeight * 0.9);
        setMargins(imgPicture1, 0,0,0,0);
        imgPicture1.setLayoutParams(imgPicture1LayoutParams);

        //set imgProfilePicture height
        int imgProfilePictureHeight = (int)(0.3 * flPicture1Height);
        int imgPPMarginTop = (int)(0.65 * flPicture1Height);
//        int imgPPMarginLeft = imgProfilePictureHeight;
        imgProfilePicture = (ImageView)findViewById(R.id.imgProfilePicture);
        ViewGroup.LayoutParams imgProfilePictureLayoutParams = imgProfilePicture.getLayoutParams();
        imgProfilePictureLayoutParams.height = imgProfilePictureHeight;//(int)(grdMainHeight * 0.9);
        imgProfilePictureLayoutParams.width = imgProfilePictureHeight;//(int)(grdMainHeight * 0.9);
        setMargins(imgProfilePicture, 30,imgPPMarginTop,0,0);
        imgProfilePicture.setLayoutParams(imgProfilePictureLayoutParams);

        //set flPicture1 height
        int flPicture2Height = (int)(0.25 * height);
        FrameLayout flPicture2 = (FrameLayout) findViewById(R.id.flPicture2);
        ViewGroup.LayoutParams flPicture2LayoutParams = flPicture2.getLayoutParams();
        flPicture2LayoutParams.height = flPicture2Height;//(int)(grdMainHeight * 0.9);
        setMargins(flPicture2, 0,0,0,0);
        flPicture2.setLayoutParams(flPicture2LayoutParams);

        //set flEmail height
        int flEmailHeight = (int)(0.07 * height);
        int marginLeft = (int)(0.05 * width);
        int marginTop = (int)(0.02 * height);
        FrameLayout flEmail = (FrameLayout) findViewById(R.id.flEmail);
        ViewGroup.LayoutParams flEmailLayoutParams = flEmail.getLayoutParams();
        flEmailLayoutParams.height = flEmailHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flEmail, marginLeft, marginTop, marginLeft,0);
        flEmail.setLayoutParams(flEmailLayoutParams);

        //set flGender height
        int flGenderHeight = (int)(0.07 * height);
        FrameLayout flGender= (FrameLayout) findViewById(R.id.flGender);
        ViewGroup.LayoutParams flGenderLayoutParams = flGender.getLayoutParams();
        flGenderLayoutParams.height = flGenderHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flGender, marginLeft, marginTop, marginLeft,0);
        flGender.setLayoutParams(flGenderLayoutParams);

        //set flDateOfBirth height
        int flDateOfBirthHeight = (int)(0.07 * height);
        FrameLayout flDateOfBirth= (FrameLayout) findViewById(R.id.flDateOfBirth);
        ViewGroup.LayoutParams flDateOfBirthLayoutParams = flDateOfBirth.getLayoutParams();
        flDateOfBirthLayoutParams.height = flDateOfBirthHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flDateOfBirth, marginLeft, marginTop, marginLeft,0);
        flDateOfBirth.setLayoutParams(flDateOfBirthLayoutParams);

        //set flHeight height
        int flHeightHeight = (int)(0.07 * height);
        FrameLayout flHeight= (FrameLayout) findViewById(R.id.flHeight);
        ViewGroup.LayoutParams flHeightLayoutParams = flHeight.getLayoutParams();
        flHeightLayoutParams.height = flHeightHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flHeight, marginLeft, marginTop, marginLeft,0);
        flHeight.setLayoutParams(flHeightLayoutParams);

        //set flWeight height
        int flWeightHeight = (int)(0.07 * height);
        FrameLayout flWeight= (FrameLayout) findViewById(R.id.flWeight);
        ViewGroup.LayoutParams flWeightLayoutParams = flWeight.getLayoutParams();
        flWeightLayoutParams.height = flWeightHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flWeight, marginLeft, marginTop, marginLeft,0);
        flWeight.setLayoutParams(flWeightLayoutParams);

        //set flUpdate height
        int flUpdateHeight = (int)(0.07 * height);
        FrameLayout flUpdate = (FrameLayout) findViewById(R.id.flUpdate);
        ViewGroup.LayoutParams flUpdateLayoutParams = flUpdate.getLayoutParams();
        flUpdateLayoutParams.height = flUpdateHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flUpdate, marginLeft, marginTop, marginLeft,0);
        flUpdate.setLayoutParams(flUpdateLayoutParams);

        spnGender = (Spinner) findViewById(R.id.spnGender);
        lstGender = new ArrayList<String>();
        lstGender.add("Male");
        lstGender.add("Female");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, lstGender);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGender.setAdapter(adapter);
        txtDOB = (TextView) findViewById(R.id.txtDOB);

    }

    public void goBackHome(View view){
        finish();
    }

    public void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        return new DatePickerDialog(this, datePickerListener, 2000, 1, 1);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            txtDOB.setText(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay);
        }
    };

    public void dobClicked(View view){
        showDialog(0);
    }

    public void updatePhoto(View view){
        showFileChooser();
    }

    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    imgProfilePicture.setImageURI(uri);
                    Log.d("", "File Uri: " + uri.toString());
                    ContentResolver cR = this.getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String filetype = mime.getExtensionFromMimeType(cR.getType(uri));

                    Toast.makeText(this, "URI: "+ uri.toString() + "Type: " + filetype , Toast.LENGTH_LONG).show();

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
