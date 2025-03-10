import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CardOrderTest {
    private WebDriver driver;

    @BeforeAll
    public static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);

        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
        driver = null;
    }

    @Test
    void shouldSubmitForm() {
        WebElement form = driver.findElement(By.cssSelector("form"));
        form.findElement(By.cssSelector("[data-test-id='name'] .input__control")).sendKeys("Петрова Анна");
        form.findElement(By.cssSelector("[data-test-id='phone'] .input__control")).sendKeys("+79998881122");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button")).click();
        WebElement actual = driver.findElement(By.cssSelector("[data-test-id='order-success']"));
        assertTrue(actual.isDisplayed());
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.",
                actual.getText().trim());
    }

    @Test
    void shouldNotSubmitFormWithEmptyFieldName() { // пустое поле имя
        WebElement form = driver.findElement(By.cssSelector("form"));
        form.findElement(By.cssSelector("[data-test-id='phone'] .input__control")).sendKeys("+79998881122");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button")).click();
        WebElement actual = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertTrue(actual.isDisplayed());
        assertEquals("Поле обязательно для заполнения", actual.getText().trim());
    }

    @Test
    void shouldNotSubmitFormContainingLatinCharactersInFieldName() { //поле имя на латинице
        WebElement form = driver.findElement(By.cssSelector("form"));
        form.findElement(By.cssSelector("[data-test-id='name'] .input__control")).sendKeys("Borisov Ivan");
        form.findElement(By.cssSelector("[data-test-id='phone'] .input__control")).sendKeys("+79998881122");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button")).click();
        WebElement actual = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertTrue(actual.isDisplayed());
        assertEquals("Фамилия и Имя указаны неверно. Допустимы только русские буквы, пробелы и дефисы",
                actual.getText().trim());
    }

    @Test
    void shouldNotSubmitFormWithEmptyFieldPhone() { // пустое поле телефон
        WebElement form = driver.findElement(By.cssSelector("form"));
        form.findElement(By.cssSelector("[data-test-id='name'] .input__control")).sendKeys("Петрова Анна");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button")).click();
        WebElement actual = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertTrue(actual.isDisplayed());
        assertEquals("Поле обязательно для заполнения", actual.getText().trim());
    }

    @Test
    void shouldNotSubmitFormContainingFirstCharacter8InFieldPhone() { //номер телефона начинается c 8
        WebElement form = driver.findElement(By.cssSelector("form"));
        form.findElement(By.cssSelector("[data-test-id='name'] .input__control")).sendKeys("Петрова Анна");
        form.findElement(By.cssSelector("[data-test-id='phone'] .input__control")).sendKeys("89998881122");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button")).click();
        WebElement actual = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertTrue(actual.isDisplayed());
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                actual.getText().trim());
    }

    @Test
    void shouldNotSubmitFormWithoutCheckingCheckbox() { // без отметки чекбокса
        WebElement form = driver.findElement(By.cssSelector("form"));
        form.findElement(By.cssSelector("[data-test-id='name'] .input__control")).sendKeys("Петрова Анна");
        form.findElement(By.cssSelector("[data-test-id='phone'] .input__control")).sendKeys("+79998881122");
        driver.findElement(By.cssSelector("button")).click();
        WebElement actual = driver.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid .checkbox__text"));
        assertTrue(actual.isDisplayed());
        assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных" +
                " данных и разрешаю сделать запрос в бюро кредитных историй", actual.getText().trim());
    }
}
