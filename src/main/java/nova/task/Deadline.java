package nova.task;
/**
 * Represents a task that must be completed by a specific date or time.
 */

public class Deadline extends Task {
    protected String by;

    /**
    * Creates a new Deadline with the given description and due date.
    */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }
    /**
    * Returns the type icon for a Deadline task.
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the display representation of this deadline, including its due date.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}