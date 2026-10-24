public class LifePlan extends InsurancePlan {

    public LifePlan() {
        super();
    }

    public LifePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium) {
        super(planId, planName, coverageAmount, durationMonths, basePremium);
    }

    public LifePlan(String planId, String planName, double coverageAmount, int durationMonths, double basePremium, boolean available) {
        super(planId, planName, coverageAmount, durationMonths, basePremium, available);
    }

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
