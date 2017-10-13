package kinesis.ui;

import kinesis.client.KinesisClientProducer;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KinesisClientApp {
    private JButton checkConnectivity;
    private JPanel mainPanel;
    private JTextArea textAreaLogger;
    private JButton chooseFileButton;

    public KinesisClientApp() {
        checkConnectivity.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                KinesisClientProducer kinesisClientProducer = new KinesisClientProducer("http://localhost:4568", textAreaLogger);
                boolean result = kinesisClientProducer.verifyConnection();
                System.out.println(result);
            }
        });

        //TODO: Implement choosing file with JSONs data
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.showOpenDialog(jFileChooser);
                System.out.println(jFileChooser.getSelectedFile().toString());
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
