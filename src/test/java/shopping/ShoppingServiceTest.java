package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import product.Product;
import product.ProductDao;

import java.util.Map;

/**
 * Тестирует класс {@link ShoppingService }
 */
@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {
    private final ProductDao productDaoMock;
    private final ShoppingService shoppingService;
    private final Customer customer = new Customer(123L, "7777");

    /**
     * Конструктор. Мокаем ProductDao и внедряем его в ShoppingService
     */
    public ShoppingServiceTest(@Mock ProductDao productDaoMock) {
        this.productDaoMock = productDaoMock;
        shoppingService = new ShoppingServiceImpl(productDaoMock);
    }

    /**
     * Тестируем получение корзины пользователя.
     * <p>Логические ошибки:</p>
     * <li>Класс {@link Cart} не должен зависеть от пользователя. Customer в классе
     * Cart вообще не используется</li>
     * <li>ShoppingServiceImpl.getCart() возвращает новую корзину</li>
     */
    @Test
    void getCart() {
        Cart cart = new Cart(customer);
        Product apple = new Product("Яблоко", 2);
        cart.add(apple, 1);

        Cart realCart = shoppingService.getCart(customer);
        Assertions.assertNotNull(realCart);
        Map<Product, Integer> productsInCart = realCart.getProducts();

        Assertions.assertEquals(cart.getProducts(), productsInCart);
    }

    /**
     * Тестируем получение всех товаров
     */
    @Test
    void getAllProducts() {
        // Метод тестировать не нужно. Так как он просто возвращает
        // productDAO.getAll(). Это должно тестироваться в ProductDao
    }

    /**
     * Тестируем получение товара по имени
     */
    @Test
    void getProductByName() {
        // Метод тестировать не нужно. Так как он просто возвращает
        // productDAO.getByName(name). Это должно тестироваться в ProductDao
    }

    /**
     * Тестируем покупку корзины. Проверки:
     * <li>Можно купить, когда хватает товара. Корзина очищается</li>
     * <li>Нельзя купить, когда товара недостаточно</li>
     *
     * <p>Ошибка: корзина не очищается при покупке</p>
     */
    @Test
    void buy() throws BuyException {
        Cart cart = new Cart(customer);
        Cart cart2 = new Cart(new Customer(140L, "111"));
        Product product = new Product("Апельсин", 3);
        cart.add(product, 2);
        cart2.add(product, 2);

        Assertions.assertTrue(shoppingService.buy(cart));
        Assertions.assertEquals(1, product.getCount());
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product);
        Assertions.assertTrue(cart.getProducts().isEmpty());

        BuyException exception = Assertions.assertThrows(BuyException.class, () ->
                shoppingService.buy(cart2)
        );
        Assertions.assertEquals("В наличии нет необходимого количества товара 'Апельсин'",
                exception.getMessage());
        Assertions.assertFalse(cart2.getProducts().isEmpty());
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product);
    }

    /**
     * Тестирует покупку корзины с отрицательным количеством товара.
     * Не пройдёт: в данный момент ShoppingService позволяет выполнить такую операцию.
     * При этом даже увеличит кол-во товара.
     */
    @Test
    void buyNegativeAmountOfProduct() {
        Cart cart = new Cart(customer);
        Product product = new Product("Апельсин", 3);
        cart.add(product, -3);
        Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart));
        Mockito.verify(productDaoMock, Mockito.never()).save(product);
        Assertions.assertEquals(3, product.getCount());
    }

    /**
     * Тестируем покупку пустой корзины
     */
    @Test
    void buyEmptyCart() throws BuyException {
        Cart cart = new Cart(customer);
        Assertions.assertFalse(shoppingService.buy(cart));
        Mockito.verify(productDaoMock, Mockito.never())
                .save(Mockito.any(Product.class));
    }
}