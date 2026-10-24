import java.time.LocalDate;

public class PurchasedPolicy {
    private String policyNumber;
    private String customerId;
    private String planId;
    private String status;           // PENDING_PAYMENT, ACTIVE, EXPIRED, CANCELLED
    private String paymentStatus;    // PENDING, PAID
    private double premium;
    private double coverageAmount;
    private LocalDate purchaseDate;
    private LocalDate startDate;
    private LocalDate expiryDate;

    // Default constructor
    public PurchasedPolicy() {
        this.purchaseDate = LocalDate.now();
        this.startDate = LocalDate.now();
        this.expiryDate = LocalDate.now().plusMonths(12);
        this.status = "PENDING_PAYMENT";
        this.paymentStatus = "PENDING";
    }

    // Backwards-compatible constructor
    public PurchasedPolicy(String policyNumber, String customerId, String planId, String status) {
        this(policyNumber, customerId, planId, status, 
             "ACTIVE".equalsIgnoreCase(status) ? "PAID" : "PENDING", 
             0.0, 0.0, LocalDate.now(), LocalDate.now(), LocalDate.now().plusMonths(12));
    }

    // Full constructor
    public PurchasedPolicy(String policyNumber, String customerId, String planId, String status,
                           String paymentStatus, double premium, double coverageAmount,
                           LocalDate purchaseDate, LocalDate startDate, LocalDate expiryDate) {
        this.policyNumber = policyNumber;
        this.customerId = customerId;
        this.planId = planId;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.premium = premium;
        this.coverageAmount = coverageAmount;
        this.purchaseDate = (purchaseDate != null) ? purchaseDate : LocalDate.now();
        this.startDate = (startDate != null) ? startDate : LocalDate.now();
        this.expiryDate = (expiryDate != null) ? expiryDate : this.startDate.plusMonths(12);
    }

    // Getters and Setters
    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = (premium < 0) ? 0.0 : premium;
    }

    public double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(double coverageAmount) {
        this.coverageAmount = (coverageAmount < 0) ? 0.0 : coverageAmount;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Checks if the policy has passed its expiry date as of a reference date.
     */
    public boolean checkAndRefreshExpiry(LocalDate referenceDate) {
        if ("CANCELLED".equalsIgnoreCase(status)) {
            return false; // Cancelled policies remain cancelled
        }
        if (expiryDate != null && referenceDate != null && referenceDate.isAfter(expiryDate)) {
            this.status = "EXPIRED";
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "PurchasedPolicy [PolicyNumber=" + policyNumber + ", CustomerId=" + customerId 
                + ", PlanId=" + planId + ", Status=" + status + ", PaymentStatus=" + paymentStatus
                + ", Premium=" + premium + ", Coverage=" + coverageAmount 
                + ", StartDate=" + startDate + ", ExpiryDate=" + expiryDate + "]";
    }
}
