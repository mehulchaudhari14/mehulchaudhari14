import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Car {
    private String carId;
    private String brand;
    private String model;
    private double basePricePerDay;
    private boolean isAvailable;

    public Car(String carId, String brand, String model, double basePricePerDay) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.basePricePerDay = basePricePerDay;
        this.isAvailable = true;
    }

    public String getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double getBasePricePerDay() {
        return basePricePerDay;
    }

    public double calculatePrice(int rentalDays) {
        // Apply a discount for rentals longer than 7 days
        double discount = rentalDays > 7 ? 0.1 : 0.0;
        return basePricePerDay * rentalDays * (1 - discount);
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void rent() {
        isAvailable = false;
    }

    public void returnCar() {
        isAvailable = true;
    }

    @Override
    public String toString() {
        return String.format("%s - %s %s ($%.2f/day)", carId, brand, model, basePricePerDay);
    }
}

class Customer {
    private String customerId;
    private String name;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }
}

class Rental {
    private Car car;
    private Customer customer;
    private int days;

    public Rental(Car car, Customer customer, int days) {
        this.car = car;
        this.customer = customer;
        this.days = days;
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }
}

class CarRentalSystem {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentCar(Car car, Customer customer, int days) {
        if (car.isAvailable()) {
            car.rent();
            rentals.add(new Rental(car, customer, days));
        } else {
            System.out.println("Car is not available for rent.");
        }
    }

    public void returnCar(Car car) {
        car.returnCar();
        Rental rentalToRemove = rentals.stream()
                .filter(rental -> rental.getCar() == car)
                .findFirst()
                .orElse(null);
        if (rentalToRemove != null) {
            rentals.remove(rentalToRemove);
        } else {
            System.out.println("Car was not rented.");
        }
    }

    public void viewRentalHistory(String customerId) {
        System.out.println("\n== Rental History ==\n");
        rentals.stream()
                .filter(rental -> rental.getCustomer().getCustomerId().equals(customerId))
                .forEach(rental -> {
                    System.out.println("Car: " + rental.getCar().getBrand() + " " + rental.getCar().getModel());
                    System.out.println("Days: " + rental.getDays());
                    System.out.printf("Total Price: $%.2f%n", rental.getCar().calculatePrice(rental.getDays()));
                    System.out.println("-----------------------");
                });
    }

    public void searchCars(String brand, String model, Double minPrice, Double maxPrice) {
        List<Car> filteredCars = cars.stream()
                .filter(car -> (brand == null || car.getBrand().equalsIgnoreCase(brand)) &&
                               (model == null || car.getModel().equalsIgnoreCase(model)) &&
                               (minPrice == null || car.getBasePricePerDay() >= minPrice) &&
                               (maxPrice == null || car.getBasePricePerDay() <= maxPrice))
                .collect(Collectors.toList());
        System.out.println("\n== Search Results ==\n");
        if (filteredCars.isEmpty()) {
            System.out.println("No cars found.");
        } else {
            filteredCars.forEach(System.out::println);
        }
    }

    public void menu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===== Car Rental System =====");
            System.out.println("1. Rent a Car");
            System.out.println("2. Return a Car");
            System.out.println("3. View Rental History");
            System.out.println("4. Search Cars");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
                continue;
            }

            switch (choice) {
                case 1: // Rent a Car
                    System.out.println("\n== Rent a Car ==\n");
                    System.out.print("Enter your name: ");
                    String customerName = scanner.nextLine();
                    System.out.print("Enter the car brand (or leave blank): ");
                    String searchBrand = scanner.nextLine();
                    System.out.print("Enter the car model (or leave blank): ");
                    String searchModel = scanner.nextLine();

                    System.out.println("\nAvailable Cars:");
                    for (Car car : cars) {
                        if (car.isAvailable() &&
                            (searchBrand.isEmpty() || car.getBrand().equalsIgnoreCase(searchBrand)) &&
                            (searchModel.isEmpty() || car.getModel().equalsIgnoreCase(searchModel))) {
                            System.out.println(car);
                        }
                    }

                    System.out.print("\nEnter the car ID you want to rent: ");
                    String carId = scanner.nextLine();
                    System.out.print("Enter the number of days for rental: ");
                    int rentalDays = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    Customer newCustomer = new Customer("CUS" + (customers.size() + 1), customerName);
                    addCustomer(newCustomer);

                    Car selectedCar = null;
                    for (Car car : cars) {
                        if (car.getCarId().equals(carId) && car.isAvailable()) {
                            selectedCar = car;
                            break;
                        }
                    }

                    if (selectedCar != null) {
                        double totalPrice = selectedCar.calculatePrice(rentalDays);
                        System.out.println("\n== Rental Information ==\n");
                        System.out.println("Customer ID: " + newCustomer.getCustomerId());
                        System.out.println("Customer Name: " + newCustomer.getName());
                        System.out.println("Car: " + selectedCar.getBrand() + " " + selectedCar.getModel());
                        System.out.println("Rental Days: " + rentalDays);
                        System.out.printf("Total Price: $%.2f%n", totalPrice);

                        System.out.print("\nConfirm rental (Y/N): ");
                        String confirm = scanner.nextLine();

                        if (confirm.equalsIgnoreCase("Y")) {
                            rentCar(selectedCar, newCustomer, rentalDays);
                            System.out.println("\nCar rented successfully.");
                        } else {
                            System.out.println("\nRental canceled.");
                        }
                    } else {
                        System.out.println("\nInvalid car selection or car not available for rent.");
                    }
                    break;

                case 2: // Return a Car
                    System.out.println("\n== Return a Car ==\n");
                    System.out.print("Enter the car ID you want to return: ");
                    String returnCarId = scanner.nextLine();

                    Car carToReturn = null;
                    for (Car car : cars) {
                        if (car.getCarId().equals(returnCarId) && !car.isAvailable()) {
                            carToReturn = car;
                            break;
                        }
                    }

                    if (carToReturn != null) {
                        Customer customer = null;
                        for (Rental rental : rentals) {
                            if (rental.getCar() == carToReturn) {
                                customer = rental.getCustomer();
                                break;
                            }
                        }

                        if (customer != null) {
                            returnCar(carToReturn);
                            System.out.println("Car returned successfully by " + customer.getName());
                        } else {
                            System.out.println("Car was not rented or rental information is missing.");
                        }
                    } else {
                        System.out.println("Invalid car ID or car is not rented.");
                    }
                    break;

                case 3: // View Rental History
                    System.out.print("Enter your customer ID: ");
                    String customerId = scanner.nextLine();
                    viewRentalHistory(customerId);
                    break;

                case 4: // Search Cars
                    System.out.println("\n== Search Cars ==\n");
                    System.out.print("Enter the car brand (or leave blank): ");
                    String searchBrandForSearch = scanner.nextLine();
                    System.out.print("Enter the car model (or leave blank): ");
                    String searchModelForSearch = scanner.nextLine();
                    System.out.print("Enter minimum price (or leave blank): ");
                    Double minPrice = null;
                    String minPriceStr = scanner.nextLine();
                    if (!minPriceStr.isEmpty()) {
                        try {
                            minPrice = Double.parseDouble(minPriceStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid minimum price format.");
                        }
                    }
                    System.out.print("Enter maximum price (or leave blank): ");
                    Double maxPrice = null;
                    String maxPriceStr = scanner.nextLine();
                    if (!maxPriceStr.isEmpty()) {
                        try {
                            maxPrice = Double.parseDouble(maxPriceStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid maximum price format.");
                        }
                    }

                    searchCars(searchBrandForSearch.isEmpty() ? null : searchBrandForSearch,
                               searchModelForSearch.isEmpty() ? null : searchModelForSearch,
                               minPrice, maxPrice);
                    break;

                case 5: // Exit
                    System.out.println("\nThank you for using the Car Rental System!");
                    return;

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        CarRentalSystem rentalSystem = new CarRentalSystem();

        // Adding sample cars
        Car car1 = new Car("C001", "Toyota", "Camry", 60.0);
        Car car2 = new Car("C002", "Honda", "Accord", 70.0);
        Car car3 = new Car("C003", "Mahindra", "Thar", 150.0);
        rentalSystem.addCar(car1);
        rentalSystem.addCar(car2);
        rentalSystem.addCar(car3);

        // Launch the menu
        rentalSystem.menu();
    }
}
