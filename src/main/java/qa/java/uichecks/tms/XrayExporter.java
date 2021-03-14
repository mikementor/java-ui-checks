package qa.java.uichecks.tms;


import com.google.auto.service.AutoService;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

/**
 * For xray docs take a look here https://docs.getxray.app/display/XRAYCLOUD/Import+Execution+Results+-+REST+v2#ImportExecutionResultsRESTv2-XrayJSONresults
 */
@AutoService(TestExecutionListener.class)
public class XrayExporter implements TestExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionListener.class);
    private final Map<TestIdentifier, String> startTime = new ConcurrentHashMap<>();

    private final Class<TmsLink> issueKeyAnnotation = TmsLink.class;

    private static final String TEST_RESULT_TEMPLATE = "{\n" +
            "   \"testKey\":\"%s\",\n" +
            "   \"start\":\"%s\",\n" +
            "   \"finish\":\"%s\",\n" +
            "   \"comment\":\"%s\",\n" +
            "   \"status\":\"%s\"\n" +
            "}";
    /**
     * Format: https://docs.getxray.app/display/XRAYCLOUD/Import+Execution+Results#ImportExecutionResults-XrayJSONformat
     */
    private static final String EXECUTION_TEMPLATE = "{\n" +
            "    \"testExecutionKey\": \"%s\",\n" +
            "    \"info\" : {\n" +
            "    \"summary\" : \"Latest Execution of automation "+ new Date().toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)+"\"\n" +
            "    },\n" +
            "    \"tests\" : [%s]}";


    private static final String  ENABLE_KEY = "IMPORT_TO_XRAY";
    private static final String  XRAY_CLIENT_ID = "XRAY_CLIENT_ID";
    private static final String  XRAY_CLIENT_SECRET = "XRAY_CLIENT_SECRET";
    private static final String  XRAY_EXECUTION_KEY = "XRAY_EXECUTION_KEY";
    private static final String  RUNNING_EXAMPLE = "mvn test -DSHOULD_START_SELENOID=true -D%s=true -D%s=092DAC83B0E442FBA46E35FB2859E22C -D%s=44DAC83B0E442FBA46E35FB2859E22C -D%s=JIRA-123\n".formatted(ENABLE_KEY,XRAY_CLIENT_ID,XRAY_CLIENT_SECRET,XRAY_EXECUTION_KEY);


    private boolean isEnabled;

    private String executionKey = "";
    private String token = "";

    @SneakyThrows
    public XrayExporter() {
        isEnabled = Boolean.parseBoolean(System.getProperty(ENABLE_KEY, "false"));
        if (!isEnabled) {
            return;
        }
        String clientId = System.getProperty(XRAY_CLIENT_ID,"");
        String clientSecret = System.getProperty(XRAY_CLIENT_SECRET,"");
        this.executionKey = System.getProperty(XRAY_EXECUTION_KEY,"");
        if(executionKey.equals("") || clientId.equals("") || clientSecret.equals("")){
            List<String> errors= new ArrayList<>();
            if(executionKey.equals(""))
                errors.add("%s is missing".formatted(XRAY_EXECUTION_KEY));
            if(clientId.equals(""))
                errors.add("%s is missing".formatted(XRAY_CLIENT_ID));
            if(clientSecret.equals(""))
                errors.add("%s is missing".formatted(XRAY_CLIENT_SECRET));
            String msg ="\nXRAYExporter misconfigured! %s.\nRunning example:\n %s\n";
            String errorMsg = msg.formatted(String.join("\n",errors),RUNNING_EXAMPLE);
            logger.error(errorMsg);
            throw new MisConfiguredExportException(errorMsg);
        }

        setToken(clientId, clientSecret);
    }
    public static class MisConfiguredExportException extends RuntimeException{
        public MisConfiguredExportException(String errorMsg) {
            super(errorMsg);
        }
    }
    public static class FailedAuthorizationException extends RuntimeException{
        public FailedAuthorizationException(String msg) {
            super(msg);
        }
    }

    @SneakyThrows
    private void setToken(String clientId, String clientSecret) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        String body = "{ \"client_id\": \"%s\",\"client_secret\": \"%s\" }".formatted(clientId, clientSecret);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://xray.cloud.xpand-it.com/api/v2/authenticate"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode()!=200){
            String msg = "XRAYExporter couldn't authorize! Request:[ %s ] . Response: [status:%s] [body:%s] ";
            String error = msg.formatted(body,response.statusCode(),response.body());
            logger.error(error);
            throw new FailedAuthorizationException(error);
        }

        this.token = response.body().replaceAll("\"", "");
    }

    public String currentTime() {
        return new Date().toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (isEnabled && testIdentifier.isTest()) {
            startTime.put(testIdentifier, currentTime());
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (isEnabled && testIdentifier.isTest()) {
            TestSource testSource = testIdentifier.getSource().orElse(null);
            if (testSource instanceof MethodSource) {
                Method testMethod = Objects.requireNonNull(getMethod((MethodSource) testSource));
                if (!testMethod.isAnnotationPresent(issueKeyAnnotation))
                {
                    logger.warn("method [%s] doesn't have annotation [%s]! Result [%s] won't be exported to XRAY.".formatted(testMethod.getName(),issueKeyAnnotation.getCanonicalName(),testExecutionResult.getStatus()));
                    return;
                }
                String issueKey = testMethod.getAnnotation(issueKeyAnnotation).value();
                try {
                    sendResults(testExecutionResult.getStatus(), issueKey, this.startTime.remove(testIdentifier), currentTime());
                } catch (IOException | InterruptedException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private Method getMethod(MethodSource testSource) {
        try {
            Class<?> testClass = Class.forName(testSource.getClassName());
            return Arrays.stream(testClass.getDeclaredMethods())
                    .filter(method -> MethodSource.from(method).equals(testSource))
                    .findFirst().orElse(null);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * sends to the atlassian jira
     *
     * @param result     test result
     * @param issueKey   issue key(from annotation)
     * @param startTime
     * @param finishTime
     */
    private void sendResults(TestExecutionResult.Status result, String issueKey, String startTime, String finishTime) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        String testResults = TEST_RESULT_TEMPLATE.formatted(issueKey, startTime, finishTime, resultToComment(result), resultToXRay(result));

        String executionBody = EXECUTION_TEMPLATE.formatted(this.executionKey,testResults);

        HttpRequest executionRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://xray.cloud.xpand-it.com/api/v2/import/execution"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer %s".formatted(this.token))
                .POST(HttpRequest.BodyPublishers.ofString(executionBody))
                .build();

        var response = client.send(executionRequest, HttpResponse.BodyHandlers.ofString());
       logger.info(String.valueOf(response.statusCode()));
       logger.info(response.body());
    }

    private String resultToXRay(TestExecutionResult.Status result) {
        switch (result) {
            case SUCCESSFUL:
                return "PASSED";
            default:
                return "FAILED";
        }
    }

    private String resultToComment(TestExecutionResult.Status result) {
        if (result.equals(SUCCESSFUL))
            return "Success";
        return "Failed";
    }
}