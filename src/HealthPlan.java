public class HealthPlan extends InsurancePlan {

    public HealthPlan() {
        super();
    }

    public HealthPlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }

    public HealthPlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium, boolean available) {
        super(planId, planName, coverageAmount, durationMonths, basePremium, available);
    }

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
