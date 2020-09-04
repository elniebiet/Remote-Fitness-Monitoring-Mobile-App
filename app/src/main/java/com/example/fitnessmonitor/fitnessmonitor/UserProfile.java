package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class UserProfile extends AppCompatActivity {

    private Spinner spnGender;
    private ArrayAdapter<String> adapter;
    private List<String> lstGender;

    private TextView txtEmail;
    private TextView txtDOB;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtHeight;
    private EditText edtWeight;
    private Calendar cldDOB;
    private ImageView imgProfilePicture;
    private Button btnUpdate;
    private static final int FILE_SELECT_CODE = 0;
    private SQLiteDatabase sqLiteDatabase;
    private Boolean dbOpened = false;
    private String userEmail = "";
    private String userFirstName = "";
    private String userLastName = "";
    private String userGender = "";
    private String userDOB = "";
    private int userHeight = 0;
    private int userWeight = 0;
    private String userPicLocation = "";
    private String userPicType = "";
    private int profileDetailsSupplied = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //get user email
        try {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(this).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    userEmail = account.name;
                    Log.i("POSSIBLE EMAIL ADDR: ", userEmail);
                    break;
                }
            }
            if (accounts.length == 0) {
                    Log.i("NO EMAIL ADDR: ", "Couldnt find email address");

            }
        } catch (Exception ex){
            Log.i("ERROR GETTING EMAIL: ", "couldn't get email address "+ex.getMessage());
        }

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
        int flPicture1Height = (int)(0.20 * height);
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
        imgProfilePicture = (ImageView)findViewById(R.id.imgProfilePicture);
        ViewGroup.LayoutParams imgProfilePictureLayoutParams = imgProfilePicture.getLayoutParams();
        imgProfilePictureLayoutParams.height = imgProfilePictureHeight;//(int)(grdMainHeight * 0.9);
        imgProfilePictureLayoutParams.width = imgProfilePictureHeight;//(int)(grdMainHeight * 0.9);
        setMargins(imgProfilePicture, 30,imgPPMarginTop,0,0);
        imgProfilePicture.setLayoutParams(imgProfilePictureLayoutParams);

        //set flPicture2 height
        int flPicture2Height = (int)(0.20 * height);
        FrameLayout flPicture2 = (FrameLayout) findViewById(R.id.flPicture2);
        ViewGroup.LayoutParams flPicture2LayoutParams = flPicture2.getLayoutParams();
        flPicture2LayoutParams.height = flPicture2Height;//(int)(grdMainHeight * 0.9);
        setMargins(flPicture2, 0,0,0,0);
        flPicture2.setLayoutParams(flPicture2LayoutParams);

        //set flEmail height
        int flEmailHeight = (int)(0.06 * height);
        int marginLeft = (int)(0.05 * width);
        int marginTop = (int)(0.02 * height);
        FrameLayout flEmail = (FrameLayout) findViewById(R.id.flEmail);
        ViewGroup.LayoutParams flEmailLayoutParams = flEmail.getLayoutParams();
        flEmailLayoutParams.height = flEmailHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flEmail, marginLeft, marginTop, marginLeft,0);
        flEmail.setLayoutParams(flEmailLayoutParams);

        //set flGender height
        int flGenderHeight = (int)(0.06 * height);
        FrameLayout flGender= (FrameLayout) findViewById(R.id.flGender);
        ViewGroup.LayoutParams flGenderLayoutParams = flGender.getLayoutParams();
        flGenderLayoutParams.height = flGenderHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flGender, marginLeft, marginTop, marginLeft,0);
        flGender.setLayoutParams(flGenderLayoutParams);

        //set flFirstName height
        int flFirstNameHeight = (int)(0.06 * height);
        FrameLayout flFirstName= (FrameLayout) findViewById(R.id.flFirstName);
        ViewGroup.LayoutParams flFirstNameLayoutParams = flFirstName.getLayoutParams();
        flFirstNameLayoutParams.height = flFirstNameHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flFirstName, marginLeft, marginTop, marginLeft,0);
        flFirstName.setLayoutParams(flFirstNameLayoutParams);


        //set flLastName height
        int flLastNameHeight = (int)(0.06 * height);
        FrameLayout flLastName= (FrameLayout) findViewById(R.id.flLastName);
        ViewGroup.LayoutParams flLastNameLayoutParams = flLastName.getLayoutParams();
        flLastNameLayoutParams.height = flLastNameHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flLastName, marginLeft, marginTop, marginLeft,0);
        flLastName.setLayoutParams(flLastNameLayoutParams);

        //set flDateOfBirth height
        int flDateOfBirthHeight = (int)(0.06 * height);
        FrameLayout flDateOfBirth = (FrameLayout) findViewById(R.id.flDateOfBirth);
        ViewGroup.LayoutParams flDateOfBirthLayoutParams = flDateOfBirth.getLayoutParams();
        flDateOfBirthLayoutParams.height = flDateOfBirthHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flDateOfBirth, marginLeft, marginTop, marginLeft,0);
        flDateOfBirth.setLayoutParams(flDateOfBirthLayoutParams);

        //set flHeight height
        int flHeightHeight = (int)(0.06 * height);
        FrameLayout flHeight= (FrameLayout) findViewById(R.id.flHeight);
        ViewGroup.LayoutParams flHeightLayoutParams = flHeight.getLayoutParams();
        flHeightLayoutParams.height = flHeightHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flHeight, marginLeft, marginTop, marginLeft,0);
        flHeight.setLayoutParams(flHeightLayoutParams);

        //set flWeight height
        int flWeightHeight = (int)(0.06 * height);
        FrameLayout flWeight= (FrameLayout) findViewById(R.id.flWeight);
        ViewGroup.LayoutParams flWeightLayoutParams = flWeight.getLayoutParams();
        flWeightLayoutParams.height = flWeightHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flWeight, marginLeft, marginTop, marginLeft,0);
        flWeight.setLayoutParams(flWeightLayoutParams);

        //set flUpdate height
        int flUpdateHeight = (int)(0.06 * height);
        FrameLayout flUpdate = (FrameLayout) findViewById(R.id.flUpdate);
        ViewGroup.LayoutParams flUpdateLayoutParams = flUpdate.getLayoutParams();
        flUpdateLayoutParams.height = flUpdateHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flUpdate, marginLeft, marginTop, marginLeft,0);
        flUpdate.setLayoutParams(flUpdateLayoutParams);

        //get elements
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        spnGender = (Spinner) findViewById(R.id.spnGender);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        txtDOB = (TextView) findViewById(R.id.txtDOB);
        edtHeight = (EditText) findViewById(R.id.edtHeight);
        edtWeight = (EditText) findViewById(R.id.edtWeight);

        txtEmail.setText(userEmail);
        if(userEmail == "")
            txtEmail.setHint("No email account found.");

        //Open DB
        try {
            sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
            Log.i("UP: SUCCESS OPENG DB: ", "database opened");
            dbOpened = true;

        } catch(Exception ex){
            Log.i("UP: FAILED OPENG DB: ", "failed to open DB");
            dbOpened = false;
        }

        //check if profile details have been supplied
        if(dbOpened){
            try {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblUserProfile", null);
                int firstNameIndex = cursor.getColumnIndex("firstName");
                int lastNameIndex = cursor.getColumnIndex("lastName");
                int genderIndex = cursor.getColumnIndex("gender");
                int DOBIndex = cursor.getColumnIndex("DOB");
                int heightIndex = cursor.getColumnIndex("height");
                int weightIndex = cursor.getColumnIndex("weight");
                int picLocationIndex = cursor.getColumnIndex("picLocation");
                int picTypeIndex = cursor.getColumnIndex("picType");


                boolean cursorResponse = cursor.moveToFirst();

                if (cursorResponse) { //profile details supplied
                    profileDetailsSupplied = 1;
                    //get the user details
                    userFirstName = cursor.getString(firstNameIndex);
                    userLastName = cursor.getString(lastNameIndex);
                    userGender = cursor.getString(genderIndex);
                    userDOB = cursor.getString(DOBIndex);
                    userHeight = cursor.getInt(heightIndex);
                    userWeight = cursor.getInt(weightIndex);
                    userPicLocation = cursor.getString(picLocationIndex);
                    userPicType = cursor.getString(picTypeIndex);

                    //update UI
                    edtFirstName.setText(userFirstName);
                    edtLastName.setText(userLastName);
                    txtDOB.setText(userDOB);

                    if(userHeight == 0)
                        edtHeight.setText("");
                    else
                        edtHeight.setText(userHeight+"");
                    if(userWeight == 0)
                        edtWeight.setText("");
                    else
                        edtWeight.setText(userWeight+"");

                    if(userPicLocation.length() != 0)
                        imgProfilePicture.setImageURI(Uri.parse(userPicLocation));

                } else {
                    profileDetailsSupplied = 0;
                    //user profile details not specified
                }
            } catch (Exception ex){
                Log.i("ERROR FETCHN USER DET", ex.getMessage());
            }
        }

        lstGender = new ArrayList<String>();
        lstGender.add("Male");
        lstGender.add("Female");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, lstGender);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGender.setAdapter(adapter);

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
        //ACTION_GET_CONTENT
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select an image to Upload"), FILE_SELECT_CODE);
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

                    ContentResolver cR = this.getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String filetype = mime.getExtensionFromMimeType(cR.getType(uri));

                    if(filetype.toLowerCase().contains("jpg") || filetype.toLowerCase().contains("jpeg") || filetype.toLowerCase().contains("png")) {
                        imgProfilePicture.setImageURI(uri);
                        userPicLocation = uri.toString();
                        userPicType = filetype.toLowerCase();
                        Log.d("", "File Uri: " + uri.toString());
//                        Toast.makeText(this, "Profile Picture Added.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid filetype, please select an image (jpg or png format) " + "|"+filetype.toLowerCase()+"|" , Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void btnUpdateProfileClicked(View view){
        Button btnUpdatProf = (Button)findViewById(R.id.btnUpdateProfile);
        //validate the data supplied
        String email = userEmail;
        String gender = spnGender.getSelectedItem().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String dob = txtDOB.getText().toString().trim();
        String height = edtHeight.getText().toString().trim();
        String weight = edtWeight.getText().toString().trim();

        if(email.length() == 0 && firstName.length() == 0){
            Toast.makeText(getApplicationContext(), "Please Enter your first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!(isAlpha(firstName))){
            Toast.makeText(this, "Please enter a valid first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!(isAlpha(lastName))){
            Toast.makeText(this, "Please enter a valid last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(checkDOB(dob) == false){
            Toast.makeText(this, "Date of Birth must be earlier than 2015", Toast.LENGTH_SHORT).show();
            return;
        }

        //set height or weight to zero if nothing is supplied
        height = (height.length() == 0) ? "0":height;
        weight = (weight.length() == 0) ? "0":weight;

        //Suppied details validated
        if(profileDetailsSupplied == 1){ //if profile details supplied already
            //update the user details
            try {
                String query = "UPDATE tblUserProfile SET email = '" + email + "', firstName = '" + firstName + "', lastName = '" + lastName + "', gender = '" + gender + "', DOB = '" + dob + "', height = " + height + ", weight = " + weight + ", picLocation = '" + userPicLocation + "', picType = '" + userPicType + "' WHERE id = 0";
                System.out.println("UPDATE PROF QUERY: " + query);
                sqLiteDatabase.execSQL(query);
                Toast.makeText(getApplicationContext(), "Profile updated successfully.", Toast.LENGTH_LONG).show();
                Log.i("SUCCESSFUL PRF UPDATE: ", "profile updated");
                finish();
            } catch(Exception ex){
                Toast.makeText(getApplicationContext(), "Error updating profile.", Toast.LENGTH_LONG).show();
                Log.i("ERROR UPDATING PROF", ex.getMessage());
            }
        } else {
            //insert new details
            try {
                String query = "INSERT INTO tblUserProfile (id, email, firstName, lastName, gender, DOB, height, weight, picLocation, picType) VALUES (0, '" + email + "', '" + firstName + "', '" + lastName + "', '" + gender + "', '" + dob + "', " + height + ", " + weight + ", '" + userPicLocation + "', '" + userPicType + "')";
                System.out.println("INSERT PROF QUERY: " + query);
                sqLiteDatabase.execSQL(query);
                Toast.makeText(getApplicationContext(), "Profile details added successfully.", Toast.LENGTH_LONG).show();
                Log.i("SUCCESSFUL ADDING PRF: ", "profile details added");
                finish();
            } catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Error adding profile details.", Toast.LENGTH_LONG).show();
                Log.i("ERROR ADDING PROF", ex.getMessage());
            }
        }

    }
    //check that string contains alphabets only
    private boolean isAlpha(String str){
        return ((!str.equals(""))
                && (str != null)
                && (str.matches("^[a-zA-Z]*$")));
    }

    //validate date of birth, must be earlier than 2015
    private boolean checkDOB(String str){
        boolean resp = true;
        if(str.length() != 0){
            String[] parts = str.split("-");
            if(Integer.parseInt(parts[0]) > 2015) {
                resp = false;
            }
        }
        return resp;
    }
}
