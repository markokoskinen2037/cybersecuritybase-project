package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Todo;
import sec.project.repository.TodoRepository;

@Controller
public class TodoController {
    
    @Autowired
    private TodoRepository todoRepository;

    

    @RequestMapping(value = "/todo", method = RequestMethod.POST)
    public String TodoView(@RequestParam String content, Model model) {     
        todoRepository.save(new Todo(content));
        
        System.out.println("SAVING NEW TODO");
        
        model.addAttribute("todos", todoRepository.findAll());
        
        System.out.println(todoRepository.findAll());
        
        return "todolist";
    }
    

}
