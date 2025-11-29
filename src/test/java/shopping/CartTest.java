package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import product.Product;

import java.util.Map;

/**
 * Тестирует класс {@link Cart}
 */
class CartTest {
    private Cart cart;

    /**
     * Пересоздаём корзины перед каждым тестом
     */
    @BeforeEach
    void setupCarts() {
        cart = new Cart(new Customer(123L, "123"));
    }

    /**
     * Тестирует добавление товаров в корзину, когда его хватает.
     * <p>Тест не пройдёт: так как в {@link Cart#validateCount(Product, int)} не
     * позволяет оставить кол-во товара равным 0 (всегда минимум 1)</p>
     */
    @Test
    void addWhenEnoughProducts() {
        Product grape = new Product("Виноград", 5);

        cart.add(grape, 5);
        Map<Product, Integer> products = cart.getProducts();
        Assertions.assertEquals(1, products.size());
        Assertions.assertTrue(products.containsKey(grape));
    }

    /**
     * Тестирует добавление товаров в корзину, когда его недостаточно
     */
    @Test
    void addWhenNotEnoughProducts() {
        Product grape = new Product("Виноград", 5);

        IllegalArgumentException exp =
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> cart.add(grape, 6));
        Assertions.assertEquals("Невозможно добавить товар 'Виноград' в корзину, т.к. "
                + "нет необходимого количества товаров", exp.getMessage());
        Assertions.assertTrue(cart.getProducts().isEmpty());
    }

    /**
     * Тестирует добавление отрицательного кол-ва продукта в корзину
     * <p>Упадёт из-за логической ошибки: cart не проверяет кол-во товаров на
     * отрицательность. Также доп. ошибка: в целом можно создать {@link Product} с
     * отрицательным значением кол-во товара</p>
     */
    @Test
    void addNegativeAmountOfProduct() {
        Product grape = new Product("Виноград", 5);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> cart.add(grape, -1));
    }

    /**
     * Тестирует изменение количества товаров в корзине, когда его хватает</li>
     */
    @Test
    void editWhenEnoughProducts() {
        Product tomato = new Product("Помидор", 8);
        cart.add(tomato, 4);
        cart.edit(tomato, 7);
        Assertions.assertEquals(7, cart.getProducts().get(tomato));
    }

    /**
     * Тестирует изменение количества товаров в корзине, когда его недостаточно</li>
     */
    @Test
    void editWhenNotEnoughProducts() {
        Product tomato = new Product("Помидор", 8);
        cart.add(tomato, 4);

        IllegalArgumentException exp =
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> cart.edit(tomato, 9));
        Assertions.assertEquals("Невозможно добавить товар 'Помидор' в корзину, т.к. "
                + "нет необходимого количества товаров", exp.getMessage());
        Assertions.assertEquals(4, cart.getProducts().get(tomato));
    }


//    void testBuyNegativeAmountProduct();
}