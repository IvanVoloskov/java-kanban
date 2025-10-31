import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        System.out.println("Поехали!");
        // Создаём задачи
        Task task1 = new Task("Купить продукты", "Купить молоко, хлеб");
        Task task2 = new Task("Почитать книгу", "Прочитать главы 3 и 4");
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаём эпик с двумя подзадачами
        Epic epic1 = new Epic();
        epic1.setTitle("Организовать праздник");
        manager.createEpic(epic1);

        SubTask sub1 = new SubTask();
        sub1.setTitle("Пригласить гостей");
        sub1.setEpicId(epic1.getId());
        sub1.setStatus(Status.NEW);
        manager.createSubTask(sub1);

        SubTask sub2 = new SubTask();
        sub2.setTitle("Заказать торт");
        sub2.setEpicId(epic1.getId());
        sub2.setStatus(Status.NEW);
        manager.createSubTask(sub2);

        // Создаём эпик с одной подзадачей
        Epic epic2 = new Epic();
        epic2.setTitle("Переезд");
        manager.createEpic(epic2);

        SubTask sub3 = new SubTask();
        sub3.setTitle("Упаковать вещи");
        sub3.setEpicId(epic2.getId());
        sub3.setStatus(Status.NEW);
        manager.createSubTask(sub3);

        // Выводим списки
        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());

        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());

        System.out.println("Все подзадачи:");
        System.out.println(manager.getAllSubTasks());

        // Обновляем статус подзадачи и смотрим, как меняется статус эпика
        sub1.setStatus(Status.DONE);
        manager.updateSubTask(sub1);

        System.out.println("Эпик после обновления статуса подзадачи:");
        System.out.println(manager.getEpicForId(epic1.getId()));

        // Удаляем задачу и эпик
        manager.removeTaskById(task2.getId());
        manager.removeEpicById(epic2.getId());

        System.out.println("Все задачи после удаления:");
        System.out.println(manager.getAllTasks());

        System.out.println("Все эпики после удаления:");
        System.out.println(manager.getAllEpics());

        System.out.println("Все подзадачи после удаления:");
        System.out.println(manager.getAllSubTasks());
    }
}
