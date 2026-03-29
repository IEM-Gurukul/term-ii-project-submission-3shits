[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/pG3gvzt-)

# PCCCS495 – Term II Project

## Project Title

Student Attendance System

---

## Problem Statement (max 150 words)

Maintaining accurate and efficient attendance records is a critical task in educational institutions. Traditional manual methods are time-consuming, error-prone, and difficult to manage at scale. There is a need for a system that can automate attendance tracking, ensure data integrity, and provide quick access to attendance reports.

## The proposed Student Attendance System aims to digitize attendance management using an object-oriented approach. It allows instructors to record, update, and retrieve attendance data efficiently. The system ensures proper validation, minimizes redundancy, and provides structured data handling, thereby improving reliability and usability for academic environments.

## Target User

Teachers / Faculty members
Academic administrators
Students (for viewing attendance records)

---

## Core Features

Add, update, and delete student records
Mark attendance for students (Present/Absent)
View attendance reports (daily / student-wise)
Search students by ID or name
Persistent data handling using file or database storage

## OOP Concepts Used

Abstraction:
Abstract classes or interfaces are used to define core functionalities like attendance marking and data handling without exposing implementation details.
Inheritance:
A base class User is extended by Student and Teacher classes to reuse common properties such as ID and name.
Polymorphism:
Method overriding is used for different behaviors such as displaying student versus teacher details. Method overloading is used in attendance marking functions.
Exception Handling:
Invalid inputs, missing records, and file or database errors are handled using try-catch blocks to ensure system stability.
Collections / Threads:
Collections such as ArrayList or HashMap are used to store and manage student and attendance data efficiently. Threads may be used for background saving or report generation.

---

## Proposed Architecture Description

The system follows a layered architecture. The presentation layer handles user interaction through a console-based interface. The service layer contains the business logic such as attendance marking, validation, and report generation. The data access layer is responsible for storing and retrieving data from files or a database. The model layer defines the core entities such as Student, Attendance, and User.

## This separation ensures modularity, maintainability, and scalability.

## How to Run

Compile the Java files
javac Main.java
Run the application
java Main

---

## Git Discipline Notes

Minimum 10 meaningful commits required.
