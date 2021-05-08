package qa.java.uichecks.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import qa.java.uichecks.testconfig.BaseTest;

import static com.codeborne.selenide.Selenide.open;

@Epic("Example")
public class ExampleTests extends BaseTest {

    @Test
    @TmsLink("E8T-31")
    void exampleCheck() {
        open("https://ya.ru/");
    }


}
