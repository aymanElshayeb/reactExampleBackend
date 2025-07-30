-- Initialize Task Management Database
-- This script will be executed when the database container starts

USE taskmanagement_db;

-- Create users table if not exists
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(50) DEFAULT 'ROLE_USER',
    PRIMARY KEY (id)
);

-- Create tasks table if not exists
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    deadline DATETIME,
    user_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(user_name);
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_deadline ON tasks(deadline);

-- Insert default admin user (password should be hashed in real application)
INSERT IGNORE INTO users (user_name, password, email, role) 
VALUES ('admin', 'admin123', 'admin@taskmanagement.com', 'ROLE_ADMIN');

-- Insert sample data for testing
INSERT IGNORE INTO users (user_name, password, email, role) 
VALUES 
    ('testuser1', 'password123', 'user1@example.com', 'ROLE_USER'),
    ('testuser2', 'password123', 'user2@example.com', 'ROLE_USER');

-- Commit the changes
COMMIT;
