package kinesis.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.kinesis.AmazonKinesis;

import javax.swing.*;
import java.util.List;

public abstract class KinesisClientAbstract {

    protected static final String SIGNING_REGION = "us-east-1";
    protected AmazonKinesis amazonKinesis;
    protected JTextArea textAreaLogger;
    protected String endpoint;

    public List<String> getAllStreamNames() {
        return amazonKinesis.listStreams().getStreamNames();
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
