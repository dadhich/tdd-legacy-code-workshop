# Legacy Loan System (TDD Workshop)  
Welcome to the **Legacy Loan System**. This project simulates a mission-critical, "spaghetti code" application used by a fictional bank.  
**The Goal:** This codebase is intentionally designed with bad practices, tight coupling, and zero tests. It serves as a practical playground for learning **Test Driven Development (TDD)**, **Refactoring**, and **Characterisation Testing** in a realistic, hostile environment.  

## High-Level Architecture  
The system consists of a Kotlin Spring Boot backend and a React frontend.  
Code snippet  
  
graph TD  
    User[Bank Clerk (Browser)] -->|HTTP POST| FE[React Frontend]  
    FE -->|JSON /api/loan/apply| BE[Spring Boot Backend]  
      
    subgraph "Legacy Backend Service"  
        BE -->|Direct Call| Logic[LegacyLoanService]  
        Logic -->|Static Method Call| Credit[External Credit Bureau]  
        Logic -->|Direct Instantiation| Email[Email Service]  
        Logic -->|JPA| DB[(H2 Database)]  
    end  
      
    style Credit fill:#ffcccc,stroke:#333,stroke-width:2px  
    style Email fill:#ffcccc,stroke:#333,stroke-width:2px  
    style Logic fill:#ffffcc,stroke:#333,stroke-width:2px  
* **Frontend:** React (Vite) - A single monolithic form component.  
* **Backend:** Kotlin (Spring Boot) - A Service class with hard dependencies.  
* **Database:** H2 (In-memory) - Data is lost when the app restarts.  
* **External Systems:** Simulated via code (Credit Bureau & Email) to introduce latency and random failures.  
  
## Prerequisites  
* **Java:** JDK 17 or higher.  
* **Node.js:** Version 18+ (LTS).  
* **IDE:** IntelliJ IDEA (Recommended for Kotlin) or VS Code.  
  
## Setup & Run Instructions  
## 1. Backend (Kotlin + Spring Boot)  
The backend manages loan processing logic. It uses **Gradle** for build management.  
**Setup:**  
1. Open a new terminal window. Navigate to the backend directory:
cd legacy-loan-backend  
2. Build the project to download dependencies:
./gradlew build  
 **Run:** Start the server using the Gradle wrapper:   
./gradlew bootRun
 
* **Success:** You will see Tomcat started on port 8080.  
* **Note:** Keep this terminal window open.

## 2. Frontend (React + Vite)  
The frontend provides the user interface for loan officers.  
**Setup:**   
1. Open a **new** terminal window.  
2. Navigate to the frontend directory: cd legacy-loan-ui  
3. Install dependencies: npm install  
**Run:** Start the development server:  
npm run dev  
* **Success:** The terminal will show a URL, typically http://localhost:5173.  
* **Action:** Open this URL in your browser.  
  
## The "Happy Path" Smoke Test  
To verify your environment is working:  
1. Open the App in your browser.  
2. Enter **Name:** John Doe.  
3. Enter **Tax ID:** 123456.  
4. Enter **Amount:** 5000.  
5. Enter **Age:** 30.  
6. Click **Submit Application**.  
**Expected Result:** You should see a green message: *"Loan approved based on moderate credit..."*  
  
## Known Issues (The "Legacy" Traits)  
As you explore the code, you will encounter several "interesting" design choices. **Do not fix these yet.** They are part of the exercise.  
1. **The Time Bomb:** The application throws errors if run outside of "business hours" (8 AM - 6 PM system time).  
2. **The Magic Number:** If you enter a Tax ID starting with 999 (e.g., 999123), the entire backend request will crash due to a simulated "External API Timeout."  
3. **The Hidden Email:** The system attempts to send emails using a hardcoded SMTP server. This often prints errors to the console logs.  
4. **No Tests:** There are currently **zero** unit or integration tests.  

## Workshop Objective  
We have received a new feature request from the business:   
*"If the applicant is under 25 years old and requesting more than $50,000, we must flag the application as 'High Risk'."*  
Your Challenge:  
How do we implement this safely using TDD when we cannot even write a test for the LegacyLoanService without triggering a database write or an external API call?  
* **Step 1:** Characterisation Testing (Creating a safety net).  
* **Step 2:** Refactoring (Breaking dependencies).  
* **Step 3:** Pure TDD (Implementing the new feature).  
