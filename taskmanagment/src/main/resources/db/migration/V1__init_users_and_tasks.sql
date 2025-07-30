-- === Users Table ===
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(50) DEFAULT 'ROLE_USER'
);

-- === Tasks Table ===
CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    deadline TIMESTAMP,
    user_id INT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- === Sample Data ===
INSERT INTO users (username, password, email, role) VALUES
('ayman', '{noop}ayman', 'ayman@example.com', 'ROLE_USER'),
('admin', '{noop}admin123', 'admin@example.com', 'ROLE_ADMIN');

INSERT INTO tasks (name, description, deadline, user_id) VALUES
('Task 1', 'Initial task assigned to ayman', CURRENT_TIMESTAMP, 1),
('Task 2', 'Another task for admin', CURRENT_TIMESTAMP, 2);
