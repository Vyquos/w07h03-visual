package pgdp.sim.ui;

import pgdp.sim.Simulation;
import pgdp.sim.Cell;

public abstract class SimStep {

	private String name;
	public String getName() {
		return this.name;
	}

	public SimStep(String name) {
		this.name = name;
	}

	public abstract void run(Simulation sim, Cell[] cells, int width, int height);

	public void run(GameFrontend gfe) {
		run(gfe.getSim(), gfe.getCells(), gfe.getWidth(), gfe.getHeight());
	}
}
