package qa.java.uichecks.steps;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

public class Helper {
    public static void findIdAndClick(String id){
        $(By.id(id)).click();
    }

    public static void findIdAndSetValue(String id, String value) {
        $(byId(id)).shouldBe(visible).setValue(value);
    }

    public static void findIdAndCheckVisibility(String id) {
        $(byId(id)).shouldBe(visible);
    }

    public static void findTextAndClick(String text) {
        $(byText(text)).shouldBe(visible).click();
    }

    public static void findTextAndCheckVisibility(String text) {
        $(byText(text)).shouldBe(visible);
    }

}
