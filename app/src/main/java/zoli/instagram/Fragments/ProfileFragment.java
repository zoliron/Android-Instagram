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

import zoli.instagram.Adapter.MyFotoAdapter;
import zoli.instagram.Api.FollowApi;
import zoli.instagram.Api.PostApi;
import zoli.instagram.Api.SaveApi;
import zoli.instagram.Api.UserApi;
import zoli.instagram.EditProfileActivity;
import zoli.instagram.FollowersActivity;
import zoli.instagram.Model.Post;
import zoli.instagram.Model.User;
import zoli.instagram.OptionsActivity;
import zoli.instagram.R;


//Display user information on the user profile
public class ProfileFragment extends Fragment {

    private Button edit_profile;
    private RecyclerView recyclerView_saves;
    private RecyclerView recyclerView;
    private String profileid;


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

        //Related to MyFotoAdapter
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Post> postList = new ArrayList<>();
        MyFotoAdapter myFotoAdapter = new MyFotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        List<Post> postList_saves = new ArrayList<>();
        MyFotoAdapter myFotoAdapter_saves = new MyFotoAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myFotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);


        UserApi.userInfo(profileid, getContext(), image_profile, username, fullname, bio);
        FollowApi.getFollowers(profileid, followers, following); //to update the numbers of the followers in the profile data bar
        PostApi.getNrPosts(profileid, posts);
        PostApi.myFotos(postList, profileid, myFotoAdapter);
        SaveApi.mySaves(postList_saves, myFotoAdapter_saves);

        if (profileid.equals(UserApi.currentUser.getUid())) {
            edit_profile.setText("Edit Profile");
        } else {
            FollowApi.checkFollow(profileid, edit_profile);
            saved_fotos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if (btn.equals("follow")) { //if the user click on "follow" btn - we will add it to the FB under "Follow"

                    FollowApi.REF_FOLLOW.child(UserApi.currentUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FollowApi.REF_FOLLOW.child(profileid)
                            .child("followers").child(UserApi.currentUser.getUid()).setValue(true);

                } else if (btn.equals("following")) {  //if the user click on "following" btn - we will remove from FB under "Follow"

                    FollowApi.REF_FOLLOW.child(UserApi.currentUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FollowApi.REF_FOLLOW.child(profileid)
                            .child("followers").child(UserApi.currentUser.getUid()).removeValue();
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

        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE); //the post is not saved

            }
        });

        saved_fotos.setOnClickListener(new View.OnClickListener() {
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
}
