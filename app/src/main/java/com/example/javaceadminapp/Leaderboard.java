package com.example.javaceadminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class Leaderboard extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<LeaderboardModel> modelLeaderboardArrayList;
    LeaderboardAdapter adapterLeaderboard;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Leaderboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching leaderboards data...");
        progressDialog.setCanceledOnTouchOutside(false);

        recyclerView = findViewById(R.id.leaderboardRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        modelLeaderboardArrayList = new ArrayList<LeaderboardModel>();
        adapterLeaderboard = new LeaderboardAdapter(Leaderboard.this, modelLeaderboardArrayList);

        recyclerView.setAdapter(adapterLeaderboard);



        fetchLeaderboard();



    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchLeaderboard();
    }

    private void fetchLeaderboard() {
        progressDialog.show();
        firestore.collection("Users").orderBy("score", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        modelLeaderboardArrayList.clear();
                        if (error != null) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Toast.makeText(Leaderboard.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                modelLeaderboardArrayList.add(documentChange.getDocument().toObject(LeaderboardModel.class));
                            }
                            adapterLeaderboard.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}