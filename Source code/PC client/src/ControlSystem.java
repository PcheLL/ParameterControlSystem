import arduino.Arduino;
import javafx.scene.media.AudioClip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ControlSystem {
    private static int temperature = 0;
    private static int humidity = 0;
    private static int state = 0;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Control System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setResizable(false);
        addComponentsToPane(frame.getContentPane());
        frame.setVisible(true);
    }

    private static void addComponentsToPane(Container pane) {

        pane.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        constraints.gridx = 0;
        constraints.gridy = 0;
        pane.add(new JLabel("Параметры контроля:"), constraints);


        constraints.gridx = 0;
        constraints.gridy = 1;
        JLabel labelTemperature = new JLabel("Температура: " + temperature + " *C");
        pane.add(labelTemperature, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel labelHumidity = new JLabel("Влажность: " + humidity + " %");
        pane.add(labelHumidity, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        JButton buttonFire = new JButton("NO FIRE");
        buttonFire.setPreferredSize(new Dimension(75, 20));
        buttonFire.setBackground(Color.GREEN);
        pane.add(buttonFire, constraints);

        buttonFire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonFire.setText("NO FIRE");
                buttonFire.setBackground(Color.GREEN);
                AudioClip note = new AudioClip(this.getClass().getResource("Fire.mp3").toString());
                note.stop();
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 4;
        JButton buttonSmoke = new JButton("NO SMOKE or GAS");
        buttonSmoke.setPreferredSize(new Dimension(75, 20));
        buttonSmoke.setBackground(Color.GREEN);
        pane.add(buttonSmoke, constraints);

        buttonSmoke.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                buttonSmoke.setText("NO SMOKE or GAS");
                buttonSmoke.setBackground(Color.GREEN);
                AudioClip note1 = new AudioClip(this.getClass().getResource("Smoke.mp3").toString());
                note1.stop();
            }
        });

        Thread thr = new Thread() {
            private int k;
            private String a;

            @Override
            public void run() {
                System.out.println("Новый поток !!!");
                Arduino arduino = new Arduino("COM3", 9600);
                boolean connected = arduino.openConnection();
                System.out.println("Соединение установленно: " + connected);
                do {
                    if (arduino.getSerialPort().bytesAvailable() > 0) {
                        a = null;
                        a = arduino.serialRead();
                        state = Integer.parseInt(a.trim());
                        if (state == 1) {
                            buttonFire.setText("FIRE");
                            buttonFire.setBackground(Color.RED);
                            AudioClip note = new AudioClip(this.getClass().getResource("Fire.mp3").toString());
                            note.play();
                        }
                        if (state == 2) {
                            buttonSmoke.setText("SMOKE or GAS");
                            buttonSmoke.setBackground(Color.RED);
                            AudioClip note1 = new AudioClip(this.getClass().getResource("Smoke.mp3").toString());
                            note1.play();
                        }
                        if (state == 4) {
                            a = null;
                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            a = arduino.serialRead();
                            temperature = Integer.parseInt(a.trim());
                            labelTemperature.setText("Температура: " + temperature + " *C");
                        }
                        if (state == 5) {
                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            a = arduino.serialRead();
                            humidity = Integer.parseInt(a.trim());
                            labelHumidity.setText("Влажность:" + humidity + " %");
                        }
                    }
                } while (k != 1);
            }
        };
        thr.start();
    }
}

