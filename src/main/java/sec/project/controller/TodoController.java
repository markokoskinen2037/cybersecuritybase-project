package sec.project.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sec.project.domain.Todo;
import sec.project.repository.TodoRepository;

@Controller
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    //Lisää todo
    @RequestMapping(value = "/todo", method = RequestMethod.POST)
    public String addTodo(@RequestParam String content, Model model) throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
        connection.createStatement().execute("INSERT INTO TODO (content) VALUES ('" + content + "');");

        System.out.println("New todo saved to database!");

        ResultSet results = connection.createStatement().executeQuery("SELECT * FROM TODO");

        ArrayList<Todo> todos = new ArrayList<>();

        while (results.next()) {
            String db_content = results.getString("content");
            Todo todo = new Todo(db_content);
            todo.setId(Long.parseLong(results.getString("id")));
            todos.add(todo);
        }

        model.addAttribute("todos", todos);

        connection.close();
        results.close();

        return "todoform";
    }

    //Listaa todot
    @RequestMapping(value = "/todolist", method = RequestMethod.GET)
    @ResponseBody
    public String todolist() throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");

        ResultSet results = connection.createStatement().executeQuery("SELECT * FROM TODO");

        ArrayList<Todo> todos = new ArrayList<>();

        while (results.next()) {
            String db_content = results.getString("content");
            Todo todo = new Todo(db_content);
            todo.setId(Long.parseLong(results.getString("id")));
            todos.add(todo);
        }

        String site = "";
        site += "<a style='text-decoration: none;color: black;background-color: antiquewhite;border: 1px solid;padding: 5px;border-radius: 2px;' href='/todo'>Go back</a>";

        site += " <a style='float:right;text-decoration: none;color: black;background-color: antiquewhite;border: 1px solid;padding: 5px;border-radius: 2px;' href='/leave'>Logout</a>";

        site += "<h1 style='background-color: green;width: 50%;color: white;padding: 5px;text-decoration: underline;'>Todolist:</h1>";
        for (Todo todo : todos) {
            site += "<p>" + todo.getContent() + "</p>";
        }

        connection.close();
        results.close();

        return site;

    }

    //Näytä todo-lomake
    @RequestMapping(value = "/todo", method = RequestMethod.GET)
    public String todoForm() {
        return "todoform";
    }
}
