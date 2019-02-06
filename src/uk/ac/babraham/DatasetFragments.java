package uk.ac.babraham;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DatasetFragments {
	String datasetName;
	String dataFile; // File from which the data was extracted

	ArrayList<Fragment> allFragments = new ArrayList<Fragment>();
	HashMap<String, ArrayList<String>> fragmentLookup = new HashMap<String, ArrayList<String>>();

	public DatasetFragments(String dataFile) {
		this.dataFile = dataFile;
		File file = new File(dataFile);
		this.datasetName = file.getName();
	}

	public void addFragment(Fragment newFragment) {
		allFragments.add(newFragment);

		// Add to the lookup data structure
		String csome = newFragment.getChromosomeName();
		int start = newFragment.getStart();
		int end = newFragment.getEnd();

		// Determine the 10kb interval
		int my10kb = (int) Math.ceil(start / 10_000);
		int end10kb = (int) Math.ceil(end / 10_000);
		String positionInfo = Integer.toString(start) + "\t" + Integer.toString(end);

		do {
			String lookupKey = csome + "\t" + Double.toString(my10kb);
			if (!fragmentLookup.containsKey(lookupKey)) {
				fragmentLookup.put(lookupKey, new ArrayList<String>());
			}
			fragmentLookup.get(lookupKey).add(positionInfo);
			my10kb++;
		} while (my10kb <= end10kb);
	}

	public boolean overlapsWithAFragment(Fragment fragToCheck) {
		String fragToCheckCsome = fragToCheck.getChromosomeName();
		int fragToCheckStart = fragToCheck.getStart();
		int fragToCheckEnd = fragToCheck.getEnd();

		int my10kb = (int) Math.ceil(fragToCheckStart / 10_000);
		int end10kb = (int) Math.ceil(fragToCheckEnd / 10_000);
		// String positionInfo = Integer.toString(fragToCheckStart) + "\t" +
		// Integer.toString(fragToCheckEnd);

		do {
			String lookupKey = fragToCheckCsome + "\t" + Double.toString(my10kb);
			if (fragmentLookup.containsKey(lookupKey)) {
				ArrayList<String> retrievedList = fragmentLookup.get(lookupKey);
				for (int i = 0; i < retrievedList.size(); i++) {
					String fragmentData = retrievedList.get(i);
					System.out.println(fragmentData);

					String[] fragmentElements = fragmentData.split("\t");
					int listFragmentStart = Integer.parseInt(fragmentElements[0]);
					int listFragmentEnd = Integer.parseInt(fragmentElements[1]);

					System.out.println(fragToCheckStart + "\t" + fragToCheckEnd + "\t" + listFragmentStart + "\t"
							+ listFragmentEnd);

					if ((fragToCheckStart >= listFragmentStart) && (fragToCheckStart <= listFragmentEnd)) {
						return true;
					} else if ((fragToCheckStart <= listFragmentStart) && (fragToCheckEnd >= listFragmentStart)) {
						return true;
					}
				}
			}
			my10kb++;
		} while (my10kb <= end10kb);

		return false;
	}

	public void rename(String newName) {
		datasetName = newName;
	}

	public String getName() {
		return datasetName;
	}

	public int getNumberFragments() {
		return allFragments.size();
	}

	// Returns a stats summary of the dataset
	public String getSummary() {
		String summaryText = "Dataset Name: " + datasetName + "\n";
		summaryText = summaryText + "Original data file: " + dataFile + "\n";
		summaryText = summaryText + "Number of fragments: " + allFragments.size() + "\n";
		return summaryText;

	}
}
