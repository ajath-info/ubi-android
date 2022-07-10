package com.ubi.android.activity;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ubi.android.R;
import com.ubi.android.adapters.ChatAdapter;
import com.ubi.android.models.ChatRequestModel;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    EditText messagtv;
    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView nodata;
    DatabaseReference myRef;
    FirebaseDatabase database;
    ChatAdapter adapter;
    ArrayList<ChatRequestModel> models = new ArrayList<>();
    UserData user;
    private String android_id;
    SwipeRefreshLayout swipe;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchdata();
            }
        });
        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        user = AppPreferences.getInstance().getUserData(getApplicationContext());
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        nodata = findViewById(R.id.nodata);
        recyclerview = findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(linearLayoutManager);
        adapter = new ChatAdapter(this, models, user.getId());
//        adapter.setListner(new OnAdapterItemClickListner() {
//            @Override
//            public void onClick(View view, int pos) {
//
//            }
//        });
        recyclerview.setAdapter(adapter);
        nodata.setVisibility(View.GONE);
        progressbar = findViewById(R.id.progressbar);
        database = FirebaseDatabase.getInstance();
        String databaseref = "Conversation/Message/" + getIntent().getStringExtra("nodeid");
        Log.d(ChatActivity.class.getName(), databaseref);
        myRef = database.getReference(databaseref);
        fetchdata();
        messagtv = findViewById(R.id.messagtv);
        findViewById(R.id.sendlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strmessagtv = messagtv.getText().toString();
                if (TextUtils.isEmpty(strmessagtv)) {
                    AppUtils.showalert(ChatActivity.this, "Please enter your message", false);
                } else {
                    messagtv.setText("");
//                    AppUtils.hidekeyboard(ChatActivity.this, messagtv);
                    sendmessage(strmessagtv);
                }
            }
        });
    }

    private void fetchdata() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    progressbar.setVisibility(View.GONE);
                    swipe.setRefreshing(false);
                    if (snapshot.exists()) {
                        models.clear();
                        adapter.notifyDataSetChanged();
                        Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            Map singleUser = (Map) entry.getValue();
                            ChatRequestModel model = new ChatRequestModel();
                            model.message = (String) singleUser.get("message");
                            model.deviceId = (String) singleUser.get("deviceId");
                            model.mediaType = (String) singleUser.get("mediaType");
                            model.receiverId = (String) singleUser.get("receiverId");
                            model.receiverName = (String) singleUser.get("receiverName");
                            model.senderId = (String) singleUser.get("senderId");
                            model.senderName = (String) singleUser.get("senderName");
                            model.timeStamp = (String) singleUser.get("timeStamp");
                            models.add(model);
                        }
                        Collections.sort(models, new Comparator<ChatRequestModel>() {
                            public int compare(ChatRequestModel obj1, ChatRequestModel obj2) {
                                return obj1.timeStamp.compareToIgnoreCase(obj2.timeStamp); // To compare string values
                            }
                        });
                        if (models.size() > 0)
                            adapter.notifyDataSetChanged();
                        else {
                            nodata.setVisibility(View.VISIBLE);
                            nodata.setText("No chat found, Let's start chat");
                        }
                        linearLayoutManager.scrollToPosition(models.size() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressbar.setVisibility(View.GONE);
                error.toException().printStackTrace();
                swipe.setRefreshing(false);
            }

        });
    }

    private void sendmessage(String strmessagtv) {
        HashMap<String, String> data = new HashMap<>();
        data.put("deviceId", android_id);
        data.put("mediaType", "text");
        data.put("message", strmessagtv);
        data.put("receiverId", getIntent().getStringExtra("receiverId"));
        data.put("senderId", user.getId());
        String receiverName = getIntent().getStringExtra("receiverName");
        if (!TextUtils.isEmpty(receiverName))
            data.put("receiverName", receiverName);
        else
            data.put("receiverName", "NA");
        data.put("senderName", user.getFirst_name() + " " + user.getLast_name());
        data.put("timeStamp", "" + System.currentTimeMillis());
        String modelID = myRef.push().getKey();
        myRef.child(modelID).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    linearLayoutManager.scrollToPosition(models.size() - 1);
//                    Toast.makeText(getApplicationContext(), "Commented", Toast.LENGTH_SHORT).show();
                } else {
                    task.getException().printStackTrace();
                }
            }
        });

    }
}