public class VehiclePlan extends InsurancePlan {

    public VehiclePlan() {
        super();
    }

    public VehiclePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }

    /**
     * Calculates premium for Vehicle Insurance.
     * Illustrative formula for academic OOP project demonstration only.
     * Not for real-world insurance pricing.
     *
     * Illustrative formula:
     * - Starting amount: basePremium
     * - Young / novice driver risk factor: if customer != null and age < 25 (and > 0), +15% of basePremium.
     * - Multi-year policy adjustment: if durationMonths > 12, +10% of basePremium for multi-year coverage.
     */
    @Override
    public double calculatePremium(Customer customer) {
        double premium = getBasePremium();
        if (customer != null && customer.getAge() < 25 && customer.getAge() > 0) {
            premium += getBasePremium() * 0.15;
        }
        if (getDurationMonths() > 12) {
            premium += getBasePremium() * 0.10;
        }
        return premium;
    }
}
