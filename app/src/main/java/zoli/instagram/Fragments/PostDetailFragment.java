package zoli.instagram.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import zoli.instagram.Adapter.PostAdapter;
import zoli.instagram.Api.PostApi;
import zoli.instagram.Model.Post;
import zoli.instagram.R;

public class PostDetailFragment extends Fragment {

    String postid;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences preferences= getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid = preferences.getString("postid","none");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter= new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        PostApi.readPost(postid, postList, postAdapter);


        return view;
    }
}
