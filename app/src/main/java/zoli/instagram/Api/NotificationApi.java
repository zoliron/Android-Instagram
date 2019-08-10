package zoli.instagram.Api;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NotificationApi {
    public static DatabaseReference REF_NOTIFICATIONS = FirebaseDatabase.getInstance().getReference().child("Notifications");

    public static void deleteNotifications(final String postid, String userid, final Context mContext){
        REF_NOTIFICATIONS.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Likes Notifications
    public static void addLikeNotifications(String userid, String postid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", UserApi.currentUser.getUid());
        hashMap.put("text", "Liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        REF_NOTIFICATIONS.push().setValue(hashMap);
    }

    // Follow Notifications
    public static void addFollowNotifications(String userid){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", UserApi.currentUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        REF_NOTIFICATIONS.child(userid).push().setValue(hashMap);
    }
}
