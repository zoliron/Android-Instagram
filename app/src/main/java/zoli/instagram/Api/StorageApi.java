package zoli.instagram.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import zoli.instagram.MainActivity;

public class StorageApi {

    public static StorageReference REF_STORAGE = FirebaseStorage.getInstance().getReference("uploads");
    public static StorageReference REF_POST_STORAGE = FirebaseStorage.getInstance().getReference("posts");

    private static StorageTask uploadTask;

    // Uploading image to Firebase Storage
    public static void uploadImage(Context pdContext, final Context editProfileActivityContext, Uri mImageUri, String fileExtension) {
        final ProgressDialog pd = new ProgressDialog(pdContext);
        pd.setMessage("Uploading");
        pd.show();

        if (mImageUri != null) {
            final StorageReference fileReference = REF_STORAGE.child(System.currentTimeMillis() + "." + fileExtension);

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", "" + myUrl);

                        UserApi.REF_USERS.child(UserApi.currentUser.getUid()).updateChildren(hashMap);
                        pd.dismiss();
                    } else {
                        Toast.makeText(editProfileActivityContext, "Failed Uploading", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(editProfileActivityContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(pdContext, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    //Edge cases when posting a photo
    public static void uploadPostImage(final Context postActivityContext, Uri imageUri, String fileExtension, final EditText description) {
        final ProgressDialog progressDialog = new ProgressDialog(postActivityContext);
        progressDialog.setMessage("Posting");
        progressDialog.show();
        if (imageUri != null) {
            final StorageReference filereference = REF_POST_STORAGE.child(System.currentTimeMillis() + "." + fileExtension);
            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()) {  //if the task was not successful
                        throw task.getException();
                    }

                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        String postid = PostApi.REF_POSTS.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", UserApi.currentUser.getUid());

                        PostApi.REF_POSTS.child(postid).setValue(hashMap);

                        progressDialog.dismiss();

                        postActivityContext.startActivity(new Intent(postActivityContext, MainActivity.class));
                        ((Activity)postActivityContext).finish();

                    } else {

                        Toast.makeText(postActivityContext, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(postActivityContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(postActivityContext, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }
}
