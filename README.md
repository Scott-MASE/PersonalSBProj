# 🐝 HiveNotes
A collaborative note-taking web app with a bee theme, built with Maven and Spring Boot.

![Maven](https://img.shields.io/badge/Maven-4.0.0-C71A36?logo=apache-maven&style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen?logo=springboot&style=for-the-badge)

## 📜 Table of Contents
- [Features](#features)
- [Installation](#installation)
- [API Documentation](#api-documentation)

## ✨ Features
Below is a short video demo to walk you through all the features of HiveNotes

### 🎥 Watch the Demo
[![Watch the video](https://img.youtube.com/vi/jEKoQhoWZrc/0.jpg)](https://www.youtube.com/watch?v=jEKoQhoWZrc)

## Installation
The project was built using maven and springboot. First, create a Schema titled "Hive" in MySQL (localhost:3306). the default MySQL account details are 'root' 'root', but if yours differ you can change them in the application.yml file. Once the MySQL schema is create and running, clone the "Proj" file into your IDE of choice, and run the project. Once launched, it can be accessed via localHost:9092. On startup it automatically adds four users: and Admin account with the credentials "admin" "admin", two User accounts "James" "user" and "Jim" "user", and a Moderator "Mod" "mod". All of these accounts except for the admin can be deleted, and if the admin account does somehow end up deleted, when the webapp is restarted it along with the other accounts will be re-added. There is a Setup.sql script included in the repository, this is optional and when run adds some sample notes to James and Jim.







