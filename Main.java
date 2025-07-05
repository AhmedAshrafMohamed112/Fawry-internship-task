import java.util.*;
class Product {
    private String name;
    private double price;
    private int quantity;
    private boolean expirable;
    private Date expiry;
    private boolean shippable;
    private double weight;

    // Create new product
    public Product(String name, double price, int quantity, boolean expirable, Date expiry, boolean shippable, double weight) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.expiry = expiry;
        this.expirable = expirable;
        this.shippable = shippable;
        this.weight = weight;
    }
    // Getters
    public String getName() {return name;}
    public double getPrice() {return price;}
    public int getQuantity() {return quantity;}
    public double getWeight() {return weight;}
    // checkers
    public boolean isShippable() {return shippable;}
    public boolean isExpired() {return expirable && new Date().after(expiry);}
    // Updates quantity
    public void reduceQuantity(int q) {quantity -= q;}

}

class Customer {
    private String name;
    private double balance;
    // Constructor
    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }
    // Getters
    public String getName() {return name;}
    public double getBalance() {return balance;}
    // Update balance
    public void pay(double amount) {balance -= amount;}
}
interface Shippable {
    String getName();
    double getWeight();
}

class Item implements Shippable {
    private  Product p;
    private  int q;
    // Constructor
    public Item(Product p, int q) {
        this.p = p;
        this.q = q;
    }
    // Getters
    public double getPrice() {return p.getPrice() * q;}
    public Product getProduct() {return p;}
    public int getQuantity() {return q;}
    // Overrides
    @Override
    public double getWeight() {return p.isShippable() ? p.getWeight() * q : 0;}
    @Override
    public String getName() {return p.getName();}

}

class ShippingService {
    public static void ship(List<Shippable> items) {
        System.out.println("** Shipment notice **");
        double totalWeight = 0;
        // Prints shipping details
        for (Shippable item : items) {
            System.out.println(item.getName() + " " + (int)(item.getWeight() * 1000) + "g");
            totalWeight += item.getWeight();
        }

        System.out.println("Total package weight " + String.format("%.1f", totalWeight) + "kg");
    }
}

public class Main {
    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 5);
        Date future = cal.getTime();

        Product cheese = new Product("Cheese", 100, 10, true, future, true, 0.2);
        Product biscuits = new Product("Biscuits", 150, 5, true, future, true, 0.7);
        Product tv = new Product("TV", 5000, 3, false, null, true, 7.0);
        Product scratchCard = new Product("ScratchCard", 50, 20, false, null, false, 0);
        Product mobile = new Product("Mobile", 3000, 5, false, null, false, 0);

        Customer user = new Customer("Ashraf", 1000);
        List<Item> cart = new ArrayList<>();
        cart.add(new Item(cheese, 2));
        cart.add(new Item(biscuits, 1));

        if (cart.isEmpty()) {
            System.out.println("cart is empty");
            return;
        }

        double subtotal = 0;
        double shipping = 0;
        List<Shippable> toShip = new ArrayList<>();

        for (Item item : cart) {
            if (item.getQuantity() > item.getProduct().getQuantity()) {
                System.out.println("not enough stock for " + item.getName());
                return;
            }
            if (item.getProduct().isExpired()) {
                System.out.println(item.getName() + " is expired");
                return;
            }
            subtotal += item.getPrice();

            if (item.getProduct().isShippable()) {
                toShip.add(item);
            }
        }

        if (!toShip.isEmpty()) shipping = 30;
        double total = subtotal + shipping;

        if (user.getBalance() < total) {
            System.out.println("Customer's balance is insufficient.");
            return;
        }

        if (!toShip.isEmpty()) {
            ShippingService.ship(toShip);
        }

        user.pay(total);
        for (Item i : cart) {
            i.getProduct().reduceQuantity(i.getQuantity());
        }

        System.out.println("** Checkout receipt **");
        for (Item i : cart) {
            System.out.println(i.getQuantity() + "x " + i.getName() + " " + (int)i.getPrice());
        }
        System.out.println("----------------------");
        System.out.println("Order subtotal: " + (int)subtotal);
        System.out.println("Shipping fees: " + (int)shipping);
        System.out.println("Total paid amount: " + (int)total);
        System.out.println("Customer balance after payment: " + (int)user.getBalance());
    }
}
