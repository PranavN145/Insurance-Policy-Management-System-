/**
 * InsuranceManager.java
 *
 * Implements Member 2's responsibilities:
 * - Application data storage using fixed-size Java object arrays
 * - Customer management (registration, unique ID generation, search, validation)
 * - Insurance plan management (listing, filtering by type, add/update/remove)
 * - Purchased policy management (purchasing with premium calculation, search, renewal, cancellation)
 * - Payment and claim operations (simulated payment, claim submission, review, approval/rejection)
 * - Admin dashboard analytics and reporting
 *
 * Designed using beginner-friendly SY B.Tech OOP concepts:
 * Object arrays, encapsulation, conditional validation, and loops.
 */
public class InsuranceManager {

    // Maximum capacities for fixed-size arrays
    private static final int MAX_CUSTOMERS = 100;
    private static final int MAX_PLANS = 50;
    private static final int MAX_POLICIES = 150;
    private static final int MAX_PAYMENTS = 150;
    private static final int MAX_CLAIMS = 100;

    // Fixed-size object arrays
    private Customer[] customers;
    private int customerCount;

    private InsurancePlan[] plans;
    private int planCount;

    private PurchasedPolicy[] policies;
    private int policyCount;

    private Payment[] payments;
    private int paymentCount;

    private Claim[] claims;
    private int claimCount;

    // Auto-increment counters for unique IDs
    private int nextCustomerId = 101;
    private int nextPolicyNumber = 1001;
    private int nextPaymentId = 501;
    private int nextClaimId = 701;

    // Constructor
    public InsuranceManager() {
        customers = new Customer[MAX_CUSTOMERS];
        customerCount = 0;

        plans = new InsurancePlan[MAX_PLANS];
        planCount = 0;

        policies = new PurchasedPolicy[MAX_POLICIES];
        policyCount = 0;

        payments = new Payment[MAX_PAYMENTS];
        paymentCount = 0;

        claims = new Claim[MAX_CLAIMS];
        claimCount = 0;

        // Seed initial default plans for immediate use
        seedInitialPlans();
    }

    /**
     * Seeds initial sample insurance plans so the system is immediately usable.
     */
    private void seedInitialPlans() {
        addPlan(new HealthPlan("HP01", "Health Care Comprehensive", 500000.0, 12, 8000.0));
        addPlan(new LifePlan("LP01", "Term Life Shield", 1000000.0, 24, 12000.0));
        addPlan(new VehiclePlan("VP01", "Motor Comprehensive Cover", 300000.0, 12, 5000.0));
        addPlan(new TravelPlan("TP01", "Global Travel Guard", 200000.0, 6, 3000.0));
    }

    // =========================================================================
    // 1. CUSTOMER OPERATIONS
    // =========================================================================

    /**
     * Registers a new customer after validating inputs.
     * Generates a unique customer ID (e.g. C101, C102).
     */
    public Customer registerCustomer(String name, int age, String phone, String address) {
        if (customerCount >= MAX_CUSTOMERS) {
            System.out.println("Error: Customer storage limit reached (" + MAX_CUSTOMERS + ").");
            return null;
        }

        // Validation
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Validation Error: Customer name cannot be empty.");
            return null;
        }
        if (age <= 0 || age > 120) {
            System.out.println("Validation Error: Please enter a valid age between 1 and 120.");
            return null;
        }
        if (phone == null || phone.trim().length() < 10) {
            System.out.println("Validation Error: Phone number must be at least 10 digits.");
            return null;
        }

        // Generate unique customer ID
        String customerId = "C" + nextCustomerId++;

        // Duplicate check (precautionary)
        if (findCustomerById(customerId) != null) {
            System.out.println("Error: Customer ID " + customerId + " already exists.");
            return null;
        }

        Customer newCustomer = new Customer(customerId, name.trim(), age, phone.trim(), address.trim());
        customers[customerCount++] = newCustomer;

        System.out.println("Customer registered successfully!");
        System.out.println("Assigned Customer ID: " + customerId);
        return newCustomer;
    }

    /**
     * Searches for a customer by Customer ID.
     */
    public Customer findCustomerById(String customerId) {
        if (customerId == null) return null;
        for (int i = 0; i < customerCount; i++) {
            if (customers[i] != null && customerId.equalsIgnoreCase(customers[i].getCustomerId())) {
                return customers[i];
            }
        }
        return null;
    }

    /**
     * Displays all registered customers.
     */
    public void displayAllCustomers() {
        if (customerCount == 0) {
            System.out.println("No customers registered yet.");
            return;
        }
        System.out.println("\n-------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-25s %-6s %-15s %-30s\n", "Cust ID", "Name", "Age", "Phone", "Address");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (int i = 0; i < customerCount; i++) {
            Customer c = customers[i];
            System.out.printf("%-10s %-25s %-6d %-15s %-30s\n",
                    c.getCustomerId(), c.getName(), c.getAge(), c.getPhone(), c.getAddress());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    /**
     * Displays details of a specific customer.
     */
    public void displayCustomerProfile(String customerId) {
        Customer c = findCustomerById(customerId);
        if (c == null) {
            System.out.println("Customer with ID '" + customerId + "' not found.");
            return;
        }
        System.out.println("\n--- CUSTOMER PROFILE ---");
        System.out.println("Customer ID : " + c.getCustomerId());
        System.out.println("Name        : " + c.getName());
        System.out.println("Age         : " + c.getAge());
        System.out.println("Phone       : " + c.getPhone());
        System.out.println("Address     : " + c.getAddress());
    }

    // =========================================================================
    // 2. INSURANCE PLAN OPERATIONS
    // =========================================================================

    /**
     * Adds an insurance plan to available plans.
     */
    public boolean addPlan(InsurancePlan plan) {
        if (planCount >= MAX_PLANS) {
            System.out.println("Error: Plan storage limit reached (" + MAX_PLANS + ").");
            return false;
        }
        if (plan == null) {
            System.out.println("Error: Plan cannot be null.");
            return false;
        }
        if (findPlanById(plan.getPlanId()) != null) {
            System.out.println("Error: Plan ID '" + plan.getPlanId() + "' already exists.");
            return false;
        }
        plans[planCount++] = plan;
        return true;
    }

    /**
     * Searches for a plan by Plan ID.
     */
    public InsurancePlan findPlanById(String planId) {
        if (planId == null) return null;
        for (int i = 0; i < planCount; i++) {
            if (plans[i] != null && planId.equalsIgnoreCase(plans[i].getPlanId())) {
                return plans[i];
            }
        }
        return null;
    }

    /**
     * Displays all available insurance plans.
     */
    public void displayAllPlans() {
        if (planCount == 0) {
            System.out.println("No insurance plans available.");
            return;
        }
        System.out.println("\n-----------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-30s %-15s %-10s %-12s\n", "Plan ID", "Type", "Plan Name", "Coverage (Rs)", "Duration", "Base Premium");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (int i = 0; i < planCount; i++) {
            InsurancePlan p = plans[i];
            String type = getPlanType(p);
            System.out.printf("%-10s %-12s %-30s %-15.2f %-2d months   Rs %-10.2f\n",
                    p.getPlanId(), type, p.getPlanName(), p.getCoverageAmount(), p.getDurationMonths(), p.getBasePremium());
        }
        System.out.println("-----------------------------------------------------------------------------------------------");
    }

    /**
     * Helper to get human-readable plan type name.
     */
    public String getPlanType(InsurancePlan p) {
        if (p instanceof HealthPlan) return "Health";
        if (p instanceof LifePlan) return "Life";
        if (p instanceof VehiclePlan) return "Vehicle";
        if (p instanceof TravelPlan) return "Travel";
        return "General";
    }

    /**
     * Displays plans filtered by type: Health, Life, Vehicle, Travel.
     */
    public void displayPlansByType(String type) {
        if (type == null) return;
        boolean found = false;
        System.out.println("\n--- " + type.toUpperCase() + " INSURANCE PLANS ---");
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-30s %-15s %-10s %-12s\n", "Plan ID", "Plan Name", "Coverage (Rs)", "Duration", "Base Premium");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (int i = 0; i < planCount; i++) {
            InsurancePlan p = plans[i];
            if (getPlanType(p).equalsIgnoreCase(type)) {
                System.out.printf("%-10s %-30s %-15.2f %-2d months   Rs %-10.2f\n",
                        p.getPlanId(), p.getPlanName(), p.getCoverageAmount(), p.getDurationMonths(), p.getBasePremium());
                found = true;
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No plans found under type: " + type);
        }
    }

    /**
     * Updates an existing plan's details.
     */
    public boolean updatePlan(String planId, String newName, double newCoverage, int newDuration, double newBasePremium) {
        InsurancePlan p = findPlanById(planId);
        if (p == null) {
            System.out.println("Error: Plan with ID '" + planId + "' not found.");
            return false;
        }
        if (newName != null && !newName.trim().isEmpty()) {
            p.setPlanName(newName.trim());
        }
        if (newCoverage > 0) {
            p.setCoverageAmount(newCoverage);
        }
        if (newDuration > 0) {
            p.setDurationMonths(newDuration);
        }
        if (newBasePremium > 0) {
            p.setBasePremium(newBasePremium);
        }
        System.out.println("Plan '" + planId + "' updated successfully.");
        return true;
    }

    /**
     * Removes a plan from available plans by shifting the array elements.
     */
    public boolean removePlan(String planId) {
        int index = -1;
        for (int i = 0; i < planCount; i++) {
            if (plans[i] != null && planId.equalsIgnoreCase(plans[i].getPlanId())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            System.out.println("Error: Plan with ID '" + planId + "' not found.");
            return false;
        }

        // Shift elements to the left
        for (int i = index; i < planCount - 1; i++) {
            plans[i] = plans[i + 1];
        }
        plans[planCount - 1] = null;
        planCount--;
        System.out.println("Plan '" + planId + "' has been successfully removed.");
        return true;
    }

    // =========================================================================
    // 3. PURCHASED POLICY OPERATIONS
    // =========================================================================

    /**
     * Purchases a policy for a customer.
     * Verifies existence of customer and plan, uses Member 1's calculatePremium,
     * generates a unique policy number, and saves the purchased policy with status 'ACTIVE'.
     */
    public PurchasedPolicy purchasePolicy(String customerId, String planId) {
        if (policyCount >= MAX_POLICIES) {
            System.out.println("Error: Policy storage capacity reached (" + MAX_POLICIES + ").");
            return null;
        }

        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Error: Customer with ID '" + customerId + "' does not exist.");
            return null;
        }

        InsurancePlan plan = findPlanById(planId);
        if (plan == null) {
            System.out.println("Error: Insurance plan with ID '" + planId + "' does not exist or has been removed.");
            return null;
        }

        // Calculate premium using Member 1's calculatePremium(Customer) method
        double calculatedPremium = plan.calculatePremium(customer);

        String policyNumber = "POL" + nextPolicyNumber++;
        PurchasedPolicy newPolicy = new PurchasedPolicy(policyNumber, customer.getCustomerId(), plan.getPlanId(), "ACTIVE");
        policies[policyCount++] = newPolicy;

        System.out.println("\nPolicy Purchased Successfully!");
        System.out.println("----------------------------------------------");
        System.out.println("Policy Number       : " + policyNumber);
        System.out.println("Customer Name       : " + customer.getName() + " (" + customer.getCustomerId() + ")");
        System.out.println("Plan Name           : " + plan.getPlanName() + " (" + plan.getPlanId() + ")");
        System.out.println("Coverage Amount     : Rs " + plan.getCoverageAmount());
        System.out.println("Duration            : " + plan.getDurationMonths() + " months");
        System.out.println("Calculated Premium  : Rs " + calculatedPremium);
        System.out.println("Status              : ACTIVE");
        System.out.println("----------------------------------------------");
        return newPolicy;
    }

    /**
     * Searches for a purchased policy by policy number.
     */
    public PurchasedPolicy findPolicyByNumber(String policyNumber) {
        if (policyNumber == null) return null;
        for (int i = 0; i < policyCount; i++) {
            if (policies[i] != null && policyNumber.equalsIgnoreCase(policies[i].getPolicyNumber())) {
                return policies[i];
            }
        }
        return null;
    }

    /**
     * Displays all purchased policies.
     */
    public void displayAllPurchasedPolicies() {
        if (policyCount == 0) {
            System.out.println("No policies purchased yet.");
            return;
        }
        System.out.println("\n-----------------------------------------------------------------");
        System.out.printf("%-15s %-12s %-12s %-12s\n", "Policy No", "Customer ID", "Plan ID", "Status");
        System.out.println("-----------------------------------------------------------------");
        for (int i = 0; i < policyCount; i++) {
            PurchasedPolicy p = policies[i];
            System.out.printf("%-15s %-12s %-12s %-12s\n",
                    p.getPolicyNumber(), p.getCustomerId(), p.getPlanId(), p.getStatus());
        }
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Displays all policies purchased by a specific customer.
     */
    public void displayPoliciesByCustomer(String customerId) {
        Customer c = findCustomerById(customerId);
        if (c == null) {
            System.out.println("Customer ID '" + customerId + "' not found.");
            return;
        }
        boolean found = false;
        System.out.println("\n--- POLICIES FOR CUSTOMER: " + c.getName() + " (" + customerId + ") ---");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%-15s %-12s %-12s %-12s\n", "Policy No", "Customer ID", "Plan ID", "Status");
        System.out.println("-----------------------------------------------------------------");
        for (int i = 0; i < policyCount; i++) {
            PurchasedPolicy p = policies[i];
            if (p != null && customerId.equalsIgnoreCase(p.getCustomerId())) {
                System.out.printf("%-15s %-12s %-12s %-12s\n",
                        p.getPolicyNumber(), p.getCustomerId(), p.getPlanId(), p.getStatus());
                found = true;
            }
        }
        System.out.println("-----------------------------------------------------------------");
        if (!found) {
            System.out.println("No policies found for this customer.");
        }
    }

    /**
     * Renews an active or expired policy.
     */
    public boolean renewPolicy(String policyNumber) {
        PurchasedPolicy p = findPolicyByNumber(policyNumber);
        if (p == null) {
            System.out.println("Error: Policy number '" + policyNumber + "' not found.");
            return false;
        }
        if ("CANCELLED".equalsIgnoreCase(p.getStatus())) {
            System.out.println("Error: Cancelled policy '" + policyNumber + "' cannot be renewed.");
            return false;
        }
        p.setStatus("ACTIVE");
        System.out.println("Policy '" + policyNumber + "' has been successfully renewed! Status is now ACTIVE.");
        return true;
    }

    /**
     * Cancels an active policy.
     */
    public boolean cancelPolicy(String policyNumber) {
        PurchasedPolicy p = findPolicyByNumber(policyNumber);
        if (p == null) {
            System.out.println("Error: Policy number '" + policyNumber + "' not found.");
            return false;
        }
        if ("CANCELLED".equalsIgnoreCase(p.getStatus())) {
            System.out.println("Notice: Policy '" + policyNumber + "' is already cancelled.");
            return false;
        }
        p.setStatus("CANCELLED");
        System.out.println("Policy '" + policyNumber + "' has been successfully CANCELLED.");
        return true;
    }

    // =========================================================================
    // 4. PAYMENT OPERATIONS
    // =========================================================================

    /**
     * Processes a simulated premium payment for a policy.
     * Uses Member 1's Payment class and makePayment(amount) method.
     */
    public Payment processPayment(String policyNumber, double amount) {
        if (paymentCount >= MAX_PAYMENTS) {
            System.out.println("Error: Payment record storage capacity reached.");
            return null;
        }

        PurchasedPolicy policy = findPolicyByNumber(policyNumber);
        if (policy == null) {
            System.out.println("Error: Policy number '" + policyNumber + "' does not exist.");
            return null;
        }

        if ("CANCELLED".equalsIgnoreCase(policy.getStatus())) {
            System.out.println("Error: Cannot make payment for a CANCELLED policy.");
            return null;
        }

        String paymentId = "PAY" + nextPaymentId++;
        Payment payment = new Payment(paymentId, policyNumber, 0.0, "PENDING");

        // Invoke Member 1's makePayment implementation
        boolean success = payment.makePayment(amount);
        payments[paymentCount++] = payment;

        if (success) {
            System.out.println("Receipt: Payment ID " + paymentId + " of Rs " + amount + " recorded for Policy " + policyNumber);
        } else {
            System.out.println("Payment transaction failed for Policy " + policyNumber);
        }
        return payment;
    }

    /**
     * Displays all payments recorded in the system.
     */
    public void displayAllPayments() {
        if (paymentCount == 0) {
            System.out.println("No payment records found.");
            return;
        }
        System.out.println("\n-----------------------------------------------------------------");
        System.out.printf("%-12s %-15s %-15s %-12s\n", "Payment ID", "Policy No", "Amount (Rs)", "Status");
        System.out.println("-----------------------------------------------------------------");
        for (int i = 0; i < paymentCount; i++) {
            Payment p = payments[i];
            System.out.printf("%-12s %-15s %-15.2f %-12s\n",
                    p.getPaymentId(), p.getPolicyNumber(), p.getAmount(), p.getStatus());
        }
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Displays payment history for a specific policy.
     */
    public void displayPaymentsByPolicy(String policyNumber) {
        PurchasedPolicy policy = findPolicyByNumber(policyNumber);
        if (policy == null) {
            System.out.println("Error: Policy number '" + policyNumber + "' not found.");
            return;
        }
        boolean found = false;
        System.out.println("\n--- PAYMENT HISTORY FOR POLICY: " + policyNumber + " ---");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%-12s %-15s %-15s %-12s\n", "Payment ID", "Policy No", "Amount (Rs)", "Status");
        System.out.println("-----------------------------------------------------------------");
        for (int i = 0; i < paymentCount; i++) {
            Payment p = payments[i];
            if (p != null && policyNumber.equalsIgnoreCase(p.getPolicyNumber())) {
                System.out.printf("%-12s %-15s %-15.2f %-12s\n",
                        p.getPaymentId(), p.getPolicyNumber(), p.getAmount(), p.getStatus());
                found = true;
            }
        }
        System.out.println("-----------------------------------------------------------------");
        if (!found) {
            System.out.println("No payments recorded for this policy.");
        }
    }

    // =========================================================================
    // 5. CLAIM OPERATIONS
    // =========================================================================

    /**
     * Submits an insurance claim against a purchased policy.
     * Verifies that the policy exists, belongs to the customer, and is ACTIVE.
     */
    public Claim submitClaim(String customerId, String policyNumber, String description, double claimAmount) {
        if (claimCount >= MAX_CLAIMS) {
            System.out.println("Error: Claim storage capacity reached.");
            return null;
        }

        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Error: Customer with ID '" + customerId + "' not found.");
            return null;
        }

        PurchasedPolicy policy = findPolicyByNumber(policyNumber);
        if (policy == null) {
            System.out.println("Error: Policy number '" + policyNumber + "' not found.");
            return null;
        }

        if (!customerId.equalsIgnoreCase(policy.getCustomerId())) {
            System.out.println("Validation Error: Policy '" + policyNumber + "' does not belong to Customer '" + customerId + "'.");
            return null;
        }

        if (!"ACTIVE".equalsIgnoreCase(policy.getStatus())) {
            System.out.println("Validation Error: Cannot submit claim. Policy status is " + policy.getStatus() + " (Must be ACTIVE).");
            return null;
        }

        if (claimAmount <= 0) {
            System.out.println("Validation Error: Claim amount must be greater than 0.");
            return null;
        }

        String claimId = "CLM" + nextClaimId++;
        Claim newClaim = new Claim(claimId, policyNumber, description.trim(), claimAmount, "PENDING");
        claims[claimCount++] = newClaim;

        System.out.println("\nClaim Submitted Successfully!");
        System.out.println("----------------------------------------------------");
        System.out.println("Claim ID     : " + claimId);
        System.out.println("Policy Number: " + policyNumber);
        System.out.println("Customer     : " + customer.getName() + " (" + customerId + ")");
        System.out.println("Claim Amount : Rs " + claimAmount);
        System.out.println("Description  : " + description);
        System.out.println("Status       : PENDING (Under Admin Review)");
        System.out.println("----------------------------------------------------");
        return newClaim;
    }

    /**
     * Searches for a claim by Claim ID.
     */
    public Claim findClaimById(String claimId) {
        if (claimId == null) return null;
        for (int i = 0; i < claimCount; i++) {
            if (claims[i] != null && claimId.equalsIgnoreCase(claims[i].getClaimId())) {
                return claims[i];
            }
        }
        return null;
    }

    /**
     * Displays all claims submitted in the system.
     */
    public void displayAllClaims() {
        if (claimCount == 0) {
            System.out.println("No claims submitted yet.");
            return;
        }
        System.out.println("\n-------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-30s %-15s %-12s\n", "Claim ID", "Policy No", "Description", "Claim Amount", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (int i = 0; i < claimCount; i++) {
            Claim c = claims[i];
            System.out.printf("%-10s %-12s %-30s Rs %-12.2f %-12s\n",
                    c.getClaimId(), c.getPolicyNumber(), c.getDescription(), c.getClaimAmount(), c.getStatus());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    /**
     * Displays all claims for policies owned by a specific customer.
     */
    public void displayClaimsByCustomer(String customerId) {
        Customer cust = findCustomerById(customerId);
        if (cust == null) {
            System.out.println("Customer ID '" + customerId + "' not found.");
            return;
        }
        boolean found = false;
        System.out.println("\n--- CLAIMS FOR CUSTOMER: " + cust.getName() + " (" + customerId + ") ---");
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-30s %-15s %-12s\n", "Claim ID", "Policy No", "Description", "Claim Amount", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (int i = 0; i < claimCount; i++) {
            Claim c = claims[i];
            PurchasedPolicy pol = findPolicyByNumber(c.getPolicyNumber());
            if (pol != null && customerId.equalsIgnoreCase(pol.getCustomerId())) {
                System.out.printf("%-10s %-12s %-30s Rs %-12.2f %-12s\n",
                        c.getClaimId(), c.getPolicyNumber(), c.getDescription(), c.getClaimAmount(), c.getStatus());
                found = true;
            }
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No claims found for this customer.");
        }
    }

    /**
     * Approves a pending claim.
     */
    public boolean approveClaim(String claimId) {
        Claim c = findClaimById(claimId);
        if (c == null) {
            System.out.println("Error: Claim ID '" + claimId + "' not found.");
            return false;
        }
        if (!"PENDING".equalsIgnoreCase(c.getStatus())) {
            System.out.println("Notice: Claim '" + claimId + "' has already been processed (Status: " + c.getStatus() + ").");
            return false;
        }
        c.setStatus("APPROVED");
        System.out.println("Claim '" + claimId + "' for Rs " + c.getClaimAmount() + " has been APPROVED.");
        return true;
    }

    /**
     * Rejects a pending claim.
     */
    public boolean rejectClaim(String claimId) {
        Claim c = findClaimById(claimId);
        if (c == null) {
            System.out.println("Error: Claim ID '" + claimId + "' not found.");
            return false;
        }
        if (!"PENDING".equalsIgnoreCase(c.getStatus())) {
            System.out.println("Notice: Claim '" + claimId + "' has already been processed (Status: " + c.getStatus() + ").");
            return false;
        }
        c.setStatus("REJECTED");
        System.out.println("Claim '" + claimId + "' has been REJECTED.");
        return true;
    }

    // =========================================================================
    // 6. ADMIN DASHBOARD & ANALYTICS
    // =========================================================================

    /**
     * Displays a comprehensive dashboard showing customer counts, plans,
     * purchased policies (active/cancelled), payment statistics, and claim metrics.
     */
    public void displayDashboard() {
        int activePolicies = 0;
        int cancelledPolicies = 0;
        for (int i = 0; i < policyCount; i++) {
            if ("ACTIVE".equalsIgnoreCase(policies[i].getStatus())) activePolicies++;
            else if ("CANCELLED".equalsIgnoreCase(policies[i].getStatus())) cancelledPolicies++;
        }

        int successfulPayments = 0;
        double totalRevenue = 0.0;
        for (int i = 0; i < paymentCount; i++) {
            if ("SUCCESS".equalsIgnoreCase(payments[i].getStatus())) {
                successfulPayments++;
                totalRevenue += payments[i].getAmount();
            }
        }

        int pendingClaims = 0;
        int approvedClaims = 0;
        int rejectedClaims = 0;
        double totalApprovedClaimAmount = 0.0;
        for (int i = 0; i < claimCount; i++) {
            if ("PENDING".equalsIgnoreCase(claims[i].getStatus())) pendingClaims++;
            else if ("APPROVED".equalsIgnoreCase(claims[i].getStatus())) {
                approvedClaims++;
                totalApprovedClaimAmount += claims[i].getClaimAmount();
            } else if ("REJECTED".equalsIgnoreCase(claims[i].getStatus())) rejectedClaims++;
        }

        System.out.println("\n=================================================================");
        System.out.println("               SYSTEM DASHBOARD & ANALYTICS                      ");
        System.out.println("=================================================================");
        System.out.println(" Registered Customers       : " + customerCount);
        System.out.println(" Available Insurance Plans  : " + planCount);
        System.out.println("-----------------------------------------------------------------");
        System.out.println(" Total Purchased Policies   : " + policyCount);
        System.out.println("   - Active Policies        : " + activePolicies);
        System.out.println("   - Cancelled Policies     : " + cancelledPolicies);
        System.out.println("-----------------------------------------------------------------");
        System.out.println(" Total Payments Processed   : " + paymentCount);
        System.out.println("   - Successful Payments    : " + successfulPayments);
        System.out.println("   - Total Revenue Collected: Rs " + String.format("%.2f", totalRevenue));
        System.out.println("-----------------------------------------------------------------");
        System.out.println(" Total Claims Submitted     : " + claimCount);
        System.out.println("   - Pending Review         : " + pendingClaims);
        System.out.println("   - Approved Claims        : " + approvedClaims);
        System.out.println("   - Rejected Claims        : " + rejectedClaims);
        System.out.println("   - Total Payout (Approved): Rs " + String.format("%.2f", totalApprovedClaimAmount));
        System.out.println("=================================================================");
    }

    // Getters for counts (useful for Member 3 tests and assertions)
    public int getCustomerCount() { return customerCount; }
    public int getPlanCount() { return planCount; }
    public int getPolicyCount() { return policyCount; }
    public int getPaymentCount() { return paymentCount; }
    public int getClaimCount() { return claimCount; }
}
