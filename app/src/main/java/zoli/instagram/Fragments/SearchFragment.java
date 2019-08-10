package zoli.instagram.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import zoli.instagram.Adapter.UserAdapter;
import zoli.instagram.Api.UserApi;
import zoli.instagram.Model.User;
import zoli.instagram.R;


public class SearchFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<User> mUsers;

    private EditText search_bar;


    // Searching users from the search bar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar =  view.findViewById(R.id.search_bar);

        mUsers =  new ArrayList<>();
        userAdapter= new UserAdapter(getContext(),mUsers,true);
        recyclerView.setAdapter(userAdapter);

        UserApi.readUsers(search_bar, mUsers, userAdapter);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                UserApi.searchUsers(charSequence.toString().toLowerCase(), mUsers, userAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }
}

