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
    private JButton producerStartBtn;
    private JTextField endpoint;
    private JTextField filePathField;
    private JTextArea producerLogsArea;
    private JLabel connectionStatusLabel;

    private JPanel producerConnectionPanel;
    private JPanel producerLoggerPanel;
    private JPanel producerFilePanel;
    private JPanel producerStreamsPanel;
    private JComboBox streamComboBox;
    private JRadioButton newStreamBtn;
    private JRadioButton existingStreamBtn;
    private JTextField newStreamNameField;
    private JPanel producerPanel;
    private JPanel consumerPanel;
    private JPanel consumerLoggerPanel;
    private JTextArea consumerLogsArea;
    private JButton consumerStartBtn;
    private JPanel consumerStreamsPanel;
    private JComboBox consumerStreamComboBox;
    private JTextField textField1;
    private JTextField textField2;
    private JPanel consumerOptionsPanel;
    private JRadioButton readOnlyNewRecordsRadioButton;
    private JRadioButton readOldAndNewRadioButton;

    private File dataFile;

    private KinesisClientProducer kinesisClientProducer;

    public KinesisClientApp() {

        consumerStreamComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumerStreamComboBox.removeAllItems();
                pushStreamNamesToComboBox();
            }
        });

        checkConnectivityBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    kinesisClientProducer = new KinesisClientProducer(endpoint.getText(), producerLogsArea);
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

        producerStartBtn.addActionListener(new ActionListener() {
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
                        producerLogsArea.append(line + "\n");
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
        /// Consumer ///
        consumerPanel.setBorder(BorderFactory.createTitledBorder("Kinesis consumer"));
        consumerLoggerPanel.setBorder(BorderFactory.createTitledBorder("Log details"));
        consumerStreamsPanel.setBorder(BorderFactory.createTitledBorder("Stream details"));
        consumerOptionsPanel.setBorder(BorderFactory.createTitledBorder("Reading options"));
        readOnlyNewRecordsRadioButton.doClick();

        /// Producer ///
        producerPanel.setBorder(BorderFactory.createTitledBorder("Kinesis producer"));
        producerConnectionPanel.setBorder(BorderFactory.createTitledBorder("Connection details"));
        producerFilePanel.setBorder(BorderFactory.createTitledBorder("Data details"));
        producerLoggerPanel.setBorder(BorderFactory.createTitledBorder("Log details"));
        producerStreamsPanel.setBorder(BorderFactory.createTitledBorder("Stream details"));
        producerStartBtn.setEnabled(false);
        chooseFileButton.setEnabled(false);
        filePathField.setEditable(false);
        streamComboBox.setEditable(false);
        newStreamBtn.doClick();


        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(newStreamBtn);
        buttonGroup.add(existingStreamBtn);

        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(readOldAndNewRadioButton);
        buttonGroup1.add(readOnlyNewRecordsRadioButton);
    }

    private void updateConnected() {
        connectionStatusLabel.setText("Connected");
        chooseFileButton.setEnabled(true);
        producerStartBtn.setEnabled(true);
    }

    private void updateNotConnected() {
        connectionStatusLabel.setText("Not Connected");
    }

    private void pushStreamNamesToComboBox() {
        for (String name : kinesisClientProducer.getAllStreamNames()) {
            streamComboBox.addItem(name);
            consumerStreamComboBox.addItem(name);
        }
    }
}
