package zoli.instagram.Api;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommentApi {

    public static DatabaseReference REF_COMMENTS = FirebaseDatabase.getInstance().getReference("Comments");

    public static void getComments(String postid, final TextView comments) {
        REF_COMMENTS.child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All " + dataSnapshot.getChildrenCount() + " Comments"); //add this line under every post that keep count the numbers of comments
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
