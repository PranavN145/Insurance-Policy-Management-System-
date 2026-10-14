import java.io.*;

/**
 * FileHandler.java
 *
 * Implements Task 1 (Member 3): File Handling & Persistence
 * Reads and writes customer, plan, policy, payment, and claim records using:
 * - FileReader, BufferedReader
 * - FileWriter, BufferedWriter
 * - Delimited text files in a dedicated 'data/' directory
 *
 * Demonstrates:
 * - Safe file creation and directory verification
 * - Graceful handling of missing data files without application crash
 * - Fault-tolerant line-by-line parsing skipping malformed records
 * - Clean resource closing in finally blocks or try-with-resources
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
                            + c.getAddress());
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
                            + p.getBasePremium());
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
                            + p.getStatus());
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
                            + p.getAmount() + DELIMITER
                            + p.getStatus());
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
                    writer.write(c.getClaimId() + DELIMITER
                            + c.getPolicyNumber() + DELIMITER
                            + c.getDescription() + DELIMITER
                            + c.getClaimAmount() + DELIMITER
                            + c.getStatus());
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
            return false; // Safe return on missing file
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
                        String address = parts[4].trim();

                        Customer c = new Customer(id, name, age, phone, address);
                        mgr.addLoadedCustomer(c);
                    } catch (NumberFormatException nfe) {
                        System.out.println("[FileHandler Warning] Skipping malformed customer record: " + line);
                    }
                } else {
                    System.out.println("[FileHandler Warning] Incomplete customer record skipped: " + line);
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

                        InsurancePlan plan = null;
                        if ("Health".equalsIgnoreCase(type)) {
                            plan = new HealthPlan(planId, name, coverage, duration, basePrem);
                        } else if ("Life".equalsIgnoreCase(type)) {
                            plan = new LifePlan(planId, name, coverage, duration, basePrem);
                        } else if ("Vehicle".equalsIgnoreCase(type)) {
                            plan = new VehiclePlan(planId, name, coverage, duration, basePrem);
                        } else if ("Travel".equalsIgnoreCase(type)) {
                            plan = new TravelPlan(planId, name, coverage, duration, basePrem);
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
                    String polNo = parts[0].trim();
                    String custId = parts[1].trim();
                    String planId = parts[2].trim();
                    String status = parts[3].trim();

                    PurchasedPolicy p = new PurchasedPolicy(polNo, custId, planId, status);
                    mgr.addLoadedPolicy(p);
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
                        double amount = Double.parseDouble(parts[2].trim());
                        String status = parts[3].trim();

                        Payment p = new Payment(payId, polNo, amount, status);
                        mgr.addLoadedPayment(p);
                    } catch (NumberFormatException nfe) {
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
                        String desc = parts[2].trim();
                        double amount = Double.parseDouble(parts[3].trim());
                        String status = parts[4].trim();

                        Claim c = new Claim(claimId, polNo, desc, amount, status);
                        mgr.addLoadedClaim(c);
                    } catch (NumberFormatException nfe) {
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
    // CLEAN RESOURCE CLOSING HELPERS
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
