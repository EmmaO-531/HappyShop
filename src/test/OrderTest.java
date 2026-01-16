import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.orderManagement.OrderState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    void orderStoresCorrectMetadata() {
        // Arrange
        ArrayList<Product> products = new ArrayList<>();

        Product milk = new Product("0001", "Milk", "milk.jpg", 1.20, 50);
        milk.setOrderedQuantity(2);

        Product bread = new Product("0002", "Bread", "bread.jpg", 0.80, 30);
        bread.setOrderedQuantity(1);

        products.add(milk);
        products.add(bread);

        int orderId = 10;
        OrderState state = OrderState.Ordered;
        String orderedDateTime = "2025-05-03 16:52:24";

        // Act
        Order order = new Order(orderId, state, orderedDateTime, products);

        // Assert – direct metadata checks
        assertEquals(orderId, order.getOrderId());
        assertEquals(state, order.getState());
        assertEquals(orderedDateTime, order.getOrderedDateTime());

        // Defensive copy check
        assertNotSame(products, order.getProductList(),
                "Order should defensively copy the product list");
        assertEquals(products, order.getProductList(),
                "Copied product list should contain the same products");
    }

    @Test
    void orderDetailsIncludesMetadataAndProducts() {
        // Arrange
        ArrayList<Product> products = new ArrayList<>();

        Product milk = new Product("0001", "Milk", "milk.jpg", 1.20, 50);
        milk.setOrderedQuantity(1);
        products.add(milk);

        int orderId = 11;
        OrderState state = OrderState.Ordered;
        String orderedDateTime = "2025-05-03 17:00:00";

        Order order = new Order(orderId, state, orderedDateTime, products);

        // Act
        String details = order.orderDetails();

        // Assert – metadata presence (not formatting)
        assertTrue(details.contains(String.valueOf(orderId)));
        assertTrue(details.contains(state.toString()));
        assertTrue(details.contains(orderedDateTime));

        // Minimal product presence check
        assertTrue(details.contains("Milk"));
    }
}

