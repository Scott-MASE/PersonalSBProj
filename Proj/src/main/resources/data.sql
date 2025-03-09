-- Step 1: Insert User into 'users' table
-- Insert a user
INSERT INTO users (username, password, role) 
VALUES ('Scott', 'admin', 'ADMIN');

INSERT INTO users (username, password, role) 
VALUES ('James', '123', 'USER');


-- Insert notes for the user (assuming user_id is generated automatically)
INSERT INTO notes (title, content, priority, deadline, tag, user_id) 
VALUES ('Note 1', 'Content for Note 1', 'HIGH', '2025-03-09', 'tag1', 1),
       ('Note 2', 'Content for Note 2', 'LOW', '2025-03-09', 'tag2', 1);
