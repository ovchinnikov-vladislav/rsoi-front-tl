package rsoi.lab3.microservices.front.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import rsoi.lab3.microservices.front.entity.Result;
import rsoi.lab3.microservices.front.entity.Task;
import rsoi.lab3.microservices.front.entity.Test;
import rsoi.lab3.microservices.front.entity.User;
import rsoi.lab3.microservices.front.model.ExecuteTaskRequest;
import rsoi.lab3.microservices.front.model.RequestUser;
import rsoi.lab3.microservices.front.model.ResultTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/")
    public ModelAndView index(Model model, HttpSession session) {
        session.setAttribute("resultTest", null);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
        }
        model.addAttribute("user", user);
        Task[] tasks = restTemplate.getForObject("http://localhost:8080/gate/tasks", Task[].class);
        model.addAttribute("tasks", tasks);
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
