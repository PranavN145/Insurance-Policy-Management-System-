import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int ch;

        do {
            System.out.println("\n=============================================");
            System.out.println("   INSURANCE POLICY MANAGEMENT SYSTEM   ");
            System.out.println("=============================================");
            System.out.println("1. Customer Menu");
            System.out.println("2. Admin Menu");
            System.out.println("3. Exit");
            System.out.print("Select role/option: ");
            ch = sc.nextInt();

            switch (ch) {
                case 1:
                    customerMenu(sc);
                    break;
                case 2:
                    adminMenu(sc);
                    break;
                case 3:
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (ch != 3);

        sc.close();
    }

    private static void customerMenu(Scanner sc) {
        int ch;
        do {
            System.out.println("\n--- CUSTOMER MENU ---");
            System.out.println("1. Customer Registration");
            System.out.println("2. View Insurance Plans");
            System.out.println("3. Purchase Policy");
            System.out.println("4. Manage Purchased Policies");
            System.out.println("5. Payments");
            System.out.println("6. Claims");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");
            ch = sc.nextInt();

            switch (ch) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (ch != 7);
    }

    private static void adminMenu(Scanner sc) {
        int ch;
        do {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Policy Management");
            System.out.println("2. Customer Management");
            System.out.println("3. Purchased Policies");
            System.out.println("4. Claim Management");
            System.out.println("5. Dashboard");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            ch = sc.nextInt();

            switch (ch) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (ch != 6);
    }
}
