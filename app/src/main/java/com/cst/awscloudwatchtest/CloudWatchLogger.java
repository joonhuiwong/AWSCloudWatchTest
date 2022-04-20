package com.cst.awscloudwatchtest;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AmazonCloudWatchLogsClient;
import com.amazonaws.services.logs.model.CreateLogGroupRequest;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.LogStream;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.amazonaws.services.logs.model.ResourceAlreadyExistsException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CloudWatchLogger {

    private AmazonCloudWatchLogsClient client;

    private PutLogEventsResult putLogEventsResult; // if you want to view the results of the put I guess

    public CloudWatchLogger() {
        // Can connect right away if you bundle the credentials in the Application.
        // If you use another method, just call connect() AFTER you have your credentials.
        //connect();
    }

    public void connect() {
        String accessKey = BuildConfig.AWS_ACCESS_KEY;
        String secretKey = BuildConfig.AWS_SECRET_KEY;
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonCloudWatchLogsClient client = new AmazonCloudWatchLogsClient(basicAWSCredentials);
        Regions regions = Regions.AP_SOUTHEAST_1; // Singapore
        client.setRegion(Region.getRegion(regions));

        this.client = client;
    }

    /**
     * As you can see, the code is put into a Thread to run in background.
     * The actual calls to CloudWatch have to be run in a background thread unless we turn off the checking.
     */
    public void log(String logGroupName, String logStreamName, String message) {
        try {
            Thread t = new Thread(() -> {
                // Create LogGroup / LogStream (Will ignore if already created)
                createCloudWatchGroups(logGroupName, logStreamName);

                // Prepare the LogMessage that you will send
                List<InputLogEvent> logEvents = new ArrayList<>();
                InputLogEvent log = new InputLogEvent();
                Calendar calendar = Calendar.getInstance();
                log.setTimestamp(calendar.getTimeInMillis());
                log.setMessage(message);
                logEvents.add(log);

                // Check Cloudwatch for logStream if exist, to get the uploadSequenceToken.
                // Without this token you can still push events, but cannot push additional events to the an existing logGroup/logStream.
                // With the token, you can "append" the logStream (which we want).
                DescribeLogStreamsRequest logStreamsRequest = new DescribeLogStreamsRequest(logGroupName);
                logStreamsRequest.withLimit(5);
                List<LogStream> logStreamList = client.describeLogStreams(logStreamsRequest).getLogStreams();

                String token = null;
                for (LogStream logStream : logStreamList) {
                    if (logStream.getLogStreamName().equalsIgnoreCase(logStreamName)) {
                        token = logStream.getUploadSequenceToken();
                    }
                }

                if (token != null) {
                    PutLogEventsRequest putLogEventsRequest = new PutLogEventsRequest();
                    putLogEventsRequest.setLogGroupName(logGroupName);
                    putLogEventsRequest.setLogStreamName(logStreamName);
                    putLogEventsRequest.setLogEvents(logEvents);

                    putLogEventsRequest.setSequenceToken(token);

                    putLogEventsResult = client.putLogEvents(putLogEventsRequest);
                }
            });
            t.start();
            t.join();
        } catch (InterruptedException ignored) {
        }
    }

    private void createCloudWatchGroups(String logGroupName, String logStreamName) {
        try {
            Thread t = new Thread(() -> {
                CreateLogGroupRequest createLogGroupRequest = new CreateLogGroupRequest(logGroupName);
                try {
                    client.createLogGroup(createLogGroupRequest);
                } catch (Exception e) {
                    if (e instanceof ResourceAlreadyExistsException) {
                        assert true; // do nothing
                    } else {
                        e.printStackTrace();
                    }

                }

                CreateLogStreamRequest createLogStreamRequest = new CreateLogStreamRequest(logGroupName, logStreamName);
                try {
                    client.createLogStream(createLogStreamRequest);
                } catch (Exception e) {
                    if (e instanceof ResourceAlreadyExistsException) {
                        assert true; // do nothing
                    } else {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            t.join();
        } catch (InterruptedException ignored) {
        }
    }
}
