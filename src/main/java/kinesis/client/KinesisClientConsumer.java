package kinesis.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.model.ShardIteratorType;

import javax.swing.*;

public class KinesisClientConsumer extends KinesisClientAbstract {

    public KinesisClientConsumer(String endpoint, JTextArea textAreaLogger) {
        this.endpoint = endpoint;
        this.textAreaLogger = textAreaLogger;
        System.setProperty("com.amazonaws.sdk.disableCbor", "1");
        AmazonKinesisClientBuilder amazonKinesisClientBuilder = AmazonKinesisClientBuilder.standard();
        amazonKinesis = amazonKinesisClientBuilder
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, SIGNING_REGION))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummy", "dummy")))
                .build();
    }

    public void getRecords(String streamName) {
        String shardId = amazonKinesis.describeStream(streamName).getStreamDescription().getShards().get(0).getShardId();
        String shardIterator = amazonKinesis.getShardIterator(streamName, shardId, ShardIteratorType.TRIM_HORIZON.toString()).getShardIterator();


        GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
        getRecordsRequest.setShardIterator(shardIterator);

        GetRecordsResult getRecordsResult = amazonKinesis.getRecords(getRecordsRequest);

        for (Record record : getRecordsResult.getRecords()) {
            textAreaLogger.append(new String(record.getData().array()));
        }
    }
}
