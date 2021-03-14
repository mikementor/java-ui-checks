package qa.java.uichecks.testconfig;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.util.Arrays;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@Slf4j
@DisplayNameGeneration(CamelCaseToSpacedDisplayNameGenerator.class)
public class BaseTest {

    public static GenericContainer selenoid = new GenericContainer(DockerImageName.parse("aerokube/selenoid:latest-release"))
            .withExposedPorts(4444)
            .withFileSystemBind("selenoid/config/", "/etc/selenoid", BindMode.READ_WRITE)
            .withFileSystemBind("/var/run/docker.sock", "/var/run/docker.sock", BindMode.READ_WRITE)
            .withFileSystemBind("selenoid/video", "/opt/selenoid/video", BindMode.READ_WRITE)
            .withFileSystemBind("selenoid/logs", "/opt/selenoid/logs", BindMode.READ_WRITE)
            .withEnv("OVERRIDE_VIDEO_OUTPUT_DIR", "/opt/selenoid/video")
            .withCommand("-conf", "/etc/selenoid/browsers.json", "-log-output-dir", "/opt/selenoid/logs");

    static {
        if (TestConfiguration.CLEAR_REPORTS_DIR)
            clearReports();
    }

    @BeforeAll
    public static void beforeAll() {
        String remote = TestConfiguration.SELENOID_URL;
        if (TestConfiguration.SHOULD_START_SELENOID) {
            selenoid.start();
            remote = remote.replaceAll(":\\d+",
                    ":" + selenoid.getMappedPort(4444));

        }
        setupSelenoid(remote);
        SelenideLogger.addListener("allure", new AllureSelenide().savePageSource(false));
    }

    @AfterAll
    public static void afterAll() {
        closeWebDriver();
        selenoid.close();
    }

    @SneakyThrows
    private static void setupSelenoid(String remote) {
        Configuration.reportsFolder = "allure-results";
        Configuration.remote = remote;
        Configuration.screenshots = false;
        Configuration.savePageSource = false;
        Configuration.reopenBrowserOnFail = true;
        Configuration.browser = "chrome";
        Configuration.baseUrl = TestConfiguration.BASE_URL;
        Configuration.browserSize = "1920x1080";
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("enableVNC", true);
        Configuration.browserCapabilities = capabilities;
    }

    public static void clearReports() {
        log.info("Clearing reports dir ...");
        File allureResults = new File("allure-results");
        if (allureResults.isDirectory()) {
            File[] list = allureResults.listFiles();
            if (list != null)
                Arrays.stream(list).sequential().filter(e->!e.getName().equals("categories.json")).forEach(File::delete);
        }
    }
}
