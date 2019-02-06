package uk.ac.babraham;

public class Fragment {
	String chromosome;
	int start;
	int end;

	public Fragment(String chromosome, int start, int end) {
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;

		if (end < start) {
			System.out.println("Start (" + start + ") cannot be more than end (" + end + ")");
		}
	}

	public String getChromosomeName() {
		return chromosome;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getLength() {
		return (end - start + 1);
	}

	// Text representation of the fragment
	public String toString() {
		return (chromosome + "\t" + start + "\t" + end);
	}

	public boolean doesOverlap(Fragment fragToCheck) {
		if (chromosome.equals(fragToCheck.getChromosomeName())) { // Check on
																	// same
																	// chromsome
			if ((start >= fragToCheck.getStart()) && (start <= fragToCheck.getEnd())) {
				return true;
			} else if ((start <= fragToCheck.getStart()) && (end >= fragToCheck.getStart())) {
				return true;
			}
		}
		return false;
	}

}
