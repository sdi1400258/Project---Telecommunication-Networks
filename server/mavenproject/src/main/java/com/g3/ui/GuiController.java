package com.g3.ui;

import com.g3.conversion.ConversionController;
import com.g3.heatmap.Heatmap;
import com.g3.heatmap.HeatmapController;
import com.g3.mqtt.MqttController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// https://www.codejava.net/java-se/swing/java-swing-hello-world-tutorial-for-beginners-using-text-editor
public class GuiController extends JFrame implements ActionListener {
    private JLabel heading;
    private JLabel heading2;
    private JLabel heading3;
    private JLabel heatmap1;
    private JLabel heatmap2;
    private JButton button1;
    private JButton button2;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private Dimension tileSize;
    private Dimension imageSize;
    private Graphics2D graphics2dRssi;
    private Graphics2D graphics2dThroughput;
    private JPanel centerPanel;

    public GuiController() throws IOException {
        super("Campus");

        initComponents();

        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    } //?

    private void initComponents() throws IOException {
        setLayout(new BorderLayout());
        int i;
        int j;

        heading = new JLabel("Control panel");
        heading2 = new JLabel("Heapmap for RSSI");
        heading3 = new JLabel("Heapmap for Throughput");
        button1 = new JButton("Convert XML files to CSV");
        button2 = new JButton("Calculate heatmap");

        BufferedImage myPicture = ImageIO.read(new File("Map.png")); //read file of map-row 12
        int height = myPicture.getHeight();
        graphics2dRssi = (Graphics2D) myPicture.getGraphics();
        graphics2dRssi.setStroke(new BasicStroke(5));
        graphics2dRssi.setColor(Color.red);
        //next,draw lines for the 4X10 pictures of map
        for (i = 133; i < 1330; i += 133) {
            graphics2dRssi.drawLine(i, 0, i, 275);
        }
        for (j = 68; j < 272; j += 68) {
            graphics2dRssi.drawLine(0, j, 1330, j);
        }
        //create the first heatmap
        tileSize = new Dimension(myPicture.getWidth()/10, myPicture.getHeight()/4);
        imageSize = new Dimension(myPicture.getWidth(), myPicture.getHeight());

        Image myPictureRescaled = myPicture.getScaledInstance((int) screenSize.getWidth() - 100, height, 0);
        heatmap1 = new JLabel(new ImageIcon(myPictureRescaled));

        myPicture = ImageIO.read(new File("Map.png"));
        graphics2dThroughput = (Graphics2D) myPicture.getGraphics();
        graphics2dThroughput.setStroke(new BasicStroke(5));
        graphics2dThroughput.setColor(Color.blue);
        for (i = 133; i < 1330; i += 133) {
            graphics2dThroughput.drawLine(i, 0, i, 275);
        }
        for (j = 68; j < 272; j += 68) {
            graphics2dThroughput.drawLine(0, j, 1330, j);
        }
        myPictureRescaled = myPicture.getScaledInstance((int) screenSize.getWidth() - 100, height, 0);
        heatmap2 = new JLabel(new ImageIcon(myPictureRescaled));


        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        northPanel.add(heading);
        northPanel.setBackground(Color.blue);
        northPanel.setForeground(Color.white);//?
//
        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        centerPanel.add(heading2);
        centerPanel.add(heatmap1);
        centerPanel.add(heading3);
        centerPanel.add(heatmap2);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        southPanel.add(button1);
        southPanel.add(button2);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        button1.addActionListener(this);
        button2.addActionListener(this);
    }

    public void display() {
        setVisible(true);
    }

    public void displayHeatmap(Heatmap heatmap) throws IOException {
        BufferedImage myPicture1 = ImageIO.read(new File("Map.png"));
        int height = myPicture1.getHeight();
        graphics2dRssi = (Graphics2D) myPicture1.getGraphics();
        graphics2dRssi.setStroke(new BasicStroke(5));
        graphics2dRssi.setColor(Color.red);
        for (int i = 133; i < 1330; i += 133) {
            graphics2dRssi.drawLine(i, 0, i, 275);
        }
        for (int j = 68; j < 272; j += 68) {
            graphics2dRssi.drawLine(0, j, 1330, j);
        }


        BufferedImage myPicture2 = ImageIO.read(new File("Map.png"));
        graphics2dThroughput = (Graphics2D) myPicture2.getGraphics();
        graphics2dThroughput.setStroke(new BasicStroke(5));
        graphics2dThroughput.setColor(Color.blue);
        for (int i = 133; i < 1330; i += 133) {
            graphics2dThroughput.drawLine(i, 0, i, 275);
        }
        for (int j = 68; j < 272; j += 68) {
            graphics2dThroughput.drawLine(0, j, 1330, j);
        }


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(heatmap.rssi_values[i][j] + "\t");
            }
            System.out.println();
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 10; j++) {
                double rssi = heatmap.rssi_values[i][j];
                double throughput = heatmap.throughput_values[i][j];

                double x = 3 + j*tileSize.width;
                double y = i*tileSize.height;

                if (rssi >= 0 && rssi < 0.1f) {
                    graphics2dRssi.setColor(new Color(1,0,0,0.5f));
                }
                /*
                if (rssi >= 0.1f && rssi < 0.2f) {
                    graphics2dRssi.setColor(new Color(1,0,0,0.5f));
                }
                if (rssi >= 0.2f && rssi < 0.3f) {
                    graphics2dRssi.setColor(new Color(1,0,0,0.5f));
                }
                if (rssi >= 0.3f && rssi < 0.4f) {
                    graphics2dRssi.setColor(new Color(1,0,0,0.5f));
                }*/
                if (rssi >= 0.4f && rssi < 0.41f) {
                    graphics2dRssi.setColor(new Color(1,0,0,0.5f));
                }

                if (rssi >= 0.41f && rssi < 0.42f) {
                    graphics2dRssi.setColor(new Color(1,0,0,0.5f));
                }
                if (rssi >= 0.42f && rssi < 0.43f) {
                    graphics2dRssi.setColor(new Color(1,0,0,0.5f));
                }
                if (rssi >= 0.43f && rssi < 0.44f) {
                    graphics2dRssi.setColor(new Color(1,0.2f,0.2f,0.5f));
                }
                if (rssi >= 0.44f && rssi < 0.45f) {
                    graphics2dRssi.setColor(new Color(1,0.4f,0.4f,0.5f));
                }
                if (rssi >= 0.45f && rssi < 0.46f) {
                    graphics2dRssi.setColor(new Color(1,0.6f,0.6f,0.5f));
                }
                if (rssi >= 0.46f && rssi < 0.47f) {
                    graphics2dRssi.setColor(new Color(1,1,0.6f,0.5f));
                }
                if (rssi >= 0.47f && rssi < 0.48f) {
                    graphics2dRssi.setColor(new Color(1,1,0.4f,0.5f));
                }
                if (rssi >= 0.48f && rssi < 0.49f) {
                    graphics2dRssi.setColor(new Color(1,1,0.2f,0.5f));
                }
                if (rssi >= 0.49f && rssi < 0.50f) {
                    graphics2dRssi.setColor(new Color(1,1,0,0.5f));
                }
                if (rssi >= 0.50f && rssi < 0.51f) {
                    graphics2dRssi.setColor(new Color(0.6f,1,0.6f,0.5f));
                }

                if (rssi >= 0.51f && rssi < 0.52f) {
                    graphics2dRssi.setColor(new Color(0.6f,1,0.4f,0.5f));
                }
                if (rssi >= 0.52f && rssi < 0.53f) {
                    graphics2dRssi.setColor(new Color(0.6f,1,0.2f,0.5f));
                }
                if (rssi >= 0.53f && rssi < 0.54f) {
                    graphics2dRssi.setColor(new Color(0.6f,1,0.0f,0.5f));
                }

                if (throughput >= 0 && throughput < 0.1f) {
                    graphics2dThroughput.setColor(new Color(1,0,0,0.5f));
                }
                /*
                if (throughput >= 0.1f && throughput < 0.2f) {
                    graphics2dthroughput.setColor(new Color(1,0,0,0.5f));
                }
                if (throughput >= 0.2f && throughput < 0.3f) {
                    graphics2dthroughput.setColor(new Color(1,0,0,0.5f));
                }
                if (throughput >= 0.3f && throughput < 0.4f) {
                    graphics2dthroughput.setColor(new Color(1,0,0,0.5f));
                }*/
                if (throughput >= 0.4f && throughput < 0.41f) {
                    graphics2dThroughput.setColor(new Color(1,0,0,0.5f));
                }

                if (throughput >= 0.41f && throughput < 0.42f) {
                    graphics2dThroughput.setColor(new Color(1,0,0,0.5f));
                }
                if (throughput >= 0.42f && throughput < 0.43f) {
                    graphics2dThroughput.setColor(new Color(1,0,0,0.5f));
                }
                if (throughput >= 0.43f && throughput < 0.44f) {
                    graphics2dThroughput.setColor(new Color(1,0.2f,0.2f,0.5f));
                }
                if (throughput >= 0.44f && throughput < 0.45f) {
                    graphics2dThroughput.setColor(new Color(1,0.4f,0.4f,0.5f));
                }
                if (throughput >= 0.45f && throughput < 0.46f) {
                    graphics2dThroughput.setColor(new Color(1,0.6f,0.6f,0.5f));
                }
                if (throughput >= 0.46f && throughput < 0.47f) {
                    graphics2dThroughput.setColor(new Color(1,1,0.6f,0.5f));
                }
                if (throughput >= 0.47f && throughput < 0.48f) {
                    graphics2dThroughput.setColor(new Color(1,1,0.4f,0.5f));
                }
                if (throughput >= 0.48f && throughput < 0.49f) {
                    graphics2dThroughput.setColor(new Color(1,1,0.2f,0.5f));
                }
                if (throughput >= 0.49f && throughput < 0.50f) {
                    graphics2dThroughput.setColor(new Color(1,1,0,0.5f));
                }
                if (throughput >= 0.50f && throughput < 0.51f) {
                    graphics2dThroughput.setColor(new Color(0.6f,1,0.6f,0.5f));
                }

                if (throughput >= 0.51f && throughput < 0.52f) {
                    graphics2dThroughput.setColor(new Color(0.6f,1,0.4f,0.5f));
                }
                if (throughput >= 0.52f && throughput < 0.53f) {
                    graphics2dThroughput.setColor(new Color(0.6f,1,0.2f,0.5f));
                }
                if (throughput >= 0.53f && throughput < 0.54f) {
                    graphics2dThroughput.setColor(new Color(0.6f,1,0.0f,0.5f));
                }
                //RECTANGLES colour CREATION
                graphics2dRssi.fillRect((int)x, (int)y, tileSize.width + 1, tileSize.height);

                graphics2dThroughput.fillRect((int)x, (int)y, tileSize.width + 1, tileSize.height);
            }
        }

        Image myPictureRescaled = myPicture1.getScaledInstance((int) screenSize.getWidth() - 100, height, 0);
        heatmap1 = new JLabel(new ImageIcon(myPictureRescaled));

        myPictureRescaled = myPicture2.getScaledInstance((int) screenSize.getWidth() - 100, height, 0);
        heatmap2 = new JLabel(new ImageIcon(myPictureRescaled));
        //after calculate heatmap
        centerPanel.removeAll();
        centerPanel.add(heading2);
        centerPanel.add(heatmap1);
        centerPanel.add(heading3);
        centerPanel.add(heatmap2);
        centerPanel.revalidate();
        centerPanel.repaint();

        MqttController.heatmap = heatmap;
    }
    //buttons configure
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            ConversionController convCtr = new ConversionController();
            try {
                convCtr.convert();
                JOptionPane.showMessageDialog(this, "Conversion complete", "All good", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error has occured", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == button2) {
            HeatmapController heatctr = new HeatmapController();
            try {
                Heatmap heatmap = heatctr.calculate();
                displayHeatmap(heatmap);
                JOptionPane.showMessageDialog(this, "Calculation complete", "All good", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error has occured", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
