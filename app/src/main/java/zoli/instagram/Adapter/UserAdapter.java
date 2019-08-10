package zoli.instagram.Adapter;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import zoli.instagram.Api.FollowApi;
import zoli.instagram.Api.NotificationApi;
import zoli.instagram.Api.UserApi;
import zoli.instagram.Fragments.ProfileFragment;
import zoli.instagram.MainActivity;
import zoli.instagram.Model.User;
import zoli.instagram.R;

import static android.content.Context.MODE_PRIVATE;

//After find the user - we will follow/unfollow him and update on the FB.
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;


    public UserAdapter(Context context, List<User> users, boolean isFragment){
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ImageViewHolder holder, final int position) {


        final User user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        FollowApi.isFollowing(user.getId(), holder.btn_follow);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);

        if (user.getId().equals(UserApi.currentUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn_follow.getText().toString().equals("follow")) {
                    FollowApi.REF_FOLLOW.child(UserApi.currentUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FollowApi.REF_FOLLOW.child(user.getId())
                            .child("followers").child(UserApi.currentUser.getUid()).setValue(true);

                    NotificationApi.addFollowNotifications(user.getId());
                } else {
                    FollowApi.REF_FOLLOW.child(UserApi.currentUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FollowApi.REF_FOLLOW.child(user.getId())
                            .child("followers").child(UserApi.currentUser.getUid()).removeValue();
                }
            }

        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }
}



