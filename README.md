# Delete Products BatchJob

## Description
The Delete Products BatchJob is a batch process designed to clean up obsolete or unnecessary product records from the system. 
This batch job interacts with the product service API to perform deletion operations efficiently and in bulk. 
It ensures data consistency and improves system performance by removing redundant data.

## Technologies Used

- **Spring Boot**: A Java-based framework for building production-ready, stand-alone, and microservice-oriented applications, offering built-in tools and convention-over-configuration.
- **Spring Batch**: A robust batch processing framework for developing scalable and reliable batch jobs.
- **Java**: A versatile, platform-independent programming language used for implementing the backend logic and API.
---

## Database Schema

### Table: `staging_table`
| Column                    | Type         | Constraints |
|---------------------------|--------------|-------------|
| `staging_id`              | VARCHAR(36)  | PRIMARY KEY |
| `product_id`              | VARCHAR(36)  | NOT NULL    |
| `product_name`            | VARCHAR(100) | NOT NULL    |
| `status`                  | VARCHAR(15)  |             |
| `scheduled_deletion_date` | DATE         | NOT NULL    |
| `completion_date`         | DATE         |             |

## Pre-requisites
Before you begin, ensure that you have the following tools and services installed:
1. **Java**  
   Ensure that you have **Java 17** installed on your system. You can verify this by running:
   ```bash
   java -version
   ```
2. **Docker** is used for containerizing the application and managing services through Docker Compose. You can check if Docker is installed with:
   ```bash
   docker --version
   ```
3. **Product Common Core**  
   This library contains shared logic, exception handlers, and other core utilities used across the microservices and batch jobs. You can find the repository for the Product Common Core [here](https://github.com/brandontan2003/product_common_core).
4. **Product Management Service**  
   This microservice is required for the batch job to make the API calls. You can find the repository for the Product Management Service [here](https://github.com/brandontan2003/product_management_service).
---

## Setup Instructions
1. Download Java 17 and verify Gradle installation
    1. Download Java from [Oracle Java](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [AdoptOpenJDK](https://adoptium.net/).  
       Verify the installation by running:
         ```bash
         java -version
         ```
       Ensure it outputs Java 17.
    2. **Gradle**: Verify Gradle is installed by running:
         ```bash
         gradle -v
         ```
       Alternatively, use the Gradle wrapper provided in the project (`./gradlew`).
2. Link Gradle project
    1. Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).
    2. Import it as a Gradle project using the build.gradle file.
3.  Build the Project
    Run the following command to build and verify the project:
    ```bash
    ./gradlew build
    ```
