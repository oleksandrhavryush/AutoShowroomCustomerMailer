# Auto Showroom Series Letter Generator

## Overview
This project is a comprehensive solution for an automobile showroom to automate the process of informing customers about the latest vehicle models via postal letters. The application is built on Spring Boot and utilizes various libraries to handle data persistence, web interaction, and PDF generation.

## Features
- **XML File Upload**: Users can upload vehicle data through an XML file directly from the browser interface. Test xml files for downloading cars can be found in the project resources folder.
- **Customer Management**: Creation and editing of customer details are facilitated through the browser, with a complete list of customers available for review and edit.
- **PDF Generation**: Customized letters are generated in PDF format, stored in a project resource folder, and designed to be trifold for envelope insertion.
- **Database Initialization**: On application startup, corresponding tables in the database are automatically created, and sample data clients and vehicles are loaded.
- **Localization and Internationalization**: The application supports English, Ukrainian, and German for the main types of errors and validation messages, ensuring a user-friendly experience across different languages.

## Technologies
- Spring Boot
- Spring Data JPA
- Thymeleaf
- Spring Web
- Spring Validation
- MySQL Connector
- Lombok
- JAXB API and Runtime
- iText PDF
- Mockito for testing

## Getting Started
To begin using the application, follow these steps:
1. Download the project files.
2. Start the database with the following Docker command: ```docker run -d -p 3306:3306 --name=db_mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=auto_showroom mysql:8.0```
3. Launch the project.

## Testing
Comprehensive tests have been written for each class and method to ensure robustness and reliability.

## Documentation
- Source code is thoroughly documented.
- A Git repository is utilized for version control.
