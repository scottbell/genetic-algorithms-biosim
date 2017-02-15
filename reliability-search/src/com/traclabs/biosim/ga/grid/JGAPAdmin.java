package com.traclabs.biosim.ga.grid;

import java.util.Iterator;
import java.util.List;

import org.homedns.dade.jcgrid.WorkerStats;
import org.homedns.dade.jcgrid.admin.GridAdmin;

public class JGAPAdmin {

	public JGAPAdmin() throws Exception {
		GridAdmin admin = new GridAdmin();
		admin.start();
		while (true) {
			List v = admin.getWorkerStats();
			System.out.println("Number of workers: " + v.size());
			Iterator it = v.iterator();
			while (it.hasNext()) {
				WorkerStats stat = (WorkerStats) it.next();
				System.out.println(" " + stat.getName() + " / "
						+ stat.getWorkingFor() + " / " + stat.getStatus()
						+ " / " + stat.getUnitSec());
			}
			Thread.sleep(1000);
		}
	}

	public static void main(String[] args) throws Exception {
		// start admin
		new JGAPAdmin();
		// new guiJCGridAdminStatus(..);
	}
}
