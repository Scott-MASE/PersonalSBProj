-- Step 1: Insert User into 'users' table
-- Insert a user

DELETE FROM notes;
DELETE FROM users;

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;


INSERT INTO users (username, password, role) 
VALUES ('user', 'user', 'USER');

INSERT INTO users (username, password, role) 
VALUES ('mod', 'mod', 'MODERATOR');

INSERT INTO users (username, password, role) 
VALUES ('admin', 'admin', 'ADMIN');


-- Insert notes for the user (assuming user_id is generated automatically)
-- User 1
INSERT INTO notes (title, content, priority, deadline, tag, user_id) 
VALUES 
    ('Meeting', 'Discussed the new project deadlines and milestones.', 'HIGH', '2025-03-09', 'tag1', 1),
    ('Feedback', 'Client provided feedback on our design proposals, need to adjust.', 'MEDIUM', '2025-03-10', 'tag2', 1),
    ('Sprint Prog', 'Evaluating sprint completion rate and planning next steps.', 'LOW', '2025-03-11', 'tag3', 1),
    ('Design', 'Meeting to finalize design concepts for the new app.', 'HIGH', '2025-03-12', 'tag4', 1),
    ('Testing', 'Collected user feedback from testing and identified improvements.', 'MEDIUM', '2025-03-13', 'tag5', 1),
    ('Bug Fixes', 'Resolved critical bugs from the last release.', 'LOW', '2025-03-14', 'tag6', 1),
    ('Planning', 'Created a detailed project plan for the next phase.', 'HIGH', '2025-03-15', 'tag7', 1),
    ('Internal', 'Discussed team roles and responsibilities for upcoming tasks.', 'MEDIUM', '2025-03-16', 'tag8', 1),
    ('Ideas', 'Brainstormed potential new features to implement in the next version.', 'LOW', '2025-03-17', 'tag9', 1),
    ('Training', 'Created a training schedule for the team to improve skills.', 'HIGH', '2025-03-18', 'tag1', 1),
    ('Demo', 'Presented the latest product demo to the client for feedback.', 'MEDIUM', '2025-03-19', 'tag1', 1),
    ('Progress', 'Summarized monthly progress for the executive team.', 'LOW', '2025-03-20', 'tag2', 1);

-- User 2
INSERT INTO notes (title, content, priority, deadline, tag, user_id) 
VALUES 
    ('Sales Strategy Meeting', 'Discussed new sales tactics and how to approach high-value clients.', 'HIGH', '2025-03-09', 'tag1', 2),
    ('Quarterly Goals', 'Set goals for the next quarter and assigned responsibilities.', 'MEDIUM', '2025-03-10', 'tag2', 2),
    ('Customer Satisfaction Survey', 'Analyzed customer feedback and suggested improvements.', 'LOW', '2025-03-11', 'tag3', 2),
    ('Team Performance Review', 'Reviewed team performance and identified areas for improvement.', 'HIGH', '2025-03-12', 'tag4', 2),
    ('Client Onboarding Process', 'Refined the client onboarding process based on feedback.', 'MEDIUM', '2025-03-13', 'tag5', 2),
    ('Product Launch Plan', 'Mapped out the timeline and strategy for the upcoming product launch.', 'LOW', '2025-03-14', 'tag6', 2),
    ('Market Research Findings', 'Conducted a market analysis to understand competitors and customer needs.', 'HIGH', '2025-03-15', 'tag7', 2),
    ('Customer Service Workflow', 'Optimized the workflow for handling customer service requests.', 'MEDIUM', '2025-03-16', 'tag8', 2),
    ('Team Building Activities', 'Organized a team-building event to improve collaboration.', 'LOW', '2025-03-17', 'tag9', 2),
    ('Marketing Campaign Ideas', 'Brainstormed ideas for the upcoming marketing campaigns.', 'HIGH', '2025-03-18', 'tag10', 2),
    ('Product Feedback', 'Collected and analyzed feedback from beta testers on new features.', 'MEDIUM', '2025-03-19', 'tag1', 2),
    ('Year-End Review', 'Conducted year-end performance reviews for the sales team.', 'LOW', '2025-03-20', 'tag2', 2);


