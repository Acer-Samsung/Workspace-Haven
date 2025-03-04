package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new ChairBookingBot());
            System.out.println("Chair Booking Bot is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
