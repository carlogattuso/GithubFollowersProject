package com.example.githubfollowersproject;

import android.app.Dialog;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class MainActivity extends AppCompatActivity {

    private String username;
    private GithubAPI api;
    private GithubUser user;
    private List<GithubFollowers> followers = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        final Intent getUser_intent = getIntent();

        username = getUser_intent.getStringExtra("username");

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(GithubAPI.class);

        getUser(username);
        getFollowers(username);

    }

    public void getUser(final String username){
        Call<GithubUser> call = api.getUser(username);

        call.enqueue(new Callback<GithubUser>(){
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<GithubUser> call, Response<GithubUser> response) {
                if(!response.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    dialogMessage("Failed of the user request");
                }

                TextView usernameText = findViewById(R.id.username);
                TextView repositories = findViewById(R.id.repositiories);
                TextView following = findViewById(R.id.following);
                ImageView userImage = findViewById(R.id.userImage);

                user = response.body();

                String firstLine = "Repositories: "+ user.getPublic_repos();
                String secondLine = "Following: "+ user.getFollowing();

                usernameText.setText(username);
                repositories.setText(firstLine);
                following.setText(secondLine);
                Picasso.get().load(user.getAvatar_url()).into(userImage);

            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<GithubUser> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                dialogMessage("Failed to get the user data");
            }
        });
    }

    public void getFollowers(final String username){
        Call<List<GithubFollowers>> call = api.getFollowers(username);

        call.enqueue(new Callback<List<GithubFollowers>>(){
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<List<GithubFollowers>> call, Response<List<GithubFollowers>> response) {
                if(!response.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    dialogMessage("Failed of the followers request");
                }

                followers = response.body();

                mAdapter = new MyAdapter(followers);
                recyclerView.setAdapter(mAdapter);

            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<List<GithubFollowers>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                dialogMessage("Failed to get the followers");
            }
        });
    }
    public void dialogMessage(String result) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        builder.setMessage(result).setTitle("Info");
        Dialog dialeg = builder.create();
        dialeg.show();
    }
}
