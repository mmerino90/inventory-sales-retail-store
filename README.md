# Retail Store Inventory & Sales Management System

A JavaFX-based desktop application for managing inventory, sales, and analytics for a retail store.

## Features

- **User Authentication**: Secure login system with role-based access
- **Product Management**: Add, update, delete, and view products
- **Sales Management**: Process sales transactions and track inventory
- **Analytics Dashboard**: View sales statistics and revenue reports
- **Database**: SQLite database for persistent data storage

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
│  │  ├─ Main.java              # Application entry point
│  │  ├─ db/                    # Database connection
│  │  ├─ model/                 # Data models (User, Product, Sale)
│  │  ├─ dao/                   # Data access objects
│  │  ├─ ui/                    # JavaFX controllers
│  │  └─ util/                  # Utility classes
│  └─ resources/
│     ├─ fxml/                  # JavaFX FXML layouts
│     └─ application.css        # Stylesheet
├─ db/
│  ├─ schema.sql               # Database schema
│  └─ store.db                 # SQLite database (initialized)
└─ pom.xml                     # Maven build configuration
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

**Cashier:**
- Username: `cashier`
- Password: `cashier123`

**Manager:**
- Username: `manager`
- Password: `manager123`

## Usage

1. **Login**: Use the default credentials to log in
2. **Admin Dashboard**: Access product management, sales, and analytics
3. **Manage Products**: Add, update, or delete products from inventory
4. **Process Sales**: Select products and quantities to record sales
5. **View Analytics**: Check sales statistics and revenue reports

## Database Schema

- **users**: Stores user authentication and role information
- **products**: Contains product details and inventory levels
- **sales**: Records all sales transactions

## Future Enhancements

- User management interface
- Advanced reporting and charts
- Barcode scanning support
- Multi-store support
- Export data to CSV/PDF

## License

This project is provided as-is for educational and commercial use.
