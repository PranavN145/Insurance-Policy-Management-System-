public class HealthPlan extends InsurancePlan {

    public HealthPlan() {
        super();
    }

    public HealthPlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }

    /**
     * Calculates premium for Health Insurance.
     * Illustrative formula for academic OOP project demonstration only.
     * Not for real-world insurance pricing.
     *
     * Illustrative formula:
     * - Starting amount: basePremium
     * - Age > 50: 20% illustrative age risk factor (multiplier 1.20)
     * - Age between 31 and 50: 10% illustrative age risk factor (multiplier 1.10)
     * - Age <= 30: standard basePremium (multiplier 1.00)
     */
    @Override
    public double calculatePremium(Customer customer) {
        double premium = getBasePremium();
        if (customer != null) {
            int age = customer.getAge();
            if (age > 50) {
                premium = getBasePremium() * 1.20;
            } else if (age > 30) {
                premium = getBasePremium() * 1.10;
            } else {
                premium = getBasePremium() * 1.00;
            }
        }
        return premium;
    }
}
