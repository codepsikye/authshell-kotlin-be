-- Initial schema migration - Complete database schema with all features
-- This creates all tables with full audit support and authentication
-- IDEMPOTENT: Can be run multiple times safely

-- Drop all objects first to ensure idempotency
DROP VIEW IF EXISTS user_role_view CASCADE;
DROP TABLE IF EXISTS app_user_role CASCADE;
DROP TABLE IF EXISTS task_update CASCADE;
DROP TABLE IF EXISTS task CASCADE;
DROP TABLE IF EXISTS role CASCADE;
DROP TABLE IF EXISTS access_metadata CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;
DROP TABLE IF EXISTS center CASCADE;
DROP TABLE IF EXISTS org CASCADE;
DROP TABLE IF EXISTS org_type CASCADE;
DROP TABLE IF EXISTS access_right CASCADE;

-- Drop sequences
DROP SEQUENCE IF EXISTS task_update_id_seq;
DROP SEQUENCE IF EXISTS task_id_seq;
DROP SEQUENCE IF EXISTS center_id_seq;
DROP SEQUENCE IF EXISTS org_id_seq;

-- Create access_right table with full audit support
CREATE TABLE access_right (
    name VARCHAR NOT NULL PRIMARY KEY
);

-- Create org_type table with full audit support
CREATE TABLE org_type (
    name VARCHAR NOT NULL PRIMARY KEY,
    access_right JSON NOT NULL DEFAULT '[]'::json,
    org_configs JSON NOT NULL DEFAULT '{}'::json,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'system'
);

-- Create org table with sequence and full audit support
CREATE SEQUENCE org_id_seq;
CREATE TABLE org (
    id INTEGER NOT NULL DEFAULT nextval('org_id_seq') PRIMARY KEY,
    name VARCHAR NOT NULL,
    address TEXT,
    phone VARCHAR,
    city VARCHAR,
    country VARCHAR,
    notes TEXT,
    org_type_name VARCHAR NOT NULL,
    org_configs JSON NOT NULL DEFAULT '{}'::json,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'system',
    FOREIGN KEY (org_type_name) REFERENCES org_type(name)
);

-- Create center table with sequence and full audit support
CREATE SEQUENCE center_id_seq;
CREATE TABLE center (
    id INTEGER NOT NULL DEFAULT nextval('center_id_seq') PRIMARY KEY,
    name VARCHAR NOT NULL,
    address TEXT,
    phone VARCHAR,
    org_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'system',
    FOREIGN KEY (org_id) REFERENCES org(id)
);

-- Create app_user table with password and full audit support
CREATE TABLE app_user (
    id VARCHAR NOT NULL PRIMARY KEY,
    org_id INTEGER NOT NULL,
    username VARCHAR NOT NULL,
    fullname VARCHAR NOT NULL,
    title VARCHAR,
    email VARCHAR NOT NULL,
    password VARCHAR(255),
    org_admin BOOLEAN NOT NULL DEFAULT false,
    user_prefs JSON NOT NULL DEFAULT '{}'::json,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'system',
    FOREIGN KEY (org_id) REFERENCES org(id)
);

-- Add comments to app_user columns
COMMENT ON COLUMN app_user.org_admin IS 'Indicates if the user has organization admin privileges';
COMMENT ON COLUMN app_user.password IS 'BCrypt hashed password for user authentication';

-- Create access_metadata table
CREATE TABLE access_metadata (
    uid VARCHAR NOT NULL,
    org_id INTEGER NOT NULL,
    center_id INTEGER NOT NULL,
    access_time TIMESTAMP NOT NULL,
    access_right JSON NOT NULL DEFAULT '[]'::json,
    PRIMARY KEY (uid, org_id, center_id, access_time),
    FOREIGN KEY (uid) REFERENCES app_user(id),
    FOREIGN KEY (org_id) REFERENCES org(id),
    FOREIGN KEY (center_id) REFERENCES center(id)
);

-- Create role table with full audit support
CREATE TABLE role (
    org_id INTEGER NOT NULL,
    name VARCHAR NOT NULL,
    access_right JSON NOT NULL DEFAULT '[]'::json,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'system',
    PRIMARY KEY (org_id, name),
    FOREIGN KEY (org_id) REFERENCES org(id)
);

-- Create task table with sequence and full audit support (using BIGINT for Long entity mapping)
CREATE SEQUENCE task_id_seq;
CREATE TABLE task (
    id BIGINT NOT NULL DEFAULT nextval('task_id_seq') PRIMARY KEY,
    subject VARCHAR NOT NULL,
    body TEXT,
    status VARCHAR NOT NULL DEFAULT 'pending',
    center_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'system',
    FOREIGN KEY (center_id) REFERENCES center(id)
);

-- Create task_update table with sequence and full audit support (using BIGINT for Long entity mapping)
CREATE SEQUENCE task_update_id_seq;
CREATE TABLE task_update (
    id BIGINT NOT NULL DEFAULT nextval('task_update_id_seq') PRIMARY KEY,
    task_id BIGINT NOT NULL,
    body TEXT,
    status VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'system',
    FOREIGN KEY (task_id) REFERENCES task(id)
);

-- Create app_user_role table with full audit support
CREATE TABLE app_user_role (
    user_id VARCHAR NOT NULL,
    org_id INTEGER NOT NULL,
    center_id INTEGER NOT NULL,
    role_name VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'system',
    PRIMARY KEY (user_id, org_id, center_id, role_name),
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (org_id) REFERENCES org(id),
    FOREIGN KEY (center_id) REFERENCES center(id),
    FOREIGN KEY (org_id, role_name) REFERENCES role(org_id, name)
);

-- Create user_role_view for optimized reading of app_user_role data
CREATE VIEW user_role_view AS
SELECT 
    aur.org_id,
    aur.center_id,
    aur.role_name,
    au.fullname,
    o.name AS org_name,
    c.name AS center_name,
    r.access_right
FROM app_user_role aur
JOIN app_user au ON aur.user_id = au.id
JOIN org o ON aur.org_id = o.id
JOIN center c ON aur.center_id = c.id
JOIN role r ON aur.org_id = r.org_id AND aur.role_name = r.name;

-- Drop existing indexes if they exist
DROP INDEX IF EXISTS IDX_ORG_ORG_TYPE_NAME;
DROP INDEX IF EXISTS IDX_CENTER_ORG_ID;
DROP INDEX IF EXISTS IDX_APP_USER_ORG_ID;
DROP INDEX IF EXISTS IDX_ACCESS_METADATA_UID;
DROP INDEX IF EXISTS IDX_ACCESS_METADATA_ORG_ID;
DROP INDEX IF EXISTS IDX_ACCESS_METADATA_CENTER_ID;
DROP INDEX IF EXISTS IDX_ROLE_ORG_ID;
DROP INDEX IF EXISTS IDX_TASK_CENTER_ID;
DROP INDEX IF EXISTS IDX_TASK_UPDATE_TASK_ID;
DROP INDEX IF EXISTS IDX_APP_USER_ROLE_USER_ID;
DROP INDEX IF EXISTS IDX_APP_USER_ROLE_ORG_ID;
DROP INDEX IF EXISTS IDX_APP_USER_ROLE_CENTER_ID;

-- Create indexes for better performance (matching Supabase structure)
CREATE INDEX IDX_ORG_ORG_TYPE_NAME ON org(org_type_name);
CREATE INDEX IDX_CENTER_ORG_ID ON center(org_id);
CREATE INDEX IDX_APP_USER_ORG_ID ON app_user(org_id);
CREATE INDEX IDX_ACCESS_METADATA_UID ON access_metadata(uid);
CREATE INDEX IDX_ACCESS_METADATA_ORG_ID ON access_metadata(org_id);
CREATE INDEX IDX_ACCESS_METADATA_CENTER_ID ON access_metadata(center_id);
CREATE INDEX IDX_ROLE_ORG_ID ON role(org_id);
CREATE INDEX IDX_TASK_CENTER_ID ON task(center_id);
CREATE INDEX IDX_TASK_UPDATE_TASK_ID ON task_update(task_id);
CREATE INDEX IDX_APP_USER_ROLE_USER_ID ON app_user_role(user_id);
CREATE INDEX IDX_APP_USER_ROLE_ORG_ID ON app_user_role(org_id);
CREATE INDEX IDX_APP_USER_ROLE_CENTER_ID ON app_user_role(center_id); 