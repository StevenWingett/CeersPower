package uk.ac.babraham;

public class InteractionChicago {
	FragmentChicago frag1;
	FragmentChicago frag2;
	int distance;
	double score;

	public InteractionChicago(FragmentChicago frag1, FragmentChicago frag2, int distance, double score) {
		if (frag1.getId() > frag2.getId()) { // Order the fragments
			this.frag1 = frag2;
			this.frag2 = frag1;
		} else {
			this.frag1 = frag1;
			this.frag2 = frag2;
		}
		this.distance = distance;
		this.score = score;
	}

	public InteractionChicago(FragmentChicago frag1, FragmentChicago frag2, double score) {
		if (frag1.getId() > frag2.getId()) { // Order the fragments
			this.frag1 = frag2;
			this.frag2 = frag1;
		} else {
			this.frag1 = frag1;
			this.frag2 = frag2;
		}
		this.score = score;

		// The distance refers to the midpoints of the two fragments
		this.distance = (int) Math
				.ceil(((frag2.getStart() + frag2.getEnd()) / 2) - ((frag1.getStart() + frag1.getEnd()) / 2));

	}

	// Peak matrix may contain 2 scores for an interaction, use max value
	public void setMaxScore(double anotherScore) {
		if (anotherScore > score) {
			score = anotherScore;
		}
	}

	public int getDistance() {
		return distance;
	}

	public double getScore() {
		return score;
	}

	public String getId() { // This should be a unique Id for the 2 fragments
		return (frag1.getId() + "_" + frag2.getId());
	}

	// Text representation of the fragment
	public String toString() {
		return (frag1.toString() + "\t" + frag2.toString() + "\t" + distance + "\t" + score);
	}

	// Checks whether this is a trans interaction
	public boolean isTrans() {
		if (frag1.getChromosomeName().equals(frag2.getChromosomeName())) {
			return false; // Cis
		} else {
			return true; // Trans
		}
	}

	// Returns the names of the genes involved in an interaction
	public String[] getGeneNames() {
		String[] geneNamePairs = new String[2];
		geneNamePairs[0] = frag1.getName();
		geneNamePairs[1] = frag2.getName();
		return geneNamePairs;
	}

	public FragmentChicago[] getFragments() {
		FragmentChicago fragmentArray[] = new FragmentChicago[2];
		fragmentArray[0] = frag1;
		fragmentArray[1] = frag2;
		return fragmentArray;
	}

	public String getCoordinates() {
		return frag1.getCoordinates() + "\t" + frag2.getCoordinates();
	}

}
