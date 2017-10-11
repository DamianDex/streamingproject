package kinesis.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class KinesisClientTest {

    @Mock
    private JTextArea jTextArea;


    @Test
    public void testConnection() {
        KinesisClient kinesisClient = new KinesisClient("http://localhost:4568", jTextArea);
        assertTrue(kinesisClient.verifyConnection());
    }

    @Test
    public void testStreamCreation() {
        KinesisClient kinesisClient = new KinesisClient("http://localhost:4568", jTextArea);
        kinesisClient.deleteAllStreams();
        kinesisClient.createStream("test1", 2);
        assertEquals(1, kinesisClient.getAllStreamNames().size());
        assertEquals("test1", kinesisClient.getAllStreamNames().get(0));
    }
}