package kinesis.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class KinesisClientProducerTest {

    @Mock
    private JTextArea jTextArea;

    @Test
    public void testConnection() {
        KinesisClientProducer kinesisClientProducer = new KinesisClientProducer("http://localhost:4568", jTextArea);
        assertTrue(kinesisClientProducer.verifyConnection());
    }

    @Test
    public void testStreamCreation() {
        KinesisClientProducer kinesisClientProducer = new KinesisClientProducer("http://localhost:4568", jTextArea);
        kinesisClientProducer.deleteAllStreams();
        kinesisClientProducer.createStream("test1", 2);
        assertEquals(1, kinesisClientProducer.getAllStreamNames().size());
        assertEquals("test1", kinesisClientProducer.getAllStreamNames().get(0));
    }

    @Test
    public void sendSingleRecord() {
        KinesisClientProducer kinesisClientProducer = new KinesisClientProducer("http://localhost:4568", jTextArea);
        //kinesisClientProducer.createStream("test1", 1);
        kinesisClientProducer.sendSingleRecord("aaa", "{a: 1}");
        KinesisClientConsumer kinesisClientConsumer = new KinesisClientConsumer("http://localhost:4568", jTextArea);
        kinesisClientConsumer.getRecords("aaa");
    }
}