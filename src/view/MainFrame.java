package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

class DataManager {
    private java.util.Properties properties;

    public DataManager() {
        properties = new java.util.Properties();
        try {
            properties.load(new java.io.FileInputStream("data.properties"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key, "");
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        try {
            properties.store(new java.io.FileOutputStream("data.properties"), null);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

public class MainFrame extends JFrame {

    private JPanel contentPane;
    private JPanel panelContainer;
    private JButton btnAdd;
    private JButton btnSave; // Kaydet butonu eklendi
    private DataManager dataManager;

    private Point initialClick;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainFrame() {
        setUndecorated(true);
        setVisible(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 473, 616);

        dataManager = new DataManager();

        contentPane = new JPanel();
        contentPane.setBackground(Color.DARK_GRAY);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setName("To Do List App");
        topPanel.setFont(new Font("Tahoma", Font.BOLD, 15));
        topPanel.setBackground(Color.DARK_GRAY);

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Tahoma", Font.BOLD, 15));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(Color.WHITE);

        closeButton.addActionListener(e -> {
            System.exit(0); // Programı kapat
        });

        topPanel.add(closeButton, BorderLayout.EAST);
        topPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        contentPane.add(topPanel, BorderLayout.NORTH);

        JLabel lblNewLabel = new JLabel("Yapılacaklar Listesi\r\n");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel.setForeground(Color.LIGHT_GRAY);
        topPanel.add(lblNewLabel, BorderLayout.CENTER);

        panelContainer = new JPanel();
        panelContainer.setForeground(Color.DARK_GRAY);
        panelContainer.setBackground(Color.DARK_GRAY);
        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelContainer);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setForeground(Color.DARK_GRAY);
        controlPanel.setBackground(Color.DARK_GRAY);
        contentPane.add(controlPanel, BorderLayout.SOUTH);

        btnAdd = new JButton("Görev Ekle");
        controlPanel.add(btnAdd);

        btnAdd.addActionListener(e -> {
            JPanel newPanel = createNewPanel();
            panelContainer.add(newPanel);
            panelContainer.add(Box.createVerticalGlue());
            panelContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            panelContainer.revalidate();
            panelContainer.repaint();

            String text = ((JTextPane) newPanel.getComponent(1)).getText();
            int panelIndex = panelContainer.getComponentCount();
           
        });

        // Kaydet butonu ve kodları eklendi
        btnSave = new JButton("Kaydet");
        controlPanel.add(btnSave);

        btnSave.addActionListener(e -> {
            for (Component component : panelContainer.getComponents()) {
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;
                    JTextPane textPane = (JTextPane) panel.getComponent(1);
                    String text = textPane.getText();
                    dataManager.setProperty("text" + (panelContainer.getComponentZOrder(panel) + 1), text);
                }
            }
        });



        // Kaydedilmiş metinleri yükleme
        for (int i = 0; i <= 1000; i++) {
            String text = dataManager.getProperty("text" + i);
            if (!text.isEmpty()) {
                JPanel newPanel = createNewPanel();
                panelContainer.add(newPanel);
                ((JTextPane) newPanel.getComponent(1)).setText(text);
                panelContainer.add(Box.createVerticalGlue());
                panelContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                panelContainer.revalidate();
                panelContainer.repaint();
            }
        }
    }

    private JPanel createNewPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.setPreferredSize(new Dimension(430, 80));
        panel.setLayout(null);

        JCheckBox checkBox = new JCheckBox("");
        checkBox.setBounds(6, 18, 21, 21);
        panel.add(checkBox);

        JTextPane textPane = new JTextPane();
        textPane.setBackground(Color.WHITE);
        textPane.setBounds(33, 8, 343, 44);
        panel.add(textPane);

        JButton btnDel = new JButton("X");
        btnDel.setForeground(Color.BLACK);
        btnDel.setBackground(Color.WHITE);
        btnDel.setBounds(386, 8, 44, 44);
        panel.add(btnDel);
        btnDel.setFont(new Font("Tahoma", Font.BOLD, 11));

        checkBox.addActionListener(e -> {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet attributes = new SimpleAttributeSet();

            if (checkBox.isSelected()) {
                StyleConstants.setStrikeThrough(attributes, true);
                StyleConstants.setForeground(attributes, Color.GRAY);
            } else {
                StyleConstants.setStrikeThrough(attributes, false);
                StyleConstants.setForeground(attributes, Color.BLACK);
            }

            doc.setCharacterAttributes(0, doc.getLength(), attributes, false);
        });

        btnDel.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Bu görevi silmek istediğinizden emin misiniz?",
                    "Görevi Sil",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                int panelIndex = panelContainer.getComponentZOrder(panel) + 1;
                dataManager.setProperty("text" + panelIndex, "");

                panelContainer.remove(panel);
                revalidate();
                repaint();

                // Re-index saved properties if needed
                int savedPropertiesCount = 0;
                for (int i = 1; i <= panelContainer.getComponentCount(); i++) {
                    Component comp = panelContainer.getComponent(i - 1);
                    if (comp instanceof JPanel) {
                        savedPropertiesCount++;
                        String text = dataManager.getProperty("text" + (i + 1));
                        dataManager.setProperty("text" + i, text);
                        dataManager.setProperty("text" + (i + 1), "");
                    }
                }

                // Remove the last unused saved property
                dataManager.setProperty("text" + (savedPropertiesCount + 1), "");
            }
        });


        return panel;

    }
}
