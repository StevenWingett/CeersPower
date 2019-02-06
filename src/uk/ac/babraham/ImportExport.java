package uk.ac.babraham;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ImportExport {

	// Fragment List format:
	// Tab-delimited list of Chromosome Start End
	public static DatasetFragments importFragmentListData(String infile) throws IOException {
		DatasetFragments importedFragments = new DatasetFragments(infile);

		// Decide whether input is zipped
		FileInputStream fis = null;
		BufferedReader br = null;

		try {
			fis = new FileInputStream(infile);
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(fis)));
		} catch (IOException ioe) {
			try {
				if (fis != null) {
					fis.close();
				}
				br = new BufferedReader(new FileReader(infile));
			} catch (IOException ex) {
				throw new IOException();
			}
		}

		// Read the file
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] lineElements = line.split("\\s");
			String chromosome = lineElements[0];
			int start = Integer.parseInt(lineElements[1]);
			int end = Integer.parseInt(lineElements[2]);
			Fragment newFragment = new Fragment(chromosome, start, end);
			importedFragments.addFragment(newFragment);
		}
		return importedFragments;
	}

	// Gene List format:
	// List of names, one name per line (split by whitesapce and take first
	// element)
	public static GeneList importGeneListData(String infile) throws IOException {
		GeneList importedGenes = new GeneList(infile);

		// Decide whether input is zipped
		FileInputStream fis = null;
		BufferedReader br = null;

		try {
			fis = new FileInputStream(infile);
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(fis)));
		} catch (IOException ioe) {
			try {
				if (fis != null) {
					fis.close();
				}
				br = new BufferedReader(new FileReader(infile));
			} catch (IOException ex) {
				throw new IOException();
			}
		}

		// Read the file
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] lineElements = line.split("\\s");
			importedGenes.addGene(lineElements[0]);
		}
		return importedGenes;
	}

	// CHiCAGO SeqMonk format:
	// Paired reads on adjacent lines, tab-delimited
	// Csome Start End baitName baitID Score
	// OEcsome OEstart OEend OEbaitName OEbaitID Score
	public static DatasetChicago importChicagoSeqmonkData(String infile) throws IOException, NumberFormatException {

		DatasetChicago newDataset = new DatasetChicago(infile);

		// Decide whether input is zipped
		FileInputStream fis = null;
		BufferedReader br = null;

		try {
			fis = new FileInputStream(infile);
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(fis)));
		} catch (IOException ioe) {
			try {
				if (fis != null) {
					fis.close();
				}
				br = new BufferedReader(new FileReader(infile));
			} catch (IOException ex) {
				// ex.printStackTrace();
				throw new IOException();
			}
		}

		try {
			String line = null;

			while ((line = br.readLine()) != null) {

				String[] lineElements = line.split("\t");

				// Csome Start End baitName baitID Score
				// OEcsome OEstart OEend OEbaitName OEbaitID Score
				String frag1Chr = lineElements[0];
				int frag1Start = Integer.parseInt(lineElements[1]);
				int frag1End = Integer.parseInt(lineElements[2]);
				int frag1Id = Integer.parseInt(lineElements[4]);
				String frag1Name = lineElements[3];
				double frag1Score = Double.parseDouble(lineElements[5]);

				line = br.readLine(); // Get next line
				lineElements = line.split("\t");

				String frag2Chr = lineElements[0];
				int frag2Start = Integer.parseInt(lineElements[1]);
				int frag2End = Integer.parseInt(lineElements[2]);
				int frag2Id = Integer.parseInt(lineElements[4]);
				String frag2Name = lineElements[3];
				double frag2Score = Double.parseDouble(lineElements[5]);

				if (frag1Score != frag2Score) {
					throw new IOException("Scores do not match in datafile");
				}

				FragmentChicago frag1 = new FragmentChicago(frag1Chr, frag1Start, frag1End, frag1Id, frag1Name);
				FragmentChicago frag2 = new FragmentChicago(frag2Chr, frag2Start, frag2End, frag2Id, frag2Name);

				InteractionChicago newInteraction = new InteractionChicago(frag1, frag2, frag1Score);
				newDataset.addInteraction(newInteraction);
				System.out.println(newInteraction.toString());

			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (NumberFormatException nf) {
			nf.printStackTrace();
			throw nf;
		}

		return newDataset;
	}

	// Peak matrix format:
	// baitChr baitStart baitEnd baitID baitName
	// oeChr oeStart oeEnd dataset1 dataset2 ...
	public static DatasetChicago[] importChicagoData(String infile) throws IOException, NumberFormatException {

		DatasetChicago[] newDatasets;

		// Decide whether input is zipped
		FileInputStream fis = null;
		BufferedReader br = null;

		try {
			fis = new FileInputStream(infile);
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(fis)));
		} catch (IOException ioe) {
			try {
				if (fis != null) {
					fis.close();
				}
				br = new BufferedReader(new FileReader(infile));
			} catch (IOException ex) {
				// ex.printStackTrace();
				throw new IOException();
			}
		}

		try {
			String line = null;

			// Find the name of the datasets and make an array of the datasets
			String[] headerElements = br.readLine().split("\t"); // Header
																	// elements
			int numberInputDatasets = headerElements.length - 11;
			newDatasets = new DatasetChicago[numberInputDatasets];
			for (int i = 0; i < numberInputDatasets; i++) {
				String datasetName = headerElements[i + 11];
				newDatasets[i] = new DatasetChicago(datasetName, infile);
			}

			while ((line = br.readLine()) != null) {

				String[] lineElements = line.split("\t");

				// baitChr baitStart baitEnd baitID baitName oeChr oeStart oeEnd
				String frag1Chr = lineElements[0];
				int frag1Start = Integer.parseInt(lineElements[1]);
				int frag1End = Integer.parseInt(lineElements[2]);
				int frag1Id = Integer.parseInt(lineElements[3]);
				String frag1Name = lineElements[4];
				String frag2Chr = lineElements[5];
				int frag2Start = Integer.parseInt(lineElements[6]);
				int frag2End = Integer.parseInt(lineElements[7]);
				int frag2Id = Integer.parseInt(lineElements[8]);
				String frag2Name = lineElements[9];
				int distance;

				if (lineElements[10].equals("NA")) { // Trans interaction, set
														// distance to 0 for now
					distance = 0;
				} else {
					distance = Integer.parseInt(lineElements[10]);
				}

				FragmentChicago frag1 = new FragmentChicago(frag1Chr, frag1Start, frag1End, frag1Id, frag1Name);
				FragmentChicago frag2 = new FragmentChicago(frag2Chr, frag2Start, frag2End, frag2Id, frag2Name);

				// Now add the scores to each dataset
				for (int i = 0; i < numberInputDatasets; i++) {
					double score = Double.parseDouble(lineElements[i + 11]);
					InteractionChicago newInteraction = new InteractionChicago(frag1, frag2, distance, score);
					newDatasets[i].addInteraction(newInteraction);
					// System.out.println(newInteraction.toString());
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (NumberFormatException nf) {
			nf.printStackTrace();
			throw nf;
		}

		return newDatasets;
	}

	public static void writeOutfile(String outfile, String data) {
		String zipExtension = outfile.substring(outfile.length() - 3);
		if (zipExtension.equals(".gz")) { // Zip output
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new GZIPOutputStream(new FileOutputStream(outfile), 2048));
				bos.write((data).getBytes());
				bos.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}

		} else { // Not zipped
			try {
				BufferedWriter bos = new BufferedWriter(new FileWriter(outfile));
				bos.write(data);
				bos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static void writeArrayOutfile(String outfile, String[] data) {
		String zipExtension = outfile.substring(outfile.length() - 3);
		if (zipExtension.equals(".gz")) { // Zip output
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new GZIPOutputStream(new FileOutputStream(outfile), 2048));
				for (int i = 0; i < data.length; i++) {
					String toOutfile = data[i] + "\n";
					bos.write((toOutfile.getBytes()));
				}
				bos.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}
		} else { // Not zipped
			try {
				BufferedWriter bos = new BufferedWriter(new FileWriter(outfile));
				for (int i = 0; i < data.length; i++) {
					String toOutfile = data[i] + "\n";
					bos.write(toOutfile);
				}
				bos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static void writeArrayOutfile(String outfile, ArrayList<String> data) {
		String zipExtension = outfile.substring(outfile.length() - 3);
		if (zipExtension.equals(".gz")) { // Zip output
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new GZIPOutputStream(new FileOutputStream(outfile), 2048));
				for (int i = 0; i < data.size(); i++) {
					String toOutfile = data.get(i) + "\n";
					bos.write((toOutfile.getBytes()));
				}
				bos.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}
		} else { // Not zipped
			try {
				BufferedWriter bos = new BufferedWriter(new FileWriter(outfile));
				for (int i = 0; i < data.size(); i++) {
					String toOutfile = data.get(i) + "\n";
					bos.write(toOutfile);
				}
				bos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}
		}
	}

}
