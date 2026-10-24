import java.time.LocalDate;

public class Customer {
    private String customerId;
    private String name;
    private int age;
    private String phone;
    private String email;
    private String address;
    private LocalDate registrationDate;

    // Default constructor
    public Customer() {
        this.registrationDate = LocalDate.now();
    }

    // Constructor with basic contact info (backwards compatibility)
    public Customer(String customerId, String name, int age, String phone, String address) {
        this(customerId, name, age, phone, "customer@insurance.com", address, LocalDate.now());
    }

    // Parameterized constructor including email
    public Customer(String customerId, String name, int age, String phone, String email, String address) {
        this(customerId, name, age, phone, email, address, LocalDate.now());
    }

    // Full constructor
    public Customer(String customerId, String name, int age, String phone, String email, String address, LocalDate registrationDate) {
        this.customerId = customerId;
        this.name = name;
        setAge(age);
        this.phone = phone;
        this.email = (email != null && !email.trim().isEmpty()) ? email.trim() : "customer@insurance.com";
        this.address = address;
        this.registrationDate = (registrationDate != null) ? registrationDate : LocalDate.now();
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            this.age = 0;
        } else {
            this.age = age;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            this.email = email.trim();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "Customer [ID=" + customerId + ", Name=" + name + ", Age=" + age 
                + ", Phone=" + phone + ", Email=" + email + ", Address=" + address 
                + ", Registered=" + registrationDate + "]";
    }
}
