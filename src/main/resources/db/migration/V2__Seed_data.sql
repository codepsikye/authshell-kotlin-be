-- Seed data migration - populates initial data matching current Supabase state
-- IDEMPOTENT: Can be run multiple times safely

-- Clear existing data first to ensure idempotency (safe for both fresh and existing installs)
-- Clear data in reverse dependency order to avoid foreign key issues
DELETE FROM app_user_role;
DELETE FROM task_update;
DELETE FROM task;
DELETE FROM role;
DELETE FROM access_metadata;
DELETE FROM app_user;
DELETE FROM center;
DELETE FROM org;
DELETE FROM org_type;
DELETE FROM access_right;

-- Insert access rights with full audit fields
INSERT INTO access_right (name, created_at, updated_at, created_by, updated_by) VALUES
('access_right_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('access_right_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('access_right_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('access_right_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_type_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_type_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_type_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_type_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_edit_this', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('center_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('center_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('center_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('center_edit_this', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('center_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('role_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('role_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('role_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('role_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('task_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('task_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('task_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('task_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('task_update_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('task_update_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('task_update_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('task_update_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_select_this', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('org_update_this', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('center_select_this', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('center_update_this', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user_role_create', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user_role_read', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user_role_edit', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user_role_remove', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system');

-- Insert organization types with full audit fields
INSERT INTO org_type (name, access_right, org_configs, created_at, updated_at, created_by, updated_by) VALUES
('org-admin', 
 '["access_right_create","access_right_read","access_right_edit","access_right_remove","org_type_create","org_type_read","org_type_edit","org_type_remove","org_create","org_read","org_edit","org_edit_this","org_remove","center_create","center_read","center_edit","center_edit_this","center_remove","user_create","user_read","user_edit","user_remove","role_create","role_read","role_edit","role_remove","task_create","task_read","task_edit","task_remove","task_update_create","task_update_read","task_update_edit","task_update_remove","org_select_this","org_update_this","center_select_this","center_update_this","user_role_create","user_role_read","user_role_edit","user_role_remove"]',
 '{}',
 '2025-07-16 22:20:55.8147',
 '2025-07-16 22:20:55.8147',
 'system',
 'system'),
('type2',
 '["access_right_create","access_right_edit"]',
 '{}',
 '2025-07-17 14:15:57.853948',
 '2025-07-17 14:15:57.853948',
 'system',
 'system');

-- Insert organizations with full audit fields
INSERT INTO org (id, name, address, phone, city, country, notes, org_type_name, created_at, updated_at, created_by, updated_by) VALUES
(1, 'Admin Org', 'Cpk Address', '123-456-7890', 'Cpk Town', 'USA', 'Primary Admin Organization', 'org-admin', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(2, 'Demo Organization', 'Demo Address', '555-0123', 'Demo City', 'USA', 'Demo organization for testing', 'org-admin', '2025-07-17 12:46:39.309257', '2025-07-17 12:46:39.309257', 'system', 'system'),
(3, 'Sample Org', 'Sample Address', '555-0456', 'Sample City', 'USA', 'Sample organization', 'type2', '2025-07-17 16:26:25.705076', '2025-07-17 16:26:25.705076', 'system', 'system');

-- Update org sequence to match inserted data
ALTER SEQUENCE org_id_seq RESTART WITH 4;

-- Insert sample centers with full audit fields
INSERT INTO center (id, name, address, phone, org_id, created_at, updated_at, created_by, updated_by) VALUES
(1, 'Main Center', '123 Main St', '555-0001', 1, '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(2, 'Branch Center', '456 Branch Ave', '555-0002', 1, '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(3, 'Demo Center', '789 Demo Blvd', '555-0003', 2, '2025-07-17 12:46:39.309257', '2025-07-17 12:46:39.309257', 'system', 'system'),
(4, 'Sample Center', '321 Sample Rd', '555-0004', 3, '2025-07-17 16:26:25.705076', '2025-07-17 16:26:25.705076', 'system', 'system');

-- Update center sequence to match inserted data
ALTER SEQUENCE center_id_seq RESTART WITH 5;

-- Insert sample app users with BCrypt-hashed passwords and full audit fields
-- Password for all users is "admin123" -> BCrypt hash: $2a$10$jdah8bY1lISQJxoYVrOWauNbEC2p96fCe4CWXE76PKIEPkyHWaK0K
INSERT INTO app_user (id, org_id, username, fullname, title, email, password, org_admin, created_at, updated_at, created_by, updated_by) VALUES
('admin-user-1', 1, 'admin', 'System Administrator', 'Admin', 'admin@example.com', '$2a$10$jdah8bY1lISQJxoYVrOWauNbEC2p96fCe4CWXE76PKIEPkyHWaK0K', true, '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user-demo-1', 2, 'demo_user', 'Demo User', 'Manager', 'demo@example.com', '$2a$10$jdah8bY1lISQJxoYVrOWauNbEC2p96fCe4CWXE76PKIEPkyHWaK0K', false, '2025-07-17 12:46:39.309257', '2025-07-17 12:46:39.309257', 'system', 'system'),
('user-sample-1', 3, 'sample_user', 'Sample User', 'Staff', 'sample@example.com', '$2a$10$jdah8bY1lISQJxoYVrOWauNbEC2p96fCe4CWXE76PKIEPkyHWaK0K', false, '2025-07-17 16:26:25.705076', '2025-07-17 16:26:25.705076', 'system', 'system');

-- Insert sample roles with full audit fields
INSERT INTO role (org_id, name, access_right, created_at, updated_at, created_by, updated_by) VALUES
(1, 'admin', '["access_right_create","access_right_read","access_right_edit","access_right_remove","org_type_create","org_type_read","org_type_edit","org_type_remove","org_create","org_read","org_edit","org_edit_this","org_remove","center_create","center_read","center_edit","center_edit_this","center_remove","user_create","user_read","user_edit","user_remove","role_create","role_read","role_edit","role_remove","task_create","task_read","task_edit","task_remove","task_update_create","task_update_read","task_update_edit","task_update_remove"]', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(1, 'manager', '["center_read","user_read","task_create","task_read","task_edit","task_update_create","task_update_read"]', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(2, 'manager', '["center_read","user_read","task_create","task_read","task_edit","task_update_create","task_update_read"]', '2025-07-17 12:46:39.309257', '2025-07-17 12:46:39.309257', 'system', 'system'),
(3, 'staff', '["task_read","task_update_create","task_update_read"]', '2025-07-17 16:26:25.705076', '2025-07-17 16:26:25.705076', 'system', 'system');

-- Insert sample app_user_role assignments with full audit fields
INSERT INTO app_user_role (user_id, org_id, center_id, role_name, created_at, updated_at, created_by, updated_by) VALUES
('admin-user-1', 1, 1, 'admin', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('admin-user-1', 1, 2, 'admin', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
('user-demo-1', 2, 3, 'manager', '2025-07-17 12:46:39.309257', '2025-07-17 12:46:39.309257', 'system', 'system'),
('user-sample-1', 3, 4, 'staff', '2025-07-17 16:26:25.705076', '2025-07-17 16:26:25.705076', 'system', 'system');

-- Insert sample tasks with full audit fields
INSERT INTO task (id, subject, body, status, center_id, created_at, updated_at, created_by, updated_by) VALUES
(1, 'Setup Initial Configuration', 'Configure the system for first use', 'completed', 1, '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(2, 'User Training', 'Conduct user training sessions', 'in_progress', 1, '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(3, 'Demo Preparation', 'Prepare demo environment', 'pending', 3, '2025-07-17 12:46:39.309257', '2025-07-17 12:46:39.309257', 'system', 'system');

-- Update task sequence to match inserted data
ALTER SEQUENCE task_id_seq RESTART WITH 4;

-- Insert sample task updates with full audit fields
INSERT INTO task_update (id, task_id, body, status, created_at, updated_at, created_by, updated_by) VALUES
(1, 1, 'Initial setup completed successfully', 'completed', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(2, 2, 'Training materials prepared', 'in_progress', '2025-07-16 22:20:55.8147', '2025-07-16 22:20:55.8147', 'system', 'system'),
(3, 2, 'First training session conducted', 'in_progress', '2025-07-17 10:30:00.000000', '2025-07-17 10:30:00.000000', 'system', 'system');

-- Update task_update sequence to match inserted data
ALTER SEQUENCE task_update_id_seq RESTART WITH 4;