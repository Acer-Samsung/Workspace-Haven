package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class ChairBookingBot extends TelegramLongPollingBot {
    private final String botUsername = "ChairBookingAssistantBot";
    private final String botToken = "7656398985:AAFD1iiHhXbcnFLh3oMcMGQQln9F-D9TeiY";
    private final String bookingServiceUrl = "http://localhost:8080"; // Change to your service URL

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void sendMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose an option:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(createButton("🪑 View Available Chairs", "view_chairs")));
        rows.add(List.of(createButton("✅ Book a Chair", "book_chair")));
        rows.add(List.of(createButton("❌ Cancel Booking", "cancel_booking")));
        rows.add(List.of(createButton("📋 My Booking", "my_booking")));
        rows.add(List.of(createButton("🔍 Check Chair Availability", "check_availability")));
        rows.add(List.of(createButton("📌 Get All Booked Chairs", "get_booked_chairs")));
        rows.add(List.of(createButton("🗑 Remove Chair (Admin)", "remove_chair"))); // Admin functionality

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onUpdateReceived(Update update) {
//
//        if (update.hasMessage()) {
//            Message message = update.getMessage();
//            long chatId = message.getChatId();
//            User telegramUser = message.getFrom();
//
//        }
//
//        if (update.hasMessage() && update.getMessage().hasText()) {
//
//
//
//            String command = update.getMessage().getText();
//            long chatId = update.getMessage().getChatId();
//
//            if (command.equals("/start")) {
//                sendMenu(chatId);
//            }
//        } else if (update.hasCallbackQuery()) {
//            String callbackData = update.getCallbackQuery().getData();
//            long chatId = update.getCallbackQuery().getMessage().getChatId();
//
//            switch (callbackData) {
//                case "view_chairs":
//                    sendChairsList(chatId);
//                    break;
//                case "book_chair":
//                    bookChair(chatId, 1); // Default chair ID, replace with dynamic logic
//                    break;
//                case "cancel_booking":
//                    cancelBooking(chatId, 1); // Default chair ID, replace dynamically
//                    break;
//                case "my_booking":
//                    checkBooking(chatId);
//                    break;
//                case "check_availability":
//                    checkChairAvailability(chatId, 1); // Default chair ID, replace dynamically
//                    break;
//                case "get_booked_chairs":
//                    getAllBookedChairs(chatId);
//                    break;
//                case "remove_chair":
//                    removeChair(chatId, 1); // Admin functionality, replace dynamically
//                    break;
//            }
//        }
//    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            User telegramUser = message.getFrom();

            // If user sends contact, register them
            if (message.hasContact()) {
                registerUser(chatId, telegramUser, message.getContact());
                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String command = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String telegramUser = update.getMessage().getFrom().getUserName();

            if (isUserRegistered(telegramUser)) {
                if (command.equals("/start")) {
                    sendMenu(chatId);
                }
            } else {
                requestContact(chatId);
            }
        } else if (update.hasCallbackQuery()) {

            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String telegramUser = update.getCallbackQuery().getFrom().getUserName();

            if (!isUserRegistered(telegramUser)) {
                sendMessage(chatId, "❌ You are not registered. Please send your contact to register first.");
                return;
            }

            String callbackData = update.getCallbackQuery().getData();


            switch (callbackData) {
                case "view_chairs":
                    sendChairsList(chatId);
                    break;
                case "book_chair":
                    bookChair(chatId, telegramUser, 4);
                    break;
                case "cancel_booking":
                    cancelBooking(chatId, telegramUser, 4);
                    break;
                case "my_booking":
                    checkBooking(telegramUser, chatId);
                    break;
                case "check_availability":
                    checkChairAvailability(chatId, 4);
                    break;
                case "get_booked_chairs":
                    getAllBookedChairs(chatId);
                    break;
                case "remove_chair":
                    removeChair(chatId, 1);
                    break;
            }
        }
    }

    // Function to check if user is registered
    private boolean isUserRegistered(String username) {
        String isRegistered = sendGetRequest(bookingServiceUrl + "/users/check/" + username);
        return isRegistered.equalsIgnoreCase("success");
    }

    // Function to request user contact
    private void requestContact(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please share your contact to register.");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardButton shareContactButton = new KeyboardButton("📞 Share Contact");
        shareContactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(shareContactButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Function to register user
    private void registerUser(long chatId, User telegramUser, Contact contact) {
        String fullName = telegramUser.getFirstName() +
                (telegramUser.getLastName() != null ? " " + telegramUser.getLastName() : "");
        String username = telegramUser.getUserName() != null ? telegramUser.getUserName() : "NoUsername";
        String phoneNumber = contact.getPhoneNumber();

        String requestBody = "{ \"telegramId\": \"" + chatId + "\", \"name\": \"" + fullName +
                "\", \"telegramUsername\": \"" + username + "\", \"phone\": \"" + phoneNumber + "\" }";
        String response = sendPostRequest(bookingServiceUrl + "/users/register", requestBody);

        if (response.contains("success")) {
            sendMessage(chatId, "✅ Registration successful! Here’s the menu:");
            sendMenu(chatId);
        } else {
            sendMessage(chatId, "❌ Registration failed. Please try again.");
        }
    }


    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void sendChairsList(long chatId) {
        try {
            // Fetch all available chairs from the Chair Booking Service
            String response = sendGetRequest(bookingServiceUrl + "/chairs");

            // Parse JSON response into a list of Chair objects
            ObjectMapper objectMapper = new ObjectMapper();
            List<Chair> chairs = objectMapper.readValue(response, new TypeReference<List<Chair>>() {
            });

            chairs.sort(Comparator.comparingLong(Chair::getId));

            // Format chairs into a readable message
            StringBuilder messageText = new StringBuilder("Available Chairs:\n");
            for (Chair chair : chairs) {
                messageText.append("🪑 Chair ID: ").append(chair.getId())
                        .append(" | Floor: ").append(chair.getFloor())
                        .append(" | Room: ").append(chair.getRoom())
                        .append(" | Available: ").append(chair.isAvailable() ? "✅ Yes" : "❌ No")
                        .append("\n");
            }

            // Send formatted message
            sendMessage(chatId, messageText.toString());

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "⚠️ Failed to fetch chairs list.");
        }
    }

    private void bookChair(long chatId, String telegramUser, int chairId) {
        // Book a specific chair using chairId
        String requestBody = "{ \"telegramUsername\": \"" + telegramUser + "\", \"chairId\": " + chairId + " }";
        String response = sendPostRequest(bookingServiceUrl + "/chairs/book", requestBody);
        sendMessage(chatId, response);
    }

    private void cancelBooking(long chatId, String username, int chairId) {
        // Cancel a specific chair booking using chairId
        String response = sendDeleteRequest(bookingServiceUrl + "/chairs/cancel/" + chairId + "?telegramUsername=" + username);
        sendMessage(chatId, response);
    }

    private void checkBooking(String username, long chatId) {
        // Check which chair the user has booked
        String response = sendGetRequest(bookingServiceUrl + "/chairs/mybooking/" + username);
        sendMessage(chatId, "Your Booking:\n" + response);
    }

    private void checkChairAvailability(long chatId, int chairId) {
        // Check if a specific chair is available
        String response = sendGetRequest(bookingServiceUrl + "/chairs/" + chairId + "/availability");
        sendMessage(chatId, "Chair " + chairId + " is " + (response.equals("true") ? "available" : "occupied"));
    }

    private void getAllBookedChairs(long chatId) {
        // Fetch all booked chairs from the Chair Booking Service
        String response = sendGetRequest(bookingServiceUrl + "/chairs/booked");
        sendMessage(chatId, "Booked Chairs:\n" + response);
    }

    private void removeChair(long chatId, int chairId) {
        // Remove a chair from the system (Admin use case)
        String response = sendDeleteRequest(bookingServiceUrl + "/chairs/delete/" + chairId);
        sendMessage(chatId, "Chair " + chairId + " has been removed.");
    }


    private void sendMessage(Long chatId, String text) {
        int maxLength = 4000; // Keep under Telegram’s 4096 limit
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());
            SendMessage message = new SendMessage(String.valueOf(chatId), text.substring(start, end));
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            start = end;
        }
    }

    // Utility method for HTTP GET requests
    private String sendGetRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return getString(connection);
            } else {
                return "GET request failed: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }

    // Utility method for HTTP POST requests
    private String sendPostRequest(String urlString, String requestBody) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                return getString(connection);
            } else {
                return "POST request failed: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }

    // Utility method for HTTP DELETE requests
    private String sendDeleteRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return getString(connection);
            } else {
                return "DELETE request failed: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }

    private String getString(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
