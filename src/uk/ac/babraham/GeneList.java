package uk.ac.babraham;

import java.io.File;
import java.util.ArrayList;

//Unique list of gene names
public class GeneList {
	String datasetName;
	String dataFile;
	ArrayList<String> genes = new ArrayList<String>();

	public GeneList(String dataFile) {
		this.dataFile = dataFile;
		File file = new File(dataFile);
		this.datasetName = file.getName();
	}

	public String getName() {
		return datasetName;
	}

	public void addGene(String newGene) {
		if (!newGene.trim().isEmpty()) { // Don't add empty spaces
			newGene = Utilities.formatSeqmonkGeneName(newGene);
			genes.add(newGene);
		}
	}

	public void rename(String newName) {
		datasetName = newName;
	}

	public boolean geneExists(String geneName) {
		return genes.contains(geneName);
	}

	// Returns a stats summary of the dataset
	public String getSummary() {
		String summaryText = "Dataset Name: " + datasetName + "\n";
		summaryText = summaryText + "Original data file: " + dataFile + "\n";
		summaryText = summaryText + "Number of genes: " + genes.size() + "\n";
		return summaryText;
	}

}
