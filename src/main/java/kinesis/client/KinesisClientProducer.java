package kinesis.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;

import javax.swing.*;
import java.nio.ByteBuffer;

public class KinesisClientProducer extends KinesisClientAbstract {


    public KinesisClientProducer(String endpoint, JTextArea textAreaLogger) {
        this.endpoint = endpoint;
        this.textAreaLogger = textAreaLogger;
        System.setProperty("com.amazonaws.sdk.disableCbor", "1");
        AmazonKinesisClientBuilder amazonKinesisClientBuilder = AmazonKinesisClientBuilder.standard();
        amazonKinesis = amazonKinesisClientBuilder
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, SIGNING_REGION))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummy", "dummy")))
                .build();
    }

    public void createStream(String streamName, int shardsNumber) {
        amazonKinesis.createStream(streamName, shardsNumber);
        textAreaLogger.append("Stream with name " + streamName + " with " + shardsNumber + " was created on " + endpoint);
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
    }
}
