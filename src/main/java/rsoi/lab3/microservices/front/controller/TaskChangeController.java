package rsoi.lab3.microservices.front.controller;

import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import rsoi.lab3.microservices.front.entity.Task;
import rsoi.lab3.microservices.front.entity.Test;
import rsoi.lab3.microservices.front.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@Controller
public class TaskChangeController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/user/{id}/task")
    public ModelAndView tasksByUser(Model model, HttpServletRequest request, @PathVariable Long id) {
        request.getSession().setAttribute("resultTest", null);
        User u = (User) request.getSession().getAttribute("user");
        request.getSession().setAttribute("task", null);
        if (u == null) {
            u = new User();
        } else if (u.getIdUser().longValue() != id) {
            u = new User();
        } else {
            Task[] tasks = restTemplate.getForObject(String.format("http://localhost:8080/gate/users/%d/tasks", id), Task[].class);
            model.addAttribute("tasks_user", tasks);
        }
        model.addAttribute("user", u);
        return new ModelAndView("tasks_user");
    }

    @GetMapping(value = "/user/{idUser}/task/{idTask}")
    public ModelAndView taskByUserIdAndTaskId(Model model, HttpServletRequest request, @PathVariable Long idUser, @PathVariable Long idTask) {
        request.getSession().setAttribute("resultTest", null);
        User u = (User) request.getSession().getAttribute("user");
        Task t = new Task();
        t.setTest(new Test());
        if (u == null) {
            u = new User();
        } else if (u.getIdUser().longValue() != idUser) {
            u = new User();
        } else {
            try {
                t = restTemplate.getForObject(String.format("http://localhost:8080/gate/users/%d/tasks/%d", idUser, idTask), Task.class, idUser, idTask);
                if (t != null)
                    request.getSession().setAttribute("image", t.getImage());
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        model.addAttribute("task", t);
        model.addAttribute("user", u);
        return new ModelAndView("task_change");
    }

    @PostMapping(value = "/user/{idUser}/task/{idTask}/upload")
    public String upload(@PathVariable Long idUser, @PathVariable Long idTask, @RequestBody MultipartFile file,
                         Model model, HttpServletRequest request) {
        if (!file.isEmpty()) {
            String directory = System.getProperty("user.dir") + File.separator + "files" + File.separator + "image" + File.separator;
            String name = directory + idTask + ".png";
            try {
                Files.deleteIfExists(Paths.get(name));
                Files.createDirectories(Paths.get(directory));
                Files.copy(file.getInputStream(), Paths.get(name));
                request.getSession().setAttribute("image", idTask + "");
            } catch (Exception e) {
                return String.format("redirect:/user/%d/task/%d", idUser, idTask);
            }
        }
        return String.format("redirect:/user/%d/task/%d", idUser, idTask);
    }

    @PostMapping(value = "/user/{idUser}/task/{idTask}/create")
    public String createTask(@PathVariable Long idUser, @PathVariable Long idTask,
                             @Valid @RequestBody Task task, Model model, HttpServletRequest request) {
        String image = (String) request.getSession().getAttribute("image");
        if (idTask == 0) {
            task.setIdUser(idUser);
            if (image != null)
                task.setImage(image);
            task.setCreateDate(new Date());
            task.getTest().setCreateDate(new Date());
            task = restTemplate.postForObject(String.format("http://localhost:8080/gate/users/%d/tasks", idUser), task, Task.class);
            if (task != null)
                return String.format("redirect:/user/%d/task/%d", task.getIdUser(), task.getIdTask());
        }
        return String.format("redirect:/user/%d/task", idUser);
    }

    @PostMapping(value = "/user/{idUser}/task/{idTask}/update")
    public String updateTask(@PathVariable Long idUser, @PathVariable Long idTask,
                             @Valid @RequestBody Task task, Model model, HttpServletRequest request) {
        String image = (String) request.getSession().getAttribute("image");
        if (image != null)
            task.setImage(image);
        task.setIdUser(idUser);
        task.setCreateDate(new Date());
        task.getTest().setCreateDate(new Date());
        restTemplate.put(String.format("http://localhost:8080/gate/users/%d/tasks/%d", idUser, idTask), task, Task.class);
        return String.format("redirect:/user/%d/task", idUser);
    }

    @PostMapping(value = "/user/{idUser}/task/{idTask}/delete")
    public String deleteTask(@PathVariable Long idUser, @PathVariable Long idTask,
                             Model model, HttpServletRequest request) {
        Task task = restTemplate.getForObject(String.format("http://localhost:8080/gate/users/%d/tasks/%d", idUser, idTask), Task.class);
        if (task != null && task.getIdTask().longValue() == idTask && task.getIdUser().longValue() == idUser) {
            restTemplate.delete(String.format("http://localhost:8080/gate/users/%d/tasks/%d", idUser, idTask), Task.class);
            try {
                Files.deleteIfExists(Paths.get(task.getImage()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return String.format("redirect:/user/%d/task", idUser);
    }

}