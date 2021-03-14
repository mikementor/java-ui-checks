package qa.java.uichecks.steps;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.Wait;

public class PageObjects {
    @Step("checkStartPage")
    public static void checkStartPage(){
        Helper.findTextAndCheckVisibility("What is Refund Booster?");
    }

    @Step("clickAcceptTerms")
    public static void clickAcceptTerms(){
        Helper.findIdAndClick("acceptTerms");
    }

    public static void clickAcceptPolicy(){
        Helper.findIdAndClick("acceptPolicy");
    }

    public static void clickNextStep(){
        Helper.findTextAndClick("Take me to the next step");
    }

    public static void checkOpeningDialogWindow(){
        Wait();
        Helper.findIdAndCheckVisibility("dialog-title");
    }

    public static void clickAgree(){
        Helper.findTextAndClick("Agree");
    }

    public static void checkInformationInDialogWindow(String info){
        Helper.findTextAndCheckVisibility(info);
    }

    public static void inputDisbursementAmount(){
        Helper.findIdAndSetValue("disbursementAmount", "3100");
    }

    public static void inputQualifiedClaimAmount(){
        Helper.findIdAndSetValue("qualifiedClaimAmount", "4500");
    }

    public static void clickNext(){
        Helper.findTextAndClick("Next");
    }

    public static void clickGotIt(){
        Helper.findTextAndClick("Got it");
    }

    public static void checkVisibilityButtonReviewProposal(){
        Helper.findTextAndCheckVisibility("Review Proposal");
    }

    public static void inputRoutingNumber(){
        Helper.findIdAndSetValue("routingNumber", "1234567");
    }

    public static void repeatInputRoutingNumber(){
        Helper.findIdAndSetValue("routingNumber2", "1234567");
    }

    public static void inputAccountNumber(){
        Helper.findIdAndSetValue("accountNumber", "7654321");
    }

    public static void repeatInputAccountNumber(){
        Helper.findIdAndSetValue("accountNumber2", "7654321");
    }

    public static void clickChecking(){
        Helper.findTextAndClick("Checking");
    }

    public static void clickSaving(){
        Helper.findTextAndClick("Savings");
    }

    public static void clickSaveChanges(){
        Helper.findTextAndClick("Save Changes");
    }
}
