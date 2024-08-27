import java.io.IOException;
import java.util.Scanner;

public class Janet {
    private TaskList tasks;
    private final Ui ui;
    private final Storage storage;

    Janet(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.textFileToArrayList());
        } catch (JanetException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    public void run() throws IOException {
        ui.showWelcome();
        Scanner input = new Scanner(System.in);
        boolean hasTypedBye = false;

        while (input.hasNext()) {
            String userCommand = input.nextLine();
            String[] commandDetails = Parser.getCommandDetails(userCommand);
            try {
                // validateCommand and checkInaccurateCommand will throw out a JanetException
                Parser.validateCommand(commandDetails, tasks.getNumberOfTasks());
                Parser.checkInaccurateCommand(commandDetails);

                CommandType commandType = Parser.getCommand(commandDetails);
                switch (commandType) {
                case BYE:
                    ui.exitMessage();
                    hasTypedBye = true;
                    break;
                case LIST:
                    ui.showTasks(tasks);
                    break;
                case MARK:
                    String markedResult = tasks.markAsDone(Integer.parseInt(commandDetails[1]));
                    ui.showMarkedMessage(markedResult, tasks.getTask(Integer.parseInt(commandDetails[1]) - 1));
                    break;
                case UNMARK:
                    String unmarkResult = tasks.unmark(Integer.parseInt(commandDetails[1]));
                    ui.showUnmarkedMessage(unmarkResult, tasks.getTask(Integer.parseInt(commandDetails[1]) - 1));
                    break;
                case DELETE:
                    ui.showDeleteTaskMessage(tasks.getTask(Integer.parseInt(commandDetails[1]) - 1),
                            tasks.getNumberOfTasks() - 1);
                    tasks.deleteTask(Integer.parseInt(commandDetails[1]));
                    break;
                case TODO:
                    Task todo = new ToDo(userCommand);
                    tasks.addTaskToList(todo);
                    ui.showSuccessfulTaskAddition(todo, tasks.getNumberOfTasks());
                    break;
                case DEADLINE:
                    Task deadline = new Deadline(userCommand);
                    tasks.addTaskToList(deadline);
                    ui.showSuccessfulTaskAddition(deadline, tasks.getNumberOfTasks());
                    break;
                case EVENT:
                    Task event = new Event(userCommand);
                    tasks.addTaskToList(event);
                    ui.showSuccessfulTaskAddition(event, tasks.getNumberOfTasks());
                    break;
                }
                if (hasTypedBye) {
                    break;
                }
            } catch (JanetException e) {
                System.out.println(e.getMessage());
            }
        }
        storage.saveToJanetTextFile(tasks.getListOfTasks());
    }

    public static void main(String[] args) throws IOException {
        new Janet("janet.txt").run();
    }

}
