import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Represents Nova, a command-line task list assistant with a sarcastic,
 * theatrical personality. Nova tracks a list of tasks, events and deadlines
 * that can be dated, added, marked, unmarked, and listed via simple text commands
 * entered on the command line.
 */

public class Nova {
    /**
     * Prints Nova's ASCII art logo to the console in cyan.
     */
    
    public static void printLogo() {
        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";

        String logo = "\n"
                + "ooooo      ooo  .oooooo.  oooooo     oooo      .o.      \n"
                + "`888b.     `8' d8P'  `Y8b  `888.     .8'      .888.     \n"
                + " 8 `88b.    8 888      888  `888.   .8'      .8\"888.    \n"
                + " 8   `88b.  8 888      888   `888. .8'      .8' `888.   \n"
                + " 8     `88b.8 888      888    `888.8'      .88ooo8888   \n"
                + " 8       `888 `88b    d88'     `888'      .8'     `888. \n"
                + "o8o        `8  `Y8bood8P'       `8'      o88o     o8888o\n";

        System.out.println(cyan + logo + reset);
    }

    /**
     * Prints a randomly chosen, sarcastic success message to the console.
     */
    public static void success() {
        String[] phrases = {"Flawless, as expected of me.",
                "Success. Try to contain your amazement.",
                "There. Was that so hard?",
                "Another triumph. You're welcome.",
                "Perfectly executed, naturally."};
        Random rand = new Random();
        int idx = rand.nextInt(phrases.length);

        System.out.println("\t" + phrases[idx]);
    }

    /**
     * Prints a randomly chosen greeting to the console when Nova starts.
     */
    public static void greet() {
        String[] greetings = {"Ah, look who seeks my counsel once again. How... predictable. What is it this time?",
                "You return. I confess, I did wonder if you'd have the nerve.",
                "Well, well. Look who requires my particular brand of brilliance. "
                        + "Tell me then, what are we doing today?",
                "Ah, you've returned... how disappointing. Very well, what do you want?",
                "Oh great one, how may I assist you on this dreadful occasion?",
                "....And here I'd hoped I wouldn't see you for a time yet. "
                        + "Alright, let's keep this snappy- what do you want?"};

        Random rand = new Random();
        int idx = rand.nextInt(greetings.length);

        separate();
        System.out.println("\t" + greetings[idx]);
        separate();
    }

    private static void separate() {
        System.out.println("\t____________________________________________________________");
    }

    /**
     * Prints a randomly chosen farewell message to the console when Nova exits.
     */
    public static void farewell() {
        String[] farewells = {"Leaving so soon? I suppose even amateurs need their rest.",
                "Farewell, then. Try not to miss me too terribly.",
                "Ah, fleeing already? Coward. Go on, then.",
                "Until next time. Do try to bring a better question with you.",
                "Leaving? Very well. I have far grander things to attend to anyway.",
                "Goodbye. I'll be here, plotting, as always."};

        Random rand = new Random();
        int idx = rand.nextInt(farewells.length);

        separate();
        System.out.println("\t" + farewells[idx]);
        separate();
    }

    /**
     * Prints confirmation that a task was added, followed by the running total.
     */
    private static void printAdded(List<Task> tasks, Task task) {
        separate();
        System.out.println("\tI've added this to your ever-growing pile:");
        System.out.println("\t  " + task);
        System.out.println("\tYou now have " + tasks.size() + " tasks. Do try to keep up.");
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

        List<Task> tasks = new ArrayList<>();

        while (true) {
            String line = scanner.nextLine();
            String trimmed = line.trim();

            try {
                if (trimmed.equalsIgnoreCase("bye")) {
                    break;
                }

                else if (trimmed.equalsIgnoreCase("list")) {
                    if (tasks.isEmpty()) {
                        System.out.println("\tEmpty. Truly groundbreaking work you've done here. "
                                + "You need to add something in before "
                                + "I can list it out for you, genius.");}

                    else {
                        separate();
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println("\t" + (i + 1) + "." + tasks.get(i));
                        }
                        separate();
                    }
                }

                else if (trimmed.toLowerCase().startsWith("unmark")) {
                    int idx = getIndex(trimmed, tasks.size());
                    Task task = tasks.get(idx);

                    if (task.isDone()) {
                        task.markAsNotDone();
                        System.out.println("\tVery well, your task is unmarked.");
                        System.out.println("\t" + (idx + 1) + "." + task);
                    }

                    else {
                        System.out.println("\tWell, isn't that embarrassing. This was already unmarked, you moron.");
                    }
                }

                else if (trimmed.toLowerCase().startsWith("mark")) {
                    int idx = getIndex(trimmed, tasks.size());
                    Task task = tasks.get(idx);
                    if (!task.isDone()) {
                        task.markAsDone();
                        System.out.println("\tHuh. I'm almost proud. Almost.\n\tYour task has been marked.");
                        System.out.println("\t" + (idx + 1) + "." + task);
                    }

                    else {
                        System.out.println("\tOh, you simpleton, this was already marked.");
                    }
                }

                else if (trimmed.toLowerCase().startsWith("todo")) {
                    String desc = trimmed.length() > 4 ? trimmed.substring(4).trim() : "";

                    if (desc.isEmpty()) {
                        throw new NovaException("No description? How very... you.");
                    }
                    Task task = new Todo(desc);
                    tasks.add(task);
                    printAdded(tasks, task);
                }

                else if (trimmed.toLowerCase().startsWith("deadline")) {
                    String rest = trimmed.length() > 8 ? trimmed.substring(8).trim() : "";
                    String[] parts = rest.split("/by", 2);

                    if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                        throw new NovaException("A deadline needs both a description and a '/by'. Do try again.");
                    }

                    Task task = new Deadline(parts[0].trim(), parts[1].trim());
                    tasks.add(task);
                    printAdded(tasks, task);
                }

                else if (trimmed.toLowerCase().startsWith("event")) {
                    String rest = trimmed.length() > 5 ? trimmed.substring(5).trim() : "";
                    String[] fromSplit = rest.split("/from", 2);
                    if (fromSplit.length < 2 || fromSplit[0].trim().isEmpty()) {
                        throw new NovaException("An event needs a description and a '/from'. Honestly.");
                    }

                    String[] toSplit = fromSplit[1].split("/to", 2);
                    if (toSplit.length < 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
                        throw new NovaException("An event needs a '/to' as well. Do finish your thought.");
                    }

                    Task task = new Event(fromSplit[0].trim(), toSplit[0].trim(), toSplit[1].trim());
                    tasks.add(task);
                    printAdded(tasks, task);
                }

                else if (!trimmed.isEmpty()) {
                    throw new NovaException("I don't recognize that command. Even I have my limits.");
                }

            }

            catch (NovaException e) {
                separate();
                System.out.println("\t" + e.getMessage());
                separate();
            }
        }
        farewell();
    }

    private static int getIndex(String trimmed, int taskCount) throws NovaException {
        String[] tokens = trimmed.split("\\s+");
        if (tokens.length < 2) {
            throw new NovaException("A number would help. Try again.");
        }

        int idx;
        try {
            idx = Integer.parseInt(tokens[1]) - 1;
        } catch (NumberFormatException e) {
            throw new NovaException("'" + tokens[1] + "' is not a number, last I checked.");
        }

        if (idx < 0 || idx >= taskCount) {
            throw new NovaException("Task " + (idx + 1) + "? Bold of you to assume that exists. "
                    + "You have " + taskCount + " task(s).");
        }

        return idx;
    }
}