import java.io.*;
import java.time.LocalDate;

/**
 * FileHandler.java
 *
 * Implements Architecture §9 & PRD §10.9, §12.2: File Handling & Persistence
 * Manages saving and loading of all application records to delimited text files.
 * 
 * Features:
 * - Robust line-by-line parsing with backwards compatibility for legacy records
 * - Graceful fallback on missing or corrupted data files without crashing
 * - Safe directory creation and atomic resource closing
 */
public class FileHandler {

    private static final String DATA_DIR = "data";
    private static final String CUSTOMERS_FILE = DATA_DIR + File.separator + "customers.txt";
    private static final String PLANS_FILE = DATA_DIR + File.separator + "plans.txt";
    private static final String POLICIES_FILE = DATA_DIR + File.separator + "policies.txt";
    private static final String PAYMENTS_FILE = DATA_DIR + File.separator + "payments.txt";
    private static final String CLAIMS_FILE = DATA_DIR + File.separator + "claims.txt";
    private static final String DELIMITER = "|";

    /**
     * Ensures the data directory exists.
     */
    private static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // =========================================================================
    // SAVE OPERATIONS
    // =========================================================================

    /**
     * Saves all system records to text files.
     */
    public static boolean saveAll(InsuranceManager mgr) {
        if (mgr == null) return false;
        ensureDataDirectory();

        boolean cSaved = saveCustomers(mgr);
        boolean pSaved = savePlans(mgr);
        boolean polSaved = savePolicies(mgr);
        boolean paySaved = savePayments(mgr);
        boolean clmSaved = saveClaims(mgr);

        return cSaved && pSaved && polSaved && paySaved && clmSaved;
    }

    public static boolean saveCustomers(InsuranceManager mgr) {
        ensureDataDirectory();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE));
            Customer[] customers = mgr.getCustomers();
            int count = mgr.getCustomerCount();
            for (int i = 0; i < count; i++) {
                Customer c = customers[i];
                if (c != null) {
                    writer.write(c.getCustomerId() + DELIMITER
                            + c.getName() + DELIMITER
                            + c.getAge() + DELIMITER
                            + c.getPhone() + DELIMITER
                            + c.getEmail() + DELIMITER
                            + c.getAddress() + DELIMITER
                            + c.getRegistrationDate());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Failed to save customers: " + e.getMessage());
            return false;
        } finally {
            closeWriter(writer);
        }
    }

    public static boolean savePlans(InsuranceManager mgr) {
        ensureDataDirectory();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(PLANS_FILE));
            InsurancePlan[] plans = mgr.getPlans();
            int count = mgr.getPlanCount();
            for (int i = 0; i < count; i++) {
                InsurancePlan p = plans[i];
                if (p != null) {
                    String type = mgr.getPlanType(p);
                    writer.write(type + DELIMITER
                            + p.getPlanId() + DELIMITER
                            + p.getPlanName() + DELIMITER
                            + p.getCoverageAmount() + DELIMITER
                            + p.getDurationMonths() + DELIMITER
                            + p.getBasePremium() + DELIMITER
                            + p.isAvailable());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Failed to save plans: " + e.getMessage());
            return false;
        } finally {
            closeWriter(writer);
        }
    }

    public static boolean savePolicies(InsuranceManager mgr) {
        ensureDataDirectory();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(POLICIES_FILE));
            PurchasedPolicy[] policies = mgr.getPolicies();
            int count = mgr.getPolicyCount();
            for (int i = 0; i < count; i++) {
                PurchasedPolicy p = policies[i];
                if (p != null) {
                    writer.write(p.getPolicyNumber() + DELIMITER
                            + p.getCustomerId() + DELIMITER
                            + p.getPlanId() + DELIMITER
                            + p.getStatus() + DELIMITER
                            + p.getPaymentStatus() + DELIMITER
                            + p.getPremium() + DELIMITER
                            + p.getCoverageAmount() + DELIMITER
                            + p.getPurchaseDate() + DELIMITER
                            + p.getStartDate() + DELIMITER
                            + p.getExpiryDate());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Failed to save policies: " + e.getMessage());
            return false;
        } finally {
            closeWriter(writer);
        }
    }

    public static boolean savePayments(InsuranceManager mgr) {
        ensureDataDirectory();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(PAYMENTS_FILE));
            Payment[] payments = mgr.getPayments();
            int count = mgr.getPaymentCount();
            for (int i = 0; i < count; i++) {
                Payment p = payments[i];
                if (p != null) {
                    writer.write(p.getPaymentId() + DELIMITER
                            + p.getPolicyNumber() + DELIMITER
                            + p.getCustomerId() + DELIMITER
                            + p.getAmount() + DELIMITER
                            + p.getStatus() + DELIMITER
                            + p.getPaymentDate());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Failed to save payments: " + e.getMessage());
            return false;
        } finally {
            closeWriter(writer);
        }
    }

    public static boolean saveClaims(InsuranceManager mgr) {
        ensureDataDirectory();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(CLAIMS_FILE));
            Claim[] claims = mgr.getClaims();
            int count = mgr.getClaimCount();
            for (int i = 0; i < count; i++) {
                Claim c = claims[i];
                if (c != null) {
                    String decDate = (c.getDecisionDate() != null) ? c.getDecisionDate().toString() : "null";
                    writer.write(c.getClaimId() + DELIMITER
                            + c.getPolicyNumber() + DELIMITER
                            + c.getCustomerId() + DELIMITER
                            + c.getDescription() + DELIMITER
                            + c.getClaimAmount() + DELIMITER
                            + c.getApprovedAmount() + DELIMITER
                            + c.getStatus() + DELIMITER
                            + c.getSubmissionDate() + DELIMITER
                            + decDate + DELIMITER
                            + c.getRemarks());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Failed to save claims: " + e.getMessage());
            return false;
        } finally {
            closeWriter(writer);
        }
    }

    // =========================================================================
    // LOAD OPERATIONS
    // =========================================================================

    /**
     * Loads all system records from text files into InsuranceManager.
     * Safely checks for file existence before attempting to read.
     */
    public static void loadAll(InsuranceManager mgr) {
        if (mgr == null) return;
        ensureDataDirectory();

        boolean plansLoaded = loadPlans(mgr);
        if (!plansLoaded) {
            // If plans file was absent or empty, seed default initial plans
            mgr.seedDefaultPlansIfEmpty();
        }

        loadCustomers(mgr);
        loadPolicies(mgr);
        loadPayments(mgr);
        loadClaims(mgr);

        // Calibrate auto-increment counters so new records get unique IDs
        mgr.syncCounters();
    }

    public static boolean loadCustomers(InsuranceManager mgr) {
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) {
            return false;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 5) {
                    try {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        int age = Integer.parseInt(parts[2].trim());
                        String phone = parts[3].trim();
                        String email = (parts.length >= 6 && !parts[4].trim().isEmpty()) ? parts[4].trim() : "customer@insurance.com";
                        String address = (parts.length >= 6) ? parts[5].trim() : parts[4].trim();
                        LocalDate regDate = LocalDate.now();
                        if (parts.length >= 7 && !parts[6].trim().isEmpty() && !parts[6].trim().equalsIgnoreCase("null")) {
                            try {
                                regDate = LocalDate.parse(parts[6].trim());
                            } catch (Exception ignored) {}
                        }

                        Customer c = new Customer(id, name, age, phone, email, address, regDate);
                        mgr.addLoadedCustomer(c);
                    } catch (NumberFormatException nfe) {
                        System.out.println("[FileHandler Warning] Skipping malformed customer record: " + line);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Error reading customers file: " + e.getMessage());
            return false;
        } finally {
            closeReader(reader);
        }
    }

    public static boolean loadPlans(InsuranceManager mgr) {
        File file = new File(PLANS_FILE);
        if (!file.exists()) {
            return false;
        }
        BufferedReader reader = null;
        boolean anyLoaded = false;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 6) {
                    try {
                        String type = parts[0].trim();
                        String planId = parts[1].trim();
                        String name = parts[2].trim();
                        double coverage = Double.parseDouble(parts[3].trim());
                        int duration = Integer.parseInt(parts[4].trim());
                        double basePrem = Double.parseDouble(parts[5].trim());
                        boolean available = (parts.length >= 7) ? Boolean.parseBoolean(parts[6].trim()) : true;

                        InsurancePlan plan = null;
                        if ("Health".equalsIgnoreCase(type)) {
                            plan = new HealthPlan(planId, name, coverage, duration, basePrem, available);
                        } else if ("Life".equalsIgnoreCase(type)) {
                            plan = new LifePlan(planId, name, coverage, duration, basePrem, available);
                        } else if ("Vehicle".equalsIgnoreCase(type)) {
                            plan = new VehiclePlan(planId, name, coverage, duration, basePrem, available);
                        } else if ("Travel".equalsIgnoreCase(type)) {
                            plan = new TravelPlan(planId, name, coverage, duration, basePrem, available);
                        }

                        if (plan != null) {
                            mgr.addLoadedPlan(plan);
                            anyLoaded = true;
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("[FileHandler Warning] Skipping malformed plan record: " + line);
                    }
                }
            }
            return anyLoaded;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Error reading plans file: " + e.getMessage());
            return false;
        } finally {
            closeReader(reader);
        }
    }

    public static boolean loadPolicies(InsuranceManager mgr) {
        File file = new File(POLICIES_FILE);
        if (!file.exists()) return false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 4) {
                    try {
                        String polNo = parts[0].trim();
                        String custId = parts[1].trim();
                        String planId = parts[2].trim();
                        String status = parts[3].trim();
                        String paymentStatus = (parts.length >= 5) ? parts[4].trim() : ("ACTIVE".equalsIgnoreCase(status) ? "PAID" : "PENDING");
                        double premium = (parts.length >= 6) ? Double.parseDouble(parts[5].trim()) : 0.0;
                        double coverage = (parts.length >= 7) ? Double.parseDouble(parts[6].trim()) : 0.0;
                        LocalDate purDate = (parts.length >= 8 && !parts[7].trim().isEmpty()) ? LocalDate.parse(parts[7].trim()) : LocalDate.now();
                        LocalDate startDate = (parts.length >= 9 && !parts[8].trim().isEmpty()) ? LocalDate.parse(parts[8].trim()) : LocalDate.now();
                        LocalDate expDate = (parts.length >= 10 && !parts[9].trim().isEmpty()) ? LocalDate.parse(parts[9].trim()) : startDate.plusMonths(12);

                        PurchasedPolicy p = new PurchasedPolicy(polNo, custId, planId, status, paymentStatus, premium, coverage, purDate, startDate, expDate);
                        mgr.addLoadedPolicy(p);
                    } catch (Exception e) {
                        System.out.println("[FileHandler Warning] Skipping malformed policy record: " + line);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Error reading policies file: " + e.getMessage());
            return false;
        } finally {
            closeReader(reader);
        }
    }

    public static boolean loadPayments(InsuranceManager mgr) {
        File file = new File(PAYMENTS_FILE);
        if (!file.exists()) return false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 4) {
                    try {
                        String payId = parts[0].trim();
                        String polNo = parts[1].trim();
                        String custId = (parts.length >= 6) ? parts[2].trim() : "CUST";
                        double amount = (parts.length >= 6) ? Double.parseDouble(parts[3].trim()) : Double.parseDouble(parts[2].trim());
                        String status = (parts.length >= 6) ? parts[4].trim() : parts[3].trim();
                        LocalDate payDate = (parts.length >= 6 && !parts[5].trim().isEmpty()) ? LocalDate.parse(parts[5].trim()) : LocalDate.now();

                        Payment p = new Payment(payId, polNo, custId, amount, status, payDate);
                        mgr.addLoadedPayment(p);
                    } catch (Exception nfe) {
                        System.out.println("[FileHandler Warning] Skipping malformed payment record: " + line);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Error reading payments file: " + e.getMessage());
            return false;
        } finally {
            closeReader(reader);
        }
    }

    public static boolean loadClaims(InsuranceManager mgr) {
        File file = new File(CLAIMS_FILE);
        if (!file.exists()) return false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 5) {
                    try {
                        String claimId = parts[0].trim();
                        String polNo = parts[1].trim();
                        String custId = (parts.length >= 10) ? parts[2].trim() : "CUST";
                        String desc = (parts.length >= 10) ? parts[3].trim() : parts[2].trim();
                        double claimAmt = (parts.length >= 10) ? Double.parseDouble(parts[4].trim()) : Double.parseDouble(parts[3].trim());
                        double appAmt = (parts.length >= 10) ? Double.parseDouble(parts[5].trim()) : ("APPROVED".equalsIgnoreCase(parts[4].trim()) ? claimAmt : 0.0);
                        String status = (parts.length >= 10) ? parts[6].trim() : parts[4].trim();
                        LocalDate subDate = (parts.length >= 10 && !parts[7].trim().isEmpty() && !parts[7].trim().equalsIgnoreCase("null")) ? LocalDate.parse(parts[7].trim()) : LocalDate.now();
                        LocalDate decDate = (parts.length >= 10 && !parts[8].trim().isEmpty() && !parts[8].trim().equalsIgnoreCase("null")) ? LocalDate.parse(parts[8].trim()) : null;
                        String remarks = (parts.length >= 10) ? parts[9].trim() : "N/A";

                        Claim c = new Claim(claimId, polNo, custId, desc, claimAmt, appAmt, status, subDate, decDate, remarks);
                        mgr.addLoadedClaim(c);
                    } catch (Exception nfe) {
                        System.out.println("[FileHandler Warning] Skipping malformed claim record: " + line);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[FileHandler Error] Error reading claims file: " + e.getMessage());
            return false;
        } finally {
            closeReader(reader);
        }
    }

    // =========================================================================
    // RESOURCE CLOSING HELPERS
    // =========================================================================

    private static void closeWriter(BufferedWriter w) {
        if (w != null) {
            try {
                w.close();
            } catch (IOException e) {
                System.out.println("[FileHandler Error] Could not close writer: " + e.getMessage());
            }
        }
    }

    private static void closeReader(BufferedReader r) {
        if (r != null) {
            try {
                r.close();
            } catch (IOException e) {
                System.out.println("[FileHandler Error] Could not close reader: " + e.getMessage());
            }
        }
    }
}
