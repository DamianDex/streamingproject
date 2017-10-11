package kinesis.ui;

import kinesis.client.KinesisClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KinesisClientApp {
    private JButton checkConnectivity;
    private JPanel mainPanel;
    private JTextArea textAreaLogger;

    public KinesisClientApp() {
        checkConnectivity.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                KinesisClient kinesisClient = new KinesisClient("http://localhost:4568", textAreaLogger);
                boolean result = kinesisClient.verifyConnection();
                System.out.println(result);
            }
        });
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("AWS Amazon Kinesis Client");
        jFrame.setContentPane(new KinesisClientApp().mainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
