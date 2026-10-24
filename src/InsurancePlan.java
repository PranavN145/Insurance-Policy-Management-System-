public abstract class InsurancePlan {
    private String planId;
    private String planName;
    private double coverageAmount;
    private int durationMonths;
    private double basePremium;
    private boolean available;

    // Default constructor
    public InsurancePlan() {
        this.available = true;
    }

    // Parameterized constructor
    public InsurancePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        this(planId, planName, coverageAmount, durationMonths, basePremium, true);
    }

    // Full constructor including availability
    public InsurancePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium, boolean available) {
        this.planId = planId;
        this.planName = planName;
        setCoverageAmount(coverageAmount);
        setDurationMonths(durationMonths);
        setBasePremium(basePremium);
        this.available = available;
    }

    // Getters and Setters
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(double coverageAmount) {
        if (coverageAmount < 0) {
            this.coverageAmount = 0.0;
        } else {
            this.coverageAmount = coverageAmount;
        }
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        if (durationMonths < 0) {
            this.durationMonths = 0;
        } else {
            this.durationMonths = durationMonths;
        }
    }

    public double getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(double basePremium) {
        if (basePremium < 0) {
            this.basePremium = 0.0;
        } else {
            this.basePremium = basePremium;
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Abstract method for premium calculation.
     * Demonstrates Method Overriding across subclasses.
     *
     * @param customer The customer for whom the premium is calculated.
     * @return The calculated premium amount.
     */
    public abstract double calculatePremium(Customer customer);

    /**
     * Overloaded method to calculate premium with a discount percentage.
     * Demonstrates Method Overloading (compile-time polymorphism).
     *
     * @param customer The customer for whom the premium is calculated.
     * @param discountPercent Promotional discount percentage (must be between 0 and 100).
     * @return The final premium amount after applying discount.
     */
    public double calculatePremium(Customer customer, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            System.out.println("Invalid discount percentage (" + discountPercent + "%). Discount must be between 0 and 100. Standard premium applied.");
            return calculatePremium(customer);
        }
        double normalPremium = calculatePremium(customer);
        double discountAmount = normalPremium * (discountPercent / 100.0);
        return normalPremium - discountAmount;
    }

    @Override
    public String toString() {
        return "InsurancePlan [PlanId=" + planId + ", Name=" + planName 
                + ", Coverage=" + coverageAmount + ", Duration=" + durationMonths 
                + " months, BasePremium=" + basePremium + ", Available=" + available + "]";
    }
}
