package pgdp.sim.ui;

import pgdp.sim.Cell;
import pgdp.sim.MovingCell;

public class LocalStepFactory {

	private String namePrefix;
	private LocalStepBuilder builder;

	public interface LocalStepBuilder {
		ParamSimStep.StepAction get(int x, int y);
	}

	public interface Initializer<T> {
		T getNew();
	}

	public static LocalStepFactory getEater(int fromX, int fromY) {
		return new LocalStepFactory((x, y) -> (sim, cells, width, height) -> {
				Cell[] newCells = cells.clone();
				((MovingCell) sim.getCells()[fromY * width + fromX])
					.eat(cells, newCells, width, height, x, y);

				for (int i=0; i<cells.length; i++) {
					if (newCells[i] != null) {
						cells[i] = newCells[i];
					}
				}
		}, String.format("(%s,%s).eat", fromX, fromY)) {

			@Override
			public String getName(String prefix, int x, int y) {
				return "a" + super.getName(prefix, x, y);
			}
		};
	}

	public static LocalStepFactory getRealPlacer(Initializer<Cell> initializer,
												 String namePrefix) {
		return new LocalStepFactory((x, y) -> (sim, cells, width, height) -> {
				cells[y * width + x] = initializer.getNew();
		}, namePrefix);
	}

	public static LocalStepFactory getPlacer(Initializer<Cell> initializer,
											 String namePrefix) {
		return new LocalStepFactory((x, y) -> (sim, cells, width, height) -> {
				initializer.getNew().place(cells, cells, width, height, x, y);
		}, namePrefix);
	}

	public LocalStepFactory(LocalStepBuilder bob, String namePrefix) {
		this.builder = bob;
		this.namePrefix = namePrefix;
	}

	public String getName(String prefix, int x, int y) {
		return String.format("%s(%s,%s)",
							 prefix, x, y);
	}

	public SimStep at(int x, int y) {
		return new ParamSimStep(builder.get(x, y),
								getName(namePrefix, x, y));
	}
}
