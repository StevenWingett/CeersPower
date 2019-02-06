package uk.ac.babraham;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DatasetChicago {

	String datasetName;
	String dataFile; // File from which the data was extracted
	HashMap<String, InteractionChicago> interactions = new HashMap<String, InteractionChicago>();
	int significanceThreshold = 12; // CHiCAGO score for a interactions to be
									// considered significant

	public DatasetChicago(String datasetName, String dataFile) {
		this.datasetName = datasetName;
		this.dataFile = dataFile;
	}

	public DatasetChicago(String dataFile) {
		this.dataFile = dataFile;
		File file = new File(dataFile);
		this.datasetName = file.getName();
	}

	public void addInteraction(InteractionChicago newInteraction) {
		String id = newInteraction.getCoordinates();

		if (interactions.containsKey(id)) { // Add if not present, else adjust
											// the max score
			double newScore = newInteraction.getScore();
			interactions.get(id).setMaxScore(newScore);
		} else {
			interactions.put(id, newInteraction);
		}
	}

	public void printInteractions() {
		for (String id : interactions.keySet()) {
			System.out.println(interactions.get(id).toString());
		}
	}

	public void rename(String newName) {
		datasetName = newName;
	}

	public String getName() {
		return datasetName;
	}

	public int getInteractions() {
		return interactions.size();
	}

	public int getSignificantInteractionsCount() {
		int count = 0;
		for (String key : interactions.keySet()) {
			double score = interactions.get(key).score;
			if (score >= significanceThreshold) {
				count++;
			}
		}
		return count;
	}

	// Returns and array of {Significant Cis Count, Significant Trans Count}
	public int[] getSignificantCisTransCount() {
		int[] sigCisTransCounter = { 0, 0 }; // Cis score, Trans score
		for (String key : interactions.keySet()) {
			double score = interactions.get(key).score;
			if (score >= significanceThreshold) {
				if (interactions.get(key).isTrans()) { // Trans interaction
					sigCisTransCounter[1] = sigCisTransCounter[1] + 1;
				} else {
					sigCisTransCounter[0] = sigCisTransCounter[0] + 1; // Cis
																		// interaction
				}
			}
		}
		return sigCisTransCounter;
	}

	// Returns a stats summary of the dataset
	public String getSummary() {
		String summaryText = "Dataset Name: " + datasetName + "\n";
		summaryText = summaryText + "Original data file: " + dataFile + "\n";
		summaryText = summaryText + "Number of interactions: ";
		summaryText = summaryText + Integer.toString(this.getInteractions()) + "\n";
		summaryText = summaryText + "Number of significant interactions: ";
		int sigInteractions = this.getSignificantInteractionsCount();
		summaryText = summaryText + Integer.toString(sigInteractions) + "\n";
		int[] sigCisTransCounter = this.getSignificantCisTransCount();
		summaryText = summaryText + "Number of significant cis interactions :";
		summaryText = summaryText + sigCisTransCounter[0] + "\n";
		summaryText = summaryText + "Number of significant trans interactions: ";
		summaryText = summaryText + sigCisTransCounter[1] + "\n";
		summaryText = summaryText + "Percentage significant trans interactions: ";
		if (sigInteractions == 0) {
			summaryText = summaryText + "NA\n";
		} else {
			double percSigTrans = 100 * sigCisTransCounter[1] / sigInteractions;
			summaryText = summaryText + percSigTrans + "\n";
		}

		return summaryText;
	}

	// Return an array of all CHiCAGO scores (including non-significant)
	public float[] getAllscores() {
		float allScores[] = new float[interactions.size()];
		int i = 0;

		for (String key : interactions.keySet()) {
			float score = (float) interactions.get(key).score;
			allScores[i] = score;
			i++;
		}

		return allScores;
	}

	// Return an array of all HiC distances (trans not returned)
	public int[] getAllDistances() {
		ArrayList<Integer> allDistances = new ArrayList<Integer>();

		for (String key : interactions.keySet()) {
			if (!interactions.get(key).isTrans()) { // Not trans
				allDistances.add(interactions.get(key).distance);
			}
		}

		// Convert to an array
		int[] arrayToReturn = new int[allDistances.size()];
		for (int i = 0; i < allDistances.size(); i++) {
			arrayToReturn[i] = allDistances.get(i);
		}
		return arrayToReturn;
	}

	// Returns the dataset in WashU format
	public String[] convertWashU() {
		String[] washuDataArray = new String[interactions.size()];
		int i = 0;

		for (InteractionChicago interactionChicago : interactions.values()) {
			String interaction = interactionChicago.toString();

			String[] interactionElements = interaction.split("\t");
			String csomeA = interactionElements[0];
			String startA = interactionElements[1];
			String endA = interactionElements[2];
			String csomeB = interactionElements[5];
			String startB = interactionElements[6];
			String endB = interactionElements[7];
			String score = interactionElements[11];

			if ((csomeA.length() < 4) || (csomeA.substring(0, 2) != "chr")) { // Make
																				// BED
																				// format
																				// style
				csomeA = "chr" + csomeA;
			}

			if ((csomeB.length() < 4) || (csomeB.substring(0, 2) != "chr")) {
				csomeB = "chr" + csomeB;
			}

			String newLine = csomeA + ":" + startA + "-" + endA + "\t";
			newLine = newLine + csomeB + ":" + startB + "-" + endB + "\t" + score;

			washuDataArray[i] = newLine;
			i++;
		}
		return washuDataArray;
	}

	public DatasetChicago filterByGene(GeneList genes) {
		String filteredDataName = this.datasetName + "_filtered_with_" + genes.getName();
		String filteredDataFilename = ""; // Leave as this (possibly change in
											// future)

		DatasetChicago filteredData = new DatasetChicago(filteredDataName, filteredDataFilename);

		for (InteractionChicago interaction : interactions.values()) {
			String[] geneNames = interaction.getGeneNames();

			if ((genes.geneExists(geneNames[0])) || (genes.geneExists(geneNames[1]))) {
				System.out.println("'" + geneNames[0] + "'" + "\t" + "'" + geneNames[1] + "'");
				filteredData.addInteraction(interaction);
			}
		}
		return filteredData;
	}

	// Use a list of fragment positions to filter the CHiCAGO dataset
	public DatasetChicago filterByFragments(DatasetFragments filterFragments) {
		String filteredDataName = this.datasetName + "_filtered_with_fragments_" + filterFragments.getName();
		String filteredDataFilename = ""; // Leave as this (possibly change in
											// future)
		DatasetChicago filteredData = new DatasetChicago(filteredDataName, filteredDataFilename);

		for (InteractionChicago interaction : interactions.values()) {
			FragmentChicago[] fragmentPair = interaction.getFragments();
			FragmentChicago frag1 = fragmentPair[0];
			FragmentChicago frag2 = fragmentPair[1];

			if (filterFragments.overlapsWithAFragment(frag1) || filterFragments.overlapsWithAFragment(frag2)) {
				filteredData.addInteraction(interaction);
			}
		}
		return filteredData;
	}

	public String[] getInteractionCoordinates() { // Returns a list of all the
													// interactions in this
													// dataset
		String[] interactionIds = new String[interactions.size()];
		int i = 0;

		for (String key : interactions.keySet()) {
			interactionIds[i] = key;
			i++;
		}

		return interactionIds;
	}

}
