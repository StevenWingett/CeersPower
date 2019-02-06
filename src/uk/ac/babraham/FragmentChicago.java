package uk.ac.babraham;

public class FragmentChicago extends Fragment {

	int id;
	String name;

	public FragmentChicago(String chromosome, int start, int end, int id, String name) {
		super(chromosome, start, end);
		this.id = id;
		this.name = Utilities.formatSeqmonkGeneName(name);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCoordinates() {
		return chromosome + "\t" + start + "\t" + end;
	}

	// Text representation of the fragment
	public String toString() {
		return (chromosome + "\t" + start + "\t" + end + "\t" + id + "\t" + name);
	}

}
