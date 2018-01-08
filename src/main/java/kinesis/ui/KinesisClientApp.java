package kinesis.ui;

import com.amazonaws.services.kinesis.model.ShardIteratorType;
import com.amazonaws.services.kinesis.model.StreamDescription;
import kinesis.client.KinesisClientConsumer;
import kinesis.client.KinesisClientProducer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KinesisClientApp {

    private JPanel mainPanel;
    private JButton producerConnectBtn;
    private JButton chooseFileButton;
    private JButton producerStartBtn;
    private JTextField producerEndpoint;
    private JTextField filePathField;
    private JTextArea producerLogsArea;
    private JLabel producerConnectionStatusLabel;

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
    private JTextField consumerShardsNumber;
    private JTextField consumerRetentionPeriod;
    private JPanel consumerOptionsPanel;
    private JRadioButton readOnlyNewRecordsRadioButton;
    private JRadioButton readOldAndNewRadioButton;
    private JPanel consumerConnectionPanel;
    private JTextField consumerEndpoint;
    private JButton consumerConnectBtn;
    private JLabel consumerConnectionStatusLabel;
    private JTextField producernewStreamNumberOfShards;
    private JButton fetchStreamdescription;

    private File dataFile;

    private KinesisClientProducer kinesisClientProducer;
    private KinesisClientConsumer kinesisClientConsumer;

    public KinesisClientApp() {
        initConsumerListeners();
        initProducerListeners();
    }

    private void initProducerListeners() {
        producerConnectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    kinesisClientProducer = new KinesisClientProducer(producerEndpoint.getText(), producerLogsArea);
                    updateSuccessfulProducerConnectionStatus();
                } catch (Exception ex) {
                    updateFailedProducerConnectionStatus();
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

                String streamName = pointProducerStream();
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
                pushStreamNamesToProducerComboBox();
            }
        });
    }

    private void initConsumerListeners() {
        consumerStreamComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pushStreamNamesToConsumerComboBox();
                updateStreamDescription();
            }
        });

        consumerConnectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    kinesisClientConsumer = new KinesisClientConsumer(consumerEndpoint.getText(), consumerLogsArea);
                    updateSuccessfulConsumerConnectionStatus();
                    pushStreamNamesToConsumerComboBox();
                } catch (Exception ex) {
                    updateFailedConsumerConnectionStatus();
                }
            }
        });

        consumerStartBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        String streamName = pointConsumerStream();
                        ShardIteratorType shardIteratorType = chooseShardIteratorType();
                        kinesisClientConsumer.getRecords(streamName, shardIteratorType);
                    }
                }.start();
            }
        });

        fetchStreamdescription.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StreamDescription streamDescription = kinesisClientConsumer.getStreamDescription(consumerStreamComboBox.getSelectedItem().toString());
                consumerShardsNumber.setText(Integer.valueOf(streamDescription.getShards().size()).toString());
                consumerRetentionPeriod.setText(streamDescription.getRetentionPeriodHours() + " hours");
            }
        });
    }

    private ShardIteratorType chooseShardIteratorType() {
        if (readOnlyNewRecordsRadioButton.isSelected()) {
            return ShardIteratorType.LATEST;
        }
        return ShardIteratorType.TRIM_HORIZON;
    }

    private String pointProducerStream() {
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

    private String pointConsumerStream() {
        return consumerStreamComboBox.getSelectedItem().toString();
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("AWS Amazon Kinesis Client");
        KinesisClientApp kinesisClientApp = new KinesisClientApp();
        jFrame.setContentPane(kinesisClientApp.mainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setSize(1000, 1000);

        kinesisClientApp.initUI();
    }

    private void initUI() {
        initConsumerUI();
        initProducerUI();
    }

    private void initProducerUI() {
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

        producerLogsArea.setLineWrap(true);
    }

    private void initConsumerUI() {
        consumerPanel.setBorder(BorderFactory.createTitledBorder("Kinesis consumer"));
        consumerLoggerPanel.setBorder(BorderFactory.createTitledBorder("Log details"));
        consumerStreamsPanel.setBorder(BorderFactory.createTitledBorder("Stream details"));
        consumerOptionsPanel.setBorder(BorderFactory.createTitledBorder("Reading options"));
        consumerConnectionPanel.setBorder(BorderFactory.createTitledBorder("Stream details"));
        consumerStartBtn.setEnabled(false);
        consumerShardsNumber.setEnabled(false);
        consumerRetentionPeriod.setEnabled(false);
        readOnlyNewRecordsRadioButton.doClick();

        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(readOldAndNewRadioButton);
        buttonGroup1.add(readOnlyNewRecordsRadioButton);

        consumerLogsArea.setLineWrap(true);
    }

    private void updateSuccessfulConsumerConnectionStatus() {
        consumerConnectionStatusLabel.setText("Connected");
        consumerStartBtn.setEnabled(true);
    }

    private void updateStreamDescription() {
        StreamDescription streamDescription = kinesisClientConsumer.getStreamDescription(consumerStreamComboBox.getName());
    }

    private void updateSuccessfulProducerConnectionStatus() {
        producerConnectionStatusLabel.setText("Connected");
        chooseFileButton.setEnabled(true);
        producerStartBtn.setEnabled(true);
    }

    private void updateFailedProducerConnectionStatus() {
        producerConnectionStatusLabel.setText("Not Connected");
    }

    private void updateFailedConsumerConnectionStatus() {
        consumerConnectionStatusLabel.setText("Not connected");
    }

    private void pushStreamNamesToProducerComboBox() {
        streamComboBox.removeAllItems();
        for (String name : kinesisClientProducer.getAllStreamNames()) {
            streamComboBox.addItem(name);
        }
    }

    private void pushStreamNamesToConsumerComboBox() {
        consumerStreamComboBox.removeAllItems();
        for (String name : kinesisClientConsumer.getAllStreamNames()) {
            consumerStreamComboBox.addItem(name);
        }
    }
}
