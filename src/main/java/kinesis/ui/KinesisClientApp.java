package kinesis.ui;

import kinesis.client.KinesisClientProducer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class KinesisClientApp {

    //GUI Elements//
    private JPanel mainPanel;
    private JButton checkConnectivityBtn;
    private JButton chooseFileButton;
    private JButton startButton;
    private JTextField endpoint;
    private JTextField filePathField;
    private JTextArea textAreaLogger;
    private JLabel connectionStatusLabel;

    private File dataFile;

    //Kinesis Producer//
    private KinesisClientProducer kinesisClientProducer;

    public KinesisClientApp() {
        checkConnectivityBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                kinesisClientProducer = new KinesisClientProducer(endpoint.getText(), textAreaLogger);

                boolean result = kinesisClientProducer.verifyConnection();
                if(result)
                    updateConnected();
                else
                    updateNotConnected();
            }
        });

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.showOpenDialog(jFileChooser);
                dataFile = jFileChooser.getSelectedFile();
                filePathField.setText(jFileChooser.getSelectedFile().toString());
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            textAreaLogger.append(line);
                            kinesisClientProducer.sendSingleRecord("test1", line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("AWS Amazon Kinesis Client");
        KinesisClientApp kinesisClientApp = new KinesisClientApp();
        jFrame.setContentPane(kinesisClientApp.mainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

        kinesisClientApp.initUI();
    }

    private void initUI() {
        startButton.setEnabled(false);
        chooseFileButton.setEnabled(false);
        filePathField.setEditable(false);
    }

    private void updateConnected() {
        connectionStatusLabel.setText("Connected");
        chooseFileButton.setEnabled(true);
        startButton.setEnabled(true);

        mainPanel.setBorder(BorderFactory.createBevelBorder(1));

    }

    private void updateNotConnected() {
        connectionStatusLabel.setText("Not Connected");
        chooseFileButton.setEnabled(false);
        startButton.setEnabled(false);

        mainPanel.setBorder(BorderFactory.createTitledBorder("Connection"));

    }
}
