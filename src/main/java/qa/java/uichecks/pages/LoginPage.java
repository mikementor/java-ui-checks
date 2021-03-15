package qa.java.uichecks.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    SelenideElement email;
    SelenideElement getEmail() {
        return $("#email");
    }
    SelenideElement getPassword() {
    return $("#password");
    }

    SelenideElement getSignInButton(){
        return $(By.xpath("//button[*[contains(text(),'Sign in')]]"));
    }

    // actions
    @Step("login")
    public LoginPage login(String login,String pass){
        getEmail().setValue(login);
        getPassword().setValue(pass);
        getSignInButton().click();
        return this;
    }
    // assertions
    @Step("assert, that main page is open")
    public void checkMainPage(){
        $(".MuiDrawer-docked").shouldBe(Condition.visible);
    }
}
