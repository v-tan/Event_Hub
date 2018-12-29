package com.vectortangent.android.eventhub;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity implements NetworkConnectivityListener{

    private RecyclerView eventsRecyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Event> events;
    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private TextView placeHolderTextView;
    private DatabaseReference eventsDbReference;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private TextView networkWarnTextView;
//    DatabaseReference databaseVisitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            initLoginActivity();
        }

        networkBroadcastReceiver = new NetworkBroadcastReceiver(this);
        eventsDbReference = FirebaseDatabase.getInstance().getReference("events");

        placeHolderTextView = findViewById(R.id.placeholder_text);
        networkWarnTextView = findViewById(R.id.networkWarningText);
        initRecyclerView();
        initFab();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(networkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        eventsDbReference.addValueEventListener(refEventListener);
    }

    private void initLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void initFab() {
        fab = findViewById(R.id.addEventFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this,
                        AddEventActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        eventsRecyclerView.setLayoutManager(llm);

        initAdapter();
        eventsRecyclerView.setAdapter(adapter);
    }

    private void initAdapter() {
        events = new ArrayList<>();
        adapter = new EventsAdapter(events);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventlist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signout:
                firebaseAuth.signOut();
                initLoginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ValueEventListener refEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            clearEvents();
            for (DataSnapshot eventSnapshot : dataSnapshot.getChildren() ){
                Event visitor = eventSnapshot.getValue(Event.class);
                events.add(visitor);
                adapter.notifyDataSetChanged();
            }
            if (events.isEmpty()) placeHolderTextView.setVisibility(View.VISIBLE);
            else
                placeHolderTextView.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(EventListActivity.this, "Problem fetching database", Toast.LENGTH_SHORT).show();
        }
    };

    private void clearEvents() {
        events.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        eventsDbReference.removeEventListener(refEventListener);
        unregisterReceiver(networkBroadcastReceiver);
        clearEvents();
        super.onStop();
    }

    @Override
    public void onNetworkChanged(String status) {
        if (status.equals("Not Connected")){
            networkWarnTextView.setVisibility(View.VISIBLE);
        } else {
            networkWarnTextView.setVisibility(View.GONE);
        }
    }
}
