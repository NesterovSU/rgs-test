import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * @author Sergey Nesterov
 */
public class MainTest {
     private WebDriver driver;
     private WebDriverWait wait;
     private Actions actions;
     private JavascriptExecutor js;
     private final By FOR_COMPANIES = By.xpath("//a[@href='/for-companies']"),
                HEALTH = By.xpath("//span[text()='Здоровье' and @class='padding']"),
                TRANSPORT = By.xpath("//span[text()='Транспорт/Перевозки' and @class='padding']"),
                DMS = By.xpath("//a[text()='Добровольное медицинское страхование']"),
                TITLE = By.xpath("//h2[contains(text(),'Оперативно перезвоним')]"),
                COOKIE_BUTTON = By.xpath("//div[@class = 'cookie block--cookie']/button[contains(text(),'Хорошо')]"),
                USER_NAME = By.xpath("//input[@name='userName']"),
                USER_TEL = By.xpath("//input[@name='userTel']"),
                USER_EMAIL = By.xpath("//input[@name='userEmail']"),
                USER_ADDRESS = By.xpath("//div[@field='InputRegion']//input"),
                POLICY_AGREEMENT_STATUS = By.xpath("//div[@class='policy-agreement text--basic']//input[@type='checkbox']"),
                POLICY_AGREEMENT_CLICK = By.xpath("//div[@class='policy-agreement text--basic']//input[@type='checkbox']/following::label[1]"),
                SUBMIT = By.xpath("//button[@type='submit' and contains(text(), 'Свяжитесь')]"),
                FRAME = By.xpath("//iframe[contains(@class, 'flocktory')]"),
                FRAME_CLOSE_BUTTON = By.xpath("//iframe[contains(@class, 'flocktory')]//button[@title='Закрыть']"),
                EMAIL_ERROR = By.xpath("//div[@formKey='email']//span[contains(text(),'Введите корректный адрес электронной почты') and contains(@class,'input__error')]");

     @Test
     public void test1() {
//          driver = WebDriverManager.chromedriver().create();
          System.setProperty("webdriver.chrome.driver", "C:\\BrowserDrivers\\chromedriver98.exe");
          driver = new ChromeDriver();
          actions = new Actions(driver);
          js = (JavascriptExecutor) driver;
          wait = new WebDriverWait(driver, 10);

          driver.manage().window().maximize();
          driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
          driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

          driver.get("http://www.rgs.ru");

          driver.findElement(FOR_COMPANIES).click();
          driver.findElement(TRANSPORT);
          driver.findElement(HEALTH).click();
          driver.findElement(DMS).click();

          if (isElementDisplayed(COOKIE_BUTTON)) driver.findElement(COOKIE_BUTTON).click();

          Assertions.assertEquals("Оперативно перезвоним\nдля оформления полиса",
                  wait.withMessage("Отсутствует заголовок на странице")
                  .until(ExpectedConditions.presenceOfElementLocated(TITLE)).getText(),"Заголовок отсутствует");

          typeInForm(USER_NAME, "Иванов Иван Иванович");
          typeInForm(USER_TEL, "9001231212");
          typeInForm(USER_EMAIL, "qwertyqwerty");
          typeInForm(USER_ADDRESS, "г Москва, г Зеленоград, ул Ленина, д 1, кв 1");
          checkRadio(POLICY_AGREEMENT_CLICK,POLICY_AGREEMENT_STATUS);

          Assertions.assertFalse(isElementDisplayed(EMAIL_ERROR),
                  "Сообщение об ошибке уже присутствует - емайл");

          js.executeScript("arguments[0].click()", driver.findElement(SUBMIT));
//          actions.moveToElement(driver.findElement(SUBMIT)).click().build().perform();

          Assertions.assertTrue(wait.withMessage("Сообщение об ошибке не выведено - емайл")
                          .until(ExpectedConditions.presenceOfElementLocated(EMAIL_ERROR))
                          .isDisplayed(),
                  "Сообщение об ошибке не выведено - емайл");
     }

     private void typeInForm(By by, String str){
          WebElement we = driver.findElement(by);
          js.executeScript("arguments[0].click()", we);
          we.clear();
          we.sendKeys(str);
          we.sendKeys(Keys.TAB);
          Assertions.assertEquals(str.equals("9001231212") ? "+7 (900) 123-1212" : str,
                  we.getAttribute("value"), "Поле не заполнено");
     }
     private void checkRadio(By click,By status) {
          WebElement clk = driver.findElement(click);
          WebElement stat = driver.findElement(status);
          if (!stat.isSelected())
               js.executeScript("arguments[0].click()", clk);
//                  myWait(2);
//                  actions.moveToElement(clk).click().build().perform();
     }

     private void myWait(long second){
          try {
               sleep(second*1000);
          }catch (InterruptedException ex){
               System.out.println(ex.getMessage());
          }
     }

     private boolean isElementDisplayed(By by){
          try {driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
               driver.findElement(by).isDisplayed();
               driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
               return true;
          }catch (NoSuchElementException ex){
               driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
               return false;
          }
     }
     @AfterEach
     public void close(){
          driver.quit();
     }
}
// прокрутка страницы (через раз недокручивает)
//          JavascriptExecutor jsDriver = (JavascriptExecutor) driver;
//          jsDriver.executeScript("arguments[0].scrollIntoView(true)", driver.findElement(sendClaim));

//action   robot   js
// java 11