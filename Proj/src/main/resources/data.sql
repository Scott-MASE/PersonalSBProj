-- Step 1: Insert User into 'users' table
-- Insert a user
INSERT INTO users (username, password, role) 
VALUES ('Scott', 'admin', 'ADMIN');

INSERT INTO users (username, password, role) 
VALUES ('James', '123', 'USER');


-- Insert notes for the user (assuming user_id is generated automatically)
INSERT INTO notes (title, content, priority, deadline, tag, user_id) 
VALUES 
    ('Note 1', 'Content for Note 1', 'HIGH', '2025-03-09', 'tag1', 1),
    ('Note 2', 'Content for Note 2', 'MEDIUM', '2025-03-10', 'tag2', 1),
    ('Note 3', 'Content for Note 3', 'LOW', '2025-03-11', 'tag3', 1),
    ('Note 4', 'Content for Note 4', 'HIGH', '2025-03-12', 'tag4', 1),
    ('Note 5', 'Content for Note 5', 'MEDIUM', '2025-03-13', 'tag5', 1),
    ('Note 6', 'Content for Note 6', 'LOW', '2025-03-14', 'tag6', 1),
    ('Note 7', 'Content for Note 7', 'HIGH', '2025-03-15', 'tag7', 2),
    ('Note 8', 'Content for Note 8', 'MEDIUM', '2025-03-16', 'tag8', 2),
    ('Note 9', 'Content for Note 9', 'LOW', '2025-03-17', 'tag9', 2),
    ('Note 10', 'Content for Note 10', 'HIGH', '2025-03-18', 'tag10', 2),
    ('Note 11', 'Content for Note 11', 'MEDIUM', '2025-03-19', 'tag11', 2),
    ('Note 12', 'Content for Note 12', 'LOW', '2025-03-20', 'tag12', 2);

