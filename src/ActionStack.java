/**
 * ActionStack.java
 *
 * Implements Task 2 (Member 3): Stack Data Structure
 * Uses a fixed-size String array to track recent administrative operations.
 * Demonstrates:
 * - push(), pop(), peek(), isEmpty(), isFull(), display()
 * - Safe handling of Stack Overflow and Stack Underflow
 * - A single reversible operation: Undo Last Added Plan
 */
public class ActionStack {

    private static final int DEFAULT_CAPACITY = 20;
    private String[] stack;
    private int top;
    private int capacity;

    // Default constructor
    public ActionStack() {
        this(DEFAULT_CAPACITY);
    }

    // Parameterized constructor
    public ActionStack(int capacity) {
        if (capacity <= 0) {
            this.capacity = DEFAULT_CAPACITY;
        } else {
            this.capacity = capacity;
        }
        this.stack = new String[this.capacity];
        this.top = -1;
    }

    /**
     * Pushes a new action description onto the stack.
     * Handles stack overflow safely without crashing.
     */
    public boolean push(String action) {
        if (isFull()) {
            System.out.println("[Stack Overflow Warning] Action stack is full. Cannot push: \"" + action + "\"");
            return false;
        }
        stack[++top] = action;
        return true;
    }

    /**
     * Pops and returns the top action from the stack.
     * Handles stack underflow safely without crashing.
     */
    public String pop() {
        if (isEmpty()) {
            System.out.println("[Stack Underflow Warning] Action stack is empty. Nothing to pop.");
            return null;
        }
        String action = stack[top];
        stack[top--] = null; // Clear reference
        return action;
    }

    /**
     * Peeks at the top action without removing it.
     */
    public String peek() {
        if (isEmpty()) {
            return null;
        }
        return stack[top];
    }

    /**
     * Checks if the stack is empty.
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * Checks if the stack is full.
     */
    public boolean isFull() {
        return top == capacity - 1;
    }

    /**
     * Displays all actions currently in the stack (LIFO order: newest to oldest).
     */
    public void display() {
        if (isEmpty()) {
            System.out.println("Action stack is currently empty. No recent operations recorded.");
            return;
        }
        System.out.println("\n-------------------------------------------------------------");
        System.out.println("             RECENT ACTIONS LOG (LIFO Stack)                 ");
        System.out.println("-------------------------------------------------------------");
        for (int i = top; i >= 0; i--) {
            int stepNumber = (top - i + 1);
            System.out.println(" [" + stepNumber + "] " + stack[i]);
        }
        System.out.println("-------------------------------------------------------------");
        System.out.println(" Total actions in stack: " + (top + 1) + " / " + capacity);
    }

    public int size() {
        return top + 1;
    }

    public int getCapacity() {
        return capacity;
    }
}
