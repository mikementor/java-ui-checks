package qa.java.uichecks.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage {

    @FindBy(id="email")
    SelenideElement email;
}
