package uk.ac.babraham;

import java.util.ArrayList;
import java.util.HashMap;

public class AllDatasets {

	HashMap<String, DatasetChicago> allDatasetsChicago = new HashMap<String, DatasetChicago>();
	HashMap<String, GeneList> allDatasetsGeneList = new HashMap<String, GeneList>();
	HashMap<String, DatasetFragments> allDatasetsFragments = new HashMap<String, DatasetFragments>();

	// Add the dataset to the name and add numerical count suffix if necessary
	public void addDatasetFragments(DatasetFragments newDataset) {

		String newDatasetName = newDataset.getName();

		if (allDatasetsFragments.containsKey(newDataset.getName())) {
			int suffix = 2;
			while (allDatasetsFragments.containsKey(newDatasetName)) {
				newDatasetName = newDataset.getName() + "_" + Integer.toString(suffix);
				suffix++;
			}
			newDataset.rename(newDatasetName);
			allDatasetsFragments.put(newDatasetName, newDataset);
		} else {
			allDatasetsFragments.put(newDatasetName, newDataset);
		}

	}

	// Add the dataset to the name and add numerical count suffix if necessary
	public void addDatasetChicago(DatasetChicago newDataset) {

		String newDatasetName = newDataset.getName();

		if (allDatasetsChicago.containsKey(newDataset.getName())) {
			int suffix = 2;
			while (allDatasetsChicago.containsKey(newDatasetName)) {
				newDatasetName = newDataset.getName() + "_" + Integer.toString(suffix);
				suffix++;
			}
			newDataset.rename(newDatasetName);
			allDatasetsChicago.put(newDatasetName, newDataset);
		} else {
			allDatasetsChicago.put(newDatasetName, newDataset);
		}

	}

	// Add the dataset to the name and add numerical count suffix if necessary
	public void addDatasetGeneList(GeneList newDataset) {

		String newDatasetName = newDataset.getName();

		if (allDatasetsGeneList.containsKey(newDataset.getName())) {
			int suffix = 2;
			while (allDatasetsGeneList.containsKey(newDatasetName)) {
				newDatasetName = newDataset.getName() + "_" + Integer.toString(suffix);
				suffix++;
			}
			newDataset.rename(newDatasetName);
			allDatasetsGeneList.put(newDatasetName, newDataset);
		} else {
			allDatasetsGeneList.put(newDatasetName, newDataset);
		}
	}

	public DatasetChicago getDatasetChicago(String datasetName) {
		return allDatasetsChicago.get(datasetName);
	}

	public ArrayList<DatasetChicago> getDatasetsChicago(ArrayList<String> myDatasets) {
		ArrayList<DatasetChicago> retrievedDatasets = new ArrayList<DatasetChicago>();
		for (String myId : myDatasets) {
			if (allDatasetsChicago.containsKey(myId)) {
				retrievedDatasets.add(allDatasetsChicago.get(myId));
			}
		}

		return retrievedDatasets;
	}

	public GeneList getDatasetGeneList(String datasetName) {
		return allDatasetsGeneList.get(datasetName);
	}

	public DatasetFragments getDatasetFragmentList(String datasetName) {
		return allDatasetsFragments.get(datasetName);
	}

	// Text representation of all the CHiCAGO datasets
	public String toString() {
		String[] datasetsArray = allDatasetsChicago.keySet().toArray(new String[allDatasetsChicago.size()]);
		return String.join("\t", datasetsArray);
	}

}