public class Claim {
    private String claimId;
    private String policyNumber;
    private String description;
    private double claimAmount;
    private String status;

    // Default constructor
    public Claim() {
    }

    // Parameterized constructor
    public Claim(String claimId, String policyNumber, String description, double claimAmount, String status) {
        this.claimId = claimId;
        this.policyNumber = policyNumber;
        this.description = description;
        setClaimAmount(claimAmount);
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Claim [ClaimId=" + claimId + ", PolicyNumber=" + policyNumber 
                + ", Description=" + description + ", ClaimAmount=" + claimAmount 
                + ", Status=" + status + "]";
    }
}
