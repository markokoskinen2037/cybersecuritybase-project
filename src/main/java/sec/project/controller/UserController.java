package sec.project.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.User;
import sec.project.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() throws Exception {
        System.out.println("Initializing UserController...");
        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");

        RunScript.execute(connection, new FileReader("database-schema.sql"));
        RunScript.execute(connection, new FileReader("database-import.sql"));

        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM User");

        while (resultSet.next()) {
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            System.out.println(username + " " + password);
        }

        resultSet.close();
        connection.close();
        System.out.println("Initialization completed.");
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm() {
        return "register";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String listUsers(Model model) throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM User");

        ArrayList<User> users = new ArrayList<>();

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("username");
            String pass = resultSet.getString("password");

            System.out.println(id + " yeah");
            User user = new User(name, pass);
            user.setId(Long.parseLong(id));
            users.add(user);
        }

        resultSet.close();
        connection.close();

        model.addAttribute("users", users);
        return "users";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String createNewUser(@RequestParam String username, @RequestParam String password) throws SQLException {
        System.out.println("Creating new user...");

        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");

        String kysely = "INSERT INTO USER (username,password) VALUES ('" + username + "','" + password + "');";

        connection.createStatement().execute(kysely);

        System.out.println("User saved to database.");

        connection.close();
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password, Model model) throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM User");

        while (resultSet.next()) {
            String name = resultSet.getString("username");
            String pass = resultSet.getString("password");
            if (name.equals(username) && pass.equals(password)) {
                System.out.println("Correct username and password!");
                model.addAttribute("legitLogin", true);
                model.addAttribute("username", username);
                if (username.equals("admin")) {
                    model.addAttribute("admin", true);
                } else {
                    model.addAttribute("admin", false);
                }

                return "todoform";
            }
        }

        resultSet.close();
        connection.close();

        System.out.println("Incorrect username or password...");
        return "loginfailed";

    }

    @RequestMapping(value = "*", method = RequestMethod.GET)
    public String redirect() {
        return "redirect:/register";
    }

    @RequestMapping(value = "/adminpage", method = RequestMethod.GET)
    public String adminpage() {
        return "adminpage";
    }

    @RequestMapping(value = "/cleardatabase", method = RequestMethod.POST)
    public String clearDatabase() throws SQLException, FileNotFoundException {

        System.out.println("Clearing database...");
        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");

        RunScript.execute(connection, new FileReader("database-schema.sql")); //Luodaan tyhjät taulut.

        connection.close();
        System.out.println("All done..");

        return "databasecleared";
    }
    
    @RequestMapping(value ="/logout", method = RequestMethod.GET)
    public String logout(){
        return "logout";
    }
}
