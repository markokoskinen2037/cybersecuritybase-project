# cybersecuritybase-project 2018
This is the first course project of Cyber Security MOOC. In this project I have created a web-application with 5 severe security flaws from the OWSAP top 10 list. Below I will explain what the vulnerabilities are, how to find them and how to fix them.

1. DOWNLOAD THE PROJECT SOURCE FILES FROM GITHUB
2. OPEN THE PROJECT IN YOUR JAVA IDE
3. RUN THE PROJECT
4. NAVIGATE TO http://localhost:8080

Users already in database:
- admin:password
- ted:ted
- homer:simpson

## Vulnerabilies and how to fix them
### A3 Sensitive Data Exposure
#### How to identify:
1. Notice how there is no direct link to http://locahost:8080/users
1. However, if a user were to find this URL by accident or with an attacking tool - he would be able to see usernames and passwords of every user.
#### How to fix:
1. Remove /users route from the web-application.
----------------
### A2 Broken Authentication
#### Steps to reproduce
1. Open http://localhost:8080/register
1. Create a new account with any username-password combination.
1. Go to http://localhost:8080/users
1. Notice that your username and password are indeed in plain text
2. Open http://localhost:8080/login
2. Login with admin:password
2. Notice that administrative accounts login details are left to default values and are therefore easy to crack by bruteforce.
#### How to fix:
1. Enable password crypting scheme. For example BCrypt.
1. Change default administrative password to a more secure one.
---------------------------
### A7 Cross-site Scripting
#### How to identify:
1. Go to http://localhost:8080/login
1. Login with any account. For example: ```ted:ted```
1. Add a new todo with Todo-content ```<script>window.location.replace("http://www.example.com")</script>```
1. View global list of todos by clicking "Click here"
1. Notive how you are now being redirected to http://www.example.com
#### How to fix:
1. The todo-listing page is currently being created to a String.
1. Instead of creating the list by appending text to a String, iterate over every todo-entity and serve them to a template processing engine such as Thymeleaf.
-----------------
### Issue: A1 Injection
#### Steps to reproduce
1. Open localhost:8080/users
1. Notice, that the are several users in the database.
1. Open http://localhost:8080/register
1. Leave name-field empty.
1. Set password-field to
``` '); DROP TABLE USER; CREATE TABLE User (id int NOT NULL AUTO_INCREMENT,username varchar(200),password varchar(200)); INSERT INTO User (username,password) VALUES ('You have been hacked',' sorry... ```
1. Click "Submit".
1. Open http://localhost:8080/users
1. Notice, that all other users have been deleted.
#### How to fix:
1. Sanitize SQL query input parameters.
---------------
---------------
### A5 Broken Access Control
#### How to identify:
1. Go to http://localhost:8080/login
1. Login with ```ted:ted```
1. Notice how there is no link to adminpage.
1. Open your web-browsers developer console.
1. Input ``` window.sessionStorage.setItem("username", "admin"); ```
1. Refresh the page (F5)
1. Notice how there is now a link to "Supersecret adminpage", from where one can clear the whole database.
#### How to fix:
1. Implement better session handling. This could be achieved with tokens like so:
- User logs in for the first time.
- Server creates a unique token that is saved on server and also sent back to users web-browser - where the token is stored.
- Check the token everytime, when a user tries to access a restricted URL such as /adminpage.
- If the token is valid, return adminpage.html. Else redirect users browser back where it came from.
---------------
