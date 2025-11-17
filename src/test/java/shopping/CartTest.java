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
    private Cart cart1;
    private Cart cart2;

    /**
     * Пересоздаём корзины перед каждым тестом
     */
    @BeforeEach
    void setupCarts() {
        cart1 = new Cart(new Customer(123L, "123"));
        cart2 = new Cart(new Customer(456L, "789"));
    }

    /**
     * Тестирует добавление товаров в корзину. Проверки:
     * <li>Добавление товара, когда его хватает</li>
     * <li>Добавление товара, когда его не хватает</li>
     * <p>Вторая проверка не пройдёт - количество товара не уменьшается после того, как
     * покупатель забрал его часть в корзину (логическая ошибка) в validateCount()</p>
     */
    @Test
    void add() {
        Product grape = new Product("Виноград", 5);

        Assertions.assertDoesNotThrow(() -> cart1.add(grape, 3));
        Map<Product, Integer> products = cart1.getProducts();
        Assertions.assertEquals(1, products.size());
        Assertions.assertTrue(products.containsKey(grape));

        IllegalArgumentException exp =
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> cart2.add(grape, 3));
        Assertions.assertEquals("Невозможно добавить товар 'Виноград' в корзину, т.к. "
                + "нет необходимого количества товаров", exp.getMessage());
        Assertions.assertTrue(cart2.getProducts().isEmpty());
    }

    /**
     * Тестирует изменение количества товаров в корзине. Проверки:
     * <li>Изменение количества товара, когда его хватает</li>
     * <li>Изменение количества товара, когда его не хватает</li>
     * <p>Вторая проверка не пройдёт - количество товара не уменьшается после того, как
     * покупатель забрал его часть в корзину (логическая ошибка) в validateCount()</p>
     */
    @Test
    void edit() {
        Product tomato = new Product("Помидор", 8);
        cart1.add(tomato, 4);
        cart2.add(tomato, 2);
        cart2.edit(tomato, 4);
        Assertions.assertEquals(4, cart2.getProducts().get(tomato));

        IllegalArgumentException exp =
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> cart2.edit(tomato, 5));
        Assertions.assertEquals("Невозможно добавить товар 'Помидор' в корзину, т.к. "
                + "нет необходимого количества товаров", exp.getMessage());
        Assertions.assertEquals(4, cart2.getProducts().get(tomato));
    }
}