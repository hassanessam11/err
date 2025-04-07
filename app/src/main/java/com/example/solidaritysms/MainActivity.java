
package com.example.solidaritysms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView messageView;
    private Button sendButton;
    private String messageText = "";
    private final String API_URL = "https://lobay95670.pythonanywhere.com/get-message";
    private final String RECIPIENT_NUMBER = "+201146028426";  // يمكن تغييره لاحقاً

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageView = findViewById(R.id.message_view);
        sendButton = findViewById(R.id.send_button);

        // طلب صلاحية إرسال SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        // جلب الرسالة من الـ API
        fetchMessage();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageText.isEmpty()) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(RECIPIENT_NUMBER, null, messageText, null, null);
                    Toast.makeText(MainActivity.this, "تم إرسال الرسالة ✅", Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(false);
                }
            }
        });
    }

    private void fetchMessage() {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject json = new JSONObject(response.toString());
                messageText = json.getString("message");

                runOnUiThread(() -> messageView.setText(messageText));

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "فشل في جلب الرسالة ❌", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }
}
        