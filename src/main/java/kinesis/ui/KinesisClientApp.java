package kinesis.ui;

import kinesis.client.KinesisClientProducer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

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

    private JPanel connectionPanel;
    private JPanel loggerPanel;
    private JPanel filePanel;
    private JPanel streamsPanel;
    private JComboBox streamComboBox;
    private JRadioButton newStreamBtn;
    private JRadioButton existingStreamBtn;
    private JTextField newStreamNameField;

    private File dataFile;

    private KinesisClientProducer kinesisClientProducer;

    public KinesisClientApp() {
        checkConnectivityBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    kinesisClientProducer = new KinesisClientProducer(endpoint.getText(), textAreaLogger);
                    updateConnected();
                } catch (Exception ex) {
                    updateNotConnected();
                }
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

                String streamName = pointStream();
                FileReader fileReader = null;

                try {
                    fileReader = new FileReader(dataFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try (BufferedReader br = new BufferedReader(fileReader)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        textAreaLogger.append(line + "\n");
                        kinesisClientProducer.sendSingleRecord(streamName, line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        existingStreamBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                streamComboBox.removeAllItems();
                pushStreamNamesToComboBox();
            }
        });
    }

    private String pointStream() {
        String streamName;

        if (newStreamBtn.isSelected()) {
            streamName = newStreamNameField.getText();
            kinesisClientProducer.createStream(streamName, 1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            streamName = streamComboBox.getSelectedItem().toString();
        }
        return streamName;
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("AWS Amazon Kinesis Client");
        KinesisClientApp kinesisClientApp = new KinesisClientApp();
        jFrame.setContentPane(kinesisClientApp.mainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setSize(600, 600);

        kinesisClientApp.initUI();
    }

    private void initUI() {
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection details"));
        filePanel.setBorder(BorderFactory.createTitledBorder("Data details"));
        loggerPanel.setBorder(BorderFactory.createTitledBorder("Log details"));
        streamsPanel.setBorder(BorderFactory.createTitledBorder("Stream details"));
        startButton.setEnabled(false);
        chooseFileButton.setEnabled(false);
        filePathField.setEditable(false);
        streamComboBox.setEditable(false);
        newStreamBtn.doClick();

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(newStreamBtn);
        buttonGroup.add(existingStreamBtn);
    }

    private void updateConnected() {
        connectionStatusLabel.setText("Connected");
        chooseFileButton.setEnabled(true);
        startButton.setEnabled(true);
    }

    private void updateNotConnected() {
        connectionStatusLabel.setText("Not Connected");
    }

    private void pushStreamNamesToComboBox() {
        for (String name : kinesisClientProducer.getAllStreamNames()) {
            streamComboBox.addItem(name);
        }
    }
}
