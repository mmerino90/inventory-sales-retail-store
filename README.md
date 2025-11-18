# Retail Store Inventory & Sales Management System

A JavaFX-based desktop application for managing inventory, sales, and analytics for a retail store.

## Features

### Core Features
- **User Authentication**: Secure login with SHA-256 password hashing
- **Role-Based Access Control**: 2 roles (Admin, Employee) with different permissions
- **Product Management**: Full CRUD operations with real-time search
- **Sales Management**: Process transactions with date and product filtering
- **Analytics Dashboard**: Interactive charts (Bar Chart, Pie Chart) with sales insights
- **Low Stock Alerts**: Visual warnings for products with quantity < 10
- **Auto Database Initialization**: SQLite database auto-creates from schema on first run
- **Maximized Window**: All screens open in full-screen mode by default

### Advanced Features
- **Real-time Search**: Filter products by name, category, or supplier as you type
- **Sales Filtering**: Filter by date range and product name
- **Password Migration**: Automatic hashing of plain-text passwords on first login
- **Smart Inventory**: Automatic stock deduction on sales
- **Visual Highlights**: Red row highlighting for low stock items
- **Form Auto-populate**: Click table row to auto-fill edit forms

## Technologies Used

- Java 21
- JavaFX 17+
- SQLite JDBC
- Maven

## Project Structure

```
inventory-sales-retail-store/
├─ src/main/
│  ├─ java/com/storeapp/
│  │  ├─ Main.java                           # Application entry point
│  │  ├─ dao/                                # Data Access Objects
│  │  │  ├─ ProductDAO.java                  # Product CRUD operations
│  │  │  ├─ SaleDAO.java                     # Sales CRUD operations
│  │  │  └─ UserDAO.java                     # User authentication & management
│  │  ├─ db/                                 # Database layer
│  │  │  └─ Database.java                    # SQLite connection & auto-initialization
│  │  ├─ model/                              # Data models
│  │  │  ├─ Product.java                     # Product entity
│  │  │  ├─ Sale.java                        # Sale entity
│  │  │  └─ User.java                        # User entity
│  │  ├─ ui/                                 # JavaFX Controllers
│  │  │  ├─ AdminDashboardController.java    # Main dashboard with role-based visibility
│  │  │  ├─ AnalyticsController.java         # Charts & statistics (Admin only)
│  │  │  ├─ LoginController.java             # Authentication screen
│  │  │  ├─ ProductListController.java       # Product management with search
│  │  │  ├─ SalesController.java             # Sales transactions with filtering
│  │  │  └─ UserManagementController.java    # User admin (Admin only)
│  │  └─ util/                               # Utilities
│  │     ├─ PasswordUtil.java                # SHA-256 password hashing
│  │     └─ UserSession.java                 # Session management (Singleton)
│  └─ resources/
│     ├─ fxml/                               # JavaFX FXML layouts
│     │  ├─ admin_dashboard.fxml             # Dashboard with dynamic sections
│     │  ├─ analytics.fxml                   # Analytics with BarChart & PieChart
│     │  ├─ login.fxml                       # Login screen
│     │  ├─ product_list.fxml                # Products with search & low stock alerts
│     │  ├─ sales.fxml                       # Sales with date/product filters
│     │  └─ users_management.fxml            # User management
│     └─ application.css                     # Global styles
├─ db/
│  ├─ schema.sql                             # Database schema with 2-role system
│  └─ store.db                               # SQLite database (auto-created)
└─ pom.xml                                   # Maven dependencies (JavaFX, SQLite JDBC)
```

## Setup Instructions

1. **Prerequisites**
   - Java JDK 21 or higher
   - Maven
   - SQLite3 (optional, for manual database initialization)

2. **Database Setup**
   The database is already initialized with sample data in `db/store.db`
   
   To reinitialize manually:
   ```bash
   sqlite3 db/store.db < db/schema.sql
   ```

3. **Build the Project**
   ```bash
   mvn clean compile
   ```

4. **Run the Application**
   ```bash
   mvn javafx:run
   ```

## Login Credentials

**Admin:**
- Username: `admin`
- Password: `admin123`
- **Permissions**: Full access to all features (Products, Sales, Analytics, Users)

**Employee:**
- Username: `employee`
- Password: `employee123`
- **Permissions**: Limited access (Products, Sales only)

## Usage

### For Admins
1. **Login**: Use admin credentials
2. **Manage Products**: Add/edit/delete products, real-time search, low stock warnings
3. **Process Sales**: Record transactions, automatic stock updates
4. **View Analytics**: Sales charts, revenue statistics, product performance
5. **Manage Users**: Add/edit/delete users with role assignment

### For Employees
1. **Login**: Use employee credentials
2. **Manage Products**: Add/edit/delete products, search inventory, check stock levels
3. **Process Sales**: Record transactions, filter by date/product

### Key Features by Screen
- **Product Management**: Search bar, auto-populate on row click, Clear Form button
- **Sales**: Date range picker, product filter, automatic inventory updates
- **Analytics** (Admin only): Bar chart (units sold), Pie chart (revenue distribution), stat cards

## Database Schema

### Tables
- **users**: User authentication and roles
  - `id` (INTEGER PRIMARY KEY)
  - `username` (TEXT UNIQUE)
  - `password` (TEXT) - SHA-256 hashed
  - `role` (TEXT) - CHECK constraint: 'admin' or 'employee'

- **products**: Product inventory
  - `id` (INTEGER PRIMARY KEY)
  - `name` (TEXT)
  - `category` (TEXT)
  - `price` (REAL)
  - `quantity` (INTEGER)
  - `supplier` (TEXT)

- **sales**: Transaction records
  - `id` (INTEGER PRIMARY KEY)
  - `product_id` (INTEGER) - Foreign key to products
  - `quantity` (INTEGER)
  - `total_price` (REAL)
  - `sale_date` (TEXT)

### Role System
- **Admin**: Full access to all 4 modules (Products, Sales, Analytics, Users)
- **Employee**: Limited access to 2 modules (Products, Sales)

## Technical Details

### Architecture
- **Pattern**: MVC (Model-View-Controller)
- **Database**: SQLite with JDBC
- **UI Framework**: JavaFX with FXML
- **Build Tool**: Maven
- **Java Version**: 21

### Key Components
- **DAO Pattern**: Separation of data access logic
- **Singleton Pattern**: UserSession for session management
- **FilteredList**: Real-time search implementation
- **TableRow Factory**: Custom row styling for visual alerts
- **Password Hashing**: SHA-256 with Base64 encoding
- **Auto-initialization**: Database creates from schema.sql on first run