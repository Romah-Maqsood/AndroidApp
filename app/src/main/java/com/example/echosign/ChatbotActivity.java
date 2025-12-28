package com.example.echosign;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private GenerativeModelFutures generativeModel;
    private boolean isGeminiInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        recyclerView = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Setup RecyclerView
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Initialize Gemini Model
        initializeGemini();

        // Send button listener
        btnSend.setOnClickListener(v -> {
            String messageText = etMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                if (!isGeminiInitialized) {
                    addMessage("The chatbot is not initialized. Please check your API key in gradle.properties.", false);
                    return;
                }
                sendMessage(messageText);
                etMessage.setText("");
            }
        });

        // Add a welcome message
        if (isGeminiInitialized) {
            addMessage("Welcome to the EchoSign Help Assistant! How can I assist you today?", false);
        }
    }

    private void initializeGemini() {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey.equals("YOUR_API_KEY") || apiKey.isEmpty()) {
            String errorMsg = "API Key not found. Please add it to your gradle.properties file and rebuild the app.";
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            addMessage(errorMsg, false); // Show error in chat
            isGeminiInitialized = false;
            return;
        }

        // THIS IS THE FIX: Using the latest, recommended model name
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash-latest",
                apiKey
        );
        generativeModel = GenerativeModelFutures.from(gm);
        isGeminiInitialized = true;
    }

    private void sendMessage(String userMessage) {
        // Add user message to the chat
        addMessage(userMessage, true);
        setLoading(true);

        // System instruction to guide the chatbot's behavior
        String systemInstruction = "You are an assistive chatbot for EchoSign, an app designed for deaf users. " +
                "Only answer questions related to deafness, sign language, accessibility, or how to use the EchoSign app. " +
                "If a question is unrelated (e.g., about movies, politics, or general knowledge), " +
                "politely redirect the user by saying something like, 'I can only answer questions about EchoSign and deaf accessibility.'";

        Content content = new Content.Builder()
                .addText(systemInstruction)
                .addText(userMessage)
                .build();

        ListenableFuture<GenerateContentResponse> response = generativeModel.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botResponse = result.getText();
                runOnUiThread(() -> {
                    setLoading(false); // Remove loading indicator before adding new message
                    addMessage(botResponse, false);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                // THIS IS THE FIX: Display the actual error message
                String errorMessage = "Error: " + t.getMessage();
                runOnUiThread(() -> {
                    setLoading(false); // Remove loading indicator
                    addMessage(errorMessage, false); // Show specific error in the chat
                });
                t.printStackTrace(); // Keep this for detailed logs in Logcat
            }
        }, this.getMainExecutor());
    }

    private void addMessage(String message, boolean isUser) {
        chatMessages.add(new ChatMessage(message, isUser));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void setLoading(boolean isLoading) {
        // Remove previous loading indicator if it exists
        if (!chatMessages.isEmpty() && chatMessages.get(chatMessages.size() - 1).getMessage().equals("...")) {
            chatMessages.remove(chatMessages.size() - 1);
            chatAdapter.notifyItemRemoved(chatMessages.size());
        }

        if (isLoading) {
            chatMessages.add(new ChatMessage("...", false)); // Placeholder for loading
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerView.scrollToPosition(chatMessages.size() - 1);
        }
    }
}