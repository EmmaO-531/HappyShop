import ci553.happyshop.client.customer.CustomerModel;
import ci553.happyshop.catalogue.Product;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerModelTest {

    @Test
    void addOrMergeMergesQuantitiesForSameProductId() throws Exception {
        CustomerModel model = new CustomerModel();

        Product p1 = new Product("0001", "Milk", "milk.jpg", 1.20, 50);
        p1.setOrderedQuantity(2);

        Product p2 = new Product("0001", "Milk", "milk.jpg", 1.20, 50);
        p2.setOrderedQuantity(3);

        // Call private add_or_merge(Product) using reflection
        Method addOrMerge = CustomerModel.class.getDeclaredMethod("add_or_merge", Product.class);
        addOrMerge.setAccessible(true);

        addOrMerge.invoke(model, p1);
        addOrMerge.invoke(model, p2);

        assertEquals(1, model.getTrolley().size(), "Trolley should contain one entry after merging");
        assertEquals("0001", model.getTrolley().get(0).getProductId());
        assertEquals(5, model.getTrolley().get(0).getOrderedQuantity(), "Quantities should be merged");
    }

    @Test
    void addOrMergeSortsTrolleyByProductId() throws Exception {
        CustomerModel model = new CustomerModel();

        Product p2 = new Product("0002", "Bread", "bread.jpg", 0.80, 50);
        p2.setOrderedQuantity(1);

        Product p1 = new Product("0001", "Milk", "milk.jpg", 1.20, 50);
        p1.setOrderedQuantity(1);

        Method addOrMerge = CustomerModel.class.getDeclaredMethod("add_or_merge", Product.class);
        addOrMerge.setAccessible(true);

        addOrMerge.invoke(model, p2);
        addOrMerge.invoke(model, p1);

        assertEquals(2, model.getTrolley().size());
        assertEquals("0001", model.getTrolley().get(0).getProductId(), "Trolley should be sorted by Product ID");
        assertEquals("0002", model.getTrolley().get(1).getProductId(), "Trolley should be sorted by Product ID");
    }

    @Test
    void groupProductsByIdCombinesQuantitiesCorrectly() throws Exception {
        CustomerModel model = new CustomerModel();

        Product p1 = new Product("0001", "Milk", "milk.jpg", 1.20, 50);
        p1.setOrderedQuantity(1);

        Product p2 = new Product("0001", "Milk", "milk.jpg", 1.20, 50);
        p2.setOrderedQuantity(2);

        ArrayList<Product> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);

        Method groupById = CustomerModel.class.getDeclaredMethod("groupProductsById", ArrayList.class);
        groupById.setAccessible(true);

        @SuppressWarnings("unchecked")
        ArrayList<Product> grouped = (ArrayList<Product>) groupById.invoke(model, list);

        assertEquals(1, grouped.size(), "Grouped list should contain one product for the same ID");
        assertEquals("0001", grouped.get(0).getProductId());
        assertEquals(3, grouped.get(0).getOrderedQuantity(), "Ordered quantity should be summed");
    }

    @Test
    void removeInsufficientProductsRemovesOnlyAffectedItems() throws Exception {
        CustomerModel model = new CustomerModel();

        Product milk = new Product("0001", "Milk", "milk.jpg", 1.20, 50);
        milk.setOrderedQuantity(1);

        Product bread = new Product("0002", "Bread", "bread.jpg", 0.80, 50);
        bread.setOrderedQuantity(1);

        // Preload trolley
        model.getTrolley().add(milk);
        model.getTrolley().add(bread);

        // Insufficient list contains only bread
        ArrayList<Product> insufficient = new ArrayList<>();
        Product insufficientBread = new Product("0002", "Bread", "bread.jpg", 0.80, 0);
        insufficientBread.setOrderedQuantity(5);
        insufficient.add(insufficientBread);

        Method removeInsufficient = CustomerModel.class.getDeclaredMethod("removeInsufficientProducts", ArrayList.class);
        removeInsufficient.setAccessible(true);
        removeInsufficient.invoke(model, insufficient);

        assertEquals(1, model.getTrolley().size(), "Only one product should remain after removal");
        assertEquals("0001", model.getTrolley().get(0).getProductId(), "Unaffected product should remain");
    }
}
