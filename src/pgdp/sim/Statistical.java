package pgdp.sim;

public interface Statistical {

	default String getStats(int targetSize) {
		if (targetSize < 1) {
			throw new IllegalArgumentException("targetSize must be greater than 1.");
		}
		String stats = getStats();
		int pad =  targetSize - stats.length();
		if (pad < 0) {
			return " ".repeat(--targetSize) + "+";
		}
		return " ".repeat(pad) + stats;
	}

	String getStats();
}
