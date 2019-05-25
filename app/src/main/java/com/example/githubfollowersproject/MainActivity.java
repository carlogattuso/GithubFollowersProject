package com.example.githubfollowersproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class MainActivity extends AppCompatActivity {

    private GithubAPI api;
    private GithubUser user;
    private List<GithubFollowers> followers = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent getUser_intent = getIntent();

        final String username = getUser_intent.getStringExtra("username");

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
                    Toast toast = Toast.makeText(getApplicationContext(),
                            response.code(),
                            Toast.LENGTH_SHORT);
                    toast.show();
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
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Unexpected error",
                        Toast.LENGTH_SHORT);
                toast.show();
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
                    Toast toast = Toast.makeText(getApplicationContext(),
                            response.code(),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

                followers = response.body();

                mAdapter = new MyAdapter(followers);
                recyclerView.setAdapter(mAdapter);

            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<List<GithubFollowers>> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Unexpected error",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
