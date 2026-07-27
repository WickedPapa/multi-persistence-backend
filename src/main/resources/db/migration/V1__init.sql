CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    price NUMERIC(38, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    user_first_name_snapshot VARCHAR(100) NOT NULL,
    user_last_name_snapshot VARCHAR(100) NOT NULL,
    user_email_snapshot VARCHAR(255) NOT NULL,
    total NUMERIC(38, 2) NOT NULL,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    quantity INTEGER NOT NULL,
    price NUMERIC(38, 2) NOT NULL,
    order_id UUID NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);
