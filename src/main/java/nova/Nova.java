package nova;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

/**
 * Represents Nova, a command-line task list assistant with a sarcastic,
 * theatrical personality. Nova manages a list of tasks, events and deadlines
 * that can be added, marked, unmarked, deleted, and listed through simple
 * text commands entered on the command line.
 */
public class Nova {

    public static final String DIVIDER =
            "\t____________________________________________________________";
    private static final String FILE_PATH = "./data/nova.txt";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";
    private static final Random RANDOM = new Random();

    /**
     * Prints Nova's ASCII art logo to the console in cyan.
     */
    public static void printLogo() {
        String logo = "\n"
                + "ooooo      ooo  .oooooo.  oooooo     oooo      .o.      \n"
                + "`888b.     `8' d8P'  `Y8b  `888.     .8'      .888.     \n"
                + " 8 `88b.    8 888      888  `888.   .8'      .8\"888.    \n"
                + " 8   `88b.  8 888      888   `888. .8'      .8' `888.   \n"
                + " 8     `88b.8 888      888    `888.8'      .88ooo8888   \n"
                + " 8       `888 `88b    d88'     `888'      .8'     `888. \n"
                + "o8o        `8  `Y8bood8P'       `8'      o88o     o8888o\n";

        System.out.println(CYAN + logo + RESET);
    }

    /**
     * Prints a randomly chosen, sarcastic success message to the console.
     */
    public static void success() {
        String[] phrases = {
                "Flawless, as expected of me.",
                "Success. Try to contain your amazement.",
                "There. Was that so hard?",
                "Another triumph. You're welcome.",
                "Perfectly executed, naturally."
        };

        int index = RANDOM.nextInt(phrases.length);
        System.out.println("\t" + phrases[index]);
    }

    /**
     * Prints a randomly chosen greeting to the console when Nova starts.
     */
    public static void greet() {
        String[] greetings = {
                "Ah, look who seeks my counsel once again. How... predictable. "
                        + "What is it this time?",
                "You return. I confess, I did wonder if you'd have the nerve.",
                "Well, well. Look who requires my particular brand of brilliance. "
                        + "Tell me then, what are we doing today?",
                "Ah, you've returned... how disappointing. Very well, what do you want?",
                "Oh great one, how may I assist you on this dreadful occasion?",
                "....And here I'd hoped I wouldn't see you for a time yet. "
                        + "Alright, let's keep this snappy- what do you want?"
        };

        int index = RANDOM.nextInt(greetings.length);

        separate();
        System.out.println("\t" + greetings[index]);
        separate();
    }

    private static void separate() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints a randomly chosen farewell message to the console when Nova exits.
     */
    public static void farewell() {
        String[] farewells = {
                "Leaving so soon? I suppose even amateurs need their rest.",
                "Farewell, then. Try not to miss me too terribly.",
                "Ah, fleeing already? Coward. Go on, then.",
                "Until next time. Do try to bring a better question with you.",
                "Leaving? Very well. I have far grander things to attend to anyway.",
                "Goodbye. I'll be here, plotting, as always."
        };

        int index = RANDOM.nextInt(farewells.length);

        separate();
        System.out.println("\t" + farewells[index]);
        separate();
    }

    /**
     * Prints confirmation that a task was added, followed by the running total.
     */
    private static void printAdded(List<Task> tasks, Task task) {
        separate();
        System.out.println("\tI've added this to your ever-growing pile:");
        System.out.println("\t  " + task);
        System.out.println("\tYou now have " + tasks.size()
                + " tasks. Do try to keep up.");
        separate();
    }

    /**
     * Runs Nova's command loop, reading commands from standard input until
     * the user enters "bye".
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        separate();
        printLogo();
        greet();

        Storage storage = new Storage(FILE_PATH);
        List<Task> tasks = storage.load();
        boolean isRunning = true;

        while (isRunning) {
            String input = scanner.nextLine().trim();
            isRunning = handleCommand(input, tasks);
            storage.save(tasks);
        }

        farewell();
    }

    /**
     * Processes a single line of user input: parses the command, dispatches
     * to the appropriate action, and reports errors in Nova's voice.
     */
    private static boolean handleCommand(String input, List<Task> tasks) {
        String command = input.toLowerCase();

        try {
            if (input.equalsIgnoreCase("bye")) {
                return false;
            } else if (input.equalsIgnoreCase("list")) {
                handleList(tasks);
            } else if (command.startsWith("unmark")) {
                handleUnmark(input, tasks);
            } else if (command.startsWith("mark")) {
                handleMark(input, tasks);
            } else if (command.startsWith("delete")) {
                handleDelete(input, tasks);
            } else if (command.startsWith("todo")) {
                handleTodo(input, tasks);
            } else if (command.startsWith("deadline")) {
                handleDeadline(input, tasks);
            } else if (command.startsWith("event")) {
                handleEvent(input, tasks);
            } else if (!input.isEmpty()) {
                throw new NovaException(
                        "I don't recognize that command. Even I have my limits.");
            }
        } catch (NovaException e) {
            separate();
            System.out.println("\t" + e.getMessage());
            separate();
        }

        return true;
    }

    /**
     * Prints all tasks currently in the task list.
     */
    private static void handleList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("\tEmpty. Truly groundbreaking work you've done here. "
                    + "You need to add something in before "
                    + "I can list it out for you, genius.");
        } else {
            separate();

            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("\t" + (i + 1) + "." + tasks.get(i));
            }

            separate();
        }
    }

    /**
     * Marks a task as not done based on the index provided by the user.
     */
    private static void handleUnmark(String input, List<Task> tasks)
            throws NovaException {
        int index = getIndex(input, tasks.size());
        Task task = tasks.get(index);

        if (task.isDone()) {
            task.markAsNotDone();
            System.out.println("\tVery well, your task is unmarked.");
            System.out.println("\t" + (index + 1) + "." + task);
        } else {
            System.out.println("\tWell, isn't that embarrassing. "
                    + "This was already unmarked, you moron.");
        }
    }

    /**
     * Marks a task as done based on the index provided by the user.
     */
    private static void handleMark(String input, List<Task> tasks)
            throws NovaException {
        int index = getIndex(input, tasks.size());
        Task task = tasks.get(index);

        if (!task.isDone()) {
            task.markAsDone();
            System.out.println("\tHuh. I'm almost proud. Almost.\n"
                    + "\tYour task has been marked.");
            System.out.println("\t" + (index + 1) + "." + task);
        } else {
            System.out.println("\tOh, you simpleton, this was already marked.");
        }
    }

    /**
     * Deletes a task based on the index provided by the user.
     */
    private static void handleDelete(String input, List<Task> tasks)
            throws NovaException {
        int index = getIndex(input, tasks.size());
        Task removedTask = tasks.remove(index);

        separate();
        System.out.println("\tNoted. I've removed this task:");
        System.out.println("\t  " + removedTask);
        System.out.println("\tNow you have " + tasks.size()
                + " tasks in the list.");
        separate();
    }

    /**
     * Creates and adds a todo task based on the user's input.
     */
    private static void handleTodo(String input, List<Task> tasks)
            throws NovaException {
        String description = input.length() > 4
                ? input.substring(4).trim()
                : "";

        if (description.isEmpty()) {
            throw new NovaException("No description? How very... you.");
        }

        Task task = new Todo(description);
        tasks.add(task);
        printAdded(tasks, task);
    }

    /**
     * Creates and adds a deadline task based on the user's input.
     */
    private static void handleDeadline(String input, List<Task> tasks)
            throws NovaException {
        String remainingInput = input.length() > 8
                ? input.substring(8).trim()
                : "";

        String[] deadlineParts = remainingInput.split("/by", 2);

        if (deadlineParts.length < 2
                || deadlineParts[0].trim().isEmpty()
                || deadlineParts[1].trim().isEmpty()) {
            throw new NovaException(
                    "A deadline needs both a description and a '/by'. "
                            + "Do try again.");
        }

        Task task = new Deadline(
                deadlineParts[0].trim(),
                deadlineParts[1].trim());

        tasks.add(task);
        printAdded(tasks, task);
    }

    /**
     * Creates and adds an event based on the user's input.
     */
    private static void handleEvent(String input, List<Task> tasks)
            throws NovaException {
        String remainingInput = input.length() > 5
                ? input.substring(5).trim()
                : "";

        String[] eventParts = remainingInput.split("/from", 2);

        if (eventParts.length < 2 || eventParts[0].trim().isEmpty()) {
            throw new NovaException(
                    "An event needs a description and a '/from'. Honestly.");
        }

        String[] timeParts = eventParts[1].split("/to", 2);

        if (timeParts.length < 2
                || timeParts[0].trim().isEmpty()
                || timeParts[1].trim().isEmpty()) {
            throw new NovaException(
                    "An event needs a '/to' as well. Do finish your thought.");
        }

        Task task = new Event(
                eventParts[0].trim(),
                timeParts[0].trim(),
                timeParts[1].trim());

        tasks.add(task);
        printAdded(tasks, task);
    }

    /**
     * Parses the index argument from a mark/unmark command and validates it.
     */
    private static int getIndex(String input, int taskCount)
            throws NovaException {
        String[] tokens = input.split("\\s+");

        if (tokens.length < 2) {
            throw new NovaException("A number would help. Try again.");
        }

        int index;

        try {
            index = Integer.parseInt(tokens[1]) - 1;
        } catch (NumberFormatException e) {
            throw new NovaException(
                    "'" + tokens[1] + "' is not a number, last I checked.");
        }

        if (index < 0 || index >= taskCount) {
            throw new NovaException(
                    "Task " + (index + 1)
                            + "? Bold of you to assume that exists. "
                            + "You have " + taskCount + " task(s).");
        }

        return index;
    }
}