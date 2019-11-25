package rsoi.lab3.microservices.front.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import rsoi.lab3.microservices.front.entity.Task;
import rsoi.lab3.microservices.front.entity.User;
import rsoi.lab3.microservices.front.model.TaskPage;
import rsoi.lab3.microservices.front.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MainController {

    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/")
    public ModelAndView index(Model model,
                              @RequestParam(value = "page") Optional<Integer> page,
                              @RequestParam(value = "size") Optional<Integer> size, HttpSession session) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(9);
        session.setAttribute("resultTest", null);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
        }
        model.addAttribute("user", user);
        TaskPage tasksPage = taskService.findAll(currentPage, pageSize);
        model.addAttribute("tasksPage", tasksPage);
        return new ModelAndView("index");
    }


    @GetMapping(value = "/image/{idImage}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] getImage(@PathVariable Long idImage, HttpServletRequest request) throws IOException {
        request.getSession().setAttribute("resultTest", null);
        Path path = Paths.get(System.getProperty("user.dir") + File.separator +
                "files" + File.separator + "image" + File.separator + idImage + ".png");
        return Files.readAllBytes(path);
    }
}
