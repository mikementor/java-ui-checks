package qa.java.uichecks.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import qa.java.uichecks.pages.LoginPage;
import qa.java.uichecks.testconfig.BaseTest;
import qa.java.uichecks.testdata.TestData;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Login")
public class LoginTests extends BaseTest {

    @Test
    @TmsLink("HSA-703")
    void login() {
        open("/auth/login");
        new LoginPage()
                .login(TestData.config.getTestdata().getLogin(),TestData.config.getTestdata().getPassword())
                .checkMainPage();
    }


}
