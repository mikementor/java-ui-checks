package qa.java.uichecks.testdata;

import lombok.Getter;

import java.io.File;

public class TestData {

    public static DatabaseConfiguration config = new ConfigurationLoader().loadConfiguration(
            new File(DatabaseConfiguration.class.getResource("/application.yaml").getFile()), DatabaseConfiguration.class);;

    @Getter
    public static class DatabaseConfiguration {
        private TestDataConfig testdata;
    }
    @Getter
    public static class TestDataConfig{
        private String login;
        private String password;
    }
}

