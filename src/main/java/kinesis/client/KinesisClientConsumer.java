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
import java.util.List;

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
        String shardIterator = amazonKinesis.getShardIterator(streamName, shardId, ShardIteratorType.LATEST.toString()).getShardIterator();

        List<Record> records;
        while (true) {
            GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
            getRecordsRequest.setShardIterator(shardIterator);
            getRecordsRequest.setLimit(25);

            GetRecordsResult result = amazonKinesis.getRecords(getRecordsRequest);
            records = result.getRecords();

            for (Record record : records) {
                textAreaLogger.append(new String(record.getData().array()));
                textAreaLogger.append("\n");
            }
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
            shardIterator = result.getNextShardIterator();
        }

    }
}
