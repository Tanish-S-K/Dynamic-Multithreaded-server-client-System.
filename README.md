🛒 Grocery Management System (Java)

📁 Project Structure

grocery_project/
│
├── src/
│   ├── model/        # Core data models (Product, Order, Basket, etc.)
│   ├── service/      # Business logic and services
│   ├── user/         # User hierarchy (User, Admin, Consumer)
│   ├── server/       # Server-side socket and threading logic
│   ├── client/       # Client-side socket communication
│   ├── util/         # Utilities (Logger, CSV handling, helpers)
│   └── Main.java     # Entry point (if applicable)
│
├── data/
│   ├── users.csv         # User accounts and roles
│   ├── products.csv      # Inventory data
│   ├── orders.csv        # Order history
│   ├── stats.csv         # Sales statistics
│   ├── logs.txt          # Centralized server logs
│   └── config.properties # Configuration settings
│
└── README.md


🔧 Technologies & Libraries Used

Language: Java (JDK 8+)

Core Concepts:

Object-Oriented Programming (OOP)

Multithreading

Socket Programming (TCP)

Libraries / APIs (Built-in Java only):

java.net – ServerSocket, Socket

java.io – BufferedReader, PrintWriter, File I/O

java.util – Collections, UUID, Scanner

Data Storage: CSV files (No external database)

Architecture: Client–Server (Command Line)

✨ Key Features

Multi-threaded server (one thread per client)

Concurrent admin and consumer logins

Role-based access (Admin / Consumer)

Inventory management (Add / Update / Remove products)

Basket and checkout system

Reward points system

Pay using money, reward points, or both

Loyalty-based automatic discounts

Order history per customer

“Buy My Usual” quick checkout

Sales statistics (most sold products)

Persistent storage using CSV files

Centralized server logging

🧠 OOP Features Used

Encapsulation – Private fields with getters/setters (Product, User, Order)

Inheritance – Admin and Consumer extend User

Polymorphism – Role-based behavior via overridden methods

Abstraction – Clear separation of model, service, util layers

Composition – Basket → Products, Order → Basket

Singleton Pattern – Logger, DataStore

Multithreading – Each client handled by a separate thread

Thread Safety – Synchronized access to shared resources

▶ How to Compile & Run

1️⃣ Compile the Project
javac -encoding UTF-8 -d out src\model\*.java src\service\*.java src\user\*.java src\util\*.java src\server\*.java src\client\*.java src\Main.java

2️⃣ Start the Server (Terminal 1)
java -cp out server.GroceryServer

3️⃣ Start a Client (Terminal 2)
java -cp out client.GroceryClient

➡ You can open multiple client terminals to test concurrent users.
