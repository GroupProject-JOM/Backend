# JOM Business and Manufacturing Process Management System


Presenting the backend of a web application designed to manage and automate the business and manufacturing processes of Jayasinghe Oil Mills Pvt. Ltd., a local virgin coconut oil manufacturer. This backend, developed using Java Servlets, powers the core functionality of the system, enabling seamless data management, communication, and business logic execution.

## Key Features

- **User Authentication and Authorization:** Securely manage user access and permissions.
- **Data Persistence with Database Integration:** Utilize MySQL for efficient data storage.
- **RESTful API Endpoints for Front-End Interaction:** Enable smooth interaction between the frontend and backend.
- **Real-Time Communication (powered by WebSockets):** Facilitate instant communication within the system.
- **Email Functionality:** Implement email-related features for notifications and user communication.
- **Location Services with Google Maps API:** Integrate Google Maps API for precise location-related functionalities.

## Technologies Used

- **Java:** The core programming language for backend development.
- **Servlets:** Java Servlets for handling HTTP requests and responses.
- **Maven:** Build tool and project management for Java applications.
- **MySQL:** Database management system for data storage.
- **WebSockets:** Enabling real-time bidirectional communication between clients and server.
- **Google Maps API:** Integrating location services for efficient coordination.

## Folder Structure

Here's a breakdown of the backend's folder structure, ensuring clarity and maintainability:

### Root Directory

- **src:** Houses the source code for the backend application.
- **.gitignore:** Specifies files and folders to be excluded from Git version control.
- **pom.xml:** The Maven project configuration file, defining dependencies and build settings.
- **README.md:** This file, providing an overview of the project structure and setup.
- **.idea:** Contains project-specific settings for IntelliJ IDEA (or other compatible IDEs).

### Source Code Directory (src)

- **main:** Contains the primary application code.
  - **java:**
    - **org.jom:** The root package for the application's Java classes.
      - **Auth:** Houses classes responsible for user authentication and authorization logic.
      - **Controller:** Contains servlet classes that handle incoming HTTP requests and responses.
      - **Dao:** Encapsulates data access object classes for interacting with the database.
      - **Database:** Potential utility classes for database-related operations.
      - **Email:** Classes for handling email functionality.
      - **Filter:** Holds servlet filters for request preprocessing and postprocessing.
      - **Model:** Represents data model classes, defining the structure of entities in the system.
      - **Socket:** Classes for WebSocket-based communication.
  - **webapp:**
    - **WEB-INF:** Contains web application configuration files and resources.
      - **web.xml:** The deployment descriptor for the servlet application, defining servlet mappings and other configurations.

