package pgdp.sim.ui;

import pgdp.sim.Simulation;
import pgdp.sim.Cell;

public class ParamSimStep extends SimStep {

	StepAction action;
	public StepAction getAction() {
		return this.action;
	}

	public interface StepAction {
		public void run(Simulation sim, Cell[] cells, int width, int height);
	}

	public ParamSimStep(StepAction action, String name) {
		super(name);
		this.action = action;
	}

	public void run(Simulation sim, Cell[] cells, int width, int height) {
		action.run(sim, cells, width, height);
	}
}
