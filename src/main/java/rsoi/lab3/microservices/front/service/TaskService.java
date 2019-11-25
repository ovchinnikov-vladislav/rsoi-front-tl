package rsoi.lab3.microservices.front.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rsoi.lab3.microservices.front.entity.Task;
import rsoi.lab3.microservices.front.model.TaskPage;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class TaskService {

    @Autowired
    private RestTemplate restTemplate;

    public Task findById(Long id) {
        return restTemplate.getForObject("http://localhost:8080/gate/tasks/{id}", Task.class, id);
    }

    public Task findByUserIdAndTaskId(Long idUser, Long idTask) {
        return restTemplate.getForObject("http://localhost:8080/gate/users/{idUser}/tasks/{idTask}", Task.class, idUser, idTask);
    }

    public TaskPage findAll(Integer page, Integer size) {
        return restTemplate.getForObject("http://localhost:8080/gate/tasks?page={page}&size={size}", TaskPage.class, page, size);
    }

    public TaskPage findByUserId(Long id, Integer page, Integer size) {
        return restTemplate.getForObject("http://localhost:8080/gate/users/{id}/tasks?page={page}&size={size}", TaskPage.class, id, page, size);
    }

    public Task create(Long idUser, Task task) {
        return restTemplate.postForObject("http://localhost:8080/gate/users/{id}/tasks", task, Task.class, idUser);
    }

    public void update(Long idUser, Long idTask, Task task) {
        restTemplate.put("http://localhost:8080/gate/users/{idUser}/tasks/{idTask}", task, idUser, idTask);
    }

    public void delete(Long idUser, Long idTask) {
        Task task = restTemplate.getForObject("http://localhost:8080/gate/users/{idUser}/tasks/{idTask}", Task.class, idUser, idTask);
        if (task != null && task.getIdTask().longValue() == idTask && task.getIdUser().longValue() == idUser) {
            restTemplate.delete("http://localhost:8080/gate/users/{idUser}/tasks/{idTask}", idUser, idTask);
            try {
                Files.deleteIfExists(Paths.get(task.getImage()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
