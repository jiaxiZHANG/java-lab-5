import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serial;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class MainFrame extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private JFileChooser fileChooser = null;

    private JMenuItem resetGraphicsMenuItem;

    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem showFillingMenuItem;
    private JCheckBoxMenuItem rotatedMenuItem;
    private JMenuItem saveToGraphicsItem;

    private final GraphicsDisplay display = new GraphicsDisplay();

    private boolean fileLoaded = false;

    public MainFrame() {
        super("lab4");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        setExtendedState(MAXIMIZED_BOTH);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком打开图像文件") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION) {
                    openGraphics(fileChooser.getSelectedFile());
                }
            }
        };

        Action saveGraphicsAction = new AbstractAction("Сохранить график в файл保存图形") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION) {
                    saveToGraphicsFile(fileChooser.getSelectedFile());
                }
            }
        };

        Action resetGraphicsAction = new AbstractAction("Отменить все изменения撤销所有更改") {
            public void actionPerformed(ActionEvent event) {
                MainFrame.this.display.reset();
            }
        };
        this.resetGraphicsMenuItem = fileMenu.add(resetGraphicsAction);
        this.resetGraphicsMenuItem.setEnabled(true);

        saveToGraphicsItem = new JMenuItem(saveGraphicsAction);
        fileMenu.add(saveToGraphicsItem);
        saveToGraphicsItem.setEnabled(false);

        fileMenu.add(openGraphicsAction);
        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);


        Action showAxisAction = new AbstractAction("Показывать оси координат显示坐标轴") {
            public void actionPerformed(ActionEvent event) {

                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new
                JCheckBoxMenuItem(showAxisAction);

        graphicsMenu.add(showAxisMenuItem);

        showAxisMenuItem.setSelected(true);
        Action showMarkersAction = new AbstractAction("Показывать маркеры точек显示点标记") {

            public void actionPerformed(ActionEvent event) {

                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);
        graphicsMenu.addMenuListener(new MenusListener());
        fileMenu.addMenuListener(new MenusListener());

        getContentPane().add(display, BorderLayout.CENTER);
    }

    protected void openGraphics(File selectedFile) {
        try {

            DataInputStream in = new DataInputStream(new
                    FileInputStream(selectedFile));

            double[][] graphicsData = new
                    double[in.available() / (Double.SIZE / 8) / 2][];

            int i = 0;
            while (in.available() > 0) {

                double x = in.readDouble();

                double y = in.readDouble();

                graphicsData[i++] = new double[]{x, y};
            }

            if (graphicsData.length > 0) {

                fileLoaded = true;

                display.displayGraphics(graphicsData);
            }

            in.close();
        } catch (FileNotFoundException ex) {

            JOptionPane.showMessageDialog(
                    MainFrame.this,
                    "Указанный файл не найден未找到指定文件",
                    " Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {

            JOptionPane.showMessageDialog(MainFrame.this,
                    "Ошибка чтения координат точек из файла",
                    " Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    protected void saveToGraphicsFile(File selectedFile) {
        try {
            DataOutputStream out = new DataOutputStream(new
                    FileOutputStream(selectedFile));
            for (int i = 0; i < display.getGraphicsData().length; i++) {
                out.writeDouble(display.getGraphicsData()[i][0]);
                out.writeDouble(display.getGraphicsData()[i][1]);
            }
            out.close();
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {

        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class MenusListener implements MenuListener {

        public void menuSelected(MenuEvent e) {

            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
            showFillingMenuItem.setEnabled(fileLoaded);
            rotatedMenuItem.setEnabled(fileLoaded);
            saveToGraphicsItem.setEnabled(fileLoaded);
        }

        public void menuDeselected(MenuEvent e) {
        }


        public void menuCanceled(MenuEvent e) {
        }
    }
}
