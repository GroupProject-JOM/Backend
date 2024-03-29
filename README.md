# JOM Business and Manufacturing Process Management System


Presenting the backend of web and mobile applications designed to manage and automate the business and manufacturing processes of Jayasinghe Oil Mills Pvt. Ltd., a local virgin coconut oil manufacturer. This backend, developed using Java Servlets, powers the core functionality of the system, enabling seamless data management, communication, and business logic execution.

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

- **[src](https://github.com/GroupProject-JOM/Backend/tree/main/src):** Houses the source code for the backend application.
- **[.gitignore](https://github.com/GroupProject-JOM/Backend/blob/main/.gitignore):** Specifies files and folders to be excluded from Git version control.
- **[pom.xml](https://github.com/GroupProject-JOM/Backend/blob/main/pom.xml):** The Maven project configuration file, defining dependencies and build settings.
- **[README.md](https://github.com/GroupProject-JOM/Backend/blob/main/README.md):** This file, provides an overview of the project structure and setup.

### Source Code Directory (src)

- **[main](https://github.com/GroupProject-JOM/Backend/tree/main/src/main):** Contains the primary application code.
  - **java:**
    - **[org.jom](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom):** The root package for the application's Java classes.
      - **[Auth](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom/Auth):** Houses classes responsible for user authentication and authorization logic.
      - **[Controller](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom/Controller):** Contains servlet classes that handle incoming HTTP requests and responses.
      - **[Dao](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom/Dao):** Encapsulates data access object classes for interacting with the database.
      - **[Database](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom/Database):** Potential utility classes for database-related operations.
      - **[Email](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom/Email):** Classes for handling email functionality.
      - **[Filter](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom/Filter):** Holds servlet filters for request preprocessing and postprocessing.
      - **[Model](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom/Model):** Represents data model classes, defining the structure of entities in the system.
      - **[Socket](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/java/org/jom/Socket):** Classes for WebSocket-based communication.
  - **[webapp](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/webapp):**
    - **[WEB-INF](https://github.com/GroupProject-JOM/Backend/tree/main/src/main/webapp/WEB-INF):** Contains web application configuration files and resources.
      - **[web.xml](https://github.com/GroupProject-JOM/Backend/blob/main/src/main/webapp/WEB-INF/web.xml):** The deployment descriptor for the servlet application, defining servlet mappings and other configurations.

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

---


<p align="center">
    <a href="https://github.com/GroupProject-JOM/Backend/blob/main/LICENSE">
      <img alt="License: GNU" src="https://img.shields.io/badge/License-GPLv3-blue.svg">
   </a>
    <a href="https://github.com/GroupProject-JOM/Backend">
      <img alt="Hits" src="https://hits.sh/github.com/GroupProject-JOM/Backend.svg?label=Views"/>
    </a>
    <a href="https://github.com/GroupProject-JOM/Backend/graphs/contributors">
      <img alt="GitHub Contributors" src="https://img.shields.io/github/contributors/GroupProject-JOM/Backend" />
    </a>
    <a href="https://github.com/GroupProject-JOM/Backend/issues">
      <img alt="Issues" src="https://img.shields.io/github/issues/GroupProject-JOM/Backend?color=0088ff" />
    </a>
    <a href="https://github.com/GroupProject-JOM/Backend/pulls">
      <img alt="GitHub pull requests" src="https://img.shields.io/github/issues-pr/GroupProject-JOM/Backend?color=0088ff" />
    </a>
  </p>
