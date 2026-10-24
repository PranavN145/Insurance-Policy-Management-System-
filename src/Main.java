import java.time.LocalDate;
import java.util.Scanner;

/**
 * Main.java
 *
 * Console Interface & Workflow Orchestrator:
 * - Boots system, starts background ExpiryMonitor daemon thread
 * - Loads and persists system state through FileHandler
 * - Customer Module: Registration, Plan Browsing, Purchasing, Payments, Claims
 * - Admin Module: Plans, Customers, Policies, Claims, Dashboard, Undo, Async Reporting
 */
public class Main {

    private static final InsuranceManager manager = new InsuranceManager();
    private static ExpiryMonitor expiryMonitor;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. Load persisted data from data/ files on startup
        System.out.println("Initializing Insurance Policy Management System...");
        FileHandler.loadAll(manager);

        // 2. Start ExpiryMonitor background thread (Architecture §8 & PRD §10.8)
        expiryMonitor = new ExpiryMonitor(manager, 15000);
        expiryMonitor.start();
        System.out.println("[System Info] Background ExpiryMonitor thread started successfully.");

        int ch = 0;
        do {
            System.out.println("\n=============================================");
            System.out.println("      INSURANCE POLICY MANAGEMENT SYSTEM     ");
            System.out.println("=============================================");
            System.out.println("1. Customer Module");
            System.out.println("2. Admin Module");
            System.out.println("3. Exit");
            ch = readInt(sc, "Select module/option (1-3): ");

            switch (ch) {
                case 1:
                    customerMenu(sc);
                    break;
                case 2:
                    adminMenu(sc);
                    break;
                case 3:
                    System.out.println("\nSaving system state to storage files...");
                    FileHandler.saveAll(manager);
                    if (expiryMonitor != null) {
                        expiryMonitor.stopMonitoring();
                    }
                    System.out.println("Exiting application. Thank you for using Insurance Policy Management System!");
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
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. Customer Registration & Profile");
            System.out.println("2. View Available Insurance Plans");
            System.out.println("3. Purchase Insurance Policy");
            System.out.println("4. View and Manage Purchased Policies");
            System.out.println("5. Premium Payment (Simulate Payment)");
            System.out.println("6. Insurance Claims");
            System.out.println("7. Return to Main Menu");
            ch = readInt(sc, "Enter your choice (1-7): ");

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
        System.out.println("2. View Profile by Customer ID");
        System.out.println("3. View My Policies by Customer ID");
        int sub = readInt(sc, "Choose option (1-3): ");

        if (sub == 1) {
            String name = readNonEmptyString(sc, "Enter full name: ");
            int age = readAge(sc, "Enter age (18-120): ");
            String phone = readPhone(sc, "Enter 10-digit phone number: ");
            String email = readEmail(sc, "Enter email address: ");
            String address = readNonEmptyString(sc, "Enter address: ");

            Customer c = manager.registerCustomer(name, age, phone, email, address);
            if (c == null) {
                System.out.println("Registration failed. Please check your inputs.");
            }
        } else if (sub == 2) {
            System.out.print("Enter Customer ID (e.g. C101): ");
            String custId = sc.nextLine().trim();
            manager.displayCustomerProfile(custId);
        } else if (sub == 3) {
            System.out.print("Enter Customer ID (e.g. C101): ");
            String custId = sc.nextLine().trim();
            manager.displayPoliciesByCustomer(custId);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleViewPlans(Scanner sc) {
        System.out.println("\n[View Insurance Plans]");
        System.out.println("1. View Available Plans (Active for Purchase)");
        System.out.println("2. Browse Plans by Type (Health, Life, Vehicle, Travel)");
        System.out.println("3. Search Plans by Name");
        int sub = readInt(sc, "Choose option (1-3): ");

        if (sub == 1) {
            manager.displayAvailablePlansOnly();
        } else if (sub == 2) {
            System.out.println("Select Plan Type: 1. Health  2. Life  3. Vehicle  4. Travel");
            int typeChoice = readInt(sc, "Select type (1-4): ");
            switch (typeChoice) {
                case 1: manager.displayPlansByType("Health"); break;
                case 2: manager.displayPlansByType("Life"); break;
                case 3: manager.displayPlansByType("Vehicle"); break;
                case 4: manager.displayPlansByType("Travel"); break;
                default: System.out.println("Invalid type choice."); break;
            }
        } else if (sub == 3) {
            System.out.print("Enter search keyword in plan name: ");
            String kw = sc.nextLine().trim();
            manager.searchPlans(kw, "ALL");
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
            System.out.println("Customer not found. Please register your profile first.");
            return;
        }

        manager.displayAvailablePlansOnly();
        System.out.print("Enter Plan ID you wish to purchase (e.g. HP01, LP01): ");
        String planId = sc.nextLine().trim();

        InsurancePlan plan = manager.findPlanById(planId);
        if (plan == null) {
            System.out.println("Plan not found. Purchase cancelled.");
            return;
        }

        if (!plan.isAvailable()) {
            System.out.println("Sorry, this plan is no longer available for purchase.");
            return;
        }

        double estimatedPremium = plan.calculatePremium(customer);
        System.out.println("\n--- PURCHASE QUOTE ---");
        System.out.println("Customer            : " + customer.getName() + " (Age: " + customer.getAge() + ")");
        System.out.println("Selected Plan       : " + plan.getPlanName() + " (" + plan.getPlanId() + ")");
        System.out.println("Coverage Amount     : Rs " + plan.getCoverageAmount());
        System.out.println("Duration            : " + plan.getDurationMonths() + " months");
        System.out.println("Calculated Premium  : Rs " + estimatedPremium);
        System.out.print("Do you confirm this purchase? (Y/N): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            PurchasedPolicy policy = manager.purchasePolicy(custId, planId);
            if (policy != null) {
                System.out.print("\nWould you like to simulate premium payment now to activate this policy? (Y/N): ");
                String payNow = sc.nextLine().trim();
                if (payNow.equalsIgnoreCase("Y")) {
                    manager.processPayment(policy.getPolicyNumber(), policy.getPremium());
                } else {
                    System.out.println("You can make the payment later under Menu -> Payments.");
                }
            }
        } else {
            System.out.println("Purchase cancelled by customer.");
        }
    }

    private static void handleManagePurchasedPolicies(Scanner sc) {
        System.out.println("\n[Manage Purchased Policies]");
        System.out.println("1. View My Policies (by Customer ID)");
        System.out.println("2. Renew a Policy");
        System.out.println("3. Cancel a Policy");
        int sub = readInt(sc, "Choose option (1-3): ");

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
        System.out.println("\n[Premium Payments]");
        System.out.println("1. View Pending Premium Dues for My Policies");
        System.out.println("2. Make Simulated Premium Payment");
        System.out.println("3. View Payment History for a Policy");
        int sub = readInt(sc, "Choose option (1-3): ");

        if (sub == 1) {
            System.out.print("Enter Customer ID: ");
            String custId = sc.nextLine().trim();
            manager.displayPayablePolicies(custId);
        } else if (sub == 2) {
            System.out.print("Enter Policy Number (e.g. POL1001): ");
            String polNo = sc.nextLine().trim();
            PurchasedPolicy pol = manager.findPolicyByNumber(polNo);
            if (pol == null) {
                System.out.println("Policy not found.");
                return;
            }
            System.out.println("Policy Premium Due: Rs " + pol.getPremium());
            double amount = readDouble(sc, "Enter payment amount (Rs): ");
            manager.processPayment(polNo, amount);
        } else if (sub == 3) {
            System.out.print("Enter Policy Number: ");
            String polNo = sc.nextLine().trim();
            manager.displayPaymentsByPolicy(polNo);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleClaims(Scanner sc) {
        System.out.println("\n[Insurance Claims]");
        System.out.println("1. Submit a New Claim");
        System.out.println("2. Track My Claims (by Customer ID)");
        System.out.println("3. View Claim Details / Audit");
        int sub = readInt(sc, "Choose option (1-3): ");

        if (sub == 1) {
            System.out.print("Enter your Customer ID: ");
            String custId = sc.nextLine().trim();
            System.out.print("Enter your Policy Number: ");
            String polNo = sc.nextLine().trim();
            String desc = readNonEmptyString(sc, "Enter claim reason / incident description: ");
            double amount = readDouble(sc, "Enter requested claim amount (Rs): ");

            manager.submitClaim(custId, polNo, desc, amount);
        } else if (sub == 2) {
            System.out.print("Enter Customer ID: ");
            String custId = sc.nextLine().trim();
            manager.displayClaimsByCustomer(custId);
        } else if (sub == 3) {
            System.out.print("Enter Claim ID (e.g. CLM701): ");
            String claimId = sc.nextLine().trim();
            manager.displayClaimDetails(claimId);
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
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Insurance Plan Management (Add, Update, Deactivate, Search)");
            System.out.println("2. Customer Management (View All, Search, Update Profile)");
            System.out.println("3. Purchased Policy Management (View, Search, Filter Status)");
            System.out.println("4. Claim Management (Review, Approve, Reject, Audit)");
            System.out.println("5. Dashboard & Analytics");
            System.out.println("6. System Operations (Undo Plan Addition, Audit Stack, Background Report)");
            System.out.println("7. Return to Main Menu");
            ch = readInt(sc, "Enter your choice (1-7): ");

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
                    handleAdminSystemOperations(sc);
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

    private static void handleAdminPlanManagement(Scanner sc) {
        System.out.println("\n[Admin - Plan Management]");
        System.out.println("1. View All Plans (with Availability Status)");
        System.out.println("2. Add New Insurance Plan");
        System.out.println("3. Update Existing Plan Details");
        System.out.println("4. Deactivate Plan (Prevent New Purchases)");
        System.out.println("5. Reactivate Plan");
        System.out.println("6. Search Plans (Overloaded Search)");
        int sub = readInt(sc, "Choose option (1-6): ");

        if (sub == 1) {
            manager.displayAllPlans();
        } else if (sub == 2) {
            System.out.println("Select Plan Type: 1. Health  2. Life  3. Vehicle  4. Travel");
            int type = readInt(sc, "Select type (1-4): ");
            String planId = readNonEmptyString(sc, "Enter Plan ID (e.g. HP02): ");
            String name = readNonEmptyString(sc, "Enter Plan Name: ");
            double coverage = readDouble(sc, "Enter Coverage Amount (Rs): ");
            int duration = readInt(sc, "Enter Duration in Months: ");
            double basePrem = readDouble(sc, "Enter Base Premium (Rs): ");

            InsurancePlan newPlan = null;
            if (type == 1) newPlan = new HealthPlan(planId, name, coverage, duration, basePrem, true);
            else if (type == 2) newPlan = new LifePlan(planId, name, coverage, duration, basePrem, true);
            else if (type == 3) newPlan = new VehiclePlan(planId, name, coverage, duration, basePrem, true);
            else if (type == 4) newPlan = new TravelPlan(planId, name, coverage, duration, basePrem, true);
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
            System.out.print("Enter Plan ID to deactivate: ");
            String planId = sc.nextLine().trim();
            manager.deactivatePlan(planId);
        } else if (sub == 5) {
            manager.displayAllPlans();
            System.out.print("Enter Plan ID to reactivate: ");
            String planId = sc.nextLine().trim();
            manager.reactivatePlan(planId);
        } else if (sub == 6) {
            System.out.print("Enter plan name keyword (or press Enter for all): ");
            String kw = sc.nextLine().trim();
            System.out.print("Enter plan type (Health, Life, Vehicle, Travel, or ALL): ");
            String type = sc.nextLine().trim();
            manager.searchPlans(kw, type);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleAdminCustomerManagement(Scanner sc) {
        System.out.println("\n[Admin - Customer Management]");
        System.out.println("1. View All Registered Customers");
        System.out.println("2. Search Customer by ID or Name/Phone (Overload)");
        System.out.println("3. Update Customer Profile Details");
        System.out.println("4. View Policies of a Specific Customer");
        int sub = readInt(sc, "Choose option (1-4): ");

        if (sub == 1) {
            manager.displayAllCustomers();
        } else if (sub == 2) {
            System.out.print("Enter Customer ID, Name, or Phone keyword: ");
            String query = sc.nextLine().trim();
            manager.searchCustomer(query);
        } else if (sub == 3) {
            System.out.print("Enter Customer ID to update: ");
            String custId = sc.nextLine().trim();
            Customer c = manager.findCustomerById(custId);
            if (c == null) {
                System.out.println("Customer not found.");
                return;
            }
            System.out.println("Current details: " + c);
            System.out.print("Enter new name (or Enter to keep): ");
            String newName = sc.nextLine().trim();
            int newAge = readInt(sc, "Enter new age (or 0 to keep): ");
            System.out.print("Enter new phone (or Enter to keep): ");
            String newPhone = sc.nextLine().trim();
            System.out.print("Enter new email (or Enter to keep): ");
            String newEmail = sc.nextLine().trim();
            System.out.print("Enter new address (or Enter to keep): ");
            String newAddress = sc.nextLine().trim();

            manager.updateCustomer(custId, newName, (newAge > 0 ? newAge : c.getAge()),
                    (newPhone.isEmpty() ? c.getPhone() : newPhone),
                    (newEmail.isEmpty() ? c.getEmail() : newEmail),
                    (newAddress.isEmpty() ? c.getAddress() : newAddress));
        } else if (sub == 4) {
            System.out.print("Enter Customer ID: ");
            String custId = sc.nextLine().trim();
            manager.displayPoliciesByCustomer(custId);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleAdminPurchasedPolicies(Scanner sc) {
        System.out.println("\n[Admin - Purchased Policies]");
        System.out.println("1. View All Purchased Policies");
        System.out.println("2. Search Policy by Policy Number");
        System.out.println("3. View Policies by Customer ID");
        System.out.println("4. Filter Policies by Status (ACTIVE, EXPIRED, CANCELLED, PENDING_PAYMENT)");
        int sub = readInt(sc, "Choose option (1-4): ");

        if (sub == 1) {
            manager.displayAllPurchasedPolicies();
        } else if (sub == 2) {
            System.out.print("Enter Policy Number: ");
            String polNo = sc.nextLine().trim();
            PurchasedPolicy p = manager.findPolicyByNumber(polNo);
            if (p != null) {
                System.out.println("\nPolicy Details:");
                System.out.println("Policy Number   : " + p.getPolicyNumber());
                System.out.println("Customer ID     : " + p.getCustomerId());
                System.out.println("Plan ID         : " + p.getPlanId());
                System.out.println("Policy Status   : " + p.getStatus());
                System.out.println("Payment Status  : " + p.getPaymentStatus());
                System.out.println("Premium         : Rs " + p.getPremium());
                System.out.println("Coverage Amount : Rs " + p.getCoverageAmount());
                System.out.println("Start Date      : " + p.getStartDate());
                System.out.println("Expiry Date     : " + p.getExpiryDate());
            } else {
                System.out.println("Policy not found.");
            }
        } else if (sub == 3) {
            System.out.print("Enter Customer ID: ");
            String custId = sc.nextLine().trim();
            manager.displayPoliciesByCustomer(custId);
        } else if (sub == 4) {
            System.out.println("Status Options: 1. ACTIVE  2. EXPIRED  3. CANCELLED  4. PENDING_PAYMENT");
            int statChoice = readInt(sc, "Select status filter (1-4): ");
            String filter = "ACTIVE";
            if (statChoice == 2) filter = "EXPIRED";
            else if (statChoice == 3) filter = "CANCELLED";
            else if (statChoice == 4) filter = "PENDING_PAYMENT";
            manager.displayPoliciesByStatus(filter);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleAdminClaimManagement(Scanner sc) {
        System.out.println("\n[Admin - Claim Management]");
        System.out.println("1. View All Claims");
        System.out.println("2. View Pending Claims Only");
        System.out.println("3. Review and Approve a Claim");
        System.out.println("4. Review and Reject a Claim");
        System.out.println("5. View Claim Details / Audit");
        int sub = readInt(sc, "Choose option (1-5): ");

        if (sub == 1) {
            manager.displayAllClaims();
        } else if (sub == 2) {
            manager.displayPendingClaims();
        } else if (sub == 3) {
            System.out.print("Enter Claim ID to Approve (e.g. CLM701): ");
            String claimId = sc.nextLine().trim();
            Claim c = manager.findClaimById(claimId);
            if (c == null) {
                System.out.println("Claim not found.");
                return;
            }
            System.out.println("Claim Requested Amount: Rs " + c.getClaimAmount());
            double approvedAmt = readDouble(sc, "Enter approved payout amount (Rs): ");
            System.out.print("Enter approval remarks / notes: ");
            String remarks = sc.nextLine().trim();
            manager.approveClaim(claimId, approvedAmt, remarks);
        } else if (sub == 4) {
            System.out.print("Enter Claim ID to Reject (e.g. CLM701): ");
            String claimId = sc.nextLine().trim();
            System.out.print("Enter rejection reason / remarks: ");
            String remarks = sc.nextLine().trim();
            manager.rejectClaim(claimId, remarks);
        } else if (sub == 5) {
            System.out.print("Enter Claim ID: ");
            String claimId = sc.nextLine().trim();
            manager.displayClaimDetails(claimId);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleAdminSystemOperations(Scanner sc) {
        System.out.println("\n[Admin - System Operations & Audit]");
        System.out.println("1. Undo Last Plan Addition (ActionStack Reversible Operation)");
        System.out.println("2. View Action Log Stack (ActionStack LIFO Display)");
        System.out.println("3. Generate Background Performance & Audit Report (ReportTask Thread)");
        System.out.println("4. Trigger Manual Expiry Check on All Policies (ExpiryMonitor)");
        int sub = readInt(sc, "Choose option (1-4): ");

        if (sub == 1) {
            manager.undoLastPlanAddition();
        } else if (sub == 2) {
            manager.getActionStack().display();
        } else if (sub == 3) {
            System.out.println("Launching ReportTask background thread...");
            manager.generateAsyncReport();
        } else if (sub == 4) {
            int newlyExpired = manager.checkAndRefreshPolicyStatuses(LocalDate.now());
            System.out.println("Manual expiry check complete. " + newlyExpired + " policies transitioned to EXPIRED.");
        } else {
            System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // ROBUST INPUT READERS WITH EXCEPTION HANDLING
    // =========================================================================

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

    private static int readAge(Scanner sc, String prompt) {
        while (true) {
            int age = readInt(sc, prompt);
            if (age < 18) {
                System.out.println("Validation Error: Age must be at least 18 for insurance policyholders.");
            } else if (age > 120) {
                System.out.println("Validation Error: Age cannot exceed 120. Please enter a valid age.");
            } else {
                return age;
            }
        }
    }

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

    private static String readEmail(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = sc.nextLine().trim();
            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Validation Error: Please enter a valid email format (e.g. name@domain.com).");
            } else {
                return email;
            }
        }
    }

    private static String readNonEmptyString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String str = sc.nextLine().trim();
            if (str.isEmpty()) {
                System.out.println("Validation Error: Field cannot be empty.");
            } else {
                return str;
            }
        }
    }
}
