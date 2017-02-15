package com.traclabs.biosim.ga;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.traclabs.biosim.ga.ui.GaPlotLive;

public class GaMain {

	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.equals("-gui"))
				startWithGui();
				return;
		}
	}

	public static void startWithGui() {
		try {
			SwingUtilities.invokeAndWait(new GuiWorker());
		} catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
		}
	}
	
	public static class GuiWorker implements Runnable {
		public void run() {
			GaPlotLive plotLive = new GaPlotLive();
			JFrame frame = new JFrame("GA Plot");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add("Center", plotLive);
			plotLive.setButtons(false);
			frame.pack();
			frame.setVisible(true);
			plotLive.start();
		}
	}

}
