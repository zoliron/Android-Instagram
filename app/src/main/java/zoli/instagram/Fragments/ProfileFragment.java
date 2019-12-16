package zoli.instagram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zoli.instagram.Adapter.MyPhotoAdapter;
import zoli.instagram.EditProfileActivity;
import zoli.instagram.FollowersActivity;
import zoli.instagram.Model.Post;
import zoli.instagram.Model.User;
import zoli.instagram.OptionsActivity;
import zoli.instagram.R;


//Display user information on the user profile
public class ProfileFragment extends Fragment {


    ImageView image_profile, options;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;

    private List<String> mySaves;

    RecyclerView recyclerView_saves;
    MyPhotoAdapter myPhotoAdapter_saves;
    List<Post> postList_saves;

    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileid;

    ImageButton my_photos, saved_photos;


    // Inflate profile fragment to the reusable container
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        ImageView image_profile = view.findViewById(R.id.image_profile);
        ImageView options = view.findViewById(R.id.options);
        TextView posts = view.findViewById(R.id.posts);
        TextView followers = view.findViewById(R.id.followers);
        TextView following = view.findViewById(R.id.following);
        TextView fullname = view.findViewById(R.id.fullname);
        TextView bio = view.findViewById(R.id.bio);
        TextView username = view.findViewById(R.id.username);
        edit_profile = view.findViewById(R.id.edit_profile);
        ImageButton my_fotos = view.findViewById(R.id.my_fotos);
        ImageButton saved_fotos = view.findViewById(R.id.saved_fotos);

        //Related to MyPhotoAdapter
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myPhotoAdapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myPhotoAdapter_saves = new MyPhotoAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myPhotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);


        userInfo();
        getFollowers(); //to update the numbers of the followers in the profile data bar
        getNrPosts();
        myPhotos();
        mySaves();

        if (profileid.equals(firebaseUser.getUid())) {
            edit_profile.setText("Edit Profile");
        } else {
            checkFollow();
            saved_photos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if (btn.equals("follow")) { //if the user click on "follow" btn - we will add it to the FB under "Follow"

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                } else if (btn.equals("following")) {  //if the user click on "following" btn - we will remove from FB under "Follow"

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });

        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE); //the post is not saved

            }
        });

        saved_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE); //the post is saved
            }
        });

        // Gets the followers list
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        // Gets the following list
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });

        return view;
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
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

    private void getFollowers() { // get the data of the follower
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // get the data of the following
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        i++;
                    }
                }

                posts.setText("" + i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Add the posts to FB
    private void myPhotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //present the saved post on my profile under the save icon
    private void mySaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mySaves.add(snapshot.getKey());
                }

                readSaves(); // update the data of the saved posts
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
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

                myPhotoAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
