package pgdp.sim;

public class Simulation {
	public Cell[] getCells() {
		return this.cells;
	}

	public Simulation(Cell[] cells, int width, int height) {
		// ...
	}

	/** Simuliert einen Tick des Spiels:
	 *  Erst nehmen alle MovingCells Nahrung zu sich,
	 *  dann wird auf allen Cells die tick()-Methode aufgerufen.
	 */
	public void tick() {
		// ...
	}
}
