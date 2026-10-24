# Project Summary: Insurance Policy Management System

> **Note:** For the complete, detailed academic report documentation with diagrams, concept mapping, and module breakdowns, please see **[docs/summary.md](file:///e:/College/2nd%20Year/OOPs/Mini-project/docs/summary.md)**.

---

## Quick Reference Summary

- **Course:** Object-Oriented Programming (Java) Mini-Project
- **Academic Year:** 2nd Year B.Tech / BE Computer Engineering
- **Architecture:** Console-based, menu-driven object-oriented architecture
- **Team Size:** 3 Members
- **Primary Roles:** Customer Module and Administrator Module

### Key Features
1. **Customer Module:** Registration, Plan browsing, Quoting with dynamic risk formulas, Policy purchase with `PENDING_PAYMENT` state, Simulated payments via `Payable` interface, Claim filing and tracking, Policy cancellation/renewal.
2. **Administrator Module:** Plan management with soft deactivation (`available` flag), Customer search & updates, Policy filtering by status (`ACTIVE`, `PENDING_PAYMENT`, `CANCELLED`, `EXPIRED`), Claim review with custom payouts and remarks, Real-time KPI Analytics Dashboard.
3. **Multithreading:**
   - Background daemon thread `ExpiryMonitor` periodically checking and updating expired policies.
   - Background worker thread `ReportTask` generating asynchronous audit reports.
4. **Data Structures & Auditing:** `ActionStack` LIFO stack tracking administrative operations with single-step reversible undo.
5. **Persistence:** Delimited text files in `data/` (`customers.txt`, `plans.txt`, `policies.txt`, `payments.txt`, `claims.txt`) automatically loaded at startup and saved on exit.

### Core OOP Concepts Demonstrated
- **Encapsulation:** Private entity fields with validated getters/setters.
- **Inheritance:** `InsurancePlan` base class extended by `HealthPlan`, `LifePlan`, `VehiclePlan`, `TravelPlan`.
- **Dynamic Polymorphism:** Subclasses override `calculatePremium(Customer)`.
- **Static Polymorphism:** Overloaded search and calculation methods.
- **Abstraction & Interfaces:** Abstract base class `InsurancePlan` and `Payable` interface.
- **Exception Handling:** Custom exception hierarchy (`InvalidInputException`, `CustomerNotFoundException`, etc.) and safe console parsing.
- **Multithreading:** Concurrency via `Thread` subclassing and daemon background execution.
- **File Handling:** Flat file serialization via `BufferedReader` and `BufferedWriter`.

---

### Project Documentation Index
- **Academic Report Guide:** [docs/summary.md](file:///e:/College/2nd%20Year/OOPs/Mini-project/docs/summary.md)
- **Software Architecture:** [docs/architecture.md](file:///e:/College/2nd%20Year/OOPs/Mini-project/docs/architecture.md)
- **Product Requirements Document (PRD):** [docs/prd.md](file:///e:/College/2nd%20Year/OOPs/Mini-project/docs/prd.md)
- **Changelog & Architecture Alignment:** [docs/changes_made.md](file:///e:/College/2nd%20Year/OOPs/Mini-project/docs/changes_made.md)
- **Test Run & Verified Output:** [docs/output.md](file:///e:/College/2nd%20Year/OOPs/Mini-project/docs/output.md)
