public class TravelPlan extends InsurancePlan {

    public TravelPlan() {
        super();
    }

    public TravelPlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }

    public TravelPlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium, boolean available) {
        super(planId, planName, coverageAmount, durationMonths, basePremium, available);
    }

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
