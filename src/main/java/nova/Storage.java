package nova;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import nova.task.Task;
import nova.task.Todo;
import nova.task.Deadline;
import nova.task.Event;

/**
 * Handles reading and writing the task list to a file on disk, so tasks
 * persist between runs of Nova.
 */
public class Storage {

    private final String filePath;

    /**
     * Creates a Storage pointing at the given file path.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from disk. Returns an empty list if the file or its
     * containing folder doesn't exist yet.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                Task task = parseLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("\tCouldn't read your saved tasks. Starting fresh, I suppose.");
        }

        return tasks;
    }

    /**
     * Saves the given list of tasks to disk, creating the folder and file
     * if they don't already exist.
     */
    public void save(List<Task> tasks) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(task.toSaveString() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("\tI couldn't save your tasks. How inconvenient.");
        }
    }

    /**
     * Parses a single saved line back into a Task. Returns null if the
     * line is malformed.
     */
    private Task parseLine(String line) {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        switch (type) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                if (parts.length < 4) {
                    return null;
                }
                task = new Deadline(description, parts[3]);
                break;
            case "E":
                if (parts.length < 5) {
                    return null;
                }
                task = new Event(description, parts[3], parts[4]);
                break;
            default:
                return null;
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}