package com.traclabs.ga.client.handlers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.traclabs.ga.client.framework.GAFrame;
import com.traclabs.ga.client.util.Fnorder;
import com.traclabs.ga.idl.handlers.GAHandler;
import com.traclabs.ga.idl.handlers.GAHandlerHelper;
import com.traclabs.ga.idl.nodes.Result;
import com.traclabs.ga.util.OrbUtils;

public class HandlerPanel extends JPanel {
    private static final int FIND_HANDLER_DELAY = 1000;

    private static final int POLL_HANDLER_GENES_DELAY = 1000;

    private final static int REFRESH_TRACKING_DELAY = 1000;

    private boolean tracking = true;

    private boolean builtFull = false;

    private JLabel waitingLabel;

    private javax.swing.Timer myFindHandlerTimer;

    private javax.swing.Timer myTrackingTimer;

    private JPanel myButtonPanel;

    private JPanel myBestResultPanel;

    private JPanel myWorstResultPanel;

    private JButton myRefreshButton;

    private JButton myTrackingButton;

    private JLabel[] myBestResultLabels;

    private JLabel[] myWorstResultLabels;

    private ChartPanel myChartPanel;

    private GAHandler myHandler;

    private JFreeChart myChart;

    private DecimalFormat numFormat;

    private JLabel myBestTickLabel;

    private JLabel myWorstTickLabel;

    public HandlerPanel() {
        super();
        setLayout(new BorderLayout());
        numFormat = new DecimalFormat("#,##0.00;(#)");
        myFindHandlerTimer = new Timer(FIND_HANDLER_DELAY,
                new FindHandlerAction());
        myTrackingTimer = new Timer(REFRESH_TRACKING_DELAY, new RefreshAction());
        buildFindingGUI();
        myFindHandlerTimer.start();
    }

    private void buildFindingGUI() {
        //Finding
        waitingLabel = new JLabel("Finding GA Handler...");
        add(waitingLabel, BorderLayout.CENTER);
        repackAndRepaint();
        //Actions
        myRefreshButton = new JButton(new RefreshButtonAction());
        myRefreshButton.setText("Refresh");
        myTrackingButton = new JButton(new TrackingAction());
        if (isTracking()) {
            myTrackingButton.setText("Stop Tracking");
            myRefreshButton.setEnabled(false);
        } else {
            myTrackingButton.setText("Start Tracking");
            myRefreshButton.setEnabled(true);
        }
        //Chart Panel
        XYSeries series = new XYSeries("Utility Received");
        XYSeriesCollection data = new XYSeriesCollection(series);
        myChart = ChartFactory.createXYLineChart("GA Results Received",
                "Simulation Iterations", "Utility", data, PlotOrientation.VERTICAL, true, true, false);

        XYPlot myPlot = myChart.getXYPlot();
        XYItemRenderer renderer = myPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(0, Color.GREEN);
        myPlot.getDomainAxis().setStandardTickUnits(
                NumberAxis.createIntegerTickUnits());
        myPlot.getRangeAxis().setStandardTickUnits(
                NumberAxis.createIntegerTickUnits());
        myChartPanel = new ChartPanel(myChart);
        myChartPanel.setMinimumDrawHeight(700);
        myChartPanel.setMinimumDrawWidth(540);
        myChartPanel.setPreferredSize(new java.awt.Dimension(640, 480));
        myChartPanel.setBorder(BorderFactory.createTitledBorder("Graph"));

        //Buttons
        myButtonPanel = new JPanel();
        myButtonPanel.add(myTrackingButton);
        myButtonPanel.add(myRefreshButton);
        myButtonPanel.setBorder(BorderFactory.createTitledBorder("Control"));

    }

    private static JPanel createResultPanel(Result pResult, String pTitle,
            JLabel[] pResultLabels, JLabel pUtilityLabel) {
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(
                pResult.getGene().getCardinality() + 1, 1));
        resultPanel.add(pUtilityLabel);
        resultPanel.setBorder(BorderFactory.createTitledBorder(pTitle));
        for (int i = 0; i < pResultLabels.length; i++) {
            pResultLabels[i] = new JLabel();
            resultPanel.add(pResultLabels[i]);
        }
        refreshResult(pResult, pUtilityLabel, pResultLabels);
        return resultPanel;
    }

    private void buildFullGUI() {
        removeAll();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);
        Result bestResult = myHandler.getBestResult();
        Result worstResult = myHandler.getWorstResult();
        myBestResultLabels = new JLabel[bestResult.getGene().getCardinality()];
        myWorstResultLabels = new JLabel[worstResult.getGene().getCardinality()];
        myBestTickLabel = new JLabel();
        myWorstTickLabel = new JLabel();
        myBestResultPanel = createResultPanel(bestResult, "Best Gene",
                myBestResultLabels, myBestTickLabel);
        myWorstResultPanel = createResultPanel(worstResult, "Worst Gene",
                myWorstResultLabels, myWorstTickLabel);
        myChart.setBackgroundPaint(myBestResultPanel.getBackground());

        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 2;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.gridwidth = 1;
        gridbag.setConstraints(myBestResultPanel, c);
        add(myBestResultPanel);

        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 2;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(myWorstResultPanel, c);
        add(myWorstResultPanel);

        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(myButtonPanel, c);
        add(myButtonPanel);

        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(myChartPanel, c);
        add(myChartPanel);

        repackAndRepaint();
        builtFull = true;
    }

    private void repackAndRepaint() {
        JFrame myFrame = getJFrameParent();
        if (myFrame != null)
            myFrame.pack();
        repaint();
    }

    public boolean isTracking() {
        return tracking;
    }

    private JFrame getJFrameParent() {
        Container myContainer = getParent();
        while (true) {
            if (myContainer instanceof JFrame)
                return ((JFrame) (myContainer));
            if (myContainer == null)
                return null;
            myContainer = myContainer.getParent();
        }
    }

    private void collectReferences() {
        try {
            myHandler = GAHandlerHelper.narrow(OrbUtils.getGANamingContext()
                    .resolve_str("GAHandler"));
            System.out.println("HandlerPanel: GA Handler found");
            myFindHandlerTimer.stop();
            waitingLabel.setText("Waiting for results...");
            repackAndRepaint();
            myTrackingTimer.start();
        } catch (org.omg.CORBA.UserException e) {
            System.err
                    .println("HandlerPanel: Had problems collecting server references, polling again...");
            OrbUtils.resetInit();
            OrbUtils.sleepAwhile();
            collectReferences();
        } catch (Exception e) {
            System.err
                    .println("HandlerPanel: Had problems contacting nameserver, polling again...");
            OrbUtils.resetInit();
            OrbUtils.sleepAwhile();
            collectReferences();
        }
    }

    public static void main(String[] args) {
        OrbUtils.initializeLog();
        if (args.length > 0) {
            if (args[0].equals("-debug")) {
                OrbUtils.initializeClientForDebug();
            }
        }
        GAFrame myFrame = new GAFrame("GA Handler Display", false);
        myFrame.getContentPane().add(new HandlerPanel());
        myFrame.pack();
        myFrame.setVisible(true);
    }

    private void refresh() {
        float[] myUtilities = myHandler.getUtilitiesReceived();
        if (myUtilities.length <= 0)
            return;
        else if (!builtFull)
            buildFullGUI();

        //Chart
        float bestUtility = -1;
        int totalUtilities = 0;
        XYSeries allUtilitiesSeries = new XYSeries("Utilities Received");
        XYSeries bestUtilitySeries = new XYSeries("Best Utility");
        XYSeries averageTickSeries = new XYSeries("Average Utility");
        for (int i = 0; i < myUtilities.length; i++) {
            if (!Float.isNaN(myUtilities[i])) {
                allUtilitiesSeries.add((float) i, myUtilities[i]);
                if (bestUtility < myUtilities[i])
                    bestUtility = myUtilities[i];
                bestUtilitySeries.add((float) i, bestUtility);
                totalUtilities += myUtilities[i];
                averageTickSeries.add((float) i,
                        (float) (totalUtilities / (i + 1)));
            }
        }
        XYSeriesCollection data = new XYSeriesCollection(allUtilitiesSeries);
        data.addSeries(bestUtilitySeries);
        data.addSeries(averageTickSeries);
        myChart.getXYPlot().setDataset(data);

        //Refresh Results
        refreshResult(myHandler.getBestResult(), myBestTickLabel,
                myBestResultLabels);
        refreshResult(myHandler.getWorstResult(), myWorstTickLabel,
                myWorstResultLabels);
    }

    private static void refreshResult(Result pResult, JLabel pUtilityLabel,
            JLabel[] pResultLabels) {
        //update best gene panel
        pUtilityLabel.setText("Utility: " + pResult.getUtility());
        String[] myDescriptors = pResult.getGene().getDescriptors();
        String[] myStringValues = pResult.getGene().getStringValues();
        for (int i = 0; i < pResultLabels.length; i++) {
            String desciption = myDescriptors[i];
            if (!desciption.equals("")) {
                String labelText = desciption + ": " + myStringValues[i];
                pResultLabels[i].setText(labelText);
            }
        }
    }

    public void setTracking(boolean pTrackingWanted) {
        if (pTrackingWanted == tracking)
            return;
        tracking = pTrackingWanted;
        if (tracking)
            myTrackingTimer.start();
        else
            myTrackingTimer.stop();
    }

    public void visibilityChange(boolean nowVisible) {
        if (nowVisible && tracking) {
            myTrackingTimer.start();
        } else {
            myTrackingTimer.stop();
        }
    }

    /**
     * Action that displays the power panel in an internal frame on the desktop.
     */
    private class FindHandlerAction extends AbstractAction {
        public void actionPerformed(ActionEvent ae) {
            collectReferences();
        }
    }

    private class TrackingAction extends AbstractAction {
        public void actionPerformed(ActionEvent ae) {
            boolean newState = !(isTracking());
            setTracking(newState);
            if (isTracking()) {
                myTrackingButton.setText("Stop Tracking");
                myRefreshButton.setEnabled(false);
            } else {
                myTrackingButton.setText("Start Tracking");
                myRefreshButton.setEnabled(true);
            }
        }
    }

    private class RefreshButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent ae) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            refresh();
            if (ae.getModifiers() == (ActionEvent.CTRL_MASK + 16)) {
                Fnorder myFnord = new Fnorder();
                String message = myFnord.getFnord();
                ImageIcon fnordIcon = new ImageIcon(
                        ClassLoader
                                .getSystemResource("ga/client/handlers/pyramid.gif"));
                JOptionPane fnordPane = new JOptionPane(message,
                        JOptionPane.INFORMATION_MESSAGE,
                        JOptionPane.DEFAULT_OPTION, fnordIcon);
                JDialog dialog = fnordPane.createDialog(null,
                        "Your message from the Illuminati");
                dialog.setVisible(true);
            }
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private class RefreshAction extends AbstractAction {
        public void actionPerformed(ActionEvent ae) {
            refresh();
        }
    }
}