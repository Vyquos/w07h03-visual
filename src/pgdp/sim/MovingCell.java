package pgdp.sim;

public abstract class MovingCell implements Cell, Statistical {

	int numTicks;

	public String getStats() {
		return "" + (numTicks > 0 ? "" : "*") + food;
	}

	public MovingCell() {
		// ...
	}

	public void move(Cell[] cells, Cell[] newCells,
					 int width, int height, int x, int y) {
		// ...
	}

	public void eat(Cell[] cells, Cell[] newCells,
					int width, int height, int x, int y) {
		// ...
	}

	public void tick(Cell[] cells, Cell[] newCells,
					 int width, int height, int x, int y) {
		// ...
	}

	public int priority() {
		// ...
	}
}
