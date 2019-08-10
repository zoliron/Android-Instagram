package zoli.instagram.Api;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import zoli.instagram.Adapter.PostAdapter;
import zoli.instagram.Model.Post;

public class FollowApi {
    public static DatabaseReference REF_FOLLOW = FirebaseDatabase.getInstance().getReference("Follow");
    private static List<String> followingList;

    public static void isFollowing(final String userid, final Button button){
        REF_FOLLOW.child(UserApi.currentUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("following");
                } else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void checkFollowing(final List<Post> postList, final PostAdapter postAdapter, final ProgressBar progressBar){
        followingList = new ArrayList<>();
        REF_FOLLOW.child(UserApi.currentUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                PostApi.readPosts(followingList, postList, postAdapter, progressBar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void checkFollow(final String profileid, final Button edit_profile) {
        FollowApi.REF_FOLLOW.child(UserApi.currentUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()) {  //if the user exist under "following" put the tag 'following' on his profile if not 'follow' tag
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getFollowers(String profileid, final TextView followers, final TextView following) { // get the data of the follower
        FollowApi.REF_FOLLOW.child(profileid).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // get the data of the following
        FollowApi.REF_FOLLOW.child(profileid).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

