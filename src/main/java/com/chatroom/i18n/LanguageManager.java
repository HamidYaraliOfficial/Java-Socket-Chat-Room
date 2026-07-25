package com.chatroom.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Central translation manager. Holds all UI strings for the three
 * supported languages and notifies listeners when the language changes
 * so the whole UI (and its LTR/RTL orientation) can refresh instantly.
 */
public final class LanguageManager {

    public interface LanguageChangeListener {
        void onLanguageChanged(Language newLanguage);
    }

    private static final LanguageManager INSTANCE = new LanguageManager();

    private final Map<Language, Map<String, String>> dictionaries = new HashMap<>();
    private final java.util.List<LanguageChangeListener> listeners = new java.util.ArrayList<>();
    private final Preferences prefs = Preferences.userNodeForPackage(LanguageManager.class);

    private Language currentLanguage;

    private LanguageManager() {
        buildEnglish();
        buildPersian();
        buildChinese();
        String saved = prefs.get("app.language", Language.ENGLISH.getCode());
        currentLanguage = Language.fromCode(saved);
    }

    public static LanguageManager getInstance() {
        return INSTANCE;
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public void setLanguage(Language language) {
        this.currentLanguage = language;
        prefs.put("app.language", language.getCode());
        for (LanguageChangeListener l : listeners) {
            l.onLanguageChanged(language);
        }
    }

    public void addListener(LanguageChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LanguageChangeListener listener) {
        listeners.remove(listener);
    }

    public String t(String key) {
        Map<String, String> dict = dictionaries.get(currentLanguage);
        if (dict != null && dict.containsKey(key)) {
            return dict.get(key);
        }
        return key;
    }

    private void put(Language lang, String key, String value) {
        dictionaries.computeIfAbsent(lang, k -> new HashMap<>()).put(key, value);
    }

    private void buildEnglish() {
        Language l = Language.ENGLISH;
        put(l, "app.title", "Java Socket Chat Room");
        put(l, "launcher.title", "Java Socket Chat Room");
        put(l, "launcher.subtitle", "Choose how you want to start");
        put(l, "launcher.server", "Start Server");
        put(l, "launcher.client", "Start Client");
        put(l, "launcher.serverDesc", "Host a chat room that other clients can connect to");
        put(l, "launcher.clientDesc", "Connect to an existing chat server");

        put(l, "menu.language", "Language");
        put(l, "menu.theme", "Theme");
        put(l, "menu.theme.windows11", "Windows 11");
        put(l, "menu.theme.light", "Light");
        put(l, "menu.theme.dark", "Dark");
        put(l, "menu.theme.red", "Red");
        put(l, "menu.theme.blue", "Blue");
        put(l, "menu.file", "File");
        put(l, "menu.file.saveLog", "Save Chat Log...");
        put(l, "menu.file.exit", "Exit");
        put(l, "menu.help", "Help");
        put(l, "menu.help.about", "About");

        put(l, "server.title", "Chat Server");
        put(l, "server.port", "Port");
        put(l, "server.start", "Start Server");
        put(l, "server.stop", "Stop Server");
        put(l, "server.status", "Status");
        put(l, "server.statusRunning", "Running on port");
        put(l, "server.statusStopped", "Stopped");
        put(l, "server.connectedClients", "Connected Clients");
        put(l, "server.kick", "Kick Selected");
        put(l, "server.broadcast", "Broadcast Message");
        put(l, "server.send", "Send");
        put(l, "server.log", "Server Log");
        put(l, "server.column.username", "Username");
        put(l, "server.column.address", "Address");
        put(l, "server.column.room", "Room");
        put(l, "server.column.connectedAt", "Connected At");

        put(l, "login.title", "Connect to Chat Server");
        put(l, "login.host", "Server Address");
        put(l, "login.port", "Port");
        put(l, "login.username", "Username");
        put(l, "login.room", "Room");
        put(l, "login.connect", "Connect");
        put(l, "login.cancel", "Cancel");
        put(l, "login.error.empty", "Please fill in all fields.");
        put(l, "login.error.connect", "Unable to connect to the server. Please check the address and port.");
        put(l, "login.error.usernameTaken", "This username is already taken in this room.");

        put(l, "client.title", "Chat Room");
        put(l, "client.onlineUsers", "Online Users");
        put(l, "client.send", "Send");
        put(l, "client.messagePlaceholder", "Type a message...");
        put(l, "client.disconnect", "Disconnect");
        put(l, "client.newRoom", "Join / Create Room");
        put(l, "client.currentRoom", "Room");
        put(l, "client.privateMessage", "Private Message");
        put(l, "client.sendPrivate", "Send Private Message");
        put(l, "client.emoji", "Emoji");
        put(l, "client.notifications", "Sound Notifications");
        put(l, "client.status.connected", "Connected");
        put(l, "client.status.disconnected", "Disconnected");
        put(l, "client.status.connecting", "Connecting...");
        put(l, "client.status.reconnecting", "Reconnecting...");

        put(l, "dialog.confirmTitle", "Confirm");
        put(l, "dialog.confirmDisconnect", "Are you sure you want to disconnect?");
        put(l, "dialog.confirmKick", "Are you sure you want to kick this user?");
        put(l, "dialog.roomNamePrompt", "Enter the room name to join or create:");
        put(l, "dialog.aboutTitle", "About Java Socket Chat Room");
        put(l, "dialog.aboutText", "Java Socket Chat Room\nVersion 1.0.0\n\nA professional real-time multi-room chat application built with Java Sockets and Swing.");
        put(l, "dialog.ok", "OK");
        put(l, "dialog.cancel", "Cancel");

        put(l, "chat.joined", "{user} joined the room.");
        put(l, "chat.left", "{user} left the room.");
        put(l, "chat.youJoined", "You joined room \"{room}\".");
        put(l, "chat.privateFrom", "[Private from {user}]");
        put(l, "chat.privateTo", "[Private to {user}]");
        put(l, "chat.serverNotice", "[Server]");
        put(l, "chat.kicked", "You have been disconnected by the server.");
        put(l, "chat.userList", "Online in this room");
    }

    private void buildPersian() {
        Language l = Language.PERSIAN;
        put(l, "app.title", "چت‌روم با جاوا سوکت");
        put(l, "launcher.title", "چت‌روم با جاوا سوکت");
        put(l, "launcher.subtitle", "نحوه شروع را انتخاب کنید");
        put(l, "launcher.server", "راه‌اندازی سرور");
        put(l, "launcher.client", "اتصال به عنوان کلاینت");
        put(l, "launcher.serverDesc", "میزبانی یک چت‌روم که کلاینت‌های دیگر بتوانند به آن متصل شوند");
        put(l, "launcher.clientDesc", "اتصال به یک سرور چت موجود");

        put(l, "menu.language", "زبان");
        put(l, "menu.theme", "پوسته");
        put(l, "menu.theme.windows11", "ویندوز 11");
        put(l, "menu.theme.light", "روشن");
        put(l, "menu.theme.dark", "تاریک");
        put(l, "menu.theme.red", "قرمز");
        put(l, "menu.theme.blue", "آبی");
        put(l, "menu.file", "فایل");
        put(l, "menu.file.saveLog", "ذخیره گزارش گفتگو...");
        put(l, "menu.file.exit", "خروج");
        put(l, "menu.help", "راهنما");
        put(l, "menu.help.about", "درباره برنامه");

        put(l, "server.title", "سرور چت");
        put(l, "server.port", "پورت");
        put(l, "server.start", "شروع سرور");
        put(l, "server.stop", "توقف سرور");
        put(l, "server.status", "وضعیت");
        put(l, "server.statusRunning", "در حال اجرا روی پورت");
        put(l, "server.statusStopped", "متوقف شده");
        put(l, "server.connectedClients", "کلاینت‌های متصل");
        put(l, "server.kick", "اخراج انتخاب‌شده");
        put(l, "server.broadcast", "پیام همگانی");
        put(l, "server.send", "ارسال");
        put(l, "server.log", "گزارش سرور");
        put(l, "server.column.username", "نام کاربری");
        put(l, "server.column.address", "آدرس");
        put(l, "server.column.room", "اتاق");
        put(l, "server.column.connectedAt", "زمان اتصال");

        put(l, "login.title", "اتصال به سرور چت");
        put(l, "login.host", "آدرس سرور");
        put(l, "login.port", "پورت");
        put(l, "login.username", "نام کاربری");
        put(l, "login.room", "اتاق");
        put(l, "login.connect", "اتصال");
        put(l, "login.cancel", "انصراف");
        put(l, "login.error.empty", "لطفا تمام فیلدها را پر کنید.");
        put(l, "login.error.connect", "اتصال به سرور امکان‌پذیر نبود. لطفا آدرس و پورت را بررسی کنید.");
        put(l, "login.error.usernameTaken", "این نام کاربری در این اتاق قبلا استفاده شده است.");

        put(l, "client.title", "اتاق گفتگو");
        put(l, "client.onlineUsers", "کاربران آنلاین");
        put(l, "client.send", "ارسال");
        put(l, "client.messagePlaceholder", "پیامی بنویسید...");
        put(l, "client.disconnect", "قطع اتصال");
        put(l, "client.newRoom", "عضویت / ساخت اتاق");
        put(l, "client.currentRoom", "اتاق");
        put(l, "client.privateMessage", "پیام خصوصی");
        put(l, "client.sendPrivate", "ارسال پیام خصوصی");
        put(l, "client.emoji", "شکلک");
        put(l, "client.notifications", "اعلان صوتی");
        put(l, "client.status.connected", "متصل");
        put(l, "client.status.disconnected", "قطع شده");
        put(l, "client.status.connecting", "در حال اتصال...");
        put(l, "client.status.reconnecting", "در حال اتصال مجدد...");

        put(l, "dialog.confirmTitle", "تایید");
        put(l, "dialog.confirmDisconnect", "آیا مطمئن هستید که می‌خواهید قطع اتصال کنید؟");
        put(l, "dialog.confirmKick", "آیا مطمئن هستید که می‌خواهید این کاربر را اخراج کنید؟");
        put(l, "dialog.roomNamePrompt", "نام اتاقی که می‌خواهید بسازید یا به آن بپیوندید را وارد کنید:");
        put(l, "dialog.aboutTitle", "درباره چت‌روم با جاوا سوکت");
        put(l, "dialog.aboutText", "چت‌روم با جاوا سوکت\nنسخه 1.0.0\n\nیک برنامه حرفه‌ای گفتگوی آنی چند‌اتاقی، ساخته‌شده با جاوا سوکت و سوئینگ.");
        put(l, "dialog.ok", "تایید");
        put(l, "dialog.cancel", "انصراف");

        put(l, "chat.joined", "{user} به اتاق پیوست.");
        put(l, "chat.left", "{user} اتاق را ترک کرد.");
        put(l, "chat.youJoined", "شما به اتاق «{room}» پیوستید.");
        put(l, "chat.privateFrom", "[پیام خصوصی از {user}]");
        put(l, "chat.privateTo", "[پیام خصوصی به {user}]");
        put(l, "chat.serverNotice", "[سرور]");
        put(l, "chat.kicked", "اتصال شما توسط سرور قطع شد.");
        put(l, "chat.userList", "آنلاین در این اتاق");
    }

    private void buildChinese() {
        Language l = Language.CHINESE;
        put(l, "app.title", "Java Socket 聊天室");
        put(l, "launcher.title", "Java Socket 聊天室");
        put(l, "launcher.subtitle", "请选择启动方式");
        put(l, "launcher.server", "启动服务器");
        put(l, "launcher.client", "启动客户端");
        put(l, "launcher.serverDesc", "托管一个聊天室，供其他客户端连接");
        put(l, "launcher.clientDesc", "连接到现有的聊天服务器");

        put(l, "menu.language", "语言");
        put(l, "menu.theme", "主题");
        put(l, "menu.theme.windows11", "Windows 11");
        put(l, "menu.theme.light", "浅色");
        put(l, "menu.theme.dark", "深色");
        put(l, "menu.theme.red", "红色");
        put(l, "menu.theme.blue", "蓝色");
        put(l, "menu.file", "文件");
        put(l, "menu.file.saveLog", "保存聊天记录...");
        put(l, "menu.file.exit", "退出");
        put(l, "menu.help", "帮助");
        put(l, "menu.help.about", "关于");

        put(l, "server.title", "聊天服务器");
        put(l, "server.port", "端口");
        put(l, "server.start", "启动服务器");
        put(l, "server.stop", "停止服务器");
        put(l, "server.status", "状态");
        put(l, "server.statusRunning", "正在运行，端口");
        put(l, "server.statusStopped", "已停止");
        put(l, "server.connectedClients", "已连接的客户端");
        put(l, "server.kick", "踢出所选");
        put(l, "server.broadcast", "群发消息");
        put(l, "server.send", "发送");
        put(l, "server.log", "服务器日志");
        put(l, "server.column.username", "用户名");
        put(l, "server.column.address", "地址");
        put(l, "server.column.room", "房间");
        put(l, "server.column.connectedAt", "连接时间");

        put(l, "login.title", "连接到聊天服务器");
        put(l, "login.host", "服务器地址");
        put(l, "login.port", "端口");
        put(l, "login.username", "用户名");
        put(l, "login.room", "房间");
        put(l, "login.connect", "连接");
        put(l, "login.cancel", "取消");
        put(l, "login.error.empty", "请填写所有字段。");
        put(l, "login.error.connect", "无法连接到服务器，请检查地址和端口。");
        put(l, "login.error.usernameTaken", "该用户名在此房间中已被使用。");

        put(l, "client.title", "聊天室");
        put(l, "client.onlineUsers", "在线用户");
        put(l, "client.send", "发送");
        put(l, "client.messagePlaceholder", "输入消息...");
        put(l, "client.disconnect", "断开连接");
        put(l, "client.newRoom", "加入 / 创建房间");
        put(l, "client.currentRoom", "房间");
        put(l, "client.privateMessage", "私聊消息");
        put(l, "client.sendPrivate", "发送私聊消息");
        put(l, "client.emoji", "表情");
        put(l, "client.notifications", "声音提示");
        put(l, "client.status.connected", "已连接");
        put(l, "client.status.disconnected", "已断开");
        put(l, "client.status.connecting", "正在连接...");
        put(l, "client.status.reconnecting", "正在重新连接...");

        put(l, "dialog.confirmTitle", "确认");
        put(l, "dialog.confirmDisconnect", "您确定要断开连接吗？");
        put(l, "dialog.confirmKick", "您确定要踢出该用户吗？");
        put(l, "dialog.roomNamePrompt", "请输入要加入或创建的房间名称：");
        put(l, "dialog.aboutTitle", "关于 Java Socket 聊天室");
        put(l, "dialog.aboutText", "Java Socket 聊天室\n版本 1.0.0\n\n一款使用 Java Socket 与 Swing 构建的专业实时多房间聊天应用程序。");
        put(l, "dialog.ok", "确定");
        put(l, "dialog.cancel", "取消");

        put(l, "chat.joined", "{user} 加入了房间。");
        put(l, "chat.left", "{user} 离开了房间。");
        put(l, "chat.youJoined", "您已加入房间「{room}」。");
        put(l, "chat.privateFrom", "[来自 {user} 的私聊]");
        put(l, "chat.privateTo", "[发给 {user} 的私聊]");
        put(l, "chat.serverNotice", "[服务器]");
        put(l, "chat.kicked", "您已被服务器断开连接。");
        put(l, "chat.userList", "此房间在线成员");
    }
}
