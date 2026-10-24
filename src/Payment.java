import java.time.LocalDate;

public class Payment implements Payable {
    private String paymentId;
    private String policyNumber;
    private String customerId;
    private double amount;
    private String status;           // PENDING, SUCCESSFUL, FAILED
    private LocalDate paymentDate;

    // Default constructor
    public Payment() {
        this.status = "PENDING";
        this.paymentDate = LocalDate.now();
    }

    // Backwards-compatible constructor
    public Payment(String paymentId, String policyNumber, double amount, String status) {
        this(paymentId, policyNumber, "CUST", amount, status, LocalDate.now());
    }

    // Full constructor
    public Payment(String paymentId, String policyNumber, String customerId, double amount, String status, LocalDate paymentDate) {
        this.paymentId = paymentId;
        this.policyNumber = policyNumber;
        this.customerId = (customerId != null) ? customerId : "CUST";
        setAmount(amount);
        this.status = ("SUCCESS".equalsIgnoreCase(status) || "SUCCESSFUL".equalsIgnoreCase(status)) ? "SUCCESSFUL" : status;
        this.paymentDate = (paymentDate != null) ? paymentDate : LocalDate.now();
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            this.amount = 0.0;
        } else {
            this.amount = amount;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Simulates payment processing using Payable interface.
     */
    @Override
    public boolean makePayment(double amount) {
        if (amount > 0) {
            this.amount = amount;
            this.status = "SUCCESSFUL";
            this.paymentDate = LocalDate.now();
            return true;
        } else {
            this.status = "FAILED";
            return false;
        }
    }

    @Override
    public String toString() {
        return "Payment [PaymentId=" + paymentId + ", PolicyNumber=" + policyNumber 
                + ", CustomerId=" + customerId + ", Amount=" + amount 
                + ", Status=" + status + ", Date=" + paymentDate + "]";
    }
}
