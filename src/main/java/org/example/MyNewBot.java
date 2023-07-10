package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.models.Emoje;
import org.example.models.Lang;
import org.example.models.Root;
import org.example.service.ConnectionImpl;
import org.example.service.FileServiceImpl;
import org.example.service.JsonDecodeImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyNewBot extends TelegramLongPollingBot {

    public String status;
    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()){
            FileServiceImpl fileService = new FileServiceImpl();


            //fileService.writeFile("org/example/Base/bot.txt", update.toString());
            long chatID = update.getMessage().getChatId();
            if (update.getMessage().getText().equals("/start")) {
                String userLang = fileService.readFile(String.valueOf(chatID) + ".txt");
                System.out.println(userLang);
                if (!userLang.isEmpty()) {
                    MainBotPage(update);
                } else {
                    ChooseLanguage(update);
                }
            }



        }else if (update.hasCallbackQuery()){

            FileServiceImpl fileService = new FileServiceImpl();
            String data = update.getCallbackQuery().getData();
            System.out.println(data);
            int messageID = update.getCallbackQuery().getMessage().getMessageId();
            long chatID = update.getCallbackQuery().getMessage().getChatId();
            if (data.contains("data_lang")){
                String[] strings = data.split("_");
                String keylang = strings[(strings.length - 1)];

                fileService.writeFile(chatID + ".txt", keylang.toString());
                MainBotPage(update);
            }else if (data.equals("allData")){
                String userLang = fileService.readFile(String.valueOf(chatID) + ".txt");
                if (userLang.equals("uz")){
                    editMessageUZ(update,"Kerakli bo'limni tanlang!");
                }else if (userLang.equals("ru")){
                    editMessageRU(update, "Выберите нужный раздел!!");
                }else if (userLang.equals("en")){
                    editMessageEN(update, "Choose the desired section!!");

                }else {
                    ChooseLanguage(update);
                }

            }else if(data.equals("change_lang")) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setMessageId(messageID);
                deleteMessage.setChatId(chatID);
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                ChooseLanguage(update);
            }else if (data.contains("ccydata_")){
                String[] strings = data.split("_");
                String key_rate = strings[(strings.length - 1)];
                System.out.println(key_rate + "Key rate");
                getRate(update, key_rate);
            }else if (data.equals("getback") || data.equals("orqaga")){
                MainBotPage(update);
            }
        }
    }
    public String checkDiff(double d) {
        String emoji = "";
        if (d > 0) {
            emoji = "\uD83D\uDD3A";
        }else if (d == 0){
            emoji = "▫️";
        }else {
            emoji = "\uD83D\uDD3B";
        }
        return emoji;
    }

    public void getRate(Update update, String rate){
        FileServiceImpl fileService = new FileServiceImpl();
        ConnectionImpl connection = new ConnectionImpl();
        String json = connection.openUrl("https://cbu.uz/uz/arkhiv-kursov-valyut/json/");
        JsonDecodeImpl jsonDecode = new JsonDecodeImpl();
        List<Root> json1 = jsonDecode.JsonDe(json);
        int counter = 0;
        for (Root root : json1){

            if (root.Ccy.equals(rate)){
                break;
            }
            counter++;
        }
        System.out.println(json1.get(counter).Ccy);

        long chatID = update.getCallbackQuery().getMessage().getChatId();
        String userLang = fileService.readFile(String.valueOf(chatID) + ".txt");
        if (userLang.equals("uz")){

            String setText1 = getEmoji(rate) + " " + json1.get(counter).CcyNm_UZ + "\n \n " + json1.get(counter).Nominal +
                    " " + rate + " = " + json1.get(counter).Rate + " -> " + json1.get(counter).Diff +" " + checkDiff(Double.parseDouble(json1.get(counter).Diff)) +
                    "\n\n \uD83D\uDCC6 " + json1.get(counter).Date;

            EditMessageText messageText = new EditMessageText();
            messageText.setChatId(chatID);
            messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            messageText.setText(setText1);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("getback");
            button.setText("Orqaga");
            rowInline.add(button);
            rowsInline.add(rowInline);
            markup.setKeyboard(rowsInline);
            messageText.setReplyMarkup(markup);

            try {
                execute(messageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }else if (userLang.equals("ru")) {
            String setText1 = getEmoji(rate) + " " + json1.get(counter).CcyNm_RU + "\n \n " + json1.get(counter).Nominal +
                    " " + rate + " = " + json1.get(counter).Rate + " -> " + json1.get(counter).Diff +" " + checkDiff(Double.parseDouble(json1.get(counter).Diff)) +
                    "\n\n \uD83D\uDCC6 " + json1.get(counter).Date;
            EditMessageText messageText = new EditMessageText();
            messageText.setChatId(chatID);
            messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
           messageText.setText(setText1);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("getback");
            button.setText("Назад");
            rowInline.add(button);
            rowsInline.add(rowInline);
            markup.setKeyboard(rowsInline);
            messageText.setReplyMarkup(markup);

            try {
                execute(messageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }else if (userLang.equals("en")) {
            String setText1 = getEmoji(rate) + " " + json1.get(counter).CcyNm_EN + "\n \n " + json1.get(counter).Nominal +
                    " " + rate + " = " + json1.get(counter).Rate + " -> " + json1.get(counter).Diff +" " + checkDiff(Double.parseDouble(json1.get(counter).Diff)) +
                    "\n\n \uD83D\uDCC6 " + json1.get(counter).Date;
            EditMessageText messageText = new EditMessageText();
            messageText.setChatId(chatID);
            messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            messageText.setText(setText1);InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("getback");
            button.setText("Back");
            rowInline.add(button);
            rowsInline.add(rowInline);
            markup.setKeyboard(rowsInline);
            messageText.setReplyMarkup(markup);


            try {
                execute(messageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }else {
            ChooseLanguage(update);
            System.out.println(2);
        }


    }



    public String getEmoji(String key){
        Gson gson = new Gson();
        FileServiceImpl fileService = new FileServiceImpl();
        Type type = new TypeToken<List<Emoje>>(){}.getType();
        List<Emoje> emojiLists = gson.fromJson(fileService.readFile("data.json"), type);
        String reEmoji = "";
        for (Emoje emoji1: emojiLists) {
            if (key.equals(emoji1.name)){
                reEmoji = emoji1.emoj;
                break;
            }
        }
        return reEmoji;
    }

    public void MainBotPage(Update update){
        FileServiceImpl fileService = new FileServiceImpl();


        if (update.hasMessage() && update.getMessage().hasText()){
            long chatID = update.getMessage().getChatId();
            String userLang = fileService.readFile(String.valueOf(chatID) + ".txt");
            if (userLang.equals("uz")){
                SendMessage message = new SendMessage();
                message.setText("Assalomu Alaykum! \uD83D\uDE0A\n" +
                        "\n" +
                        "\uD83D\uDCAC Ushbu botda 74 - davlat kunlik valyuta kursi kuzatib borish imkoniyati mavjud.");
                message.setChatId(chatID);
                message.setReplyMarkup(MainKeyboard("uz"));
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }else if (userLang.equals("ru")) {
                SendMessage message = new SendMessage();
                message.setText("Привет!  \uD83D\uDE0A\n" +
                        "\n" +
                        " \uD83D\uDCAC Этот бот имеет возможность ежедневно отслеживать обменный курс 74 стран.");
                message.setChatId(chatID);
                message.setReplyMarkup(MainKeyboard("ru"));
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }else if (userLang.equals("en")) {
                SendMessage message = new SendMessage();
                message.setText("Hello!  \uD83D\uDE0A\n" +
                        "\n" +
                        " \uD83D\uDCAC This bot has the ability to track the daily exchange rate of 74 countries.");
                message.setChatId(chatID);
                message.setReplyMarkup(MainKeyboard("en"));
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }else {
                ChooseLanguage(update);
                System.out.println("1");
            }

        }else if (update.hasCallbackQuery()) {
            long chatID = update.getCallbackQuery().getMessage().getChatId();
            String userLang = fileService.readFile(String.valueOf(chatID) + ".txt");
            if (userLang.equals("uz")){
                EditMessageText messageText = new EditMessageText();
                messageText.setChatId(chatID);
                messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                messageText.setText("Assalomu Alaykum! \uD83D\uDE0A\n" +
                        "\n" +
                        "\uD83D\uDCAC Ushbu botda 74 - davlat kunlik valyuta kursi kuzatib borish imkoniyati mavjud.");
                messageText.setReplyMarkup(MainKeyboard("uz"));
                try {
                    execute(messageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }else if (userLang.equals("ru")) {
                EditMessageText messageText = new EditMessageText();
                messageText.setChatId(chatID);
                messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                messageText.setText("Привет!  \uD83D\uDE0A\n" +
                        "\n" +
                        " \uD83D\uDCAC Этот бот имеет возможность ежедневно отслеживать обменный курс 74 стран.");
                messageText.setReplyMarkup(MainKeyboard("ru"));
                try {
                    execute(messageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }else if (userLang.equals("en")) {
                EditMessageText messageText = new EditMessageText();
                messageText.setChatId(chatID);
                messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                messageText.setText("Hello!  \uD83D\uDE0A\n" +
                        "\n" +
                        " \uD83D\uDCAC This bot has the ability to track the daily exchange rate of 74 countries.");
                messageText.setReplyMarkup(MainKeyboard("en"));
                try {
                    execute(messageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }else {
                ChooseLanguage(update);
                System.out.println(2);
            }


        }
    }

    public InlineKeyboardMarkup MainKeyboard(String lang){
        ConnectionImpl connection = new ConnectionImpl();
        String json = connection.openUrl("https://cbu.uz/uz/arkhiv-kursov-valyut/json/");
        JsonDecodeImpl jsonDecode = new JsonDecodeImpl();
        List<Root> json1 = jsonDecode.JsonDe(json);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        if (lang.equals("uz")){
            for (int i = 0; i < 3; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setCallbackData("ccydata_" + json1.get(i).Ccy);
                String emoji = getEmoji(json1.get(i).Ccy);
                button.setText(emoji+" " +json1.get(i).CcyNm_UZ);
                rowInline.add(button);
            }
            rowsInline.add(rowInline);
            List<InlineKeyboardButton> keyboardButton = new ArrayList<>();
            InlineKeyboardButton allkurs = new InlineKeyboardButton();
            allkurs.setCallbackData("allData");
            allkurs.setText("Barcha ma'lumotlarni ko'rish");
            keyboardButton.add(allkurs);
            rowsInline.add(keyboardButton);
            List<InlineKeyboardButton> keyboardButtonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("change_lang");
            button.setText("Tilni o'zgartirish");
            keyboardButtonList.add(button);

            rowsInline.add(keyboardButtonList);
            markup.setKeyboard(rowsInline);

        }else if(lang.equals("ru")){
            for (int i = 0; i < 3; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setCallbackData("ccydata_" + json1.get(i).Ccy);
                String emoji = getEmoji(json1.get(i).Ccy);
                button.setText(emoji+" " +json1.get(i).CcyNm_RU);
                rowInline.add(button);
            }
            rowsInline.add(rowInline);
            List<InlineKeyboardButton> keyboardButton = new ArrayList<>();
            InlineKeyboardButton allkurs = new InlineKeyboardButton();
            allkurs.setCallbackData("allData");
            allkurs.setText("Посмотреть все курсы");
            keyboardButton.add(allkurs);
            rowsInline.add(keyboardButton);
            List<InlineKeyboardButton> keyboardButtonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("change_lang");
            button.setText("Изменить язык");
            keyboardButtonList.add(button);

            rowsInline.add(keyboardButtonList);
            markup.setKeyboard(rowsInline);

        }else if (lang.equals("en")){
            for (int i = 0; i < 3; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setCallbackData("ccydata_" + json1.get(i).Ccy);
                String emoji = getEmoji(json1.get(i).Ccy);
                button.setText(emoji+" " +json1.get(i).CcyNm_EN);
                rowInline.add(button);
            }
            rowsInline.add(rowInline);
            List<InlineKeyboardButton> keyboardButton = new ArrayList<>();
            InlineKeyboardButton allkurs = new InlineKeyboardButton();
            allkurs.setCallbackData("allData");
            allkurs.setText("View all exchange rates");
            keyboardButton.add(allkurs);
            rowsInline.add(keyboardButton);
            List<InlineKeyboardButton> keyboardButtonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("change_lang");
            button.setText("Change language");
            keyboardButtonList.add(button);

            rowsInline.add(keyboardButtonList);
            markup.setKeyboard(rowsInline);

        }

        return markup;
    }

    public void ChooseLanguage(Update update){
        long chatID = 0;
        if (update.hasMessage() && update.getMessage().hasText()){
            chatID = update.getMessage().getChatId();
        }else if (update.hasCallbackQuery()) {
            chatID = update.getCallbackQuery().getMessage().getChatId();
        }
        List<Lang> lang = new ArrayList<>();
        lang.add(new Lang("\uD83C\uDDFA\uD83C\uDDFF O'zbekcha \uD83C\uDDFA\uD83C\uDDFF ", "uz"));
        lang.add(new Lang("\uD83C\uDDF7\uD83C\uDDFA Русский \uD83C\uDDF7\uD83C\uDDFA", "ru"));
        lang.add(new Lang("\uD83C\uDDEC\uD83C\uDDE7 English \uD83C\uDDEC\uD83C\uDDE7", "en"));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText("\uD83C\uDDFA\uD83C\uDDFF Tilni tanlang \n \uD83C\uDDF7\uD83C\uDDFA Выберите язык\n\uD83C\uDDEC\uD83C\uDDE7 Choose language");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowInline = new ArrayList<>();
        for (int i = 0; i < lang.size(); i++) {
            List<InlineKeyboardButton> rowsInline = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("data_lang_" + lang.get(i).getKey());
            button.setText(lang.get(i).getName());
            rowsInline.add(button);
            rowInline.add(rowsInline);
        }
        markup.setKeyboard(rowInline);
        sendMessage.setReplyMarkup(markup);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }


    public void editMessageUZ(Update update, String text){
        ConnectionImpl connection = new ConnectionImpl();
        String json = connection.openUrl("https://cbu.uz/uz/arkhiv-kursov-valyut/json/");
        JsonDecodeImpl jsonDecode = new JsonDecodeImpl();
        List<Root> json1 = jsonDecode.JsonDe(json);

        EditMessageText text1 = new EditMessageText();
        text1.setChatId(update.getCallbackQuery().getMessage().getChatId());
        text1.setText(text);
        text1.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < (json1.size() / 3); i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();

                button.setCallbackData("ccydata_" + json1.get(counter).Ccy);
                button.setText(getEmoji(json1.get(counter).Ccy) + " " + json1.get(counter).CcyNm_UZ);
                rowInline.add(button);
                counter++;
            }
            rowsInline.add(rowInline);
        }
        List<InlineKeyboardButton> rowInlineback = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();

        keyboardButton.setCallbackData("orqaga");
        keyboardButton.setText("Orqaga");
        rowInlineback.add(keyboardButton);
        rowsInline.add(rowInlineback);
        markup.setKeyboard(rowsInline);
        text1.setReplyMarkup(markup);
        try {
            execute(text1);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void editMessageRU(Update update, String text){
        ConnectionImpl connection = new ConnectionImpl();
        String json = connection.openUrl("https://cbu.uz/uz/arkhiv-kursov-valyut/json/");
        JsonDecodeImpl jsonDecode = new JsonDecodeImpl();
        List<Root> json1 = jsonDecode.JsonDe(json);

        EditMessageText text1 = new EditMessageText();
        text1.setChatId(update.getCallbackQuery().getMessage().getChatId());
        text1.setText(text);
        text1.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < (json1.size() / 3); i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();

                button.setCallbackData("ccydata_" + json1.get(counter).Ccy);
                button.setText(getEmoji(json1.get(counter).Ccy) + " " + json1.get(counter).CcyNm_RU);
                rowInline.add(button);
                counter++;
            }
            rowsInline.add(rowInline);
        }
        List<InlineKeyboardButton> rowInlineback = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();

        keyboardButton.setCallbackData("orqaga");
        keyboardButton.setText("Назад");
        rowInlineback.add(keyboardButton);
        rowsInline.add(rowInlineback);

        markup.setKeyboard(rowsInline);
        text1.setReplyMarkup(markup);
        try {
            execute(text1);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void editMessageEN(Update update, String text){
        ConnectionImpl connection = new ConnectionImpl();
        String json = connection.openUrl("https://cbu.uz/uz/arkhiv-kursov-valyut/json/");
        JsonDecodeImpl jsonDecode = new JsonDecodeImpl();
        List<Root> json1 = jsonDecode.JsonDe(json);

        EditMessageText text1 = new EditMessageText();
        text1.setChatId(update.getCallbackQuery().getMessage().getChatId());
        text1.setText(text);
        text1.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < (json1.size() / 3); i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();

                button.setCallbackData("ccydata_" + json1.get(counter).Ccy);
                button.setText(getEmoji(json1.get(counter).Ccy) + " " + json1.get(counter).CcyNm_EN);
                rowInline.add(button);
                counter++;
            }
            rowsInline.add(rowInline);
        }
        List<InlineKeyboardButton> rowInlineback = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();

        keyboardButton.setCallbackData("orqaga");
        keyboardButton.setText("Back");
        rowInlineback.add(keyboardButton);
        rowsInline.add(rowInlineback);

        markup.setKeyboard(rowsInline);
        text1.setReplyMarkup(markup);
        try {
            execute(text1);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return "@Valyut_example_bot";
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public String getBotToken() {
        return "6242849295:AAGmTO9Lrd4xCRU43YhYqPvQ7hgVUMQE4LM";
    }


}
