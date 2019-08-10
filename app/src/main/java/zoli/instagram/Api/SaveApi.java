package zoli.instagram.Api;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import zoli.instagram.R;

public class SaveApi {

    public static DatabaseReference REF_SAVES = FirebaseDatabase.getInstance().getReference("Saves");

    public static void isSaved(final String postid, final ImageView imageView) {
        REF_SAVES.child(UserApi.currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //set the save icone and add him the tag 'saved'
                if (dataSnapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_savee_black);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
