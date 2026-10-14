public class TravelPlan extends InsurancePlan {

    public TravelPlan() {
        super();
    }

    public TravelPlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }

    /**
     * Calculates premium for Travel Insurance.
     * Illustrative formula for academic OOP project demonstration only.
     * Not for real-world insurance pricing.
     *
     * Illustrative formula:
     * - Starting amount: basePremium
     * - Extended duration surcharge: if durationMonths > 6, +20% of basePremium.
     * - Senior traveler surcharge: if customer != null and age > 60, +15% of basePremium.
     */
    @Override
    public double calculatePremium(Customer customer) {
        double premium = getBasePremium();
        if (getDurationMonths() > 6) {
            premium += getBasePremium() * 0.20;
        }
        if (customer != null && customer.getAge() > 60) {
            premium += getBasePremium() * 0.15;
        }
        return premium;
    }
}
