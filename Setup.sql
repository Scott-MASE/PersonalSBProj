use Hive;


INSERT INTO notes (title, content, priority, deadline, tag, user_id, access) 
VALUES 
    ('Meeting', 'Discussed the new project deadlines and milestones.', 'HIGH', '2025-03-09', 'Work', 2, "PRIVATE"),
    ('Feedback', 'Client provided feedback on our design proposals, need to adjust.', 'MEDIUM', '2025-03-10', 'work', 2, "PRIVATE"),
    ('Sprint Prog', 'Evaluating sprint completion rate and planning next steps.', 'LOW', '2025-03-11', 'work', 2, "PRIVATE"),
    ('Design', 'Meeting to finalize design concepts for the new app.', 'HIGH', '2025-03-12', 'work', 2, "PRIVATE"),
    ('Testing', 'Collected user feedback from testing and identified improvements.', 'MEDIUM', '2025-03-13', 'personal', 2, "PRIVATE"),
    ('Bug Fixes', 'Resolved critical bugs from the last release.', 'LOW', '2025-03-14', 'personal', 2, "PRIVATE"),
    ('Planning', 'Created a detailed project plan for the next phase.', 'HIGH', '2025-03-15', 'personal', 2, "PRIVATE"),
    ('Internal', 'Discussed team roles and responsibilities for upcoming tasks.', 'MEDIUM', '2025-03-16', 'personal', 2, "PRIVATE"),
    ('Ideas', 'Brainstormed potential new features to implement in the next version.', 'LOW', '2025-03-17', 'School', 2, "PRIVATE"),
    ('Training', 'Created a training schedule for the team to improve skills.', 'HIGH', '2025-03-18', 'School', 2, "PRIVATE"),
    ('Demo', 'Presented the latest product demo to the client for feedback.', 'MEDIUM', '2025-03-19', 'School', 2, "PRIVATE"),
    ('Progress', 'Summarized monthly progress for the executive team.', 'LOW', '2025-03-20', 'School', 2, "PRIVATE");

-- User 2
INSERT INTO notes (title, content, priority, deadline, tag, user_id, access) 
VALUES 
    ('Strategy', 'tactics and how to approach high-value clients.', 'HIGH', '2025-03-09', 'war', 3, "PRIVATE"),
    ('Goals', 'Set goals for the next quarter and assigned responsibilities.', 'MEDIUM', '2025-03-10', 'war', 3, "PRIVATE"),
    ('Survey', 'Analyzed customer feedback and suggested improvements.', 'LOW', '2025-03-11', 'land', 3, "PRIVATE"),
    ('Performance', 'Reviewed team performance and identified areas for improvement.', 'HIGH', '2025-03-12', 'war', 3, "PRIVATE"),
    ('Onboarding', 'Refined the client onboarding process based on feedback.', 'MEDIUM', '2025-03-13', 'war', 3, "PRIVATE"),
    ('Launch', 'Mapped out the timeline and strategy for the upcoming product launch.', 'LOW', '2025-03-14', 'war', 3, "PRIVATE"),
    ('Research', 'Conducted a market analysis to understand competitors and customer needs.', 'HIGH', '2025-03-15', 'lab', 3, "PRIVATE"),
    ('Workflow', 'Optimized the workflow for handling customer service requests.', 'MEDIUM', '2025-03-16', 'lab', 3, "PRIVATE"),
    ('Activities', 'Organized a team-building event to improve collaboration.', 'LOW', '2025-03-17', 'lab', 3, "PRIVATE"),
    ('Campaign', 'Brainstormed ideas for the upcoming marketing campaigns.', 'HIGH', '2025-03-18', 'land', 3, "PRIVATE"),
    ('Feedback', 'Collected and analyzed feedback from beta testers on new features.', 'MEDIUM', '2025-03-19', 'land', 3, "PRIVATE"),
    ('Year-End', 'Conducted year-end performance reviews for the sales team.', 'LOW', '2025-03-20', 'land', 3, "PRIVATE");



select * from notes;
select * from users;

