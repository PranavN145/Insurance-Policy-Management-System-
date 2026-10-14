public class Payment implements Payable {
    private String paymentId;
    private String policyNumber;
    private double amount;
    private String status;

    // Default constructor
    public Payment() {
    }

    // Parameterized constructor
    public Payment(String paymentId, String policyNumber, double amount, String status) {
        this.paymentId = paymentId;
        this.policyNumber = policyNumber;
        setAmount(amount);
        this.status = status;
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

    /**
     * Simulates payment processing.
     * Accepts positive amounts, updates payment status, and returns true.
     * Rejects non-positive amounts and returns false.
     */
    @Override
    public boolean makePayment(double amount) {
        if (amount > 0) {
            this.amount = amount;
            this.status = "SUCCESS";
            System.out.println("Payment of " + amount + " processed successfully for Policy: " + this.policyNumber);
            return true;
        } else {
            this.status = "FAILED";
            System.out.println("Payment failed: Amount must be greater than 0.");
            return false;
        }
    }

    @Override
    public String toString() {
        return "Payment [PaymentId=" + paymentId + ", PolicyNumber=" + policyNumber 
                + ", Amount=" + amount + ", Status=" + status + "]";
    }
}
