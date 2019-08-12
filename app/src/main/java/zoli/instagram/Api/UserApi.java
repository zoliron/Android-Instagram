package zoli.instagram.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import zoli.instagram.Adapter.UserAdapter;
import zoli.instagram.LoginActivity;
import zoli.instagram.MainActivity;
import zoli.instagram.Model.User;
import zoli.instagram.RegisterActivity;

public class UserApi {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    public static DatabaseReference REF_USERS = FirebaseDatabase.getInstance().getReference().child("Users");

    public static void getUserInfo(final ImageView imageView, final TextView username, String publisherid, final Context mContext) {
        REF_USERS.child(publisherid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid, final Context mContext) {
        REF_USERS.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void userInfo(String profileid, final Context mContext, final ImageView image_profile, final TextView username, final TextView fullname, final TextView bio) {
        REF_USERS.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mContext == null) {
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void searchUsers(String s, final List<User> mUsers, final UserAdapter userAdapter) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void readUsers(final EditText search_bar, final List<User> mUsers, final UserAdapter userAdapter) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_bar.getText().toString().equals("")) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void editProfile(final EditText fullname, final EditText username, final EditText bio, final Context mContext, final ImageView image_profile) {
        // Editing profile info according to user data
        REF_USERS.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullname.setText(user.getFullname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Updates the user data at Firebase Database
    public static void updateProfile(String fullname, String username, String bio, Context editProfileContext) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fullname", fullname);
        hashMap.put("username", username);
        hashMap.put("bio", bio);

        REF_USERS.child(currentUser.getUid()).updateChildren(hashMap);

        Toast.makeText(editProfileContext, "Successfully Updated", Toast.LENGTH_SHORT).show();
    }

    public static void showUsers(final List<String> idList, final List<User> userList, final UserAdapter userAdapter) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (String id : idList) {
                        if (user.getId().equals(id)) {
                            userList.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void loginUser(final ProgressDialog pd, String str_email, String str_password, final Activity loginActivity, final Context loginActivityContext) {
        // SignIn using Firebase Authentication
        firebaseAuth.signInWithEmailAndPassword(str_email, str_password).addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pd.dismiss();
                            Intent intent = new Intent(loginActivityContext, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            loginActivityContext.startActivity(intent);
                            loginActivity.finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            pd.dismiss();
                        }
                    });
                } else {
                    // Raise error if authentication fails
                    pd.dismiss();
                    Toast.makeText(loginActivityContext, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Creating new user in Firebase Authentication and store his/her information in Firebase Database
    public static void register(final ProgressDialog pd, final String username, final String fullname, final String email, String password, final Activity registerActivity, final Context registerActivityContext){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userID);
                            hashMap.put("username", username);
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            hashMap.put("email", email);
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/android-instagram-89c73.appspot.com/o/placeholder.png?alt=media&token=d5425fe4-9252-492f-8b69-44c81da0c754");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(registerActivityContext, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        registerActivity.startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(registerActivityContext, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
