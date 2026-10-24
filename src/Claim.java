import java.time.LocalDate;

public class Claim {
    private String claimId;
    private String policyNumber;
    private String customerId;
    private String description;
    private double claimAmount;      // Requested amount
    private double approvedAmount;   // Amount approved by admin
    private String status;           // PENDING, APPROVED, REJECTED
    private LocalDate submissionDate;
    private LocalDate decisionDate;
    private String remarks;

    // Default constructor
    public Claim() {
        this.status = "PENDING";
        this.submissionDate = LocalDate.now();
        this.remarks = "Under review";
    }

    // Backwards-compatible constructor
    public Claim(String claimId, String policyNumber, String description, double claimAmount, String status) {
        this(claimId, policyNumber, "CUST", description, claimAmount, 
             "APPROVED".equalsIgnoreCase(status) ? claimAmount : 0.0, 
             status, LocalDate.now(), null, "Under review");
    }

    // Full constructor
    public Claim(String claimId, String policyNumber, String customerId, String description,
                 double claimAmount, double approvedAmount, String status,
                 LocalDate submissionDate, LocalDate decisionDate, String remarks) {
        this.claimId = claimId;
        this.policyNumber = policyNumber;
        this.customerId = (customerId != null) ? customerId : "CUST";
        this.description = description;
        setClaimAmount(claimAmount);
        setApprovedAmount(approvedAmount);
        this.status = status;
        this.submissionDate = (submissionDate != null) ? submissionDate : LocalDate.now();
        this.decisionDate = decisionDate;
        this.remarks = (remarks != null) ? remarks : "N/A";
    }

    // Getters and Setters
    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        if (claimAmount < 0) {
            this.claimAmount = 0.0;
        } else {
            this.claimAmount = claimAmount;
        }
    }

    public double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(double approvedAmount) {
        if (approvedAmount < 0) {
            this.approvedAmount = 0.0;
        } else {
            this.approvedAmount = approvedAmount;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDate getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(LocalDate decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Claim [ClaimId=" + claimId + ", PolicyNumber=" + policyNumber 
                + ", CustomerId=" + customerId + ", Requested=" + claimAmount 
                + ", Approved=" + approvedAmount + ", Status=" + status 
                + ", Submitted=" + submissionDate + ", Decided=" + decisionDate 
                + ", Remarks=" + remarks + "]";
    }
}
