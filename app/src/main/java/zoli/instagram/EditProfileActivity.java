package zoli.instagram;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import zoli.instagram.Api.StorageApi;
import zoli.instagram.Api.UserApi;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close, image_profile;
    TextView save, tv_change;
    MaterialEditText fullname, username, bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        UserApi.editProfile(fullname, username, bio, getApplicationContext(), image_profile);

        // OnClickListener to define what to do when pressing close - closing
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // OnClickListener to define what to do when pressing change photo - let you change photo by clicking the profile image
        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(EditProfileActivity.this);
            }
        });

        // OnClickListener to define what to do when pressing the android photo - let you change photo by clicking the "change photo"
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(EditProfileActivity.this);
            }
        });

        // OnClickListener to define what to do when pressing the android photo - saves the changes
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApi.updateProfile(fullname.getText().toString(), username.getText().toString(), bio.getText().toString(), EditProfileActivity.this);
                Toast.makeText(EditProfileActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    // Getting the activity result and uploading the profile image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri mImageUri = result.getUri();

            StorageApi.uploadImage(this,EditProfileActivity.this, mImageUri, getFileExtension(mImageUri));

        } else {
            Toast.makeText(this, "Something Gone Wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
