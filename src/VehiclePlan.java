public class VehiclePlan extends InsurancePlan {

    public VehiclePlan() {
        super();
    }

    public VehiclePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }

    public VehiclePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium, boolean available) {
        super(planId, planName, coverageAmount, durationMonths, basePremium, available);
    }

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
