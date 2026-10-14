public class LifePlan extends InsurancePlan {

    public LifePlan() {
        super();
    }

    public LifePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }

    /**
     * Calculates premium for Life Insurance.
     * Illustrative formula for academic OOP project demonstration only.
     * Not for real-world insurance pricing.
     *
     * Illustrative formula:
     * - Starting amount: basePremium
     * - Age > 60: 40% illustrative mortality risk factor (multiplier 1.40)
     * - Age between 41 and 60: 20% illustrative mortality risk factor (multiplier 1.20)
     * - Age <= 40: standard basePremium (multiplier 1.00)
     */
    @Override
    public double calculatePremium(Customer customer) {
        double premium = getBasePremium();
        if (customer != null) {
            int age = customer.getAge();
            if (age > 60) {
                premium = getBasePremium() * 1.40;
            } else if (age > 40) {
                premium = getBasePremium() * 1.20;
            } else {
                premium = getBasePremium() * 1.00;
            }
        }
        return premium;
    }
}
