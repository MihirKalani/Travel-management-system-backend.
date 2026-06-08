# Travel Management System Frontend GitHub Repository: https://github.com/MihirKalani/Travel-Management-System-Frontend.git

# Travel Management System

## Overview

Travel Management System (TMS) is a full-stack web application designed to streamline corporate travel processes. The system enables employees to submit travel requests, managers to review and approve requests, finance teams to process reimbursements, and administrators to manage users and departments.

The application follows a role-based workflow to ensure transparency and efficient travel management within an organization.

---

## Features

### Employee
- Register and log in securely
- Submit travel requests
- Track request status
- Submit expense claims
- View reimbursement status
- Manage personal profile

### Manager
- Review travel requests
- Approve or reject requests
- Monitor employee travel activities

### Finance
- Review approved travel requests
- Verify expense claims
- Process reimbursements

### Administrator
- Manage users and departments
- Assign roles and permissions
- Monitor overall system operations

---

## Technology Stack

### Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Lombok

### Frontend
- Angular
- TypeScript
- HTML
- CSS

### Database
- MySQL

---

## Architecture

The application follows a layered architecture:

```text
Controller Layer
        │
Service Layer
        │
Repository Layer
        │
Database
```

This separation ensures maintainability, scalability, and clean code organization.

---

## Project Structure

```text
Travel_Management_System
│
├── src/main/java
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── config
│   └── security
│
├── src/main/resources
│
└── pom.xml

tms-frontend
│
├── src/app
│   ├── components
│   ├── services
│   ├── models
│   └── guards
│
└── angular.json
```

---

## Workflow

1. Employee submits a travel request.
2. Manager reviews and approves or rejects the request.
3. Approved requests proceed to the finance department.
4. Employee submits expense details after completing travel.
5. Finance verifies expenses and processes reimbursement.
6. Status updates are available throughout the workflow.

---

## Key Modules

### Authentication Module
Handles user registration, login, and authorization.

### Travel Request Module
Allows employees to create and manage travel requests.

### Approval Module
Provides approval workflows for managers and finance personnel.

### Expense Management Module
Handles expense claims and reimbursement processing.

### Administration Module
Manages users, departments, and system settings.

---

## Database Entities

The system is built around the following core entities:

- User
- Department
- TravelRequest
- ExpenseClaim
- Approval

Entity relationships are managed using JPA and Hibernate.

---

## Security

- Role-Based Access Control (RBAC)
- Secure authentication using Spring Security
- Protected API endpoints
- Authorization based on user roles

---

## Installation

### Backend Setup

Clone the repository:

```bash
git clone https://github.com/your-username/travel-management-system.git
```

Navigate to the backend project:

```bash
cd Travel_Management_System
```

Configure database settings in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tms_db
spring.datasource.username=root
spring.datasource.password=your_password
```

Run the application:

```bash
mvn spring-boot:run
```

Backend server:

```text
http://localhost:8080
```

### Frontend Setup

Navigate to the frontend project:

```bash
cd tms-frontend
```

Install dependencies:

```bash
npm install
```

Run the Angular application:

```bash
ng serve
```

Frontend server:

```text
http://localhost:4200
```

---

## Learning Outcomes

This project provided practical experience in:

- Full-Stack Web Development
- RESTful API Design
- Spring Boot Development
- Angular Application Development
- Database Design and Management
- Authentication and Authorization
- Role-Based Access Control
- Software Architecture and Design Patterns
