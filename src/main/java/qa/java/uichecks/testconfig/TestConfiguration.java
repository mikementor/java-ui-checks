package qa.java.uichecks.testconfig;

public class TestConfiguration {
    public static boolean CLEAR_REPORTS_DIR = Boolean.parseBoolean(System.getProperty("CLEAR_REPORTS_DIR","true"));

    public static boolean SHOULD_START_SELENOID = Boolean.parseBoolean(System.getProperty("SHOULD_START_SELENOID","false"));

    public static String BASE_URL = System.getProperty("BASE_URL","https://provectus.com");

    public static String SELENOID_URL = System.getProperty("SELENOID_URL","http://localhost:4444/wd/hub");

}

