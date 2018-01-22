package info.androidhive.firebase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by francisclarke on 12/01/2018.
 */

public class UploadInformation extends AppCompatActivity
{
    Button select_image,upload_button;
    ImageView user_image;
    TextView title;
    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private Firebase mRoofRef;
    private Uri mImageUri = null;
    private DatabaseReference mdatabaseRef;
    private StorageReference mStorage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.upload_layout);

        Firebase.setAndroidContext(this);

        select_image = (Button) findViewById(R.id.select_image);
        upload_button = (Button) findViewById(R.id.upload_bttn);
        user_image = (ImageView) findViewById(R.id.user_image);
        //title = (TextView)findViewById(R.id.etTitle);

        //Initialise the Progress Bar
        mProgressDialog = new ProgressDialog(UploadInformation.this);

        //Select the image from External Storage
        select_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Check for Runtime Permission
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                        Toast.makeText(getApplicationContext(), "Step 2", Toast.LENGTH_SHORT).show();

                    } //end inner if
                } //end outer if
                else
                {
                    Toast.makeText(getApplicationContext(), "Step 3",Toast.LENGTH_LONG).show();
                    callgallery();
                }
            }
        });

        //Initialise Firebase Database paths for database and Storage
        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        //This is the URL for the actual Firebase Database
        mRoofRef = new Firebase("https://fir-5c0c8.firebaseio.com/").child("User_Details").push();
        //This is the URL for the Firebase Storage
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://fir-5c0c8.appspot.com/");


        //Click on Upload button title will upload to Database
        upload_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String mName = title.getText().toString().trim();

                if (mName.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Fill all field", Toast.LENGTH_SHORT).show();
                    return;
                }
                Firebase childRef_name = mRoofRef.child("Image_Title");
                childRef_name.setValue(mName);

                Toast.makeText(getApplicationContext(), "Updated Info", Toast.LENGTH_SHORT).show();
            }
        });
    }


        //Check for Realtime Permissions for Storage Access
        public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @NonNull int[] grantResults)
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch(requestCode)
            {
                case READ_EXTERNAL_STORAGE:
                    //if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //if(grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if(1==1)
                    {
                        callgallery();
                        return;
                    }
            }
            Toast.makeText(getApplicationContext(), "...",Toast.LENGTH_SHORT).show();
        }

    //If Access Granted galary will open
    private void callgallery()
    {
        Toast.makeText(getApplicationContext(), "Step 4",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    //After selecting image from gallary image will directly uploaded to Firebase Database
    //and image will show in Image View
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            mImageUri = data.getData();
            user_image.setImageURI(mImageUri);
            StorageReference filePath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());

            mProgressDialog.setMessage("Uploading Image...");
            mProgressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri downloadUri = taskSnapshot.getDownloadUrl(); //Ignore this error

                    mRoofRef.child("Image_URL").setValue(downloadUri.toString());

                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            .crossFade()
                            .placeholder(R.drawable.image1)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(user_image);
                    Toast.makeText(getApplicationContext(),"Updated.", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }
    }
}


