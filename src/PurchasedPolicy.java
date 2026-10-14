public class PurchasedPolicy {
    private String policyNumber;
    private String customerId;
    private String planId;
    private String status;

    // Default constructor
    public PurchasedPolicy() {
    }

    // Parameterized constructor
    public PurchasedPolicy(String policyNumber, String customerId, String planId, String status) {
        this.policyNumber = policyNumber;
        this.customerId = customerId;
        this.planId = planId;
        this.status = status;
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

    @Override
    public String toString() {
        return "PurchasedPolicy [PolicyNumber=" + policyNumber + ", CustomerId=" + customerId 
                + ", PlanId=" + planId + ", Status=" + status + "]";
    }
}
