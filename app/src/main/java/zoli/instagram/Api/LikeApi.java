package zoli.instagram.Api;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import zoli.instagram.Adapter.UserAdapter;
import zoli.instagram.Model.User;
import zoli.instagram.R;

public class LikeApi {
    public static DatabaseReference REF_LIKES = FirebaseDatabase.getInstance().getReference("Likes");
    public static DatabaseReference REF_CHILD_LIKES = FirebaseDatabase.getInstance().getReference().child("Likes");

    //Make possible to users to like a post
    public static void isLikes(String postid, final ImageView imageView) {
        //change the image like
        REF_CHILD_LIKES.child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(UserApi.currentUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Get numbers of likes
    public static void nrLikes(final TextView likes, String postid) {
        REF_CHILD_LIKES.child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getLikes(String id, final List<String> idList, final List<User> userList, final UserAdapter userAdapter) {
        REF_LIKES.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    idList.add(snapshot.getKey());
                }
                UserApi.showUsers(idList, userList, userAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
