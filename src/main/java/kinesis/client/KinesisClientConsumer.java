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

    public void getRecords() {

        ///TODO: Temporary
        String shardId = amazonKinesis.describeStream("test1").getStreamDescription().getShards().get(0).getShardId();
        String shardIterator = amazonKinesis.getShardIterator("test1", shardId, ShardIteratorType.TRIM_HORIZON.toString()).getShardIterator();
        ///

        GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
        getRecordsRequest.setShardIterator(shardIterator);

        GetRecordsResult getRecordsResult = amazonKinesis.getRecords(getRecordsRequest);

        ///TODO: Temporary
        for (Record record : getRecordsResult.getRecords()) {
            System.out.println(new String(record.getData().array()));
        }
        ///
    }

}
