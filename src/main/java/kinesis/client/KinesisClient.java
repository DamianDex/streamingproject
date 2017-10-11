package kinesis.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.PutRecordRequest;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.util.List;

public class KinesisClient {

    private static final String SIGNING_REGION = "us-east-1";
    private AmazonKinesis amazonKinesis;
    private JTextArea textAreaLogger;
    private String endpoint;

    public KinesisClient(String endpoint, JTextArea textAreaLogger) {
        this.endpoint = endpoint;
        this.textAreaLogger = textAreaLogger;
        System.setProperty("com.amazonaws.sdk.disableCbor", "1");
        AmazonKinesisClientBuilder amazonKinesisClientBuilder = AmazonKinesisClientBuilder.standard();
        amazonKinesis = amazonKinesisClientBuilder
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "aa"))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummy", "dummy")))
                .build();
    }

    public void createStream(String streamName, int shardsNumber) {
        amazonKinesis.createStream(streamName, shardsNumber);
        textAreaLogger.append("Stream with name " + streamName + " with " + shardsNumber + " was created on " + endpoint);
    }

    public List<String> getAllStreamNames() {
        return amazonKinesis.listStreams().getStreamNames();
    }

    public void deleteStream(String streamName) {
        amazonKinesis.deleteStream(streamName);
    }

    public void deleteAllStreams() {
        for (String streamName : amazonKinesis.listStreams().getStreamNames())
            amazonKinesis.deleteStream(streamName);
    }

    public void sendSingleRecord(String streamName, String data) {
        PutRecordRequest putRecordRequest = new PutRecordRequest();
        putRecordRequest.setStreamName(streamName);
        putRecordRequest.setData(ByteBuffer.wrap(data.getBytes()));
        putRecordRequest.setPartitionKey("dummy partition key");
        amazonKinesis.putRecord(putRecordRequest);
    }

    public boolean verifyConnection() {
        try {
            amazonKinesis.listStreams();
        } catch (AmazonClientException ex) {
            ex.printStackTrace();
            textAreaLogger.append("Connection failed");
            return false;
        }
        textAreaLogger.append("Connection successful");
        return true;
    }
}
