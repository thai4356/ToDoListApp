-- V1__init_schema.sql (MySQL 8)
-- KHÔNG có CREATE DATABASE/USE

-- 1) USERS
CREATE TABLE IF NOT EXISTS users (
  id             BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  email          VARCHAR(191) NOT NULL UNIQUE,
  password_hash  VARCHAR(255) NOT NULL,
  full_name      VARCHAR(191) NOT NULL,
  avatar_url     VARCHAR(512),
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at     TIMESTAMP NULL,
  INDEX idx_users_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2) TEAMS
CREATE TABLE IF NOT EXISTS teams (
  id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(191) NOT NULL,
  description   TEXT,
  owner_id      BIGINT UNSIGNED NOT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at    TIMESTAMP NULL,
  CONSTRAINT fk_teams_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_teams_owner (owner_id),
  INDEX idx_teams_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3) TEAM MEMBERS
CREATE TABLE IF NOT EXISTS team_members (
  team_id     BIGINT UNSIGNED NOT NULL,
  user_id     BIGINT UNSIGNED NOT NULL,
  role        ENUM('owner','admin','member') NOT NULL DEFAULT 'member',
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at  TIMESTAMP NULL,
  PRIMARY KEY (team_id, user_id),
  CONSTRAINT fk_tm_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
  CONSTRAINT fk_tm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_tm_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4) TASKS
CREATE TABLE IF NOT EXISTS tasks (
  id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  team_id       BIGINT UNSIGNED NULL,
  creator_id    BIGINT UNSIGNED NOT NULL,
  assignee_id   BIGINT UNSIGNED NULL,
  title         VARCHAR(255) NOT NULL,
  description   TEXT,
  status        ENUM('todo','in_progress','done','blocked') NOT NULL DEFAULT 'todo',
  priority      ENUM('low','medium','high','urgent') NOT NULL DEFAULT 'medium',
  due_date      DATE NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at    TIMESTAMP NULL,
  CONSTRAINT fk_tasks_team     FOREIGN KEY (team_id)     REFERENCES teams(id) ON DELETE SET NULL,
  CONSTRAINT fk_tasks_creator  FOREIGN KEY (creator_id)  REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_tasks_assignee FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_tasks_team (team_id),
  INDEX idx_tasks_assignee (assignee_id),
  INDEX idx_tasks_status (status),
  INDEX idx_tasks_due (due_date),
  INDEX idx_tasks_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5) SUBTASKS
CREATE TABLE IF NOT EXISTS subtasks (
  id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  task_id       BIGINT UNSIGNED NOT NULL,
  title         VARCHAR(255) NOT NULL,
  is_done       TINYINT(1) NOT NULL DEFAULT 0,
  order_index   INT UNSIGNED NOT NULL DEFAULT 0,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at    TIMESTAMP NULL,
  CONSTRAINT fk_subtasks_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  INDEX idx_subtasks_task (task_id),
  INDEX idx_subtasks_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
