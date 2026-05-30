SET FOREIGN_KEY_CHECKS = 0;

-- Drop partial tables in case of previous failed run (reverse FK order)
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS expense_items;
DROP TABLE IF EXISTS expense_claims;
DROP TABLE IF EXISTS travel_tickets;
DROP TABLE IF EXISTS itinerary_segments;
DROP TABLE IF EXISTS approval_steps;
DROP TABLE IF EXISTS travel_request_seq;
DROP TABLE IF EXISTS travel_requests;
DROP TABLE IF EXISTS travel_policies;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS departments;

-- Also drop tables from the very first legacy schema
DROP TABLE IF EXISTS SystemAudit;
DROP TABLE IF EXISTS ExpenseEntry;
DROP TABLE IF EXISTS TravelSegment;
DROP TABLE IF EXISTS StayDetail;
DROP TABLE IF EXISTS ApprovalLog;
DROP TABLE IF EXISTS TravelRequest;
DROP TABLE IF EXISTS BudgetPolicy;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Role;
DROP TABLE IF EXISTS Department;

-- ── departments ───────────────────────────────────────────────
CREATE TABLE departments (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(150)    NOT NULL,
    code        VARCHAR(20)     NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_dept_name UNIQUE (name),
    CONSTRAINT uq_dept_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── users ─────────────────────────────────────────────────────
CREATE TABLE users (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    full_name       VARCHAR(200)    NOT NULL,
    email           VARCHAR(200)    NOT NULL,
    user_code       VARCHAR(20)     NULL,
    phone_number    VARCHAR(20)     NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    role            ENUM('admin','employee','manager','finance') NOT NULL DEFAULT 'employee',
    department_id   BIGINT          NULL,
    manager_id      BIGINT          NULL,
    is_active       TINYINT(1)      NOT NULL DEFAULT 1,
    created_by      BIGINT          NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_user_code UNIQUE (user_code),
    CONSTRAINT fk_users_department  FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    CONSTRAINT fk_users_manager     FOREIGN KEY (manager_id)    REFERENCES users(id)       ON DELETE SET NULL,
    CONSTRAINT fk_users_created_by  FOREIGN KEY (created_by)    REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_manager_id    ON users(manager_id);
CREATE INDEX idx_users_department_id ON users(department_id);
CREATE INDEX idx_users_role          ON users(role);

-- ── travel_policies ───────────────────────────────────────────
CREATE TABLE travel_policies (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(200)    NOT NULL,
    trip_type       ENUM('domestic','international') NOT NULL,
    role            ENUM('admin','employee','manager','finance') NOT NULL,
    max_budget      DECIMAL(12,2)   NOT NULL,
    allowed_modes   VARCHAR(100)    NOT NULL,
    is_active       TINYINT(1)      NOT NULL DEFAULT 1,
    version         INT             NOT NULL DEFAULT 1,
    created_by      BIGINT          NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_max_budget CHECK (max_budget > 0),
    CONSTRAINT fk_policy_creator FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── travel_requests ───────────────────────────────────────────
CREATE TABLE travel_requests (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    reference_no            VARCHAR(30)     NOT NULL,
    requester_id            BIGINT          NOT NULL,
    trip_type               ENUM('domestic','international') NOT NULL,
    destination             VARCHAR(300)    NOT NULL,
    starting_place          VARCHAR(300)    NULL,
    purpose                 TEXT            NOT NULL,
    travel_from             DATE            NOT NULL,
    travel_to               DATE            NOT NULL,
    transport_mode          ENUM('train','road','air') NOT NULL,
    travel_class            ENUM('first_class','second_class','sleeper','general') NULL,
    estimated_budget        DECIMAL(12,2)   NOT NULL,
    approved_budget         DECIMAL(12,2)   NULL,
    status                  ENUM('draft','manager_pending','manager_approved','finance_pending','finance_approved','manager_disapproved','finance_disapproved','cancelled','completed') NOT NULL DEFAULT 'draft',
    assigned_manager_id     BIGINT          NULL,
    has_policy_violation    TINYINT(1)      NOT NULL DEFAULT 0,
    policy_violation_notes  TEXT            NULL,
    override_note           TEXT            NULL,
    submitted_at            DATETIME        NULL,
    approved_at             DATETIME        NULL,
    rejected_at             DATETIME        NULL,
    completed_at            DATETIME        NULL,
    created_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_tr_reference UNIQUE (reference_no),
    CONSTRAINT chk_date_order  CHECK (travel_to >= travel_from),
    CONSTRAINT chk_budget_pos  CHECK (estimated_budget > 0),
    CONSTRAINT fk_tr_requester FOREIGN KEY (requester_id)        REFERENCES users(id),
    CONSTRAINT fk_tr_manager   FOREIGN KEY (assigned_manager_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tr_requester    ON travel_requests(requester_id);
CREATE INDEX idx_tr_status       ON travel_requests(status);
CREATE INDEX idx_tr_manager      ON travel_requests(assigned_manager_id);
CREATE INDEX idx_tr_travel_dates ON travel_requests(travel_from, travel_to);

-- Auto-increment sequence for reference numbers
CREATE TABLE travel_request_seq (
    next_val BIGINT NOT NULL DEFAULT 1
) ENGINE=InnoDB;
INSERT INTO travel_request_seq VALUES (1);

-- ── approval_steps ────────────────────────────────────────────
CREATE TABLE approval_steps (
    id                  BIGINT      NOT NULL AUTO_INCREMENT,
    travel_request_id   BIGINT      NOT NULL,
    level               SMALLINT    NOT NULL,
    approver_id         BIGINT      NOT NULL,
    action              ENUM('approved','rejected','budget_modified','override_approved') NULL,
    comment             TEXT        NULL,
    budget_override     DECIMAL(12,2) NULL,
    acted_at            DATETIME    NULL,
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_approval_level    UNIQUE (travel_request_id, level),
    CONSTRAINT fk_approval_request  FOREIGN KEY (travel_request_id) REFERENCES travel_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_approval_approver FOREIGN KEY (approver_id)       REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_approval_request  ON approval_steps(travel_request_id);
CREATE INDEX idx_approval_approver ON approval_steps(approver_id);

-- ── itinerary_segments ────────────────────────────────────────
CREATE TABLE itinerary_segments (
    id                  BIGINT      NOT NULL AUTO_INCREMENT,
    travel_request_id   BIGINT      NOT NULL,
    segment_order       SMALLINT    NOT NULL,
    segment_type        ENUM('travel','stay') NOT NULL,
    transport_mode      ENUM('train','road','air') NULL,
    from_location       VARCHAR(300) NULL,
    to_location         VARCHAR(300) NULL,
    departure_at        DATETIME    NULL,
    arrival_at          DATETIME    NULL,
    carrier             VARCHAR(200) NULL,
    property_name       VARCHAR(300) NULL,
    property_address    TEXT        NULL,
    check_in            DATE        NULL,
    check_out           DATE        NULL,
    notes               TEXT        NULL,
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_itin_request FOREIGN KEY (travel_request_id) REFERENCES travel_requests(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_itin_request ON itinerary_segments(travel_request_id, segment_order);

-- ── travel_tickets ────────────────────────────────────────────
CREATE TABLE travel_tickets (
    id                  BIGINT      NOT NULL AUTO_INCREMENT,
    travel_request_id   BIGINT      NOT NULL,
    uploaded_by         BIGINT      NOT NULL,
    transport_mode      ENUM('train','road','air') NOT NULL,
    journey_date        DATE        NOT NULL,
    from_location       VARCHAR(300) NOT NULL,
    to_location         VARCHAR(300) NOT NULL,
    ticket_reference    VARCHAR(100) NULL,
    amount              DECIMAL(10,2) NOT NULL,
    file_name           VARCHAR(500) NOT NULL,
    file_path           VARCHAR(1000) NOT NULL,
    file_mime_type      VARCHAR(100) NULL,
    file_size_bytes     BIGINT      NULL,
    review_status       ENUM('pending','accepted','rejected','query_raised') NOT NULL DEFAULT 'pending',
    reviewed_by         BIGINT      NULL,
    review_note         TEXT        NULL,
    reviewed_at         DATETIME    NULL,
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_ticket_amount CHECK (amount >= 0),
    CONSTRAINT fk_ticket_request  FOREIGN KEY (travel_request_id) REFERENCES travel_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_uploader FOREIGN KEY (uploaded_by)       REFERENCES users(id),
    CONSTRAINT fk_ticket_reviewer FOREIGN KEY (reviewed_by)       REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tickets_request  ON travel_tickets(travel_request_id);
CREATE INDEX idx_tickets_uploader ON travel_tickets(uploaded_by);
CREATE INDEX idx_tickets_status   ON travel_tickets(review_status);

-- ── expense_claims ────────────────────────────────────────────
CREATE TABLE expense_claims (
    id                      BIGINT      NOT NULL AUTO_INCREMENT,
    travel_request_id       BIGINT      NOT NULL,
    submitted_by            BIGINT      NOT NULL,
    reimbursement_status    ENUM('not_submitted','submitted','under_review','approved','rejected','paid') NOT NULL DEFAULT 'not_submitted',
    total_claimed           DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_approved          DECIMAL(12,2) NULL,
    submitted_at            DATETIME    NULL,
    paid_at                 DATETIME    NULL,
    payment_reference       VARCHAR(200) NULL,
    bill_pdf                LONGBLOB    NULL,
    bill_file_name          VARCHAR(500) NULL,
    created_at              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_one_claim_per_trip UNIQUE (travel_request_id),
    CONSTRAINT fk_claim_request      FOREIGN KEY (travel_request_id) REFERENCES travel_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_claim_submitter    FOREIGN KEY (submitted_by)      REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── expense_items ─────────────────────────────────────────────
CREATE TABLE expense_items (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    claim_id        BIGINT      NOT NULL,
    category        ENUM('food','stay','transport','other') NOT NULL,
    expense_date    DATE        NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    description     TEXT        NULL,
    file_name       VARCHAR(500) NULL,
    file_path       VARCHAR(1000) NULL,
    file_mime_type  VARCHAR(100) NULL,
    is_approved     TINYINT(1)  NULL,
    rejection_note  TEXT        NULL,
    reviewed_by     BIGINT      NULL,
    reviewed_at     DATETIME    NULL,
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_item_amount CHECK (amount > 0),
    CONSTRAINT fk_item_claim    FOREIGN KEY (claim_id)    REFERENCES expense_claims(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_expense_claim    ON expense_items(claim_id);
CREATE INDEX idx_expense_category ON expense_items(category);

-- Trigger: keep expense_claims.total_claimed in sync
CREATE TRIGGER trg_sync_total_claimed_insert
AFTER INSERT ON expense_items
FOR EACH ROW
    UPDATE expense_claims
    SET total_claimed = (
        SELECT COALESCE(SUM(amount), 0) FROM expense_items WHERE claim_id = NEW.claim_id
    ),
    updated_at = NOW()
    WHERE id = NEW.claim_id;

CREATE TRIGGER trg_sync_total_claimed_update
AFTER UPDATE ON expense_items
FOR EACH ROW
    UPDATE expense_claims
    SET total_claimed = (
        SELECT COALESCE(SUM(amount), 0) FROM expense_items WHERE claim_id = NEW.claim_id
    ),
    updated_at = NOW()
    WHERE id = NEW.claim_id;

CREATE TRIGGER trg_sync_total_claimed_delete
AFTER DELETE ON expense_items
FOR EACH ROW
    UPDATE expense_claims
    SET total_claimed = (
        SELECT COALESCE(SUM(amount), 0) FROM expense_items WHERE claim_id = OLD.claim_id
    ),
    updated_at = NOW()
    WHERE id = OLD.claim_id;

-- ── notifications ─────────────────────────────────────────────
CREATE TABLE notifications (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    user_id         BIGINT      NOT NULL,
    title           VARCHAR(300) NOT NULL,
    body            TEXT        NOT NULL,
    is_read         TINYINT(1)  NOT NULL DEFAULT 0,
    related_entity  VARCHAR(100) NULL,
    related_id      BIGINT      NULL,
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notif_user   ON notifications(user_id, is_read);
CREATE INDEX idx_notif_entity ON notifications(related_entity, related_id);

-- ── audit_logs ────────────────────────────────────────────────
CREATE TABLE audit_logs (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    action          VARCHAR(50) NOT NULL,
    actor_id        BIGINT      NULL,
    actor_role      ENUM('admin','employee','manager','finance') NULL,
    target_entity   VARCHAR(100) NOT NULL,
    target_id       BIGINT      NOT NULL,
    old_data        JSON        NULL,
    new_data        JSON        NULL,
    ip_address      VARCHAR(45) NULL,
    user_agent      TEXT        NULL,
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_audit_actor  ON audit_logs(actor_id);
CREATE INDEX idx_audit_target ON audit_logs(target_entity, target_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_time   ON audit_logs(created_at DESC);

-- ── Reporting Views ───────────────────────────────────────────
CREATE OR REPLACE VIEW vw_dept_spend_summary AS
SELECT
    d.id            AS department_id,
    d.name          AS department_name,
    COUNT(tr.id)    AS total_trips,
    SUM(tr.estimated_budget)    AS total_estimated,
    SUM(tr.approved_budget)     AS total_approved,
    SUM(ec.total_claimed)       AS total_claimed,
    SUM(ec.total_approved)      AS total_reimbursed
FROM departments d
LEFT JOIN users u            ON u.department_id = d.id
LEFT JOIN travel_requests tr ON tr.requester_id  = u.id
LEFT JOIN expense_claims  ec ON ec.travel_request_id = tr.id
GROUP BY d.id, d.name;

CREATE OR REPLACE VIEW vw_pending_reimbursements AS
SELECT
    ec.id                   AS claim_id,
    tr.reference_no,
    u.full_name             AS employee_name,
    d.name                  AS department,
    ec.total_claimed,
    ec.total_approved,
    ec.reimbursement_status,
    ec.submitted_at
FROM expense_claims ec
JOIN travel_requests tr ON tr.id = ec.travel_request_id
JOIN users u            ON u.id  = ec.submitted_by
LEFT JOIN departments d ON d.id  = u.department_id
WHERE ec.reimbursement_status IN ('submitted','under_review','approved');

CREATE OR REPLACE VIEW vw_user_travel_history AS
SELECT
    tr.id,
    tr.reference_no,
    u.full_name             AS requester,
    tr.destination,
    tr.trip_type,
    tr.transport_mode,
    tr.travel_from,
    tr.travel_to,
    tr.status,
    tr.estimated_budget,
    tr.approved_budget,
    ec.total_claimed,
    ec.reimbursement_status,
    tr.has_policy_violation
FROM travel_requests tr
JOIN users u            ON u.id  = tr.requester_id
LEFT JOIN expense_claims ec ON ec.travel_request_id = tr.id;

SET FOREIGN_KEY_CHECKS = 1;
