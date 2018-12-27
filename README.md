### Cybersecurity-project I

For my project, I have decided to create a simple todo-application with 6 severe vulnerabilities from the OWASP top 10 list.
In the project I have used the starter-template with Java Spring, Thymeleft and H2 database.

The project is in GitHub: https://github.com/markokoskinen2037/cybersecuritybase-project

In order to get started:
1. Download the project from GitHub.
2. Open the project in your Java IDE. 
3. Run the project.

All application data including (usernames,passwords,credit-card-information,todos) are stored in a local SQL database.
When you start the application, a new database is created and some sample data is inserted. The following username:password combinations are at your disposal (you can also create new ones):
admin:password
ted:ted
homer:simpson

Please complete the examination process from top to bottom. Otherwise some of my flaw-reproduction instructions might not work correctly due to changes in the SQL database.

Below I will explain how to reproduce the flaws and how to fix them.

 ### Issue: A3 Sensitive Data Exposure
Steps to reproduce:
1. Run OWASP ZAP.
2. Create a new "Forced Browse" attack on localhost:8080
3. Select "directory-list-1.0.txt" as the list to be used.
4. Go to Settings:Forced Browse. 
5. Disable recursion. Increase maximum threads to 100.
6. Run the Forced Browse attack.
7. Look through the list of found routes, and visit localhost:8080/Users
8. Notice how this hidden endpoint provides sensitive user information.

How to fix:
1. Remove /Users route from the web-application. Because it's insane to provide a route that allows anyone who finds it to obtain a list of sensitive user-data... Maybe it does not matter so much that someone can use their accounts to create some todos, but the users probably use the same username:password combinations on other more important sites. Also, credit card information could be used for fraud.
2. If /Users route needs to be available, for example to admins - I would recommend implementing token based authentication (JWT token for example) so that only admins are able to access /Users route.
3. Users credit-card information is stored, but never used, therefore it makes no sense to even ask for it. Remove credit-card-field from registration page.

-----------------------------------------

 ### Issue: A2 Broken Authentication
Steps to reproduce:

First flaw:

1. Open http://localhost:8080/register
2. Create a new account with any username-password-credit-card combination.
3. Go to http://localhost:8080/Users
4. Notice that your username, password and credit-card are indeed in plain text.

Second flaw:
1. Open http://localhost:8080/login
2. Login with admin:password
3. Notice that the default administrative account's login details are really weak, and therefore easy to crack by a brute-force attack.

Third flaw:
1. Run a basic attack on OWASP Zap. Notice how the application allows this obvious attack to continue.

How to fix:

1st flaw:

Enable a password encryption scheme for passwords and credit-card-numbers, for example BCrypt. Once passwords and credit-card-numbers are encrypted, potential breach will be a lot less harmful. The attacker will only be able obtain sensitive information in its encrypted form - making it almost worthless.

2nd flaw:
Change default administrative password to a more secure one. I would advice, that the administrative password should be around 20 characters long sequence of seemingly random letters - and the password should be changed at least once per month. Also enabling two-factor authentication would make authentication a lot more secure.

3rd flaw: Implement server-side functionality that tracks incoming requests. If an unusual amount of requests stats pouring in from a single IP address - stop responding to requests coming from that IP for 15 minutes to prevent brute-force and credential stuffing attacks.

-----------------------------------------

 ### Issue: A7 Cross-site Scripting
Steps to reproduce:
1. Click the Logout button.
2. Go to http://localhost:8080/login
3. Login with any account. For example: ted:ted
4. Add a new todo with Todo-content: <script>window.location.replace("http://www.example.com")</script>
5. View global list of todos by clicking "Click here"
6. Notice how you are now being redirected to "http://www.example.com"

How to fix:
- The todo-listing page is currently being created to a String, by appending todos to the end of it. This String is then returned straight to the browser for rendering.
- Instead of creating the list by appending text to a String, iterate over every todo-entity and serve them to a template processing-engine Thymeleaf, that uses th:text as an attribute. By using th:text attribute, Thymeleaf template-engine automatically checks for "shady" input values and acts accordingly. 
- Now todos that contain <script> tags wont be interpreted as html-elements, but instead they are treated as text.
  
-----------------------------------------

 ### Issue: A5 Broken Access Control
Steps to reproduce:
1. Go to http://localhost:8080/todo
1. Click the Logout button.
2. Go to http://localhost:8080/login
3. Login with ted:ted
4. Notice how there is no link to adminpage.
5. Open your browser's developer console.
6. Input window.sessionStorage.setItem("username", "admin");
7. Refresh the page (press F5).
8. Notice how there is now a link to "Supersecret adminpage", from where you can now access functions that should be unavailable to everyone except user: "admin". (Don't click the "clear sql database" button just yet!)

How to fix:

Implement session handling and request validation. This could be achieved with tokens like so:
- User logs in for the first time.
- Server creates a unique token (for example JWT-token) that is saved on server and also sent back to users web-browser - where the token is stored.
- Check the token every time, when a user tries to access a restricted URL such as /adminpage.
- If the token is valid, return adminpage.html. Else redirect user back to where he came from.
- Because the token is created on the server by highly sophisticated Signature Algorithm, it's basically impossible to guess a valid token.
-----------------------------------------

 ### Issue: A1 Injection (SQL)
Steps to reproduce:
1. Open localhost:8080/Users
2. Notice, that the are several users in the database. (If you clicked "CLEAR SQL DATABASE" in the previous part, there wont be any users! If that is the case; create a new user and then continue from step 3)
3. Open http://localhost:8080/register
4. Leave name- and creditcard-fields empty.
5. Set password-field to: '); DROP TABLE USER; CREATE TABLE User (id int NOT NULL AUTO_INCREMENT,cc varchar(200),username varchar(200),password varchar(200)); INSERT INTO User (cc,username,password) VALUES ('911','You have been hacked',' sorry
6. Click "Submit".
7. Open http://localhost:8080/Users
8. Notice, that all other users have been deleted.

How to fix:

Sanitize SQL query input parameters by using Prepared Statements. This can easily be implemented in application code by following these steps:

1. Create a String query = "INSERT INTO User (username,password,cc) VALUES(?,?);"
2. Perform some validation on the input parameters. I would suggest at least checking the length of the input parameters, and that if they contain html-elements.
3. Create a PreparedStatement object from the query.
4. Insert input parameters into the PreparedStatement.
5. Execute the PreparedStatement.
-----------------------------------------

 ### Issue: A10 Insufficient Logging & Monitoring
Steps to reproduce:

- Examine the application code and notice that logging & monitoring is not enabled.
How to fix:
- Enable logging for the most important features, such as login-requests with LoggerFactory.
- Configure automatic backups.
- Implement real-time monitoring alerts.
-----------------------------------------
