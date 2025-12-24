Authors: Tanish-S-K, Tarun-S, Suman Raj.

A server client billing system with dynamic number of threads depending on the number of active ports and real time data synchronization

Readme: 

🛒 Grocery Management System (Java)

A multi-threaded, client–server grocery management system built using core Java, demonstrating strong OOP design, concurrency, and file-based persistence without external frameworks or databases.

📁 Project Structure

OS/
        ├── boot/
        │   └── bootloader.asm
        │
        ├── kernel/
        │   ├── asm/
        │   │   └── entry.asm
        │   ├── kernel.c
        │   ├── file_system.c
        │   ├── cli.c
        │   ├── auth.c
        │   └── mystdlib.c

▶ How to Execute
1. Compile
javac -encoding UTF-8 -d out src\model\*.java src\service\*.java src\user\*.java src\util\*.java src\server\*.java src\client\*.java src\Main.java

2. Start Server (Terminal 1)
java -cp out server.GroceryServer

3. Start Client (Terminal 2+)
java -cp out client.GroceryClient


Multiple clients can be started simultaneously to test concurrency.

⭐ Key Features

Multi-threaded server (one thread per client)

Concurrent admin and consumer sessions

Role-based access control

Inventory management (CRUD)

Basket and checkout system

Reward points & loyalty discounts

Pay using money, points, or both

“Buy My Usual” quick checkout

Sales analytics (most sold products)

Persistent storage using CSV files

Centralized server logging

🛠 Technology Used

Language: Java (JDK 8+)

Concepts: OOP, Multithreading, Socket Programming

Networking: TCP (ServerSocket, Socket)

Data Storage: CSV (file-based persistence)

Libraries: Java Standard Library only
