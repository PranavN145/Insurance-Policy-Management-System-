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

// Interface
interface Payable {
    boolean makePayment(double amount);
}

// Model Classes
class Customer {
    private String customerId;
    private String name;
    private int age;
    private String phone;
    private String address;

    public Customer() {}

    public Customer(String customerId, String name, int age, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.address = address;
    }
}

class PurchasedPolicy {
    private String policyNumber;
    private String customerId;
    private String planId;
    private String status;

    public PurchasedPolicy() {}

    public PurchasedPolicy(String policyNumber, String customerId, String planId, String status) {
        this.policyNumber = policyNumber;
        this.customerId = customerId;
        this.planId = planId;
        this.status = status;
    }
}

class Payment implements Payable {
    private String paymentId;
    private String policyNumber;
    private double amount;
    private String status;

    public Payment() {}

    public Payment(String paymentId, String policyNumber, double amount, String status) {
        this.paymentId = paymentId;
        this.policyNumber = policyNumber;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public boolean makePayment(double amount) {
        return false;
    }
}

class Claim {
    private String claimId;
    private String policyNumber;
    private String description;
    private double claimAmount;
    private String status;

    public Claim() {}

    public Claim(String claimId, String policyNumber, String description, double claimAmount, String status) {
        this.claimId = claimId;
        this.policyNumber = policyNumber;
        this.description = description;
        this.claimAmount = claimAmount;
        this.status = status;
    }
}

// Plan Abstract Class & Subclasses
abstract class InsurancePlan {
    private String planId;
    private String planName;
    private double coverageAmount;
    private int durationMonths;
    private double basePremium;

    public InsurancePlan() {}

    public InsurancePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        this.planId = planId;
        this.planName = planName;
        this.coverageAmount = coverageAmount;
        this.durationMonths = durationMonths;
        this.basePremium = basePremium;
    }

    public abstract double calculatePremium(Customer customer);
}

class HealthPlan extends InsurancePlan {
    public HealthPlan() { super(); }
    public HealthPlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }
    @Override
    public double calculatePremium(Customer customer) { return 0.0; }
}

class LifePlan extends InsurancePlan {
    public LifePlan() { super(); }
    public LifePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }
    @Override
    public double calculatePremium(Customer customer) { return 0.0; }
}

class VehiclePlan extends InsurancePlan {
    public VehiclePlan() { super(); }
    public VehiclePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }
    @Override
    public double calculatePremium(Customer customer) { return 0.0; }
}

class TravelPlan extends InsurancePlan {
    public TravelPlan() { super(); }
    public TravelPlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }
    @Override
    public double calculatePremium(Customer customer) { return 0.0; }
}
