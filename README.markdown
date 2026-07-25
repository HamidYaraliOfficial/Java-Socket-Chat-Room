# Java Socket Chat Room

A professional, real-time, multi-room chat application built with **Java Sockets** and **Java Swing**. It supports a Windows 11 style theme, Light, Dark, Red and Blue themes, and three interface languages (English, Persian, Chinese) with correct LTR/RTL text direction handling.

---

## 🇬🇧 English

### Overview
Java Socket Chat Room is a two-role desktop application — a **Server** that hosts the chat rooms and a **Client** that connects to it — communicating over raw TCP sockets with a custom object-based protocol. A single launcher screen lets any user start either role from the same program. Written entirely in Java (Swing + `java.net` sockets), it runs on Windows, Linux and macOS.

### Features
- Multi-threaded **chat server**: one thread per connected client, unlimited simultaneous connections
- **Multiple chat rooms**: users can join or create any room by name and switch between rooms at any time
- **Real-time group messaging** with timestamps
- **Private (direct) messaging** between two users — just double-click a name in the online-users list
- Live **online users list** per room, updated instantly on join/leave
- Join/leave system notifications shown in the transcript
- Server administration window: configurable port, start/stop control, live table of connected clients (username, address, room, connection time), **kick** a selected client, **broadcast** a server-wide announcement, and a scrollable server log
- Username-collision protection: the server rejects a join if the name is already taken in that room
- Export the chat transcript or the server log to a plain text file
- Optional sound notification on new messages
- 5 visual themes: Windows 11, Light, Dark, Red, Blue — switchable at runtime
- 3 languages: English, Persian (فارسی), Chinese (中文) — switchable at runtime, with automatic Right-to-Left layout for Persian and Left-to-Right layout for English/Chinese
- All preferences (theme/language) are remembered between sessions

### Requirements
- **Java Development Kit (JDK) 17 or newer**
- Operating system: Windows 10/11, Linux, or macOS
- Network connectivity between the server machine and every client (the chosen port must be reachable/allowed through any firewall)

### Required Libraries / Dependencies
This project uses **only the standard Java SE library** (Swing, `java.net` sockets, `java.io` object serialization, `java.util.concurrent`, `java.util.prefs`, etc.) — there are **no external third-party libraries to install**. You only need a working JDK.

Install a JDK if you don't already have one:

```bash
# Ubuntu / Debian
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# Fedora
sudo dnf install -y java-17-openjdk-devel

# macOS (Homebrew)
brew install openjdk@17

# Windows (winget)
winget install EclipseAdoptium.Temurin.17.JDK
```

### Installation & Build

**Option A — Build with Maven**
```bash
mvn clean package
java -jar target/java-socket-chat-room.jar
```

**Option B — Compile manually with javac**
```bash
# From the project root folder
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
java -cp out com.chatroom.Main
```

### Usage
1. Launch the application and choose **Start Server** on the machine that will host the chat, or **Start Client** to join an existing one.
2. On the server, set the desired port and click **Start Server**. Share the server machine's IP address and the port with your users.
3. On each client, enter the server address, port, a username and a room name, then click **Connect**.
4. Type messages in the composer and press Enter or click **Send**. Double-click a name in the user list to send a private message. Use the room menu to switch to (or create) another room at any time.

### Project Structure
```
JavaSocketChatRoom/
├── pom.xml
└── src/main/java/com/chatroom/
    ├── Main.java
    ├── i18n/          (Language, LanguageManager)
    ├── gui/           (LauncherWindow, Theme, ThemeManager)
    ├── gui/server/    (ServerWindow)
    ├── gui/client/    (LoginDialog, ClientWindow)
    ├── gui/client/components/ (ChatAreaPanel, UserListPanel)
    ├── net/           (ChatServer, ClientHandler, ChatClient, ChatMessage,
    │                   MessageType, ConnectedClientInfo)
    └── util/          (TextFileExporter)
```

### Disclaimer
This application transmits chat data in plain text over the network. Do not use it over an untrusted network for sensitive information.

---

## 🇮🇷 فارسی

### معرفی
چت‌روم با جاوا سوکت یک برنامه دسکتاپ دو‌نقشی است — یک **سرور** که اتاق‌های گفتگو را میزبانی می‌کند و یک **کلاینت** که به آن متصل می‌شود — که از طریق سوکت‌های خام TCP و یک پروتکل سفارشی مبتنی بر اشیاء با یکدیگر ارتباط برقرار می‌کنند. یک صفحه شروع واحد به هر کاربر اجازه می‌دهد از همان برنامه هر یک از این دو نقش را انتخاب کند. این برنامه به‌طور کامل با جاوا (Swing و سوکت‌های `java.net`) نوشته شده و روی ویندوز، لینوکس و مک اجرا می‌شود.

### ویژگی‌ها
- **سرور چت چندنخی**: یک نخ برای هر کلاینت متصل، بدون محدودیت در تعداد اتصال‌های همزمان
- **چند اتاق گفتگو**: کاربران می‌توانند با وارد کردن نام، به هر اتاقی بپیوندند یا آن را بسازند و در هر لحظه بین اتاق‌ها جابجا شوند
- **پیام‌رسانی گروهی آنی** همراه با زمان دقیق ارسال
- **پیام خصوصی (مستقیم)** بین دو کاربر — فقط کافیست روی نام کاربر در لیست کاربران آنلاین دوبار کلیک کنید
- **لیست کاربران آنلاین زنده** برای هر اتاق، که با پیوستن یا ترک هر کاربر فورا بروزرسانی می‌شود
- نمایش اعلان‌های سیستمی پیوستن/ترک اتاق در متن گفتگو
- پنجره مدیریت سرور: تنظیم پورت، کنترل شروع/توقف، جدول زنده کلاینت‌های متصل (نام کاربری، آدرس، اتاق، زمان اتصال)، **اخراج** یک کلاینت انتخاب‌شده، **پیام همگانی** به همه کاربران، و گزارش قابل پیمایش سرور
- جلوگیری از تکرار نام کاربری: سرور در صورت تکراری بودن نام در همان اتاق، درخواست پیوستن را رد می‌کند
- خروجی گرفتن از متن گفتگو یا گزارش سرور به فایل متنی
- اعلان صوتی اختیاری برای پیام‌های جدید
- ۵ پوسته بصری: ویندوز ۱۱، روشن، تاریک، قرمز، آبی — قابل تغییر در حین اجرا
- ۳ زبان: انگلیسی، فارسی، چینی — قابل تغییر در حین اجرا، با چیدمان خودکار راست‌به‌چپ برای فارسی و چپ‌به‌راست برای انگلیسی/چینی
- تمام تنظیمات (پوسته/زبان) بین اجراهای مختلف برنامه به خاطر سپرده می‌شود

### پیش‌نیازها
- **جاوا (JDK) نسخه ۱۷ یا جدیدتر**
- سیستم‌عامل: ویندوز ۱۰/۱۱، لینوکس یا مک
- اتصال شبکه بین دستگاه سرور و تمام کلاینت‌ها (پورت انتخاب‌شده باید در فایروال باز و در دسترس باشد)

### کتابخانه‌ها و پیش‌نیازهای لازم
این پروژه فقط از **کتابخانه استاندارد جاوا** (Swing، سوکت‌های `java.net`، سریال‌سازی اشیاء `java.io`، `java.util.concurrent`، `java.util.prefs` و غیره) استفاده می‌کند و **هیچ کتابخانه شخص‌ثالثی نیاز به نصب ندارد**. تنها کافیست یک JDK فعال روی سیستم داشته باشید.

اگر JDK ندارید، آن را نصب کنید:

```bash
# اوبونتو / دبیان
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# فدورا
sudo dnf install -y java-17-openjdk-devel

# مک (Homebrew)
brew install openjdk@17

# ویندوز (winget)
winget install EclipseAdoptium.Temurin.17.JDK
```

### نصب و بیلد پروژه

**روش الف — بیلد با Maven**
```bash
mvn clean package
java -jar target/java-socket-chat-room.jar
```

**روش ب — کامپایل دستی با javac**
```bash
# از داخل پوشه اصلی پروژه
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
java -cp out com.chatroom.Main
```

### نحوه استفاده
۱. برنامه را اجرا کنید و روی دستگاهی که قرار است گفتگو را میزبانی کند، **راه‌اندازی سرور** را انتخاب کنید، یا برای پیوستن به یک سرور موجود، **اتصال به عنوان کلاینت** را انتخاب کنید.
۲. در سرور، پورت مورد نظر را تنظیم کرده و روی **شروع سرور** کلیک کنید. آدرس IP دستگاه سرور و پورت را با کاربران خود به اشتراک بگذارید.
۳. در هر کلاینت، آدرس سرور، پورت، یک نام کاربری و نام اتاق را وارد کرده و روی **اتصال** کلیک کنید.
۴. پیام خود را در کادر نوشتن وارد کنید و Enter یا **ارسال** را بزنید. برای ارسال پیام خصوصی، روی نام کاربر در لیست کاربران دوبار کلیک کنید. از منوی اتاق برای پیوستن به اتاق دیگر یا ساخت اتاق جدید در هر لحظه استفاده کنید.

### ساختار پروژه
```
JavaSocketChatRoom/
├── pom.xml
└── src/main/java/com/chatroom/
    ├── Main.java
    ├── i18n/          (Language, LanguageManager)
    ├── gui/           (LauncherWindow, Theme, ThemeManager)
    ├── gui/server/    (ServerWindow)
    ├── gui/client/    (LoginDialog, ClientWindow)
    ├── gui/client/components/ (ChatAreaPanel, UserListPanel)
    ├── net/           (ChatServer, ClientHandler, ChatClient, ChatMessage,
    │                   MessageType, ConnectedClientInfo)
    └── util/          (TextFileExporter)
```

### سلب مسئولیت
این برنامه داده‌های گفتگو را به‌صورت متن ساده (بدون رمزنگاری) روی شبکه منتقل می‌کند. از آن روی شبکه‌های غیرقابل‌اعتماد برای اطلاعات حساس استفاده نکنید.

---

## 🇨🇳 中文

### 概述
Java Socket 聊天室是一款双角色桌面应用程序——一个托管聊天室的**服务器**和一个连接到它的**客户端**——两者通过原始 TCP 套接字，使用自定义的基于对象的协议进行通信。同一个启动界面允许任何用户从同一个程序中选择这两种角色之一。该应用完全使用 Java（Swing 与 `java.net` 套接字）编写，可在 Windows、Linux 和 macOS 上运行。

### 功能特性
- **多线程聊天服务器**：每个已连接的客户端对应一个独立线程，支持无限数量的并发连接
- **多个聊天房间**：用户可以通过输入名称加入或创建任意房间，并可随时在房间之间切换
- 带时间戳的**实时群组消息**
- 两个用户之间的**私聊（点对点）消息** — 只需在在线用户列表中双击对方名字即可
- 每个房间的**实时在线用户列表**，用户加入/离开时立即更新
- 在聊天记录中显示加入/离开的系统通知
- 服务器管理窗口：可配置端口、启动/停止控制、已连接客户端的实时表格（用户名、地址、房间、连接时间）、**踢出**选中的客户端、向所有人**群发**公告，以及可滚动查看的服务器日志
- 用户名冲突保护：如果该名称在该房间中已被使用，服务器会拒绝加入请求
- 将聊天记录或服务器日志导出为纯文本文件
- 新消息可选的声音提示
- 5 种视觉主题：Windows 11、浅色、深色、红色、蓝色 — 可在运行时切换
- 3 种语言：英语、波斯语、中文 — 可在运行时切换，波斯语自动使用从右到左布局，英语/中文自动使用从左到右布局
- 所有偏好设置（主题/语言）会在会话之间自动保存

### 系统要求
- **Java 开发工具包（JDK）17 或更高版本**
- 操作系统：Windows 10/11、Linux 或 macOS
- 服务器主机与每个客户端之间的网络连通性（所选端口必须在防火墙中开放并可访问）

### 所需库/依赖项
该项目**仅使用标准 Java SE 库**（Swing、`java.net` 套接字、`java.io` 对象序列化、`java.util.concurrent`、`java.util.prefs` 等）——**无需安装任何第三方库**。您只需要一个可用的 JDK。

如果尚未安装 JDK，请安装：

```bash
# Ubuntu / Debian
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# Fedora
sudo dnf install -y java-17-openjdk-devel

# macOS (Homebrew)
brew install openjdk@17

# Windows (winget)
winget install EclipseAdoptium.Temurin.17.JDK
```

### 安装与构建

**方式一 — 使用 Maven 构建**
```bash
mvn clean package
java -jar target/java-socket-chat-room.jar
```

**方式二 — 使用 javac 手动编译**
```bash
# 在项目根目录下执行
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
java -cp out com.chatroom.Main
```

### 使用方法
1. 启动应用程序，在将要托管聊天的机器上选择**启动服务器**，或选择**启动客户端**以加入现有的服务器。
2. 在服务器端，设置所需的端口并点击**启动服务器**。将服务器主机的 IP 地址和端口分享给您的用户。
3. 在每个客户端上，输入服务器地址、端口、用户名和房间名称，然后点击**连接**。
4. 在输入框中键入消息，按 Enter 或点击**发送**。双击用户列表中的名字即可发送私聊消息。随时可通过房间菜单切换到（或创建）另一个房间。

### 项目结构
```
JavaSocketChatRoom/
├── pom.xml
└── src/main/java/com/chatroom/
    ├── Main.java
    ├── i18n/          (Language, LanguageManager)
    ├── gui/           (LauncherWindow, Theme, ThemeManager)
    ├── gui/server/    (ServerWindow)
    ├── gui/client/    (LoginDialog, ClientWindow)
    ├── gui/client/components/ (ChatAreaPanel, UserListPanel)
    ├── net/           (ChatServer, ClientHandler, ChatClient, ChatMessage,
    │                   MessageType, ConnectedClientInfo)
    └── util/          (TextFileExporter)
```

### 免责声明
本应用程序以明文形式在网络上传输聊天数据。请勿在不受信任的网络上使用它传输敏感信息。
