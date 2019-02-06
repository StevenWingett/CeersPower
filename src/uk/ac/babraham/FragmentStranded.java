package uk.ac.babraham;

public class FragmentStranded extends Fragment {

	boolean strand;

	public FragmentStranded(String chromosome, int start, int end, boolean strand) {
		super(chromosome, start, end);
		this.strand = strand;
	}

	public boolean getStrand() {
		return strand;
	}

	// Text representation of the fragment
	public String toString() {
		return (chromosome + "\t" + start + "\t" + end + "\t" + strand);
	}

}
