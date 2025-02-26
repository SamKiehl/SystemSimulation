import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Plot extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
    private final List<double[]> xDataList = new ArrayList<>();
    private final List<double[]> yDataList = new ArrayList<>();
    private final List<Color> colors = new ArrayList<>();
    private final List<String> labels = new ArrayList<>();
    private final int padding = 70;
    private final int pointRadius = 2;
    private final int tickLength = 5;
    private String title;
    private String xlabel;
    private String ylabel;
    private JFrame frame;
    private static int numPlots = 1;
    private double zoomFactor = 1.0; // Zoom factor
    private double xOffset = 0.0; // Panning offset for x-axis
    private double yOffset = 0.0; // Panning offset for y-axis
    private Point lastMousePoint; // Last mouse position for dragging
    private boolean showGrid = true; // Toggle grid lines

    public Plot() {
        this.xlabel = "";
        this.ylabel = "";
        this.addMouseWheelListener(this); // Register mouse wheel listener
        this.addMouseListener(this); // Register mouse listener
        this.addMouseMotionListener(this); // Register mouse motion listener
    }

    public void add(double[] xData, double[] yData, Color color, String label) {
        xDataList.add(xData);
        yDataList.add(yData);
        colors.add(color);
        labels.add(label);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Set up rendering hints for better quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw grid lines (if enabled)
        if (showGrid) {
            drawGrid(g2);
        }

        // Draw axes
        drawAxes(g2);

        // Plot the data points and lines
        plotData(g2);

        // Draw legend
        drawLegend(g2);
    }

    private void drawGrid(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();

        // Find min and max values for scaling
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;

        for (double[] xData : xDataList) {
            for (double x : xData) {
                if (x < xMin) xMin = x;
                if (x > xMax) xMax = x;
            }
        }
        for (double[] yData : yDataList) {
            for (double y : yData) {
                if (y < yMin) yMin = y;
                if (y > yMax) yMax = y;
            }
        }

        // Avoid division by zero in case of constant data
        if (xMin == xMax) {
            xMin -= 1;
            xMax += 1;
        }
        if (yMin == yMax) {
            yMin -= 1;
            yMax += 1;
        }

        // Apply zoom factor and panning offsets
        double xRange = (xMax - xMin) * zoomFactor;
        double yRange = (yMax - yMin) * zoomFactor;
        double xCenter = (xMin + xMax) / 2 + xOffset;
        double yCenter = (yMin + yMax) / 2 + yOffset;
        xMin = xCenter - xRange / 2;
        xMax = xCenter + xRange / 2;
        yMin = yCenter - yRange / 2;
        yMax = yCenter + yRange / 2;

        // Calculate the number of ticks
        int numTicks = 5;
        double xStep = (xMax - xMin) / numTicks;
        double yStep = (yMax - yMin) / numTicks;

        // Draw grid lines
        g2.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= numTicks; i++) {
            double xValue = xMin + i * xStep;
            int xPixel = (int) (padding + (xValue - xMin) * (width - 2 * padding) / (xMax - xMin));
            g2.drawLine(xPixel, padding, xPixel, height - padding);

            double yValue = yMin + i * yStep;
            int yPixel = (int) (height - padding - (yValue - yMin) * (height - 2 * padding) / (yMax - yMin));
            g2.drawLine(padding, yPixel, width - padding, yPixel);
        }
    }

    private void drawAxes(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();

        // Draw x-axis
        g2.setColor(Color.BLACK);
        g2.drawLine(padding, height - padding, width - padding, height - padding);

        // Draw y-axis
        g2.drawLine(padding, padding, padding, height - padding);

        // Draw labels
        g2.drawString(this.getXLabel(), width - padding + 10, height - padding);
        g2.drawString(this.getYLabel(), padding, padding - 10);

        // Add numbers and ticks to the axes
        addAxisNumbers(g2, width, height);
    }

    private void addAxisNumbers(Graphics2D g2, int width, int height) {
        // Find min and max values for scaling
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;
    
        for (double[] xData : xDataList) {
            for (double x : xData) {
                if (x < xMin) xMin = x;
                if (x > xMax) xMax = x;
            }
        }
        for (double[] yData : yDataList) {
            for (double y : yData) {
                if (y < yMin) yMin = y;
                if (y > yMax) yMax = y;
            }
        }
    
        // Avoid division by zero in case of constant data
        if (xMin == xMax) {
            xMin -= 1;
            xMax += 1;
        }
        if (yMin == yMax) {
            yMin -= 1;
            yMax += 1;
        }
    
        // Apply zoom factor and panning offsets
        double xRange = (xMax - xMin) * zoomFactor;
        double yRange = (yMax - yMin) * zoomFactor;
        double xCenter = (xMin + xMax) / 2 + xOffset;
        double yCenter = (yMin + yMax) / 2 + yOffset;
        xMin = xCenter - xRange / 2;
        xMax = xCenter + xRange / 2;
        yMin = yCenter - yRange / 2;
        yMax = yCenter + yRange / 2;
    
        // Calculate the number of ticks
        int numTicks = 5;
        double xStep = (xMax - xMin) / numTicks;
        double yStep = (yMax - yMin) / numTicks;
    
        // Draw x-axis ticks and numbers
        g2.setColor(Color.BLACK);
        for (int i = 0; i <= numTicks; i++) {
            double xValue = xMin + i * xStep;
            int xPixel = (int) (padding + (xValue - xMin) * (width - 2 * padding) / (xMax - xMin));
    
            // Draw tick
            g2.drawLine(xPixel, height - padding, xPixel, height - padding + tickLength);
    
            // Draw number
            String label = formatNumber(xValue, xRange);
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, xPixel - labelWidth / 2, height - padding + 20);
        }
    
        // Draw y-axis ticks and numbers
        for (int i = 0; i <= numTicks; i++) {
            double yValue = yMin + i * yStep;
            int yPixel = (int) (height - padding - (yValue - yMin) * (height - 2 * padding) / (yMax - yMin));
    
            // Draw tick
            g2.drawLine(padding - tickLength, yPixel, padding, yPixel);
    
            // Draw number
            String label = formatNumber(yValue, yRange);
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, padding - labelWidth - 10, yPixel + 5);
        }
    }

    private String formatNumber(double value, double range) {
        // Use scientific notation for very small or very large numbers
        if (Math.abs(value) >= 1e3 || Math.abs(value) <= 1e-3) {
            return String.format("%.2e", value);
        }
    
        // Otherwise, use dynamic precision based on the range
        int decimalPlaces = (int) Math.max(0, Math.min(6, -Math.log10(range) + 2));
        return String.format("%." + decimalPlaces + "f", value);
    }

    private void plotData(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();

        // Find min and max values for scaling
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;

        for (double[] xData : xDataList) {
            for (double x : xData) {
                if (x < xMin) xMin = x;
                if (x > xMax) xMax = x;
            }
        }
        for (double[] yData : yDataList) {
            for (double y : yData) {
                if (y < yMin) yMin = y;
                if (y > yMax) yMax = y;
            }
        }

        // Avoid division by zero in case of constant data
        if (xMin == xMax) {
            xMin -= 1;
            xMax += 1;
        }
        if (yMin == yMax) {
            yMin -= 1;
            yMax += 1;
        }

        // Apply zoom factor and panning offsets
        double xRange = (xMax - xMin) * zoomFactor;
        double yRange = (yMax - yMin) * zoomFactor;
        double xCenter = (xMin + xMax) / 2 + xOffset;
        double yCenter = (yMin + yMax) / 2 + yOffset;
        xMin = xCenter - xRange / 2;
        xMax = xCenter + xRange / 2;
        yMin = yCenter - yRange / 2;
        yMax = yCenter + yRange / 2;

        // Scale data to fit the plot area
        double xScale = (width - 2 * padding) / (xMax - xMin);
        double yScale = (height - 2 * padding) / (yMax - yMin);

        // Plot points and lines for each dataset
        for (int datasetIndex = 0; datasetIndex < xDataList.size(); datasetIndex++) {
            double[] xData = xDataList.get(datasetIndex);
            double[] yData = yDataList.get(datasetIndex);
            Color color = colors.get(datasetIndex);

            g2.setColor(color);
            Point2D prevPoint = null;
            for (int i = 0; i < xData.length; i++) {
                double x = xData[i];
                double y = yData[i];

                // Apply panning offsets to the data points
                double xPlot = x - xOffset;
                double yPlot = y - yOffset;

                // Scale and translate to plot coordinates
                int plotX = (int) (padding + (xPlot - xMin) * xScale);
                int plotY = (int) (height - padding - (yPlot - yMin) * yScale);

                // Draw point
                g2.fillOval(plotX - pointRadius, plotY - pointRadius, 2 * pointRadius, 2 * pointRadius);

                // Draw line to previous point
                if (prevPoint != null) {
                    g2.draw(new Line2D.Double(prevPoint.getX(), prevPoint.getY(), plotX, plotY));
                }
                prevPoint = new Point2D.Double(plotX, plotY);
            }
        }
    }

    private void drawLegend(Graphics2D g2) {
        int legendX = getWidth() - 150;
        int legendY = 20;
        int legendWidth = 130;
        int legendHeight = 20 * labels.size();

        // Draw legend background
        g2.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
        g2.fillRect(legendX, legendY, legendWidth, legendHeight);

        // Draw legend entries
        g2.setColor(Color.BLACK);
        for (int i = 0; i < labels.size(); i++) {
            int entryY = legendY + 20 * i + 15;
            g2.setColor(colors.get(i));
            g2.fillRect(legendX + 5, entryY - 10, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawString(labels.get(i), legendX + 20, entryY);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Adjust zoom factor based on mouse wheel rotation
        int rotation = e.getWheelRotation();
        if (rotation < 0) {
            zoomFactor *= 0.9; // Zoom in
        } else {
            zoomFactor *= 1.1; // Zoom out
        }

        // Repaint the plot
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Save the current mouse position when dragging starts
        lastMousePoint = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastMousePoint != null) {
            // Calculate the mouse movement in plot coordinates
            int dx = e.getX() - lastMousePoint.x;
            int dy = e.getY() - lastMousePoint.y;

            // Calculate the current displayed range of x and y
            double xMin = Double.MAX_VALUE;
            double xMax = Double.MIN_VALUE;
            double yMin = Double.MAX_VALUE;
            double yMax = Double.MIN_VALUE;

            for (double[] xData : xDataList) {
                for (double x : xData) {
                    if (x < xMin) xMin = x;
                    if (x > xMax) xMax = x;
                }
            }
            for (double[] yData : yDataList) {
                for (double y : yData) {
                    if (y < yMin) yMin = y;
                    if (y > yMax) yMax = y;
                }
            }

            double xRange = (xMax - xMin) * zoomFactor;
            double yRange = (yMax - yMin) * zoomFactor;

            // Adjust the panning offsets based on mouse movement and current displayed range
            xOffset -= 0.5 * dx / (getWidth() - 2 * padding) * xRange;
            yOffset += 0.5 * dy / (getHeight() - 2 * padding) * yRange;

            // Save the new mouse position
            lastMousePoint = e.getPoint();

            // Repaint the plot
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Reset the last mouse position when dragging ends
        lastMousePoint = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    public void title(String title) {
        this.title = title;
        this.frame.setTitle(title);
    }

    public void xLabel(String xlabel) { this.xlabel = xlabel; }

    public String getXLabel() { return this.xlabel; }

    public void yLabel(String ylabel) { this.ylabel = ylabel; }

    public String getYLabel() { return this.ylabel; }

    public void setShowGrid(boolean showGrid) { this.showGrid = showGrid; }

    public void plot() {
        this.title = "Figure " + numPlots;
        this.frame = new JFrame(title);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 600);

        this.frame.add(this);

        this.frame.setVisible(true);
    }
}