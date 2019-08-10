package zoli.instagram.Api;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import zoli.instagram.Adapter.MyFotoAdapter;
import zoli.instagram.Model.Post;
import zoli.instagram.R;

public class SaveApi {

    public static DatabaseReference REF_SAVES = FirebaseDatabase.getInstance().getReference("Saves");
    private static List<String> mySaves;


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

    //present the saved post on my profile under the save icon
    public static void mySaves(final List<Post> postList_saves, final MyFotoAdapter myFotoAdapter_saves) {
        mySaves = new ArrayList<>();
        REF_SAVES.child(UserApi.currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mySaves.add(snapshot.getKey());
                }

                readSaves(postList_saves, myFotoAdapter_saves); // update the data of the saved posts
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void readSaves(final List<Post> postList_saves, final MyFotoAdapter myFotoAdapter_saves) {
        PostApi.REF_POSTS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Post post = snapshot.getValue(Post.class);

                    for (String id : mySaves) {
                        if (post.getPostid().equals(id)) {
                            postList_saves.add(post);
                        }
                    }
                }

                myFotoAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
