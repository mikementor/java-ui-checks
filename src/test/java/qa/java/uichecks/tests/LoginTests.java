package qa.java.uichecks.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import qa.java.uichecks.pages.LoginPage;
import qa.java.uichecks.testconfig.BaseTest;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

@Epic("Login")
public class LoginTests extends BaseTest {

    @Test
    @TmsLink("E8T-31")
    void login() {

        open("/auth/login");
        new LoginPage()
                .login("mchukmarov+lhemployee+dev+single@provectus.com", "Password@1")
                .checkMainPage();
    }


}
