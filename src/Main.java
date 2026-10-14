import java.util.*;

public class Main {

    // Shared business logic manager instance (Member 2)
    private static InsuranceManager manager = new InsuranceManager();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int ch = 0;

        do {
            System.out.println("\n=============================================");
            System.out.println("      INSURANCE POLICY MANAGEMENT SYSTEM     ");
            System.out.println("=============================================");
            System.out.println("1. Customer Menu");
            System.out.println("2. Admin Menu");
            System.out.println("3. Exit");
            ch = readInt(sc, "Select role/option: ");

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
                    System.out.println("Invalid choice. Please select 1, 2, or 3.");
                    break;
            }
        } while (ch != 3);

        sc.close();
    }

    // =========================================================================
    // CUSTOMER MENU IMPLEMENTATION
    // =========================================================================
    private static void customerMenu(Scanner sc) {
        int ch = 0;
        do {
            System.out.println("\n--- CUSTOMER MENU ---");
            System.out.println("1. Customer Registration & Profile");
            System.out.println("2. View Insurance Plans");
            System.out.println("3. Purchase Policy");
            System.out.println("4. Manage Purchased Policies (View, Renew, Cancel)");
            System.out.println("5. Payments");
            System.out.println("6. Claims");
            System.out.println("7. Back to Main Menu");
            ch = readInt(sc, "Enter your choice: ");

            switch (ch) {
                case 1:
                    handleCustomerProfile(sc);
                    break;
                case 2:
                    handleViewPlans(sc);
                    break;
                case 3:
                    handlePurchasePolicy(sc);
                    break;
                case 4:
                    handleManagePurchasedPolicies(sc);
                    break;
                case 5:
                    handlePayments(sc);
                    break;
                case 6:
                    handleClaims(sc);
                    break;
                case 7:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                    break;
            }
        } while (ch != 7);
    }

    private static void handleCustomerProfile(Scanner sc) {
        System.out.println("\n[Customer Registration & Profile]");
        System.out.println("1. Register New Customer");
        System.out.println("2. View Existing Profile by Customer ID");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            System.out.print("Enter full name: ");
            String name = sc.nextLine().trim();
            while (name.isEmpty()) {
                System.out.print("Name cannot be empty. Please enter full name: ");
                name = sc.nextLine().trim();
            }

            int age = readAge(sc, "Enter age: ");
            String phone = readPhone(sc, "Enter 10-digit phone number: ");

            System.out.print("Enter address: ");
            String address = sc.nextLine().trim();
            while (address.isEmpty()) {
                System.out.print("Address cannot be empty. Please enter address: ");
                address = sc.nextLine().trim();
            }

            Customer c = manager.registerCustomer(name, age, phone, address);
            if (c == null) {
                System.out.println("Registration could not be completed. Please verify your inputs.");
            }
        } else if (sub == 2) {
            System.out.print("Enter Customer ID (e.g. C101): ");
            String custId = sc.nextLine().trim();
            manager.displayCustomerProfile(custId);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleViewPlans(Scanner sc) {
        System.out.println("\n[View Insurance Plans]");
        System.out.println("1. View All Available Plans");
        System.out.println("2. Browse Plans by Type (Health, Life, Vehicle, Travel)");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            manager.displayAllPlans();
        } else if (sub == 2) {
            System.out.println("Select Plan Type: 1. Health  2. Life  3. Vehicle  4. Travel");
            int typeChoice = readInt(sc, "Select type (1-4): ");
            switch (typeChoice) {
                case 1:
                    manager.displayPlansByType("Health");
                    break;
                case 2:
                    manager.displayPlansByType("Life");
                    break;
                case 3:
                    manager.displayPlansByType("Vehicle");
                    break;
                case 4:
                    manager.displayPlansByType("Travel");
                    break;
                default:
                    System.out.println("Invalid type choice.");
                    break;
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handlePurchasePolicy(Scanner sc) {
        System.out.println("\n[Purchase Insurance Policy]");
        System.out.print("Enter Customer ID: ");
        String custId = sc.nextLine().trim();

        Customer customer = manager.findCustomerById(custId);
        if (customer == null) {
            System.out.println("Customer not found. Please register first or verify Customer ID.");
            return;
        }

        manager.displayAllPlans();
        System.out.print("Enter Plan ID you wish to purchase (e.g. HP01, LP01): ");
        String planId = sc.nextLine().trim();

        InsurancePlan plan = manager.findPlanById(planId);
        if (plan == null) {
            System.out.println("Plan not found. Purchase cancelled.");
            return;
        }

        // Preview premium calculated using Member 1's method
        double estimatedPremium = plan.calculatePremium(customer);
        System.out.println("\n--- PREMIUM ESTIMATE ---");
        System.out.println("Customer: " + customer.getName() + " (Age: " + customer.getAge() + ")");
        System.out.println("Plan    : " + plan.getPlanName());
        System.out.println("Base Premium      : Rs " + plan.getBasePremium());
        System.out.println("Calculated Premium: Rs " + estimatedPremium);
        System.out.print("Do you want to confirm this purchase? (Y/N): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            manager.purchasePolicy(custId, planId);
        } else {
            System.out.println("Purchase cancelled by customer.");
        }
    }

    private static void handleManagePurchasedPolicies(Scanner sc) {
        System.out.println("\n[Manage Purchased Policies]");
        System.out.println("1. View My Policies (by Customer ID)");
        System.out.println("2. Renew a Policy");
        System.out.println("3. Cancel a Policy");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            System.out.print("Enter Customer ID: ");
            String custId = sc.nextLine().trim();
            manager.displayPoliciesByCustomer(custId);
        } else if (sub == 2) {
            System.out.print("Enter Policy Number to Renew (e.g. POL1001): ");
            String polNo = sc.nextLine().trim();
            manager.renewPolicy(polNo);
        } else if (sub == 3) {
            System.out.print("Enter Policy Number to Cancel (e.g. POL1001): ");
            String polNo = sc.nextLine().trim();
            System.out.print("Are you sure you want to cancel policy " + polNo + "? (Y/N): ");
            String conf = sc.nextLine().trim();
            if (conf.equalsIgnoreCase("Y")) {
                manager.cancelPolicy(polNo);
            } else {
                System.out.println("Cancellation aborted.");
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handlePayments(Scanner sc) {
        System.out.println("\n[Payments]");
        System.out.println("1. Make Premium Payment for a Policy");
        System.out.println("2. View Payment History for a Policy");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            System.out.print("Enter Policy Number (e.g. POL1001): ");
            String polNo = sc.nextLine().trim();
            PurchasedPolicy pol = manager.findPolicyByNumber(polNo);
            if (pol == null) {
                System.out.println("Policy not found.");
                return;
            }
            double amount = readDouble(sc, "Enter payment amount (Rs): ");
            manager.processPayment(polNo, amount);
        } else if (sub == 2) {
            System.out.print("Enter Policy Number: ");
            String polNo = sc.nextLine().trim();
            manager.displayPaymentsByPolicy(polNo);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleClaims(Scanner sc) {
        System.out.println("\n[Claims]");
        System.out.println("1. Submit a New Claim");
        System.out.println("2. Track My Claims (by Customer ID)");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            System.out.print("Enter your Customer ID: ");
            String custId = sc.nextLine().trim();
            System.out.print("Enter your Policy Number: ");
            String polNo = sc.nextLine().trim();
            System.out.print("Enter claim description / reason: ");
            String desc = sc.nextLine().trim();
            double amount = readDouble(sc, "Enter claim amount (Rs): ");

            manager.submitClaim(custId, polNo, desc, amount);
        } else if (sub == 2) {
            System.out.print("Enter Customer ID: ");
            String custId = sc.nextLine().trim();
            manager.displayClaimsByCustomer(custId);
        } else {
            System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // ADMIN MENU IMPLEMENTATION
    // =========================================================================
    private static void adminMenu(Scanner sc) {
        int ch = 0;
        do {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Policy Management (Plans: View, Add, Update, Remove)");
            System.out.println("2. Customer Management (View All, Search)");
            System.out.println("3. Purchased Policies");
            System.out.println("4. Claim Management (Review, Approve, Reject)");
            System.out.println("5. Dashboard & Analytics");
            System.out.println("6. Back to Main Menu");
            ch = readInt(sc, "Enter your choice: ");

            switch (ch) {
                case 1:
                    handleAdminPlanManagement(sc);
                    break;
                case 2:
                    handleAdminCustomerManagement(sc);
                    break;
                case 3:
                    handleAdminPurchasedPolicies(sc);
                    break;
                case 4:
                    handleAdminClaimManagement(sc);
                    break;
                case 5:
                    manager.displayDashboard();
                    break;
                case 6:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                    break;
            }
        } while (ch != 6);
    }

    private static void handleAdminPlanManagement(Scanner sc) {
        System.out.println("\n[Admin - Policy / Plan Management]");
        System.out.println("1. View All Plans");
        System.out.println("2. Add New Plan");
        System.out.println("3. Update Plan Details");
        System.out.println("4. Remove a Plan");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            manager.displayAllPlans();
        } else if (sub == 2) {
            System.out.println("Select Plan Type: 1. Health  2. Life  3. Vehicle  4. Travel");
            int type = readInt(sc, "Select type (1-4): ");
            System.out.print("Enter Plan ID (e.g. HP02): ");
            String planId = sc.nextLine().trim();
            System.out.print("Enter Plan Name: ");
            String name = sc.nextLine().trim();
            double coverage = readDouble(sc, "Enter Coverage Amount (Rs): ");
            int duration = readInt(sc, "Enter Duration in Months: ");
            double basePrem = readDouble(sc, "Enter Base Premium (Rs): ");

            InsurancePlan newPlan = null;
            if (type == 1)
                newPlan = new HealthPlan(planId, name, coverage, duration, basePrem);
            else if (type == 2)
                newPlan = new LifePlan(planId, name, coverage, duration, basePrem);
            else if (type == 3)
                newPlan = new VehiclePlan(planId, name, coverage, duration, basePrem);
            else if (type == 4)
                newPlan = new TravelPlan(planId, name, coverage, duration, basePrem);
            else {
                System.out.println("Invalid plan type. Add cancelled.");
                return;
            }

            if (manager.addPlan(newPlan)) {
                System.out.println("New " + manager.getPlanType(newPlan) + " plan added successfully!");
            }
        } else if (sub == 3) {
            manager.displayAllPlans();
            System.out.print("Enter Plan ID to update: ");
            String planId = sc.nextLine().trim();
            InsurancePlan p = manager.findPlanById(planId);
            if (p == null) {
                System.out.println("Plan not found.");
                return;
            }
            System.out.print("Enter new plan name (or press Enter to keep current): ");
            String newName = sc.nextLine().trim();
            double newCov = readDouble(sc, "Enter new coverage amount (or 0 to keep current): ");
            int newDur = readInt(sc, "Enter new duration in months (or 0 to keep current): ");
            double newPrem = readDouble(sc, "Enter new base premium (or 0 to keep current): ");

            manager.updatePlan(planId, newName, newCov, newDur, newPrem);
        } else if (sub == 4) {
            manager.displayAllPlans();
            System.out.print("Enter Plan ID to remove: ");
            String planId = sc.nextLine().trim();
            manager.removePlan(planId);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleAdminCustomerManagement(Scanner sc) {
        System.out.println("\n[Admin - Customer Management]");
        System.out.println("1. View All Registered Customers");
        System.out.println("2. Search Customer by ID");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            manager.displayAllCustomers();
        } else if (sub == 2) {
            System.out.print("Enter Customer ID: ");
            String custId = sc.nextLine().trim();
            manager.displayCustomerProfile(custId);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleAdminPurchasedPolicies(Scanner sc) {
        System.out.println("\n[Admin - Purchased Policies]");
        System.out.println("1. View All Purchased Policies");
        System.out.println("2. Search Policy by Policy Number");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            manager.displayAllPurchasedPolicies();
        } else if (sub == 2) {
            System.out.print("Enter Policy Number: ");
            String polNo = sc.nextLine().trim();
            PurchasedPolicy p = manager.findPolicyByNumber(polNo);
            if (p != null) {
                System.out.println("\nPolicy Details:");
                System.out.println("Policy Number : " + p.getPolicyNumber());
                System.out.println("Customer ID   : " + p.getCustomerId());
                System.out.println("Plan ID       : " + p.getPlanId());
                System.out.println("Status        : " + p.getStatus());
            } else {
                System.out.println("Policy not found.");
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleAdminClaimManagement(Scanner sc) {
        System.out.println("\n[Admin - Claim Management]");
        System.out.println("1. View All Claims");
        System.out.println("2. Approve a Claim");
        System.out.println("3. Reject a Claim");
        int sub = readInt(sc, "Choose option: ");

        if (sub == 1) {
            manager.displayAllClaims();
        } else if (sub == 2) {
            System.out.print("Enter Claim ID to Approve (e.g. CLM701): ");
            String claimId = sc.nextLine().trim();
            manager.approveClaim(claimId);
        } else if (sub == 3) {
            System.out.print("Enter Claim ID to Reject (e.g. CLM701): ");
            String claimId = sc.nextLine().trim();
            manager.rejectClaim(claimId);
        } else {
            System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // ROBUST INPUT READERS WITH EXCEPTION HANDLING
    // =========================================================================

    /**
     * Reads integer input safely, preventing InputMismatchException crashes.
     */
    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = sc.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Input Error: Please enter a valid whole number.");
            }
        }
    }

    /**
     * Reads double input safely, preventing InputMismatchException crashes.
     */
    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = sc.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Input Error: Please enter a valid decimal number.");
            }
        }
    }

    /**
     * Reads and strictly validates age input.
     * Rejects negative numbers and zero with an immediate error message and re-prompts.
     */
    private static int readAge(Scanner sc, String prompt) {
        while (true) {
            int age = readInt(sc, prompt);
            if (age <= 0) {
                System.out.println("Validation Error: Age cannot be negative or zero. Please enter a valid positive age (1-120).");
            } else if (age > 120) {
                System.out.println("Validation Error: Age cannot exceed 120. Please enter a realistic age (1-120).");
            } else {
                return age;
            }
        }
    }

    /**
     * Reads and strictly validates phone number input.
     * Rejects phone numbers less than 10 digits with an immediate error message and re-prompts.
     */
    private static String readPhone(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String phone = sc.nextLine().trim();
            if (phone.length() < 10) {
                System.out.println("Validation Error: Phone number must be at least 10 digits.");
            } else {
                return phone;
            }
        }
    }
}
