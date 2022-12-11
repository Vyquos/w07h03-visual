package pgdp.sim.ui;

// import pgdp.sim.SimConfig;
// import pgdp.sim.Simulation;
// import pgdp.sim.CellSymbol;
// import pgdp.sim.Cell;
import pgdp.sim.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Arrays;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

public class GameFrontend {

	Simulation sim;
	Cell[] cells;
	int width;
	int height;

	public Simulation getSim() {
		return this.sim;
	}
	public void setSim(Simulation sim) {
		this.sim = sim;
	}
	public Cell[] getCells() {
		return this.cells;
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}

	static HashMap<CellSymbol, String> symbolMap = new HashMap<>() {{
			put(CellSymbol.PLANT, "🌱");
			put(CellSymbol.HAMSTER, "🐹");
			put(CellSymbol.PINGU, "🐧");
			put(CellSymbol.WOLF, "🐺");
		}};

	// ┌───┬───┐
	// │   │   │
	// ├───┼───┤
	// │   │   │
	// └───┴───┘

	private enum BoxPosition {
		top,
		center,
		bottom
	}

	public GameFrontend(int width, int height) {
		this.width = width;
		this.height = height;
		this.cells = new Cell[width * height];
		this.sim = new Simulation(cells, width, height);
	}

	public String getBoxLine(int cellWidth, int numCells,
							 BoxPosition position) {
		if (cellWidth < 1 || numCells < 1) {
			return "";
		}
		char[] cellBorders = switch(position) {
			case top ->		new char[]{'┌', '┬', '┐'};
			case center ->	new char[]{'├', '┼', '┤'};
			case bottom ->	new char[]{'└', '┴', '┘'};
		};
		String line = ""+cellBorders[0];
		for (int c=0; c<numCells; c++) {
			for (int s=0; s<cellWidth; s++) {
				line += "─";
			}
			if (c != numCells-1) {
				line += cellBorders[1];
			}
		}
		line += cellBorders[2];
		return line;
	}

	public Cell cellAt(int x, int y) {
		return cells[y * width + x];
	}

	public String show() {

		int colWidth = 2;

		String visualized = "";
		for (int row=0; row < height; row++) {
			if (row == 0) {
				visualized += getBoxLine(colWidth+2, width, BoxPosition.top) + "\n";
			} else {
				visualized += getBoxLine(colWidth+2, width, BoxPosition.center) + "\n";
			}

			String icons = "";
			String stats = "";
			for (int col=0; col < width; col++) {
				String sym = " ".repeat(colWidth);
				String stat = " ".repeat(colWidth);
				Cell c = cellAt(col, row);
				if (c != null) {
					sym = symbolMap.get(
							c.getSymbol())
						.toString();

					if (c instanceof Statistical) {
						stat = ((Statistical) c).getStats(colWidth);
					}
				}
				icons += String.format("│ %s ", sym);
				stats += String.format("│ %s ", stat);
			}
			icons += "│" + "\n";
			stats += "│" + "\n";
			visualized += icons + stats;
		}
		visualized += getBoxLine(colWidth+2, width, BoxPosition.bottom) + "\n";
		return visualized;
	}

	public static int countLines(String s) {
		return s.split("\n", -1).length;
	}

	public static void ansiStartLine() {
		System.out.print("\033[G");
	}

	public static void ansiBackwardLines(int n) {
		System.out.format("\033[%sF", n);
	}

	public static void ansiKillLine() {
		System.out.print("\033[2K");
	}

	public static void ansiBackwardKillLines(int num) {
		for (; num > 0; num--) {
			ansiBackwardLines(1);
			ansiKillLine();
		}
	}

	public static int printTable(String table) {
		System.out.println(table);
		return countLines(table);
	}

	public static int refreshTable(int prevHeight, String table) {
		ansiBackwardLines(prevHeight);
		ansiStartLine();
		return printTable(table);
	}

	public static void modifyCellConfig(
			int plantReproductionCost,
			int plantMaxGrowth,
			int plantMinGrowth,
			int hamsterFoodConsumption,
			int hamsterConsumedFood,
			int hamsterReproductionCost,
			int hamsterInitialFood,
			int pinguFoodConsumption,
			int pinguConsumedFood,
			int pinguReproductionCost,
			int pinguInitialFood,
			int wolfFoodConsumption,
			int wolfConsumedFood,
			int wolfReproductionCost,
			int wolfInitialFood) {
        SimConfig.plantReproductionCost   = plantReproductionCost;
        SimConfig.plantMaxGrowth          = plantMaxGrowth;
        SimConfig.plantMinGrowth          = plantMinGrowth;
        SimConfig.hamsterFoodConsumption  = hamsterFoodConsumption;
        SimConfig.hamsterConsumedFood     = hamsterConsumedFood;
        SimConfig.hamsterReproductionCost = hamsterReproductionCost;
        SimConfig.hamsterInitialFood      = hamsterInitialFood;
        SimConfig.pinguFoodConsumption    = pinguFoodConsumption;
        SimConfig.pinguConsumedFood       = pinguConsumedFood;
        SimConfig.pinguReproductionCost   = pinguReproductionCost;
        SimConfig.pinguInitialFood        = pinguInitialFood;
        SimConfig.wolfFoodConsumption     = wolfFoodConsumption;
        SimConfig.wolfConsumedFood        = wolfConsumedFood;
        SimConfig.wolfReproductionCost    = wolfReproductionCost;
        SimConfig.wolfInitialFood         = wolfInitialFood;
	}

	public static void modifyInitConfig(
			int numInitialPlant,
			int numInitialHamster,
			int numInitialPingu,
			int numInitialWolf,
			int width,
			int height) {
		SimConfig.numInitialPlant   = numInitialPlant;
		SimConfig.numInitialHamster = numInitialHamster;
		SimConfig.numInitialPingu   = numInitialPingu;
		SimConfig.numInitialWolf    = numInitialWolf;
		SimConfig.width             = width;
		SimConfig.height            = height;
	}

	public static boolean doPrompt(Scanner sc, boolean hasNext, String stepName) {
		String prompt;
		if (hasNext) {
			prompt = String.format("next step is: %s%n"
								   + "continue? [y/N]: ",
								   stepName);
		} else {
			prompt = "no more steps. "+stepName+"? [y/N]: ";
		}
		System.out.print(prompt);
		String answer = sc.nextLine();
		ansiBackwardKillLines(countLines(prompt));
		return answer.equals("y") || answer.equals("");
	}

	public static class StdoutCapture {

		PrintStream old;
		ByteArrayOutputStream capture;
		PrintStream ps;

		public StdoutCapture() {
			this.old = System.out;
			this.capture = new ByteArrayOutputStream();
			this.ps = new PrintStream(capture);
			System.setOut(ps);
		}

		public void restore() {
			System.setOut(old);
			try {
				capture.close();
				ps.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public String toString() {
			restore();
			return capture.toString();
		}
	}

	public void stepThrough(Iterator<SimStep> steps, SimStep defaultStep) {
		System.out.println("");
		int tableHeight = refreshTable(-1, show());
		int debugHeight = 0;

		Scanner sc = new Scanner(System.in);

		boolean hasNext = steps.hasNext();
		SimStep next = hasNext ? steps.next() : defaultStep;

		while (doPrompt(sc, hasNext, next.getName())) {
			ansiBackwardKillLines(tableHeight+debugHeight);

			StdoutCapture cap = new StdoutCapture();

			System.out.println("DEBUG:");
			next.run(this);
			System.out.println("===============");

			cap.restore();
			System.out.println(cap.toString());
			debugHeight = countLines(cap.toString());

			tableHeight = printTable(show());

			next = (hasNext = steps.hasNext()) ? steps.next() : defaultStep;
		}
	}

	public static void main(String[] args) {

		modifyCellConfig(0,  2,  1, // Plant
						 2,  8,  6,  4, // Hamster
						 1,  0,  1,  3, // Pingu
						 4, 10, 20, 10); // Wolf

		modifyInitConfig(80, 10, 5, 5,
						 5, 5);

		GameFrontend gfe = new GameFrontend(SimConfig.width, SimConfig.height);

		SimStep tick = new SimStep("tick") {
				public void run(Simulation sim, Cell[] c, int w, int h)  {
					sim.tick();
				}
			};

		LocalStepFactory plant = LocalStepFactory.getRealPlacer(() -> {
				return new Plant();
		}, "plant");
		SimStep plant0 = plant.at(0, 0);
		LocalStepFactory wolf =
			LocalStepFactory.getRealPlacer(() -> new Wolf(),
										   "wolf");
		LocalStepFactory hamster =
			LocalStepFactory.getRealPlacer(() -> new Hamster(),
								 "hamster");
		LocalStepFactory pingu =
			LocalStepFactory.getRealPlacer(() -> new Pingu(),
								 "pingu");

		SimStep plantSeed = new ParamSimStep((s, c, w, h) -> {
				RandomGenerator.reseed("plant".getBytes(StandardCharsets.UTF_8));
		}, "plant seed");

		SimStep resetSim = new SimStep("reset Simulation") {
				@Override
				public void run(Simulation s, Cell[] c, int w, int h) {}

				public void run(GameFrontend gfe) {
					gfe.setSim(new Simulation(gfe.getCells(),
											  gfe.getWidth(), gfe.getWidth()));
				}
			};

		// Hamster must be at (0,1)!
		LocalStepFactory hamsterEat = LocalStepFactory.getEater(0, 1);

		SimStep[] steps = new SimStep[]{
			plant0, plant0, plant0, tick, tick, tick, tick, plant.at(5, 5),
			tick, tick, tick, tick, tick,
			hamster.at(7, 7), hamster.at(8, 4), tick, tick, tick, tick
		};

		steps = new SimStep[]{
			plant.at(2, 0), plant.at(1, 2),
			pingu.at(1, 0), hamster.at(0, 1),
			hamsterEat.at(0, 1)
		};

		steps = new SimStep[]{
			plant.at(0, 0), plantSeed, resetSim, tick, tick, tick
		};
		steps[0] = pingu.at(0, 0);

		/* steps = */ SimStep[] my = new SimStep[]{
			pingu.at(0, 0), plant.at(1, 0), plantSeed, resetSim, tick, tick, tick
		};

		TestSetup tenBySparse = () -> {
			SimConfig.plantMinGrowth = 1;
			SimConfig.plantMaxGrowth = 2;
			SimConfig.plantReproductionCost = 3;
			SimConfig.hamsterInitialFood = 1;
			SimConfig.hamsterConsumedFood = 2;
			SimConfig.hamsterFoodConsumption = 1;
			SimConfig.hamsterReproductionCost = 3;
			SimConfig.wolfInitialFood = 2;
			SimConfig.wolfConsumedFood = 3;
			SimConfig.wolfFoodConsumption = 1;
			SimConfig.wolfReproductionCost = 3;
			SimConfig.pinguInitialFood = 1;
			SimConfig.pinguFoodConsumption = 1;
			SimConfig.pinguReproductionCost = 2;

			SimConfig.height = 10;
			SimConfig.width =  10;

			return new SimStep[]{
				hamster.at(0, 0), plant.at(1, 0),   wolf.at(0, 2),  pingu.at(3, 2),
				hamster.at(7, 3), hamster.at(0, 4), plant.at(1, 4), wolf.at(0, 6),
				plant.at(5, 6),   pingu.at(6, 8), plantSeed
			};
		};

		TestSetup tenByDense = () -> {
			SimConfig.plantMinGrowth = 1;
			SimConfig.plantMaxGrowth = 2;
			SimConfig.plantReproductionCost = 3;
			SimConfig.hamsterInitialFood = 1;
			SimConfig.hamsterConsumedFood = 2;
			SimConfig.hamsterFoodConsumption = 1;
			SimConfig.hamsterReproductionCost = 3;
			SimConfig.wolfInitialFood = 2;
			SimConfig.wolfConsumedFood = 3;
			SimConfig.wolfFoodConsumption = 1;
			SimConfig.wolfReproductionCost = 3;

			SimConfig.height = 10;
			SimConfig.width =  10;

			return new SimStep[]{
				hamster.at(0, 0), plant.at(1, 0), plant.at(6, 0), pingu.at(1, 1), plant.at(3, 1), pingu.at(5, 1), hamster.at(7, 1), wolf.at(0, 2), wolf.at(2, 2), wolf.at(3, 2), pingu.at(5, 2), pingu.at(8, 2), wolf.at(1, 3), pingu.at(3, 3), plant.at(5, 3), hamster.at(7, 3), hamster.at(0, 4), plant.at(1, 4), wolf.at(4, 4), pingu.at(6, 4), hamster.at(8, 4), plant.at(3, 5), plant.at(5, 5), pingu.at(7, 5), wolf.at(0, 6), pingu.at(1, 6), plant.at(5, 6), hamster.at(8, 6), wolf.at(2, 7), plant.at(4, 7), hamster.at(7, 7), wolf.at(1, 8), pingu.at(3, 8), pingu.at(4, 8), hamster.at(5, 8), plant.at(7, 8), hamster.at(9, 8), plantSeed
			};
		};

		steps = tenByDense.run();

		gfe = new GameFrontend(SimConfig.width, SimConfig.height);

		gfe.stepThrough(Arrays.asList(steps).iterator(), tick);
	}

	public interface TestSetup {

		SimStep[] run();
	}
}
