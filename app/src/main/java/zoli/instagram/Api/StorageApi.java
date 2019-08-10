package zoli.instagram.Api;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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

import zoli.instagram.EditProfileActivity;

public class StorageApi {

    public static StorageReference REF_STORAGE = FirebaseStorage.getInstance().getReference("uploads");
    private static StorageTask uploadTask;

    // Uploading image to Firebase Storage
    public static void uploadImage(Context pdContext, final Context editProfileActivityContext, Uri mImageUri, String fileExtension){
        final ProgressDialog pd = new ProgressDialog(pdContext);
        pd.setMessage("Uploading");
        pd.show();

        if (mImageUri != null){
            final StorageReference fileReference = REF_STORAGE.child(System.currentTimeMillis() + "." + fileExtension);

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", ""+myUrl);

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
}
