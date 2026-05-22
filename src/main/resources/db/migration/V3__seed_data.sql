-- ============================================================
--  V3 — SEED DATA
--  Departments + Admin user (login: admin@tms.com / admin123)
-- ============================================================

-- ── Departments ───────────────────────────────────────────────
INSERT INTO departments (name, code) VALUES
    ('Engineering',     'ENG'),
    ('Finance',         'FIN'),
    ('Operations',      'OPS'),
    ('Human Resources', 'HR');

-- ── Admin user ────────────────────────────────────────────────
-- Password stored as plain text here for demo.
-- In production, store a BCrypt hash.
INSERT INTO users (full_name, email, password_hash, role, department_id, is_active)
VALUES ('System Admin', 'admin@tms.com', 'admin123', 'admin', NULL, 1);

-- ── Travel Policies ───────────────────────────────────────────
-- Created by the admin user (id=1)
INSERT INTO travel_policies (name, trip_type, role, max_budget, allowed_modes, is_active, created_by) VALUES
    ('Employee Domestic',       'domestic',      'employee', 10000.00,  'train,road',     1, 1),
    ('Employee International',  'international', 'employee', 50000.00,  'train,road',     1, 1),
    ('Manager Domestic',        'domestic',      'manager',  25000.00,  'train,road,air', 1, 1),
    ('Manager International',   'international', 'manager',  100000.00, 'train,road,air', 1, 1);
