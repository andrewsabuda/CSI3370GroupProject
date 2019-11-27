package com.example.csi3370project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

public class HomeActivity extends AppCompatActivity {
    // Scaledrone connection info
    private String channelID = "g9AinhUXKb3esN1g";
    private String roomName = "CSI3370";
    private Scaledrone scaledrone;
    LoginActivity loginActivity = new LoginActivity();

    // Firebase
    Button btnLogout;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLogout = findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intToMain);
            }
        });

        LoginActivity loginActivity = new LoginActivity();

        String userEmail = loginActivity.userEmail;
        MemberData data = new MemberData(userEmail);
        System.out.println("Logging: " + userEmail);

        // Scaledrone connection done in onCreate, this may change
        scaledrone = new Scaledrone(channelID, data);

        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                scaledrone.subscribe(roomName, new RoomListener() {
                    @Override
                    public void onOpen(Room room) {
                        room.publish("Hello world");
                    }

                    @Override
                    public void onOpenFailure(Room room, Exception ex) {
                        System.out.println("Failed to subscribe to room: " + ex.getMessage());
                    }

                    @Override
                    public void onMessage(Room room, Message message) {
                        System.out.println("Message: " + message.getData().asText());
                    }
                });
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.out.println("Failed to open connection: " + ex.getMessage());
            }

            @Override
            public void onFailure(Exception ex) {
                System.out.println("Unexpected failure: " + ex.getMessage());
            }

            @Override
            public void onClosed(String reason) {
                System.out.println("Connection closed: " + reason);
            }
        });
    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            scaledrone.publish(roomName, message);
            editText.getText().clear();
        }
    }
}