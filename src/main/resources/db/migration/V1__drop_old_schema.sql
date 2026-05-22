-- ============================================================
--  V1 — DROP OLD SCHEMA (reverse dependency order)
--  Drops all tables from the previous schema so V2 can
--  create the new schema cleanly.
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

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

SET FOREIGN_KEY_CHECKS = 1;
