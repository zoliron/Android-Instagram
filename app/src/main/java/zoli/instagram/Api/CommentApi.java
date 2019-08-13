package zoli.instagram.Api;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import zoli.instagram.Adapter.CommentAdapter;
import zoli.instagram.Model.Comment;
import zoli.instagram.Model.User;

public class CommentApi {

    public static DatabaseReference REF_COMMENTS = FirebaseDatabase.getInstance().getReference("Comments");
    public static DatabaseReference REF_CHILD_COMMENTS = FirebaseDatabase.getInstance().getReference().child("Comments");

    public static void getComments(String postid, final TextView comments) {
        REF_CHILD_COMMENTS.child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All " + dataSnapshot.getChildrenCount() + " Comments"); //add this line under every post that keep count the numbers of comments
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Add the "comment" & "publisher" info for FB
    public static void addComment(String postid, EditText addComment){
        String commentid =  REF_COMMENTS.child(postid).push().getKey();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("comment", addComment.getText().toString());
        hashMap.put("publisher", UserApi.currentUser.getUid());
        hashMap.put("commentid", commentid);

        REF_COMMENTS.child(postid).child(commentid).setValue(hashMap);
        NotificationApi.addCommentNotifications(UserApi.currentUser.getUid(), addComment, postid);

        addComment.setText("");
    }

    public static void getImage(final Context mContext, final ImageView image_profile){
        UserApi.REF_USERS.child(UserApi.currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void readComments(String postid, final List<Comment> commentList, final CommentAdapter commentAdapter){
        REF_COMMENTS.child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
