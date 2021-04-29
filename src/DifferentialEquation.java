import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Altynbek uulu Yiman, AMI-119
 *
 * Numerical methods.
 *
 * Differential equation. Method of Runge-Kutta.
 *
 * Library: XChart for charting graphs
 *
 * 2021/04/01
 *
 * */

public class DifferentialEquation extends JFrame{
    // Double
    private static double[] arr, arr_sol;
    private static double u0, T = 1,h, a = 0.6, phi = 0.05, lambda = 0.3, eps, error;
    // Integer
    private static int N = 8, problem = 0, meth = 0; // M:1,2;
    // X and Y coordinate lists
    private static ArrayList<Double> xData1;
    private static ArrayList<Double> yData1;
    private static ArrayList<Double> xData2;
    private static ArrayList<Double> yData2;
    // User Interface
    private static XYChart chart;
    private static XYSeries testFunctionSeries, interpolateFunctionSeries;
    private final JComboBox<Integer> nodesChoice;
    private final JComboBox<String> problemsChoice;
    private final JComboBox<String> methodChoice;
    private final JComboBox<Double> epsilonChoice;
    private final JComboBox<Double> lambdaChoice;

    private JButton display = new JButton("Display");
    // Series names
    private static final String seriesName1 = "Analytical solution";
    private static final String seriesName2 = "Numerical solution";
   // private static final String seriesName3 = "Whatever";


    // Building the user interface
    DifferentialEquation() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);

        // Parameter, methods selection
        Integer[] choicesNodes = { 5, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4056, 8112, 2*8112, 4*8112 };
        String[] choicesProblem = { "Problem 1", "Problem 5", "Problem 13.1", "Problem 13.4" };
        String[] choicesMethod = { "Method 1", "Method 2" };
        Double[] choicesEpsilon = { 0.5, 0.3, 0.1, 0.08, 0.0625, 0.015, 0.009 };
        Double[] choicesLambda = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        // Node selection
        nodesChoice = new JComboBox<>(choicesNodes);
        nodesChoice.setPreferredSize(new Dimension(1,25));
        // Problems selection
        problemsChoice = new JComboBox<>(choicesProblem);
        nodesChoice.setPreferredSize(new Dimension(1,25));

        // Method selection
        methodChoice = new JComboBox<>(choicesMethod);
        nodesChoice.setPreferredSize(new Dimension(1,25));

        // Epsilon selection
        epsilonChoice = new JComboBox<>(choicesEpsilon);
        nodesChoice.setPreferredSize(new Dimension(1,25));

        // Lambda selection
        lambdaChoice = new JComboBox<>(choicesLambda);
        lambdaChoice.setPreferredSize(new Dimension(1,25));

        // Control panel
        JPanel controlPanel = new JPanel();
        //controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setLayout(new GridLayout(6, 1));
        controlPanel.setBackground(Color.LIGHT_GRAY);
        controlPanel.add(nodesChoice);
        controlPanel.add(problemsChoice);
        controlPanel.add(methodChoice);
        controlPanel.add(epsilonChoice);
        controlPanel.add(lambdaChoice);
        controlPanel.add(display);
        add(controlPanel, BorderLayout.EAST);

        // Listen to display button pressed, update the graph
        display.addActionListener(actionEvent -> {
            N = Integer.parseInt(Objects.requireNonNull(nodesChoice.getSelectedItem()).toString()); // Get number of nodes
            problem = problemsChoice.getSelectedIndex(); // Get problem ubdex
            meth = methodChoice.getSelectedIndex(); // Get method index
            eps = Double.parseDouble(Objects.requireNonNull(epsilonChoice.getSelectedItem()).toString()); // Get epsilon index
            lambda = Double.parseDouble(Objects.requireNonNull(lambdaChoice.getSelectedItem()).toString()); // Get lambda index

            Draw(); // Draw graphs
            repaint(); // Show the change
        });
    }
    /**
     * Test problems
     *
     * P: 1, 5, 13.1, 13.4
     *
     * */
    private static double Func(double x, double y) {
        switch(problem) {
            case 0:
                return -2*y/eps*(x+1);
            case 1:
                return 2*Math.sqrt(1-Math.pow(y,2))/(eps*Math.pow(2-x,2));
            case 2:
                return (y+1)*(y-2)/eps;
            case 3:
                return (Math.pow(a,2)-Math.pow(y,2))/eps*a;
            default:
                return 0;
        }
    }
    /**
     * Solution of problems.
     *
     * P: 1, 5, 13.1, 13.4
     *
     * */
    private static double SolutionFunc(double x) {
        switch(problem) {
            case 0:
                return 2/Math.pow(1+x, 2/eps);
            case 1:
                return Math.sin(x/eps*(2-x));
            case 2:
                return (-1+4*Math.exp(-3*x/eps))/(1+2*Math.exp(-3*x/eps));
            case 3:
                return a*((phi+a*Math.tanh(x/eps))/(a+phi*Math.tanh(x/eps)));
            default:
                return 0;
        }
    }

    /**
     * Compute error
     * */
    public static double err(double[] m1, double[] m2) {
        double max1 = 0;
        double max2 = 0;

        for (int i = 0; i <= N; i++) {
            if (Math.abs(m1[i] - m2[i]) > max1) {
                max1 = Math.abs(m1[i] - m2[i]);
            }
            if (Math.abs(m1[i]) > max2) {
                max2 = Math.abs(m1[i]);
            }
        }
        return max1 / max2 * 100;
    }

    // Set up
    private static void Setup() {
        xData1 = new ArrayList<>();
        yData1 = new ArrayList<>();
        xData2 = new ArrayList<>();
        yData2 = new ArrayList<>();
        xData1.add(0d);
        yData1.add(0d);
        xData2.add(0d);
        yData2.add(0d);
    }

    public static void addCoord(double x, double y) {
        xData2.add(x);
        yData2.add(y);
    }

    // Draw
    private static void Draw() {
        xData1.clear();
        yData1.clear();
        xData2.clear();
        yData2.clear();
        //---------------------------------------------------------------
        h = T/(N-1);
        // Set Cauchy condition
        switch(problem) {
            case 0:
                u0 = 2; break;
            case 1:
                u0 = 0; break;
            case 2:
                u0 = 1; break;
            case 3:
                u0 = phi;
        }
        // Draw graph of solution function
        for (double x = 0.0; x <= 1.0; x += 0.001) {
            xData1.add(x);
            yData1.add(SolutionFunc(x));
        }
        chart.updateXYSeries(seriesName1, xData1, yData1, null);

        // Initialize solutions array
        arr_sol = new double[N+1];
        arr_sol[0] = u0;

        int i = 1;
        for (double x = h; x <= 1.0; x += h) {
            arr_sol[i] = SolutionFunc(x);
            ++i;
        }

        arr = new double[N+1];
        arr[0] = u0;
        xData2.add(0d);
        yData2.add(u0);
        i = 1;
        // Method 1 or 2
        switch(meth) {
            case 0:
                for(double x=h; x<=1; x+=h, ++i) {
                    u0 += h*Func(x-h, u0);
                    arr[i] = u0;
                    addCoord(x, arr[i]);
                }
                break;
            case 1:
                double yh, ukh;
                for(double x=h; x<=1; x+=h, ++i) {
                    ukh = arr[i-1];
                    yh = ukh + h * lambda * Func(x-h, ukh);
                    arr[i] = ukh + h*((1-1.0/(2*lambda))*Func(x-h, ukh) + Func(x-h+h*lambda, yh))/(2*lambda);
                    addCoord(x, arr[i]);
                }
                break;
        }
        chart.updateXYSeries(seriesName2, xData2, yData2, null);
        // Compute error
        error = err(arr, arr_sol);
        chart.setTitle("error: "+error);
    }

    // Main
    public static void main(String[] args) {
        try {
            // Setup data
            Setup();

            // Create Chart
            chart = new XYChartBuilder().width(1800).height(1000).xAxisTitle("X").yAxisTitle("Y").build();
            // Customize Chart
            chart.getStyler().setChartBackgroundColor(Color.LIGHT_GRAY);
            chart.getStyler().setCursorBackgroundColor(Color.GRAY);
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideS);
            chart.getStyler().setZoomEnabled(true);
            chart.getStyler().setZoomResetByButton(true);
            chart.getStyler().setMarkerSize(0);

            // Series 1
            testFunctionSeries = chart.addSeries(seriesName1, xData1, yData1);
            testFunctionSeries.setLineColor(Color.blue);
            testFunctionSeries.setLineWidth(1.2f);

            // Series 2
            interpolateFunctionSeries = chart.addSeries(seriesName2, xData2, yData2);
            interpolateFunctionSeries.setLineColor(Color.RED);
            interpolateFunctionSeries.setLineWidth(1.2f);

            // Build frame and show
            DifferentialEquation frame = new DifferentialEquation();
            frame.setTitle("Differential Equation: Runge-Kutta");
            frame.add(new XChartPanel<>(chart));
            frame.setSize(frame.getWidth(), frame.getHeight());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }
}
