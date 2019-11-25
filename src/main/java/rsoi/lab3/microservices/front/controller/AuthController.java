package rsoi.lab3.microservices.front.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import rsoi.lab3.microservices.front.entity.User;
import rsoi.lab3.microservices.front.model.RequestUser;
import rsoi.lab3.microservices.front.model.ResultTest;
import rsoi.lab3.microservices.front.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/log_in", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String logIn(Model model, HttpServletRequest request, @ModelAttribute User user) {
        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            u = authService.check(user);
            if (u != null) {
                model.addAttribute("user", u);
                request.getSession().setAttribute("user", u);
            }
        }
        return "redirect:/";
    }

    @GetMapping(value = "/sign_in")
    public ModelAndView signIn(Model model, HttpServletRequest request) {
        request.getSession().setAttribute("resultTest", null);
        model.addAttribute("user", new User());
        return new ModelAndView("sign_in");
    }

    @PostMapping(value = "/sign_in")
    public String signIn(HttpServletRequest request, @ModelAttribute @Valid User user) {
        User u = authService.create(user);
        return "redirect:/";
    }

    @GetMapping(value = "/exit")
    public String exit(Model model, HttpServletRequest request) {
        request.getSession().setAttribute("resultTest", null);
        model.addAttribute("user", new User());
        request.getSession().invalidate();
        return "redirect:/";
    }

}
