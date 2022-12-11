package pgdp.sim;

public class Plant implements Cell, Statistical {

	// ...

	public String getStats() {
		return "" + growth;
	}

	public CellSymbol getSymbol() {
		// ...
	}

	public int priority() {
		// ...
	}

	public Plant() {
		// ...
	}

	public void tick(Cell[] cells, Cell[] newCells,
					 int width, int height, int x, int y) {
		// ...
	}
}
