package sec.project.controller;

import java.util.List;
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

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm() {
        return "register";
    }
    
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String listUsers(Model model){
        
        if(userRepository.findAll().isEmpty()){
            return "redirect:/register";
        }
        
        
        model.addAttribute("users", userRepository.findAll());
        
        return "users";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String createNewUser(@RequestParam String username, @RequestParam String password) {     
        userRepository.save(new User(username,password));
        
        System.out.println("SAVING NEW USER AND REDIRECTING");
        
        return "redirect:/login";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(){
        return "login";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password, Model model){
        
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if(user.getUsername().equals(username)){
                if(user.getPassword().equals(password)){
                    System.out.println("Correct username and password!");
                    //Laitetaan modeliin adminbitti = true, jos tunnuksen nimi on admin.
                    if(username.equals("admin")){
                        model.addAttribute("admin", true);
                    } else {
                        model.addAttribute("admin", false);
                    }
                    
                    return "todolist";
                }
            }
        }
        
        System.out.println("Incorrect username or password...");
        return "login";
        
    }

}
