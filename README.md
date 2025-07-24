# BookStore API Automation Framework - Rest Assured 

This project is a comprehensive **API Test Automation Framework** of BookStore API , built using **Java**, **RestAssured**, **TestNG**, 
and **ExtentReports** to validate the API functionalities of the [BookStore API](https://bookstore.toolsqa.com/swagger/). 

The framework developed to supports **CI/CD using GitHub Actions** and **generates test reports**.

## Tech Stack

| Component        | Version       | Purpose                                  |
|------------------|---------------|------------------------------------------|
| Java             | 21            | Programming Language                     |
| Maven            | 4.0.0         | Build and Dependency Management          |
| REST-assured     | 5.3.0         | API Testing Library                      |
| TestNG           | 7.8.0         | Test Execution Engine                    |
| ExtentReports    | 5.0.9         | Test Reporting                           |
| Jenkins          | Optional      | CI/CD Automation                         |
| Jacoco           | 0.8.12        | Code Coverage                            |


## API Coverage

The framework covers all **CRUD operations** of BOOK API and it includes the below :

- **User Management**  
  - Create User  
  - Generate Token  
  - Get User  

- **Book Collection**  
  - Add Book  
  - Get Book
  - Update Book
  - Delete Book 

- **Negative test**
  -Neg Test1: Verify user creation when Payload data missing.
  -Neg Test2: Verify user creation when user enters invalid password.
  -Neg Test3: Verify Generate token with invalid credentials. Generate token gives status code as 200 for valid and invalid details.
  -Neg Test4: Verify Create book with no Authorisation and check API responsed status code as 401
  -Neg Test5: Verify Get user with wrong user id to search ,API should respondstatus code 401 'Not Authorised'.
  -Neg Test6: Verify attempt Update book when don't provide ISBN.
  -Neg Test7: Verify Delete book when pass wrong user id.

## Testing Strategy

## 1. **Approach to Developed Test Script Flows**
- Created tests for user and book operations.
- Used `dependsOnMethods` in TestNG  in logical flow (example : Token creation depends on user creation).
- Ensured test data like usernames are **random username creating using Math.Random class** to avoid conflicts for duplicacy of user.
- Used request chaining as userID picking from User Creation and same used for Book`create and so on)
- Validating all the API Response again the API Response data.

## 2. **Reliability & Maintainability**
- Centralized base URL and credentials in `ApplicationConfig.properties File.
- Separated test logic, config, utilities, and reporting.
- Used assertions on status codes, response payloads.
- Scenarios included ** positive and negative** test.
- Implemented `BasicAPITest` to initialize ExtentReports and RestAssured.

## 3. **Challenges & Solutions**

- Some API endpoints returned 400/401 unexpectedly to handle Added proper headers data and verified request body using Swagger documents |
- To avoid duplicacy used randomization to generate unique usernames|

## CI/CD Pipeline

### Trigger:  
Runs on **every push** and **pull request** to `main`.

### Steps in CICD process:
- Checkout code
- Setup Java (21)
- Build and run tests via Maven
- Upload test reports:
  - Reports (TestNG)
  - ExtentReports (HTML)
  - JaCoCo (code coverage)

**Execute the Automation Suite**:
   - Run the `testng.xml` file. This will trigger all feature scenarios written Test Script in a human-readable format.

## Running Tests Locally
### Clone the repo in the required directory on your PC if you want to run locally:
   Git Clone Repo     :  https://github.com/niwaskumary1801/BookStoreAPIAutomationTesting.git

   cd BookStoreAPIAutomationTesting

-Run test using Maven:
-mvn clean test

**View Reports**:
   - Once test execution completes, **ExtentReports** will be generated at:
   - View the report in your browser. Reports are present in the path directory.
     test-output/ExtentReports.html
   - Open the HTML report in your browser to view test execution results.

## How to View GitHub Actions Reports : 
Navigate to Repo URL : https://github.com/niwaskumary1801/BookStoreAPIAutomationTesting.git
Click on the latest workflow run.
Scroll to Artifacts.

## Authors 
userID: niwasKumary1801 [https://github.com/niwaskumary1801/]

