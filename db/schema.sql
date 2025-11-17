-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('admin', 'cashier', 'manager'))
);

-- Create Products table
CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    cost_price REAL NOT NULL CHECK(cost_price >= 0),
    selling_price REAL NOT NULL CHECK(selling_price >= 0),
    quantity INTEGER NOT NULL CHECK(quantity >= 0),
    category TEXT NOT NULL,
    supplier TEXT,
    expiry_date DATE
);

-- Create Sales table
CREATE TABLE IF NOT EXISTS sales (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK(quantity > 0),
    total_price REAL NOT NULL CHECK(total_price >= 0),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert default users
-- Admin: username=admin, password=admin123
-- Cashier: username=cashier, password=cashier123
-- Manager: username=manager, password=manager123
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'admin'),
('cashier', 'cashier123', 'cashier'),
('manager', 'manager123', 'manager');

-- Insert sample products
INSERT INTO products (name, description, cost_price, selling_price, quantity, category, supplier, expiry_date) VALUES 
('Laptop', 'High-performance laptop', 750.00, 999.99, 10, 'Electronics', 'TechSupply Co', NULL),
('Mouse', 'Wireless mouse', 15.00, 29.99, 50, 'Electronics', 'TechSupply Co', NULL),
('Keyboard', 'Mechanical keyboard', 45.00, 79.99, 30, 'Electronics', 'TechSupply Co', NULL),
('Monitor', '24-inch LED monitor', 120.00, 199.99, 15, 'Electronics', 'DisplayTech Inc', NULL),
('Desk Chair', 'Ergonomic office chair', 150.00, 249.99, 20, 'Furniture', 'OfficeMax Ltd', NULL);
