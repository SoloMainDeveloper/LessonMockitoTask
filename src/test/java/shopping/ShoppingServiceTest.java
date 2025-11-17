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
     * <li>Класс Cart не должен зависеть от пользователя</li>
     * <li>ShoppingServiceImpl.getCart() возвращает новую корзину</li>
     */
    @Test
    void getCart() {
        Cart cart = new Cart(customer);
        Product apple = new Product("Яблоко", 2);
        cart.add(apple, 1);

        Cart realCart = shoppingService.getCart(customer);
        Assertions.assertNotNull(cart);
        Map<Product, Integer> productsInCart = realCart.getProducts();
        Assertions.assertEquals(1, productsInCart.size());
        Assertions.assertTrue(productsInCart.containsKey(apple));
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
     * <li>Можно купить, когда хватает товара</li>
     * <li>Нельзя купить, когда товара недостаточно</li>
     */
    @Test
    void buy() {
        Cart cart = new Cart(customer);
        Product product = new Product("Апельсин", 3);
        cart.add(product, 2);
        boolean isSuccessful = Assertions.assertDoesNotThrow(() ->
                shoppingService.buy(cart)
        );
        Assertions.assertTrue(isSuccessful);
        Assertions.assertEquals(1, product.getCount());
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product);

        BuyException exception = Assertions.assertThrows(BuyException.class, () ->
                shoppingService.buy(cart)
        );
        Assertions.assertEquals("В наличии нет необходимого количества товара 'Апельсин'",
                exception.getMessage());
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product);
    }

    /**
     * Тестируем покупку пустой корзины
     */
    @Test
    void buyEmptyCart() {
        Cart cart = new Cart(customer);

        boolean isSuccessful = Assertions.assertDoesNotThrow(() ->
                shoppingService.buy(cart)
        );
        Mockito.verify(productDaoMock, Mockito.never())
                .save(Mockito.any(Product.class));
        Assertions.assertFalse(isSuccessful);
    }
}