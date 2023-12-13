import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.awt.Point;
import javax.swing.JLabel;


public class GraphicsDisplay extends JPanel {

    private Double currentX;
    private Double currentY;
    private JLabel coordinatesLabel;

    private double[][] graphicsData;
    private double[][] originalData;

    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showFilling = false;
    private boolean isRotated = false;

    private double minX = 0;
    private double maxX = 0;
    private double minY = 0;
    private double maxY = 0;
    private double[][] viewport = new double[2][2];
    private double scaleX;
    private double scaleY;
    // Различные стили черчения линий
    private final BasicStroke graphicsStroke;
    private final BasicStroke axisStroke;
    private BasicStroke gridStroke;
    private final BasicStroke markerStroke;
    private final BasicStroke selectionStroke;
    private boolean scaleMode = false;
    // Различные шрифты отображения надписей
    private final Font axisFont;
    private Font labelsFont;

    private int selectedMarker = -1;
    private boolean changeMode = false;
    private static DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance();
    private final Rectangle2D.Double selectionRect = new Rectangle2D.Double();

    private final ArrayList<double[][]> undoHistory = new ArrayList<>();
    private double[] originalPoint = new double[2];

    public GraphicsDisplay() {



        coordinatesLabel = new JLabel();
        coordinatesLabel.setForeground(Color.BLACK); // Set label color
        coordinatesLabel.setFont(new Font("Serif", Font.PLAIN, 12)); // Set label font
        coordinatesLabel.setBounds(10, 10, 150, 20); // Set label bounds
        add(coordinatesLabel);
        JLabel coordinatesLabel = new JLabel();
        this.add(coordinatesLabel); // 将标签添加到 JPanel 中
        this.setBackground(Color.WHITE);
        this.axisStroke = new BasicStroke(2.0F, 0, 0, 10.0F, (float[])null, 0.0F);
        this.gridStroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[]{4.0F, 4.0F}, 0.0F);
        this.markerStroke = new BasicStroke(1.0F, 0, 0, 10.0F, (float[])null, 0.0F);
        this.selectionStroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[]{10.0F, 10.0F}, 0.0F);
        this.axisFont = new Font("Serif", 1, 36);
        this.labelsFont = new Font("Serif", 0, 10);
        formatter.setMaximumFractionDigits(5);
//        this.addMouseListener(new MouseHandler());
//        this.addMouseMotionListener(new MouseMotionHandler());
//        this.addMouseMotionListener(new MouseMotionAdapter() {  // 添加鼠标监听器以响应鼠标移动事件
//
//
//            @Override
//
//
//
//            // 在 MouseMotionHandler 的 mouseMoved 方法中更新坐标值文本标签
//            public void mouseMoved(MouseEvent ev) {
//                double[] graphCoords = translatePointToXY(ev.getX(), ev.getY());
//                double graphX = graphCoords[0];
//                double graphY = graphCoords[1];
//
//                coordinatesLabel.setText("X = " + graphX + ", Y = " + graphY);
//                coordinatesLabel.setBounds(ev.getX(), ev.getY(), 150, 20); // 设置标签位置
//                coordinatesLabel.setVisible(true);
//            }
//        });

        setBackground(Color.WHITE);
        float[] dashPattern = {
                10.0f, 4.0f,
                10.0f, 4.0f,
                10.0f, 4.0f,
                30.0f, 4.0f,
                18.0f, 4.0f,
                18.0f, 4.0f
        };

        gridStroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[]{4.0F, 4.0F}, 0.0F);

        graphicsStroke = new BasicStroke(
                3.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                10.0f,
                dashPattern,
                0.0f
        );







        this.labelsFont = new Font("Serif", 0, 10);
        addMouseMotionListener(new MouseMotionHandler());
        addMouseListener(new MouseHandler());
    }
//    public void addMouseListenerForScaling() {
//        this.addMouseListener(new MouseAdapter() {
//            private Point start;
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//                start = e.getPoint();
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                Point end = e.getPoint();
//                double[] startCoords = translatePointToXY(start.x, start.y);
//                double[] endCoords = translatePointToXY(end.x, end.y);
//                double x1 = Math.min(startCoords[0], endCoords[0]);
//                double y1 = Math.max(startCoords[1], endCoords[1]);
//                double x2 = Math.max(startCoords[0], endCoords[0]);
//                double y2 = Math.min(startCoords[1], endCoords[1]);
//
//                // 缩放到选定区域并更新视口
//                zoomTo(x1, y1, x2, y2);
//            }
//        });
//    }
//
//    public void addMouseListenerForResettingScale() {
//        this.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (e.getButton() == MouseEvent.BUTTON3) {
//                    // 恢复图表原始比例的逻辑
//                    reset(); // 调用 reset 方法来重置图表到初始状态
//                }
//            }
//        });
//    }
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }
    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }
    public void setShowFilling(boolean showFilling) {
        this.showFilling = showFilling;
        repaint();
    }
    public void setRotated(boolean selected) {
        isRotated = selected;
        repaint();
    }
    public double[][] getGraphicsData() {
        return Arrays.copyOf(graphicsData, graphicsData.length);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graphicsData == null || graphicsData.length == 0) return;
        if (!isRotated) {
            scaleX = getSize().getWidth() / (viewport[1][0] - viewport[0][0]);
            scaleY = getSize().getHeight() / (viewport[0][1] - viewport[1][1]);
        }
        if (currentX != null && currentY != null) {
            coordinatesLabel.setText("X = " + formatter.format(currentX) + ", Y = " + formatter.format(currentY));
        } else {
            coordinatesLabel.setText(""); // Clear label if no coordinates found
        }
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
        if (isRotated) {
            canvas.rotate(-Math.PI / 2);
            this.scaleX = this.getSize().getHeight() / (this.viewport[1][0] - this.viewport[0][0]);
            this.scaleY = this.getSize().getWidth() / (this.viewport[0][1] - this.viewport[1][1]);

            zoomToNoUpdate(viewport[0][0] + (this.viewport[1][0] - this.viewport[0][0]),viewport[0][1],viewport[1][0] - (this.viewport[1][0] - this.viewport[0][0]),viewport[1][1]);
        }

        this.paintGrid(canvas);
        this.paintGraphics(canvas);
        this.paintLabels(canvas);
        this.paintSelection(canvas);

        if (showFilling) {fillGraphics(canvas);}
        if (showAxis) {paintAxis(canvas);}
        paintGraphics(canvas);
        if (showMarkers) {paintMarkers(canvas);}

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
        paintSelection(canvas);
    }
    private void updateCoordinates(MouseEvent e) {
        double[] graphCoords = translatePointToXY(e.getX(), e.getY());
        currentX = graphCoords[0];
        currentY = graphCoords[1];
        System.out.println("Current X: " + currentX + ", Current Y: " + currentY); // Add print statements for debugging

        repaint();
    }
    private void paintSelection(Graphics2D canvas) {
        if (scaleMode) {
            canvas.setStroke(selectionStroke);
            canvas.setColor(Color.BLACK);
            canvas.draw(this.selectionRect);
        }
    }


    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        Double currentX = null;
        Double currentY = null;

        for (double[] point : graphicsData) {
            if (!(point[0] < this.viewport[0][0]) && !(point[1] > this.viewport[0][1]) &&
                    !(point[0] > this.viewport[1][0]) && !(point[1] < this.viewport[1][1])) {

                int integerPart = (int) point[1]; // 计算函数在这个点的整数部分
                if (integerPart % 2 != 0) {
                    canvas.setColor(Color.RED);
                    Point2D.Double markerCenter = xyToPoint(point[0], point[1]);

                    int markerSize = 15; // 大小
                    drawDiamondMarker(canvas, markerCenter, markerSize); // 绘制红色菱形标记
                } else {
                    canvas.setColor(Color.BLACK); // 如果整数部分为偶数，使用默认颜色
                }

                if (currentX != null && currentY != null) {
                    Point2D.Double currentPoint = xyToPoint(currentX, currentY);
                    Point2D.Double newPoint = xyToPoint(point[0], point[1]);

                    canvas.setColor(integerPart % 2 != 0 ? Color.BLACK : Color.BLACK); // 根据整数部分选择颜色
                    canvas.draw(new Line2D.Double(currentPoint, newPoint));
                }

                currentX = point[0];
                currentY = point[1];
            }
        }
    }

    private void drawDiamondMarker(Graphics2D canvas, Point2D.Double center, int size) {
        int[] xPoints = {(int) center.getX(), (int) (center.getX() + size / 2),
                (int) center.getX(), (int) (center.getX() - size / 2)};
        int[] yPoints = {(int) (center.getY() - size / 2), (int) center.getY(),
                (int) (center.getY() + size / 2), (int) center.getY()};
        canvas.fillPolygon(xPoints, yPoints, 4);
    }





    public void displayGraphics(double[][] graphicsData) {
        this.graphicsData = graphicsData;
        originalData = new double[graphicsData.length][];

        for (int i = 0; i < graphicsData.length; i++) {
            originalData[i] = new double[graphicsData[i].length];
            for (int j = 0; j < graphicsData[i].length; j++) {
                originalData[i][j] = graphicsData[i][j];
            }
        }

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;


        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }

        zoomTo(this.minX, this.maxY, this.maxX, this.minY);
    }
    protected void fillGraphics(Graphics2D canvas) {
        boolean regionStarted = false;
        boolean last = graphicsData[0][1] > 0;
        double areaSum = 0d;
        int start = 0;

        for (int i = 0; i < graphicsData.length; i++) {

            if (!regionStarted && last != graphicsData[i][1] > 0) {
                regionStarted = true;
                last = graphicsData[i][1] > 0;
                start = i;
                areaSum = 0.0;
            }

            if (last != graphicsData[i][1] > 0) {

                regionStarted = false;
                last = graphicsData[i][1] > 0;
                areaSum += calculateArea(Arrays.copyOfRange(
                        graphicsData,
                        start,
                        i
                ));

                Point2D.Double startPoint = xyToPoint(
                        graphicsData[start][0],
                        graphicsData[start][1]
                );

                Point2D.Double currentPoint = xyToPoint(
                        graphicsData[i][0],
                        graphicsData[i][1]
                );

                canvas.setColor(new Color(0, 128, 255));
                GeneralPath regionPath = new GeneralPath();
                regionPath.moveTo(startPoint.getX(), startPoint.getY());
                for (int j = start; j < i; j++) {
                    currentPoint = xyToPoint(
                            graphicsData[j][0],
                            graphicsData[j][1]
                    );
                    regionPath.lineTo(currentPoint.getX(), currentPoint.getY());
                }
                //if it is the end of graph
                //we should fill anyway
                if (i == graphicsData.length - 1) {
                    currentPoint = xyToPoint(
                            graphicsData[i][0],
                            graphicsData[i][1]
                    );
                    regionPath.lineTo(currentPoint.getX(), currentPoint.getY());
                }
                Point2D.Double yZero = xyToPoint(graphicsData[i][0], 0);
                Point2D.Double yZeroStart = xyToPoint(
                        graphicsData[start][0],
                        0
                );
                regionPath.lineTo(yZero.getX(), yZero.getY());
                regionPath.lineTo(yZeroStart.getX(), yZeroStart.getY());
                regionPath.lineTo(startPoint.getX(), startPoint.getY());
                regionPath.closePath();
                canvas.fill(regionPath);

                // Display the area value in the center of the region
                DecimalFormat df = new DecimalFormat("#.##");
                String areaValue = df.format(areaSum);
                int centerX = (int) ((startPoint.getX() + currentPoint.getX()) / 2);
                canvas.setColor(Color.BLACK);
                canvas.drawString(areaValue, centerX, (int) startPoint.getY());
            }
        }
    }
    private void paintLabels(Graphics2D canvas) {
        canvas.setColor(Color.BLACK);
        canvas.setFont(this.labelsFont);
        FontRenderContext context = canvas.getFontRenderContext();
        double labelYPos;
        if (this.viewport[1][1] < 0.0D && this.viewport[0][1] > 0.0D) {
            labelYPos = 0.0D;
        } else {
            labelYPos = this.viewport[1][1];
        }

        double labelXPos;
        if (this.viewport[0][0] < 0.0D && this.viewport[1][0] > 0.0D) {
            labelXPos = 0.0D;
        } else {
            labelXPos = this.viewport[0][0];
        }

        double pos = this.viewport[0][0];

        double step;
        Point2D.Double point;
        String label;
        Rectangle2D bounds;
        for(step = (this.viewport[1][0] - this.viewport[0][0]) / 10.0D; pos < this.viewport[1][0]; pos += step) {
            point = this.translateXYtoPoint(pos, labelYPos);
            label = formatter.format(pos);
            bounds = this.labelsFont.getStringBounds(label, context);
            canvas.drawString(label, (float)(point.getX() + 5.0D), (float)(point.getY() - bounds.getHeight()));
        }

        pos = this.viewport[1][1];

        for(step = (this.viewport[0][1] - this.viewport[1][1]) / 10.0D; pos < this.viewport[0][1]; pos += step) {
            point = this.translateXYtoPoint(labelXPos, pos);
            label = formatter.format(pos);
            bounds = this.labelsFont.getStringBounds(label, context);
            canvas.drawString(label, (float)(point.getX() + 5.0D), (float)(point.getY() - bounds.getHeight()));
        }
        if (this.selectedMarker >= 0) {
            point = this.xyToPoint(this.graphicsData[selectedMarker][0], this.graphicsData[selectedMarker][1]);
            label = "X=" + formatter.format(this.graphicsData[selectedMarker][0]) + ", Y=" + formatter.format(this.graphicsData[selectedMarker][1]);
            bounds = this.labelsFont.getStringBounds(label, context);
            canvas.setColor(Color.BLACK);
            canvas.drawString(label, (float)(point.getX() + 5.0D), (float)(point.getY() - bounds.getHeight()));
        }



    }

    private void paintGrid(Graphics2D canvas) {

    }


    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        for (double[] point : graphicsData) {
            if (isAscending(point)) {
                canvas.setColor(Color.BLACK);
            } else {
                int integerPart = (int) point[1]; // 计算函数在这个点的整数部分
                if (integerPart % 2 != 0) {
                    canvas.setColor(Color.RED); // 当整数部分为奇数时使用红色
                } else {
                    canvas.setColor(Color.BLACK); // 整数部分为偶数时使用默认颜色
                }
            }

            Point2D.Double markerCenter = xyToPoint(point[0], point[1]);
            int markerSize = 12;

            Point2D.Double topPoint = new Point2D.Double(markerCenter.getX(), markerCenter.getY() - markerSize / 2);
            Point2D.Double bottomPoint = new Point2D.Double(markerCenter.getX(), markerCenter.getY() + markerSize / 2);
            Point2D.Double leftPoint = new Point2D.Double(markerCenter.getX() - markerSize / 2, markerCenter.getY());
            Point2D.Double rightPoint = new Point2D.Double(markerCenter.getX() + markerSize / 2, markerCenter.getY());

            GeneralPath diamond = new GeneralPath();
            diamond.moveTo(topPoint.getX(), topPoint.getY());
            diamond.lineTo(leftPoint.getX(), leftPoint.getY());
            diamond.lineTo(bottomPoint.getX(), bottomPoint.getY());
            diamond.lineTo(rightPoint.getX(), rightPoint.getY());
            diamond.closePath();

            canvas.draw(diamond);
        }
    }

    // 在指定位置画一个菱形
    private void drawDiamond(Graphics2D canvas, Point2D.Double center, int markerSize) {
        Point2D.Double topPoint = new Point2D.Double(center.getX(), center.getY() - markerSize / 2);
        Point2D.Double bottomPoint = new Point2D.Double(center.getX(), center.getY() + markerSize / 2);
        Point2D.Double leftPoint = new Point2D.Double(center.getX() - markerSize / 2, center.getY());
        Point2D.Double rightPoint = new Point2D.Double(center.getX() + markerSize / 2, center.getY());

        GeneralPath diamond = new GeneralPath();
        diamond.moveTo(topPoint.getX(), topPoint.getY());
        diamond.lineTo(leftPoint.getX(), leftPoint.getY());
        diamond.lineTo(bottomPoint.getX(), bottomPoint.getY());
        diamond.lineTo(rightPoint.getX(), rightPoint.getY());
        diamond.closePath();

        canvas.draw(diamond);
    }




    private boolean isAscending(double[] point) {
        point[0] = (int) (point[0] * 100) / 100d;
        point[1] = (int) (point[1] * 100) / 100d;
        String fst = String.valueOf(point[0]);
        String scnd = String.valueOf(point[1]);
        return isAscending(fst) && isAscending(scnd);
    }
    private boolean isAscending(String num) {
        return (
                num.substring(0, num.indexOf(".")) +
                        num.substring(num.indexOf(".") + 1)
        )
                .equals(
                        Arrays.stream(num.split(""))
                                .filter(s -> !s.equals("."))
                                .sorted()
                                .collect(Collectors.joining())
                );
    }
    private Point2D.Double constructPoint(
            Point2D.Double point,
            double dx,
            double dy
    ) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(
                point.getX() + dx,
                point.getY() + dy
        );
        return dest;
    }


    protected void paintAxis(Graphics2D canvas) {     //在X轴和Y轴上添加XYO标签
        canvas.setStroke(this.axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setFont(this.axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        Rectangle2D bounds;
        Point2D.Double labelPos;
        String zeroLabel = "0";
        bounds = this.axisFont.getStringBounds(zeroLabel, context);
        labelPos = this.xyToPoint(0, 0);
        canvas.drawString(zeroLabel, (float)(labelPos.x - bounds.getWidth() / 2), (float)(labelPos.y + bounds.getHeight()));


        if (this.viewport[0][0] <= 0.0 && this.viewport[1][0] >= 0.0) {
            canvas.draw(new Line2D.Double(this.xyToPoint(0.0, this.viewport[0][1]), this.xyToPoint(0.0, this.viewport[1][1])));
            canvas.draw(new Line2D.Double(this.xyToPoint(-(this.viewport[1][0] - this.viewport[0][0]) * 0.0025, this.viewport[0][1] - (this.viewport[0][1] - this.viewport[1][1]) * 0.015), this.xyToPoint(0.0, this.viewport[0][1])));
            canvas.draw(new Line2D.Double(this.xyToPoint((this.viewport[1][0] - this.viewport[0][0]) * 0.0025, this.viewport[0][1] - (this.viewport[0][1] - this.viewport[1][1]) * 0.015), this.xyToPoint(0.0, this.viewport[0][1])));
            bounds = this.axisFont.getStringBounds("y", context);
            labelPos = this.xyToPoint(0.0, this.viewport[0][1]);
            canvas.drawString("y", (float)labelPos.x + 10.0F, (float)(labelPos.y + bounds.getHeight() / 2.0));
        }

        if (this.viewport[1][1] <= 0.0 && this.viewport[0][1] >= 0.0) {
            canvas.draw(new Line2D.Double(this.xyToPoint(this.viewport[0][0], 0.0), this.xyToPoint(this.viewport[1][0], 0.0)));
            canvas.draw(new Line2D.Double(this.xyToPoint(this.viewport[1][0] - (this.viewport[1][0] - this.viewport[0][0]) * 0.01, (this.viewport[0][1] - this.viewport[1][1]) * 0.005), this.xyToPoint(this.viewport[1][0], 0.0)));
            canvas.draw(new Line2D.Double(this.xyToPoint(this.viewport[1][0] - (this.viewport[1][0] - this.viewport[0][0]) * 0.01, -(this.viewport[0][1] - this.viewport[1][1]) * 0.005), this.xyToPoint(this.viewport[1][0], 0.0)));
            bounds = this.axisFont.getStringBounds("x", context);
            labelPos = this.xyToPoint(this.viewport[1][0], 0.0);
            canvas.drawString("x", (float)(labelPos.x - bounds.getWidth() - 10.0), (float)(labelPos.y - bounds.getHeight() / 2.0));
        }
    }
    protected java.awt.geom.Point2D.Double translateXYtoPoint(double x, double y) {
        double deltaX = x - this.viewport[0][0];
        double deltaY = this.viewport[0][1] - y;
        return new java.awt.geom.Point2D.Double(deltaX * this.scaleX, deltaY * this.scaleY);
    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - this.viewport[0][0];
        double deltaY = this.viewport[0][1] - y;
        return new Point2D.Double(deltaX * this.scaleX, deltaY * this.scaleY);
    }
    protected double[] translatePointToXY(int x, int y) {
        return new double[]{this.viewport[0][0] + (double)x / this.scaleX, this.viewport[0][1] - (double)y / this.scaleY};
    }
    private static double calculateArea(double[][] points) {
        int n = points.length;
        if (n < 2) {
            throw new IllegalArgumentException("At least two points are required.");
        }

        double area = 0.0;
        for (int i = 1; i < n; i++) {
            double x0 = points[i - 1][0];
            double y0 = points[i - 1][1];
            double x1 = points[i][0];
            double y1 = points[i][1];

            area += (y0 + y1) * (x1 - x0) / 2;
        }

        return Math.abs(area);
    }
    private void zoomTo(double x1, double y1, double x2, double y2) {
        zoomToNoUpdate(x1, y1, x2, y2);
        repaint();
    }
    private void zoomToNoUpdate(double x1, double y1, double x2, double y2) {
        viewport[0][0] = x1;
        viewport[0][1] = y1;
        viewport[1][0] = x2;
        viewport[1][1] = y2;
    }
    private int findSelectedPoint(double x, double y) {
        if (graphicsData == null) {
            return -1;
        }
        for (int i = 0; i < graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(
                    graphicsData[i][0],
                    graphicsData[i][1]
            );
            double distance = (point.getX() - x) *
                    (point.getX() - x) +
                    (point.getY() - y) *
                            (point.getY() - y);
            if (distance <= 100) {
                return i;
            }
        }
        return -1;
    }

    public void reset() {
        this.displayGraphics(this.originalData);
    }


    public class MouseHandler extends MouseAdapter {
        public MouseHandler() {
        }

        public void mouseClicked(MouseEvent ev) {
            if (ev.getButton() == 3) {
                if (!undoHistory.isEmpty()) {
                    viewport = undoHistory.get(undoHistory.size() - 1);
                    undoHistory.remove(undoHistory.size() - 1);
                } else {
                    zoomTo(minX, maxY, maxX, minY);
                }

                repaint();
            }

        }



//        public void mousePressed(MouseEvent ev) {
//            if (ev.getButton() == 1) {
//                selectedMarker = findSelectedPoint(ev.getX(), ev.getY());
//                originalPoint = translatePointToXY(ev.getX(), ev.getY());
//                if (selectedMarker >= 0) {
//                    GraphicsDisplay.this.changeMode = true;
//                    setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
//                } else {
//                    scaleMode = true;
//                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
//                    selectionRect.setFrame(ev.getX(), ev.getY(), 1.0, 1.0);
//                }
//
//            }
//        }
public void mousePressed(MouseEvent ev) {
    if (ev.getButton() == 1) {
        GraphicsDisplay.this.selectedMarker = GraphicsDisplay.this.findSelectedPoint(ev.getX(), ev.getY());
        GraphicsDisplay.this.originalPoint = GraphicsDisplay.this.translatePointToXY(ev.getX(), ev.getY());
        if (GraphicsDisplay.this.selectedMarker >= 0) {
            GraphicsDisplay.this.changeMode = true;
            GraphicsDisplay.this.setCursor(Cursor.getPredefinedCursor(8));
        } else {
            GraphicsDisplay.this.scaleMode = true;
            GraphicsDisplay.this.setCursor(Cursor.getPredefinedCursor(5));
            GraphicsDisplay.this.selectionRect.setFrame((double)ev.getX(), (double)ev.getY(), 1.0, 1.0);
        }

    }
}

        public void mouseReleased(MouseEvent ev) {
            if (ev.getButton() == 1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                if (GraphicsDisplay.this.changeMode) {
                    GraphicsDisplay.this.changeMode = false;
                } else {
                    scaleMode = false;
                    double[] finalPoint = translatePointToXY(ev.getX(), ev.getY());
                    undoHistory.add(viewport);
                    viewport = new double[2][2];
                    zoomTo(originalPoint[0], originalPoint[1], finalPoint[0], finalPoint[1]);
                    repaint();
                }

            }
        }
    }

    public class MouseMotionHandler implements MouseMotionListener {
        public MouseMotionHandler() {

        }

        public void mouseMoved(MouseEvent ev) {

            selectedMarker = findSelectedPoint(ev.getX(), ev.getY());
            if (selectedMarker >= 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            repaint();
        }



        public void mouseDragged(MouseEvent ev) {


            if (GraphicsDisplay.this.changeMode) {
                double[] currentPoint = translatePointToXY(ev.getX(), ev.getY());
                double newY = currentPoint[1];

                if (newY > viewport[0][1]) {
                    newY = viewport[0][1];
                }

                if (newY < viewport[1][1]) {
                    newY = viewport[1][1];
                }

                graphicsData[selectedMarker][1] = newY;
                repaint();
            } else {
                double width = (double) ev.getX() - selectionRect.getX();
                if (width < 5.0) {
                    width = 5.0;
                }

                double height = (double) ev.getY() - selectionRect.getY();
                if (height < 5.0) {
                    height = 5.0;
                }

                selectionRect.setFrame(selectionRect.getX(), selectionRect.getY(), width, height);
                repaint();
            }

        }
    }

}
