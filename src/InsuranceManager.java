import java.time.LocalDate;

/**
 * InsuranceManager.java
 *
 * Core Business Logic Controller:
 * - Manages Customers, Plans, Policies, Payments, and Claims
 * - Enforces business rules per PRD & Architecture specifications
 * - Integrates ActionStack for auditing and undo operations
 * - Integrates FileHandler for persistent storage
 * - Supports multithreaded ExpiryMonitor and asynchronous ReportTask
 */
public class InsuranceManager {

    private static final int MAX_CUSTOMERS = 100;
    private static final int MAX_PLANS = 50;
    private static final int MAX_POLICIES = 150;
    private static final int MAX_PAYMENTS = 150;
    private static final int MAX_CLAIMS = 100;

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

    private ActionStack actionStack;

    // Auto-increment counters for unique IDs
    private int nextCustomerId = 101;
    private int nextPolicyNumber = 1001;
    private int nextPaymentId = 501;
    private int nextClaimId = 701;

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

        actionStack = new ActionStack(30);
    }

    public void seedDefaultPlansIfEmpty() {
        if (findPlanByIdInternal("HP01") == null) {
            addPlanInternal(new HealthPlan("HP01", "Health Care Comprehensive", 500000.0, 12, 8000.0, true));
        }
        if (findPlanByIdInternal("LP01") == null) {
            addPlanInternal(new LifePlan("LP01", "Term Life Shield", 1000000.0, 24, 12000.0, true));
        }
        if (findPlanByIdInternal("VP01") == null) {
            addPlanInternal(new VehiclePlan("VP01", "Motor Comprehensive Cover", 300000.0, 12, 5000.0, true));
        }
        if (findPlanByIdInternal("TP01") == null) {
            addPlanInternal(new TravelPlan("TP01", "Global Travel Guard", 200000.0, 6, 3000.0, true));
        }
    }

    private void addPlanInternal(InsurancePlan plan) {
        if (planCount < MAX_PLANS && plan != null && findPlanByIdInternal(plan.getPlanId()) == null) {
            plans[planCount++] = plan;
        }
    }

    private InsurancePlan findPlanByIdInternal(String planId) {
        if (planId == null) return null;
        for (int i = 0; i < planCount; i++) {
            if (plans[i] != null && planId.equalsIgnoreCase(plans[i].getPlanId())) {
                return plans[i];
            }
        }
        return null;
    }

    // =========================================================================
    // 1. CUSTOMER OPERATIONS
    // =========================================================================

    public Customer registerCustomer(String name, int age, String phone, String address) {
        return registerCustomer(name, age, phone, "customer@insurance.com", address);
    }

    public Customer registerCustomer(String name, int age, String phone, String email, String address) {
        if (customerCount >= MAX_CUSTOMERS) {
            System.out.println("Error: Customer storage capacity reached (" + MAX_CUSTOMERS + ").");
            return null;
        }

        if (name == null || name.trim().isEmpty()) {
            System.out.println("Validation Error: Name cannot be blank.");
            return null;
        }
        if (age < 18 || age > 120) {
            System.out.println("Validation Error: Age must be between 18 and 120.");
            return null;
        }
        if (phone == null || phone.trim().length() < 10) {
            System.out.println("Validation Error: Phone number must be at least 10 digits.");
            return null;
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            System.out.println("Validation Error: Please provide a valid email address.");
            return null;
        }

        String customerId = "C" + nextCustomerId++;
        Customer newCustomer = new Customer(customerId, name.trim(), age, phone.trim(), email.trim(), address.trim(), LocalDate.now());
        customers[customerCount++] = newCustomer;

        actionStack.push("Registered Customer: " + newCustomer.getName() + " (" + customerId + ")");
        FileHandler.saveCustomers(this);

        System.out.println("Customer registered successfully!");
        System.out.println("Assigned Customer ID: " + customerId);
        return newCustomer;
    }

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
     * Overloaded search: search customer by name or phone number.
     * Demonstrates Method Overloading (Arch §4.3 & PRD §10.3).
     */
    public void searchCustomer(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }
        keyword = keyword.trim().toLowerCase();
        boolean found = false;
        System.out.println("\n--- CUSTOMER SEARCH RESULTS FOR: \"" + keyword + "\" ---");
        System.out.printf("%-10s %-22s %-6s %-15s %-25s %-20s\n", "Cust ID", "Name", "Age", "Phone", "Email", "Address");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < customerCount; i++) {
            Customer c = customers[i];
            if (c != null && (c.getName().toLowerCase().contains(keyword) || c.getPhone().contains(keyword) || c.getCustomerId().toLowerCase().contains(keyword))) {
                System.out.printf("%-10s %-22s %-6d %-15s %-25s %-20s\n",
                        c.getCustomerId(), c.getName(), c.getAge(), c.getPhone(), c.getEmail(), c.getAddress());
                found = true;
            }
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No matching customer records found.");
        }
    }

    public boolean updateCustomer(String customerId, String name, int age, String phone, String email, String address) {
        Customer c = findCustomerById(customerId);
        if (c == null) {
            System.out.println("Error: Customer with ID '" + customerId + "' not found.");
            return false;
        }

        if (name != null && !name.trim().isEmpty()) c.setName(name.trim());
        if (age >= 18 && age <= 120) c.setAge(age);
        if (phone != null && phone.trim().length() >= 10) c.setPhone(phone.trim());
        if (email != null && email.contains("@")) c.setEmail(email.trim());
        if (address != null && !address.trim().isEmpty()) c.setAddress(address.trim());

        actionStack.push("Updated Customer Details: " + customerId);
        FileHandler.saveCustomers(this);

        System.out.println("Customer profile for '" + customerId + "' updated successfully.");
        return true;
    }

    public void displayAllCustomers() {
        if (customerCount == 0) {
            System.out.println("No customers registered yet.");
            return;
        }
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-22s %-6s %-15s %-25s %-18s %-12s\n", "Cust ID", "Name", "Age", "Phone", "Email", "Address", "Reg Date");
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < customerCount; i++) {
            Customer c = customers[i];
            System.out.printf("%-10s %-22s %-6d %-15s %-25s %-18s %-12s\n",
                    c.getCustomerId(), c.getName(), c.getAge(), c.getPhone(), c.getEmail(), c.getAddress(), c.getRegistrationDate());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
    }

    public void displayCustomerProfile(String customerId) {
        Customer c = findCustomerById(customerId);
        if (c == null) {
            System.out.println("Customer with ID '" + customerId + "' not found.");
            return;
        }
        System.out.println("\n--- CUSTOMER PROFILE ---");
        System.out.println("Customer ID       : " + c.getCustomerId());
        System.out.println("Name              : " + c.getName());
        System.out.println("Age               : " + c.getAge());
        System.out.println("Phone             : " + c.getPhone());
        System.out.println("Email             : " + c.getEmail());
        System.out.println("Address           : " + c.getAddress());
        System.out.println("Registration Date : " + c.getRegistrationDate());
    }

    // =========================================================================
    // 2. INSURANCE PLAN OPERATIONS
    // =========================================================================

    public boolean addPlan(InsurancePlan plan) {
        if (planCount >= MAX_PLANS) {
            System.out.println("Error: Plan storage capacity reached (" + MAX_PLANS + ").");
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

        actionStack.push("Added Plan: " + plan.getPlanId() + " (" + plan.getPlanName() + ")");
        FileHandler.savePlans(this);

        return true;
    }

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
     * Overloaded search: search plan by name and type.
     * Demonstrates Method Overloading (Arch §4.3 & PRD §10.3).
     */
    public void searchPlans(String nameKeyword, String planType) {
        boolean found = false;
        System.out.println("\n--- PLAN SEARCH RESULTS ---");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-30s %-15s %-10s %-12s %-12s\n", "Plan ID", "Type", "Plan Name", "Coverage (Rs)", "Duration", "Base Premium", "Availability");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < planCount; i++) {
            InsurancePlan p = plans[i];
            String type = getPlanType(p);
            boolean matchType = (planType == null || planType.trim().isEmpty() || "ALL".equalsIgnoreCase(planType) || type.equalsIgnoreCase(planType));
            boolean matchName = (nameKeyword == null || nameKeyword.trim().isEmpty() || p.getPlanName().toLowerCase().contains(nameKeyword.toLowerCase()));

            if (matchType && matchName) {
                System.out.printf("%-10s %-12s %-30s %-15.2f %-2d months   Rs %-10.2f %-12s\n",
                        p.getPlanId(), type, p.getPlanName(), p.getCoverageAmount(), p.getDurationMonths(), p.getBasePremium(), (p.isAvailable() ? "AVAILABLE" : "DEACTIVATED"));
                found = true;
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No matching plans found.");
        }
    }

    public void displayAllPlans() {
        if (planCount == 0) {
            System.out.println("No insurance plans available.");
            return;
        }
        System.out.println("\n-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-30s %-15s %-10s %-12s %-12s\n", "Plan ID", "Type", "Plan Name", "Coverage (Rs)", "Duration", "Base Premium", "Availability");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < planCount; i++) {
            InsurancePlan p = plans[i];
            String type = getPlanType(p);
            System.out.printf("%-10s %-12s %-30s %-15.2f %-2d months   Rs %-10.2f %-12s\n",
                    p.getPlanId(), type, p.getPlanName(), p.getCoverageAmount(), p.getDurationMonths(), p.getBasePremium(), (p.isAvailable() ? "AVAILABLE" : "DEACTIVATED"));
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }

    public void displayAvailablePlansOnly() {
        boolean found = false;
        System.out.println("\n-----------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-30s %-15s %-10s %-12s\n", "Plan ID", "Type", "Plan Name", "Coverage (Rs)", "Duration", "Base Premium");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (int i = 0; i < planCount; i++) {
            InsurancePlan p = plans[i];
            if (p != null && p.isAvailable()) {
                String type = getPlanType(p);
                System.out.printf("%-10s %-12s %-30s %-15.2f %-2d months   Rs %-10.2f\n",
                        p.getPlanId(), type, p.getPlanName(), p.getCoverageAmount(), p.getDurationMonths(), p.getBasePremium());
                found = true;
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No active insurance plans available for purchase at this time.");
        }
    }

    public String getPlanType(InsurancePlan p) {
        if (p instanceof HealthPlan) return "Health";
        if (p instanceof LifePlan) return "Life";
        if (p instanceof VehiclePlan) return "Vehicle";
        if (p instanceof TravelPlan) return "Travel";
        return "General";
    }

    public void displayPlansByType(String type) {
        if (type == null) return;
        boolean found = false;
        System.out.println("\n--- " + type.toUpperCase() + " INSURANCE PLANS ---");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-30s %-15s %-10s %-12s %-12s\n", "Plan ID", "Plan Name", "Coverage (Rs)", "Duration", "Base Premium", "Availability");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < planCount; i++) {
            InsurancePlan p = plans[i];
            if (getPlanType(p).equalsIgnoreCase(type)) {
                System.out.printf("%-10s %-30s %-15.2f %-2d months   Rs %-10.2f %-12s\n",
                        p.getPlanId(), p.getPlanName(), p.getCoverageAmount(), p.getDurationMonths(), p.getBasePremium(), (p.isAvailable() ? "AVAILABLE" : "DEACTIVATED"));
                found = true;
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No plans found under type: " + type);
        }
    }

    public boolean updatePlan(String planId, String newName, double newCoverage, int newDuration, double newBasePremium) {
        InsurancePlan p = findPlanById(planId);
        if (p == null) {
            System.out.println("Error: Plan with ID '" + planId + "' not found.");
            return false;
        }
        if (newName != null && !newName.trim().isEmpty()) p.setPlanName(newName.trim());
        if (newCoverage > 0) p.setCoverageAmount(newCoverage);
        if (newDuration > 0) p.setDurationMonths(newDuration);
        if (newBasePremium > 0) p.setBasePremium(newBasePremium);

        actionStack.push("Updated Plan: " + planId);
        FileHandler.savePlans(this);

        System.out.println("Plan '" + planId + "' updated successfully.");
        return true;
    }

    /**
     * Deactivates a plan instead of deleting it, maintaining referential integrity for purchased policies.
     * Complies with PRD §6.3, §6.4 and Architecture §5.
     */
    public boolean deactivatePlan(String planId) {
        InsurancePlan p = findPlanById(planId);
        if (p == null) {
            System.out.println("Error: Plan with ID '" + planId + "' not found.");
            return false;
        }
        if (!p.isAvailable()) {
            System.out.println("Notice: Plan '" + planId + "' is already deactivated.");
            return false;
        }
        p.setAvailable(false);

        actionStack.push("Deactivated Plan: " + planId);
        FileHandler.savePlans(this);

        System.out.println("Plan '" + planId + "' has been deactivated. Existing policies remain intact, but no new purchases are allowed.");
        return true;
    }

    public boolean reactivatePlan(String planId) {
        InsurancePlan p = findPlanById(planId);
        if (p == null) {
            System.out.println("Error: Plan with ID '" + planId + "' not found.");
            return false;
        }
        p.setAvailable(true);

        actionStack.push("Reactivated Plan: " + planId);
        FileHandler.savePlans(this);

        System.out.println("Plan '" + planId + "' is now AVAILABLE for new purchases.");
        return true;
    }

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

        // Shift elements
        for (int i = index; i < planCount - 1; i++) {
            plans[i] = plans[i + 1];
        }
        plans[planCount - 1] = null;
        planCount--;

        actionStack.push("Removed Plan: " + planId);
        FileHandler.savePlans(this);

        System.out.println("Plan '" + planId + "' has been physically removed.");
        return true;
    }

    // =========================================================================
    // 3. PURCHASED POLICY OPERATIONS
    // =========================================================================

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
            System.out.println("Error: Insurance plan with ID '" + planId + "' does not exist.");
            return null;
        }

        if (!plan.isAvailable()) {
            System.out.println("Error: Plan '" + plan.getPlanName() + " (" + planId + ")' has been deactivated and is not available for new purchases.");
            return null;
        }

        double calculatedPremium = plan.calculatePremium(customer);
        LocalDate purchaseDate = LocalDate.now();
        LocalDate startDate = purchaseDate;
        LocalDate expiryDate = startDate.plusMonths(plan.getDurationMonths());

        String policyNumber = "POL" + nextPolicyNumber++;
        PurchasedPolicy newPolicy = new PurchasedPolicy(policyNumber, customer.getCustomerId(), plan.getPlanId(),
                "PENDING_PAYMENT", "PENDING", calculatedPremium, plan.getCoverageAmount(),
                purchaseDate, startDate, expiryDate);

        policies[policyCount++] = newPolicy;

        actionStack.push("Purchased Policy: " + policyNumber + " for " + customer.getName() + " (" + planId + ")");
        FileHandler.savePolicies(this);

        System.out.println("\nPolicy Enrolled Successfully!");
        System.out.println("-------------------------------------------------------------");
        System.out.println("Policy Number       : " + policyNumber);
        System.out.println("Customer Name       : " + customer.getName() + " (" + customer.getCustomerId() + ")");
        System.out.println("Plan Name           : " + plan.getPlanName() + " (" + plan.getPlanId() + ")");
        System.out.println("Coverage Amount     : Rs " + plan.getCoverageAmount());
        System.out.println("Calculated Premium  : Rs " + calculatedPremium);
        System.out.println("Duration            : " + plan.getDurationMonths() + " months");
        System.out.println("Start Date          : " + startDate);
        System.out.println("Expiry Date         : " + expiryDate);
        System.out.println("Policy Status       : PENDING_PAYMENT");
        System.out.println("Payment Status      : PENDING (Simulate payment to activate)");
        System.out.println("-------------------------------------------------------------");
        return newPolicy;
    }

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
     * Checks all policies against referenceDate and refreshes status to EXPIRED.
     * Complies with PRD §6.10, §15 and Architecture §8.
     */
    public synchronized int checkAndRefreshPolicyStatuses(LocalDate referenceDate) {
        if (referenceDate == null) referenceDate = LocalDate.now();
        int newlyExpired = 0;
        for (int i = 0; i < policyCount; i++) {
            PurchasedPolicy p = policies[i];
            if (p != null && !"CANCELLED".equalsIgnoreCase(p.getStatus())) {
                if (p.getExpiryDate() != null && referenceDate.isAfter(p.getExpiryDate())) {
                    if (!"EXPIRED".equalsIgnoreCase(p.getStatus())) {
                        p.setStatus("EXPIRED");
                        newlyExpired++;
                    }
                }
            }
        }
        if (newlyExpired > 0) {
            FileHandler.savePolicies(this);
        }
        return newlyExpired;
    }

    public void displayAllPurchasedPolicies() {
        checkAndRefreshPolicyStatuses(LocalDate.now());
        if (policyCount == 0) {
            System.out.println("No policies purchased yet.");
            return;
        }
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-10s %-10s %-16s %-10s %-12s %-12s %-12s %-12s\n",
                "Policy No", "Cust ID", "Plan ID", "Status", "Payment", "Premium (Rs)", "Coverage", "Start Date", "Expiry Date");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < policyCount; i++) {
            PurchasedPolicy p = policies[i];
            System.out.printf("%-12s %-10s %-10s %-16s %-10s %-12.2f %-12.2f %-12s %-12s\n",
                    p.getPolicyNumber(), p.getCustomerId(), p.getPlanId(), p.getStatus(), p.getPaymentStatus(),
                    p.getPremium(), p.getCoverageAmount(), p.getStartDate(), p.getExpiryDate());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
    }

    public void displayPoliciesByStatus(String statusFilter) {
        checkAndRefreshPolicyStatuses(LocalDate.now());
        boolean found = false;
        System.out.println("\n--- POLICIES WITH STATUS: " + statusFilter.toUpperCase() + " ---");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-10s %-10s %-16s %-10s %-12s %-12s %-12s %-12s\n",
                "Policy No", "Cust ID", "Plan ID", "Status", "Payment", "Premium (Rs)", "Coverage", "Start Date", "Expiry Date");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < policyCount; i++) {
            PurchasedPolicy p = policies[i];
            if (p != null && (statusFilter == null || statusFilter.equalsIgnoreCase(p.getStatus()))) {
                System.out.printf("%-12s %-10s %-10s %-16s %-10s %-12.2f %-12.2f %-12s %-12s\n",
                        p.getPolicyNumber(), p.getCustomerId(), p.getPlanId(), p.getStatus(), p.getPaymentStatus(),
                        p.getPremium(), p.getCoverageAmount(), p.getStartDate(), p.getExpiryDate());
                found = true;
            }
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No policies found with status: " + statusFilter);
        }
    }

    public void displayPoliciesByCustomer(String customerId) {
        checkAndRefreshPolicyStatuses(LocalDate.now());
        Customer c = findCustomerById(customerId);
        if (c == null) {
            System.out.println("Customer ID '" + customerId + "' not found.");
            return;
        }
        boolean found = false;
        System.out.println("\n--- POLICIES FOR CUSTOMER: " + c.getName() + " (" + customerId + ") ---");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-10s %-16s %-10s %-12s %-12s %-12s %-12s\n",
                "Policy No", "Plan ID", "Status", "Payment", "Premium (Rs)", "Coverage", "Start Date", "Expiry Date");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < policyCount; i++) {
            PurchasedPolicy p = policies[i];
            if (p != null && customerId.equalsIgnoreCase(p.getCustomerId())) {
                System.out.printf("%-12s %-10s %-16s %-10s %-12.2f %-12.2f %-12s %-12s\n",
                        p.getPolicyNumber(), p.getPlanId(), p.getStatus(), p.getPaymentStatus(),
                        p.getPremium(), p.getCoverageAmount(), p.getStartDate(), p.getExpiryDate());
                found = true;
            }
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No policies found for this customer.");
        }
    }

    public void displayPayablePolicies(String customerId) {
        checkAndRefreshPolicyStatuses(LocalDate.now());
        boolean found = false;
        System.out.println("\n--- PENDING PAYABLE POLICIES ---");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-10s %-12s %-15s %-15s\n", "Policy No", "Plan ID", "Status", "Premium Due", "Expiry Date");
        System.out.println("------------------------------------------------------------------------------------");
        for (int i = 0; i < policyCount; i++) {
            PurchasedPolicy p = policies[i];
            if (p != null && (customerId == null || customerId.equalsIgnoreCase(p.getCustomerId()))) {
                if ("PENDING".equalsIgnoreCase(p.getPaymentStatus()) && !"CANCELLED".equalsIgnoreCase(p.getStatus())) {
                    System.out.printf("%-12s %-10s %-12s Rs %-12.2f %-15s\n",
                            p.getPolicyNumber(), p.getPlanId(), p.getStatus(), p.getPremium(), p.getExpiryDate());
                    found = true;
                }
            }
        }
        System.out.println("------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No pending premium dues found.");
        }
    }

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

        InsurancePlan plan = findPlanById(p.getPlanId());
        if (plan != null && !plan.isAvailable()) {
            System.out.println("Error: Cannot renew. The linked plan '" + plan.getPlanName() + "' is no longer available.");
            return false;
        }

        int durationMonths = (plan != null) ? plan.getDurationMonths() : 12;
        LocalDate currentExp = p.getExpiryDate();
        LocalDate baseDate = (currentExp != null && currentExp.isAfter(LocalDate.now())) ? currentExp : LocalDate.now();
        LocalDate newExpiry = baseDate.plusMonths(durationMonths);

        p.setExpiryDate(newExpiry);
        p.setStatus("PENDING_PAYMENT");
        p.setPaymentStatus("PENDING");

        actionStack.push("Renewed Policy: " + policyNumber + " (New Expiry: " + newExpiry + ")");
        FileHandler.savePolicies(this);

        System.out.println("Policy '" + policyNumber + "' renewed! New Expiry Date: " + newExpiry);
        System.out.println("Status is PENDING_PAYMENT. Please make the simulated premium payment of Rs " + p.getPremium() + " to activate.");
        return true;
    }

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

        actionStack.push("Cancelled Policy: " + policyNumber);
        FileHandler.savePolicies(this);

        System.out.println("Policy '" + policyNumber + "' has been successfully CANCELLED.");
        return true;
    }

    // =========================================================================
    // 4. PAYMENT OPERATIONS
    // =========================================================================

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

        if ("PAID".equalsIgnoreCase(policy.getPaymentStatus()) && "ACTIVE".equalsIgnoreCase(policy.getStatus())) {
            System.out.println("Notice: Policy '" + policyNumber + "' has already been paid and is currently ACTIVE.");
            return null;
        }

        if (amount < policy.getPremium()) {
            System.out.println("Validation Error: Payment amount (Rs " + amount + ") is less than required premium (Rs " + policy.getPremium() + ").");
            return null;
        }

        String paymentId = "PAY" + nextPaymentId++;
        Payment payment = new Payment(paymentId, policyNumber, policy.getCustomerId(), amount, "PENDING", LocalDate.now());

        boolean success = payment.makePayment(amount);
        payments[paymentCount++] = payment;

        if (success) {
            policy.setPaymentStatus("PAID");
            policy.setStatus("ACTIVE"); // Policy becomes ACTIVE upon successful payment!

            actionStack.push("Processed Payment: " + paymentId + " of Rs " + amount + " for Policy " + policyNumber);
            FileHandler.savePayments(this);
            FileHandler.savePolicies(this);

            System.out.println("\nPayment Processed Successfully!");
            System.out.println("----------------------------------------------");
            System.out.println("Transaction ID      : " + paymentId);
            System.out.println("Policy Number       : " + policyNumber);
            System.out.println("Customer ID         : " + policy.getCustomerId());
            System.out.println("Amount Paid         : Rs " + amount);
            System.out.println("Payment Date        : " + payment.getPaymentDate());
            System.out.println("Policy Status       : ACTIVE");
            System.out.println("----------------------------------------------");
        } else {
            System.out.println("Payment transaction failed for Policy " + policyNumber);
        }
        return payment;
    }

    public void displayAllPayments() {
        if (paymentCount == 0) {
            System.out.println("No payment records found.");
            return;
        }
        System.out.println("\n-------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-15s %-12s %-15s %-15s %-12s\n", "Payment ID", "Policy No", "Cust ID", "Amount (Rs)", "Status", "Date");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (int i = 0; i < paymentCount; i++) {
            Payment p = payments[i];
            System.out.printf("%-12s %-15s %-12s Rs %-12.2f %-15s %-12s\n",
                    p.getPaymentId(), p.getPolicyNumber(), p.getCustomerId(), p.getAmount(), p.getStatus(), p.getPaymentDate());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    public void displayPaymentsByPolicy(String policyNumber) {
        boolean found = false;
        System.out.println("\n--- PAYMENT HISTORY FOR POLICY: " + policyNumber + " ---");
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-15s %-12s %-15s %-15s %-12s\n", "Payment ID", "Policy No", "Cust ID", "Amount (Rs)", "Status", "Date");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (int i = 0; i < paymentCount; i++) {
            Payment p = payments[i];
            if (p != null && policyNumber.equalsIgnoreCase(p.getPolicyNumber())) {
                System.out.printf("%-12s %-15s %-12s Rs %-12.2f %-15s %-12s\n",
                        p.getPaymentId(), p.getPolicyNumber(), p.getCustomerId(), p.getAmount(), p.getStatus(), p.getPaymentDate());
                found = true;
            }
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No payments recorded for this policy.");
        }
    }

    // =========================================================================
    // 5. CLAIM OPERATIONS
    // =========================================================================

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

        checkAndRefreshPolicyStatuses(LocalDate.now());

        if (!"ACTIVE".equalsIgnoreCase(policy.getStatus())) {
            System.out.println("Validation Error: Cannot submit claim. Policy status is '" + policy.getStatus() + "' (Must be ACTIVE).");
            return null;
        }

        if (!"PAID".equalsIgnoreCase(policy.getPaymentStatus())) {
            System.out.println("Validation Error: Premium payment for Policy '" + policyNumber + "' is pending. Claims require paid premium.");
            return null;
        }

        if (claimAmount <= 0) {
            System.out.println("Validation Error: Claim amount must be greater than 0.");
            return null;
        }

        if (claimAmount > policy.getCoverageAmount()) {
            System.out.println("Validation Error: Requested claim amount (Rs " + claimAmount + ") exceeds maximum policy coverage (Rs " + policy.getCoverageAmount() + ").");
            return null;
        }

        String claimId = "CLM" + nextClaimId++;
        Claim newClaim = new Claim(claimId, policyNumber, customerId, description.trim(),
                claimAmount, 0.0, "PENDING", LocalDate.now(), null, "Under administrative review");

        claims[claimCount++] = newClaim;

        actionStack.push("Submitted Claim: " + claimId + " for Policy " + policyNumber + " (Rs " + claimAmount + ")");
        FileHandler.saveClaims(this);

        System.out.println("\nClaim Submitted Successfully!");
        System.out.println("-------------------------------------------------------------");
        System.out.println("Claim ID          : " + claimId);
        System.out.println("Policy Number     : " + policyNumber);
        System.out.println("Customer          : " + customer.getName() + " (" + customerId + ")");
        System.out.println("Requested Amount  : Rs " + claimAmount);
        System.out.println("Policy Coverage   : Rs " + policy.getCoverageAmount());
        System.out.println("Description       : " + description);
        System.out.println("Submission Date   : " + newClaim.getSubmissionDate());
        System.out.println("Status            : PENDING (Under Admin Review)");
        System.out.println("-------------------------------------------------------------");
        return newClaim;
    }

    public Claim findClaimById(String claimId) {
        if (claimId == null) return null;
        for (int i = 0; i < claimCount; i++) {
            if (claims[i] != null && claimId.equalsIgnoreCase(claims[i].getClaimId())) {
                return claims[i];
            }
        }
        return null;
    }

    public boolean approveClaim(String claimId, double approvedAmount, String remarks) {
        Claim c = findClaimById(claimId);
        if (c == null) {
            System.out.println("Error: Claim ID '" + claimId + "' not found.");
            return false;
        }
        if (!"PENDING".equalsIgnoreCase(c.getStatus())) {
            System.out.println("Notice: Claim '" + claimId + "' has already been processed (Status: " + c.getStatus() + ").");
            return false;
        }

        if (approvedAmount <= 0) {
            System.out.println("Validation Error: Approved amount must be greater than 0.");
            return false;
        }
        if (approvedAmount > c.getClaimAmount()) {
            System.out.println("Validation Error: Approved amount (Rs " + approvedAmount + ") cannot exceed requested claim amount (Rs " + c.getClaimAmount() + ").");
            return false;
        }

        PurchasedPolicy pol = findPolicyByNumber(c.getPolicyNumber());
        if (pol != null && approvedAmount > pol.getCoverageAmount()) {
            System.out.println("Validation Error: Approved amount cannot exceed policy coverage limit of Rs " + pol.getCoverageAmount());
            return false;
        }

        c.setApprovedAmount(approvedAmount);
        c.setStatus("APPROVED");
        c.setDecisionDate(LocalDate.now());
        c.setRemarks((remarks != null && !remarks.trim().isEmpty()) ? remarks.trim() : "Approved by administrator");

        actionStack.push("Approved Claim: " + claimId + " for Rs " + approvedAmount);
        FileHandler.saveClaims(this);

        System.out.println("Claim '" + claimId + "' has been APPROVED for Rs " + approvedAmount);
        return true;
    }

    public boolean rejectClaim(String claimId, String remarks) {
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
        c.setApprovedAmount(0.0);
        c.setDecisionDate(LocalDate.now());
        c.setRemarks((remarks != null && !remarks.trim().isEmpty()) ? remarks.trim() : "Rejected by administrator");

        actionStack.push("Rejected Claim: " + claimId);
        FileHandler.saveClaims(this);

        System.out.println("Claim '" + claimId + "' has been REJECTED. Reason: " + c.getRemarks());
        return true;
    }

    public void displayAllClaims() {
        if (claimCount == 0) {
            System.out.println("No claims submitted yet.");
            return;
        }
        System.out.println("\n--------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-10s %-25s %-15s %-15s %-12s %-12s\n",
                "Claim ID", "Policy No", "Cust ID", "Description", "Requested (Rs)", "Approved (Rs)", "Status", "Date");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < claimCount; i++) {
            Claim c = claims[i];
            System.out.printf("%-10s %-12s %-10s %-25s Rs %-12.2f Rs %-12.2f %-12s %-12s\n",
                    c.getClaimId(), c.getPolicyNumber(), c.getCustomerId(), c.getDescription(),
                    c.getClaimAmount(), c.getApprovedAmount(), c.getStatus(), c.getSubmissionDate());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
    }

    public void displayPendingClaims() {
        boolean found = false;
        System.out.println("\n--- PENDING CLAIMS FOR REVIEW ---");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-10s %-25s %-15s %-12s\n",
                "Claim ID", "Policy No", "Cust ID", "Description", "Requested (Rs)", "Submission Date");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (int i = 0; i < claimCount; i++) {
            Claim c = claims[i];
            if (c != null && "PENDING".equalsIgnoreCase(c.getStatus())) {
                System.out.printf("%-10s %-12s %-10s %-25s Rs %-12.2f %-12s\n",
                        c.getClaimId(), c.getPolicyNumber(), c.getCustomerId(), c.getDescription(),
                        c.getClaimAmount(), c.getSubmissionDate());
                found = true;
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No pending claims waiting for review.");
        }
    }

    public void displayClaimsByCustomer(String customerId) {
        Customer cust = findCustomerById(customerId);
        if (cust == null) {
            System.out.println("Customer ID '" + customerId + "' not found.");
            return;
        }
        boolean found = false;
        System.out.println("\n--- CLAIMS FOR CUSTOMER: " + cust.getName() + " (" + customerId + ") ---");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-12s %-22s %-15s %-15s %-12s %-20s\n",
                "Claim ID", "Policy No", "Description", "Requested (Rs)", "Approved (Rs)", "Status", "Remarks");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < claimCount; i++) {
            Claim c = claims[i];
            if (c != null && customerId.equalsIgnoreCase(c.getCustomerId())) {
                System.out.printf("%-10s %-12s %-22s Rs %-12.2f Rs %-12.2f %-12s %-20s\n",
                        c.getClaimId(), c.getPolicyNumber(), c.getDescription(),
                        c.getClaimAmount(), c.getApprovedAmount(), c.getStatus(), c.getRemarks());
                found = true;
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
        if (!found) {
            System.out.println("No claims found for this customer.");
        }
    }

    public void displayClaimDetails(String claimId) {
        Claim c = findClaimById(claimId);
        if (c == null) {
            System.out.println("Claim ID '" + claimId + "' not found.");
            return;
        }
        System.out.println("\n--- CLAIM AUDIT DETAILS ---");
        System.out.println("Claim ID         : " + c.getClaimId());
        System.out.println("Policy Number    : " + c.getPolicyNumber());
        System.out.println("Customer ID      : " + c.getCustomerId());
        System.out.println("Description      : " + c.getDescription());
        System.out.println("Requested Amount : Rs " + c.getClaimAmount());
        System.out.println("Approved Amount  : Rs " + c.getApprovedAmount());
        System.out.println("Status           : " + c.getStatus());
        System.out.println("Submission Date  : " + c.getSubmissionDate());
        System.out.println("Decision Date    : " + (c.getDecisionDate() != null ? c.getDecisionDate() : "Pending"));
        System.out.println("Admin Remarks    : " + c.getRemarks());
    }

    // =========================================================================
    // 6. ACTION STACK & REVERSIBLE UNDO OPERATION
    // =========================================================================

    public ActionStack getActionStack() {
        return actionStack;
    }

    public boolean undoLastPlanAddition() {
        if (actionStack.isEmpty()) {
            System.out.println("Undo Failed: Action stack is empty. No operations to undo.");
            return false;
        }

        String topAction = actionStack.peek();
        if (topAction == null || !topAction.startsWith("Added Plan: ")) {
            System.out.println("Undo Limitation: Only the most recent Plan Addition can be undone.");
            System.out.println("Top action on stack is: \"" + topAction + "\" (Non-reversible via this operation).");
            return false;
        }

        int start = "Added Plan: ".length();
        int spaceIdx = topAction.indexOf(" ", start);
        String planId = (spaceIdx != -1) ? topAction.substring(start, spaceIdx) : topAction.substring(start);

        for (int i = 0; i < policyCount; i++) {
            if (policies[i] != null && planId.equalsIgnoreCase(policies[i].getPlanId())) {
                System.out.println("Cannot Undo: Policies have already been purchased under Plan '" + planId + "'.");
                return false;
            }
        }

        boolean removed = removePlan(planId);
        if (removed) {
            actionStack.pop();
            actionStack.pop();
            actionStack.push("Undone Addition of Plan: " + planId);
            System.out.println("SUCCESS: Undo completed. Plan '" + planId + "' was rolled back.");
            return true;
        }
        return false;
    }

    // =========================================================================
    // 7. ADMIN DASHBOARD & ASYNCHRONOUS REPORT GENERATOR
    // =========================================================================

    public void displayDashboard() {
        checkAndRefreshPolicyStatuses(LocalDate.now());

        int activePolicies = 0;
        int expiredPolicies = 0;
        int cancelledPolicies = 0;
        int pendingPaymentPolicies = 0;
        for (int i = 0; i < policyCount; i++) {
            if ("ACTIVE".equalsIgnoreCase(policies[i].getStatus())) activePolicies++;
            else if ("EXPIRED".equalsIgnoreCase(policies[i].getStatus())) expiredPolicies++;
            else if ("CANCELLED".equalsIgnoreCase(policies[i].getStatus())) cancelledPolicies++;
            else if ("PENDING_PAYMENT".equalsIgnoreCase(policies[i].getStatus())) pendingPaymentPolicies++;
        }

        int successfulPayments = 0;
        double totalRevenue = 0.0;
        for (int i = 0; i < paymentCount; i++) {
            if ("SUCCESSFUL".equalsIgnoreCase(payments[i].getStatus()) || "SUCCESS".equalsIgnoreCase(payments[i].getStatus())) {
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
                totalApprovedClaimAmount += claims[i].getApprovedAmount();
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
        System.out.println("   - Pending Payment        : " + pendingPaymentPolicies);
        System.out.println("   - Expired Policies       : " + expiredPolicies);
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

    public void generateAsyncReport() {
        checkAndRefreshPolicyStatuses(LocalDate.now());

        int activePolicies = 0;
        int cancelledPolicies = 0;
        for (int i = 0; i < policyCount; i++) {
            if ("ACTIVE".equalsIgnoreCase(policies[i].getStatus())) activePolicies++;
            else if ("CANCELLED".equalsIgnoreCase(policies[i].getStatus())) cancelledPolicies++;
        }

        int successfulPayments = 0;
        double totalRevenue = 0.0;
        for (int i = 0; i < paymentCount; i++) {
            if ("SUCCESSFUL".equalsIgnoreCase(payments[i].getStatus()) || "SUCCESS".equalsIgnoreCase(payments[i].getStatus())) {
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
                totalApprovedClaimAmount += claims[i].getApprovedAmount();
            } else if ("REJECTED".equalsIgnoreCase(claims[i].getStatus())) rejectedClaims++;
        }

        ReportTask reportTask = new ReportTask(customerCount, planCount, policyCount,
                activePolicies, cancelledPolicies, successfulPayments, totalRevenue,
                claimCount, pendingClaims, approvedClaims, rejectedClaims, totalApprovedClaimAmount);

        reportTask.start();
        try {
            reportTask.join();
        } catch (InterruptedException e) {
            System.out.println("[Main Thread] Interrupted while waiting for ReportTask: " + e.getMessage());
        }
    }

    // =========================================================================
    // 8. DATA LOADERS & ARRAY ACCESSORS
    // =========================================================================

    public Customer[] getCustomers() { return customers; }
    public int getCustomerCount() { return customerCount; }

    public InsurancePlan[] getPlans() { return plans; }
    public int getPlanCount() { return planCount; }

    public PurchasedPolicy[] getPolicies() { return policies; }
    public int getPolicyCount() { return policyCount; }

    public Payment[] getPayments() { return payments; }
    public int getPaymentCount() { return paymentCount; }

    public Claim[] getClaims() { return claims; }
    public int getClaimCount() { return claimCount; }

    public boolean addLoadedCustomer(Customer c) {
        if (customerCount < MAX_CUSTOMERS && c != null && findCustomerById(c.getCustomerId()) == null) {
            customers[customerCount++] = c;
            return true;
        }
        return false;
    }

    public boolean addLoadedPlan(InsurancePlan p) {
        if (planCount < MAX_PLANS && p != null && findPlanById(p.getPlanId()) == null) {
            plans[planCount++] = p;
            return true;
        }
        return false;
    }

    public boolean addLoadedPolicy(PurchasedPolicy p) {
        if (policyCount < MAX_POLICIES && p != null && findPolicyByNumber(p.getPolicyNumber()) == null) {
            policies[policyCount++] = p;
            return true;
        }
        return false;
    }

    public boolean addLoadedPayment(Payment p) {
        if (paymentCount < MAX_PAYMENTS && p != null) {
            payments[paymentCount++] = p;
            return true;
        }
        return false;
    }

    public boolean addLoadedClaim(Claim c) {
        if (claimCount < MAX_CLAIMS && c != null && findClaimById(c.getClaimId()) == null) {
            claims[claimCount++] = c;
            return true;
        }
        return false;
    }

    public void syncCounters() {
        for (int i = 0; i < customerCount; i++) {
            if (customers[i] != null && customers[i].getCustomerId().startsWith("C")) {
                try {
                    int num = Integer.parseInt(customers[i].getCustomerId().substring(1));
                    if (num >= nextCustomerId) nextCustomerId = num + 1;
                } catch (Exception ignored) {}
            }
        }

        for (int i = 0; i < policyCount; i++) {
            if (policies[i] != null && policies[i].getPolicyNumber().startsWith("POL")) {
                try {
                    int num = Integer.parseInt(policies[i].getPolicyNumber().substring(3));
                    if (num >= nextPolicyNumber) nextPolicyNumber = num + 1;
                } catch (Exception ignored) {}
            }
        }

        for (int i = 0; i < paymentCount; i++) {
            if (payments[i] != null && payments[i].getPaymentId().startsWith("PAY")) {
                try {
                    int num = Integer.parseInt(payments[i].getPaymentId().substring(3));
                    if (num >= nextPaymentId) nextPaymentId = num + 1;
                } catch (Exception ignored) {}
            }
        }

        for (int i = 0; i < claimCount; i++) {
            if (claims[i] != null && claims[i].getClaimId().startsWith("CLM")) {
                try {
                    int num = Integer.parseInt(claims[i].getClaimId().substring(3));
                    if (num >= nextClaimId) nextClaimId = num + 1;
                } catch (Exception ignored) {}
            }
        }
    }
}
