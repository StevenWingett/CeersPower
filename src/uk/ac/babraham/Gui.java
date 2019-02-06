package uk.ac.babraham;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.tree.TreePath;

public class Gui extends JFrame implements ActionListener {

	// GUI variables
	String infile;
	String outfile;
	JTextArea dialogBox;
	DataTree guiDataTree = new DataTree();
	JPanel graphPanel;

	public Gui() {

		// Classes for menu item listeners
		class plotHicDistancesListener implements ActionListener {

			public void actionPerformed(ActionEvent event) {
				// Determine the selected datasets
				if (guiDataTree.tree.getSelectionPaths() == null) {
					displayErrorMessage();
					return;
				}

				int countSelectedNodes = guiDataTree.tree.getSelectionCount();
				String nodeInfo = guiDataTree.tree.getSelectionPath().toString();
				String[] nodeInfoElements = nodeInfo.split(", ");

				if ((countSelectedNodes == 1) && (nodeInfoElements.length == 3)) {
					String dataType = nodeInfoElements[1];
					String dataSet = nodeInfoElements[2];
					dataSet = dataSet.substring(0, (dataSet.length() - 1));

					if (dataType.equals("CHiCAGO")) { // Analyse the dataset
						// Histogram
						float[] histData = Utilities
								.intArray2FloatArray(CeersPower.allData.getDatasetChicago(dataSet).getAllDistances());

						if (histData.length > 1) { // Need at least 2 points to
													// plot a histogram
							graphPanel.removeAll();
							HistogramPanel myHist = new HistogramPanel(histData);
							graphPanel.add(myHist, BorderLayout.CENTER);
							graphPanel.setVisible(true);
							graphPanel.revalidate();
							myHist.setVisible(true);
							graphPanel.revalidate();
						}
					} else {
						displayErrorMessage();
						return;
					}
				} else {
					displayErrorMessage();
					return;
				}
			}

			public void displayErrorMessage() {
				JFrame frame = new JFrame();
				String message = "Select 1 CHiCAGO dataset";
				JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		class plotChicagoScoresOptionListener implements ActionListener {

			public void actionPerformed(ActionEvent event) {
				// Determine the selected datasets
				if (guiDataTree.tree.getSelectionPaths() == null) {
					displayErrorMessage();
					return;
				}

				int countSelectedNodes = guiDataTree.tree.getSelectionCount();
				String nodeInfo = guiDataTree.tree.getSelectionPath().toString();
				String[] nodeInfoElements = nodeInfo.split(", ");

				if ((countSelectedNodes == 1) && (nodeInfoElements.length == 3)) {
					String dataType = nodeInfoElements[1];
					String dataSet = nodeInfoElements[2];
					dataSet = dataSet.substring(0, (dataSet.length() - 1));

					if (dataType.equals("CHiCAGO")) { // Analyse the dataset
						// Histogram
						float[] histData = CeersPower.allData.getDatasetChicago(dataSet).getAllscores();

						if (histData.length > 1) { // Need at least 2 points to
													// plot a histogram
							graphPanel.removeAll();
							HistogramPanel myHist = new HistogramPanel(histData);
							graphPanel.add(myHist, BorderLayout.CENTER);
							graphPanel.setVisible(true);
							graphPanel.revalidate();
							myHist.setVisible(true);
							graphPanel.revalidate();
						}
					} else {
						displayErrorMessage();
						return;
					}
				} else {
					displayErrorMessage();
					return;
				}
			}

			public void displayErrorMessage() {
				JFrame frame = new JFrame();
				String message = "Select 1 CHiCAGO dataset";
				JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		class importChicagoSeqmonkOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				String infile = selectFileDialog();

				// Import data
				if (infile != null) {
					try {
						DatasetChicago newDataset = ImportExport.importChicagoSeqmonkData(infile);
						CeersPower.allData.addDatasetChicago(newDataset);
						String newChicagoDatasetName = newDataset.getName();
						guiDataTree.addNode("CHiCAGO", newChicagoDatasetName);
					} catch (IOException e) {
						JFrame frame = new JFrame("JOptionPane showMessageDialog example");
						JOptionPane.showMessageDialog(frame, "Could not read " + infile, "Import Error",
								JOptionPane.ERROR_MESSAGE);
					} catch (NumberFormatException nf) {
						JFrame frame = new JFrame("JOptionPane showMessageDialog example");
						JOptionPane.showMessageDialog(frame, "Could not read " + infile, "Import Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		class ChicagoOverlapsOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {

				// Determine the selected datasets
				ArrayList<String> selectedChicago = new ArrayList<String>();
				boolean parametersOK = true;

				TreePath[] treePaths = guiDataTree.tree.getSelectionPaths();

				if (treePaths != null) { // Check datasets have been selected
					for (int i = 0; i < treePaths.length; i++) {
						String nodeInfo = treePaths[i].toString();

						System.out.println("NodeInfo: " + nodeInfo);
						String[] nodeInfoElements = nodeInfo.split(", ");
						System.out.println(nodeInfoElements.length);

						if (nodeInfoElements.length < 3) { // Check this is a
															// file on the tree
							parametersOK = false;
							break;
						}

						String dataType = nodeInfoElements[1];
						String dataSet = nodeInfoElements[2];
						dataSet = dataSet.substring(0, (dataSet.length() - 1));

						if (dataType.equals("CHiCAGO")) {
							selectedChicago.add(dataSet);
						} else {
							parametersOK = false; // This is not a CHiCAGO
													// dataset
						}
					}
				}

				if ((parametersOK == true) && (selectedChicago.size() > 1)) {
					dialogBox.append("\nCalculating overlaps between CHiCAGO datasets");

					JFrame frame = new JFrame();
					String message = "Ensure the datasets have the same genome assembly and HiC restriction enzyme";
					JOptionPane.showMessageDialog(frame, message);

					// Determine which interactions are found in which datasets
					HashMap<String, Boolean[]> commonInteractionsTracker = Utilities
							.findChicagoCommonInteractions(CeersPower.allData.getDatasetsChicago(selectedChicago));

					// Identify interactions common to all datasets and create
					// UpSet dataset
					int numberDatasetsProcessed = selectedChicago.size();
					ArrayList<String> interactionsCommonAllDatasets = new ArrayList<String>();
					String[] upsetData = new String[numberDatasetsProcessed];
					String upsetHeaderString = "Interaction";

					ArrayList<String> upsetResults = new ArrayList<String>();
					String delim = ";"; // The delimiter character to use

					for (int i = 0; i < numberDatasetsProcessed; i++) {
						upsetData[i] = selectedChicago.get(i); // Initialise
																// each row of
																// dataset
						upsetHeaderString = upsetHeaderString + delim + selectedChicago.get(i);
					}

					upsetResults.add(upsetHeaderString);

					for (String interaction : commonInteractionsTracker.keySet()) {
						String upsetDataString = interaction;
						Boolean[] interactionResults = commonInteractionsTracker.get(interaction);
						int trueCount = 0;
						for (int i = 0; i < interactionResults.length; i++) {
							if (interactionResults[i]) {
								upsetDataString = upsetDataString + delim + "1";
								trueCount++;
							} else {
								upsetDataString = upsetDataString + delim + "0";
							}
						}

						upsetResults.add(upsetDataString);

						if (trueCount == numberDatasetsProcessed) {
							interactionsCommonAllDatasets.add(interaction);
						}
					}

					// Write the UpSet dataset to a file
					String outputFilename = saveFileDialog();
					ImportExport.writeArrayOutfile(outputFilename, upsetResults);

					// Summarise the results in the dialog box
					message = "";
					for (String myChicago : selectedChicago) {
						message = "CHiCAGO Dataset: " + CeersPower.allData.getDatasetChicago(myChicago).getName();
						message = message + "\t" + CeersPower.allData.getDatasetChicago(myChicago).getInteractions()
								+ " interactions\n";
					}
					message = message + "Common to all " + selectedChicago.size() + " datasets: "
							+ interactionsCommonAllDatasets.size() + " interactions.\n";
					message = message + "Import results into UpSet as semi-colon separated data:\n";
					message = message + "https://gehlenborglab.shinyapps.io/upsetr/\n";
					dialogBox.append(message);

				} else {
					JFrame frame = new JFrame();
					String message = "Select at least two CHiCAGO datasets, but not other data types";
					JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		}

		class filterDatasetFragmentListOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {

				// Determine the selected datasets
				int selectedChicagoCount = 0;
				int selectedFragmentListCount = 0;
				String selectedChicago = "";
				String selectedFragmentList = "";

				TreePath[] treePaths = guiDataTree.tree.getSelectionPaths();

				if (treePaths != null) { // Check datasets have been selected
					for (int i = 0; i < treePaths.length; i++) {
						String nodeInfo = treePaths[i].toString();

						System.out.println("NodeInfo: " + nodeInfo);
						String[] nodeInfoElements = nodeInfo.split(", ");

						if (nodeInfoElements.length < 3) { // Check Fragment
															// list and CHiCAGO
															// file added
							break;
						}

						String dataType = nodeInfoElements[1];
						String dataSet = nodeInfoElements[2];
						dataSet = dataSet.substring(0, (dataSet.length() - 1));

						if (dataType.equals("CHiCAGO")) {
							selectedChicagoCount++;
							selectedChicago = dataSet;
						} else if (dataType.equals("FragmentsLists")) {
							selectedFragmentListCount++;
							selectedFragmentList = dataSet;
						}
					}
				}

				if ((selectedChicagoCount == 1) && (selectedFragmentListCount == 1)) {
					dialogBox.append("\nFiltering CHiCAGO dataset " + selectedChicago + " with fragment list "
							+ selectedFragmentList);

					// Filter the data
					DatasetChicago filteredData = CeersPower.allData.getDatasetChicago(selectedChicago)
							.filterByFragments(CeersPower.allData.getDatasetFragmentList(selectedFragmentList));

					if (filteredData.getInteractions() > 0) { // Add the new
																// filtered
																// dataset
						guiDataTree.addNode("CHiCAGO", filteredData.getName());
						CeersPower.allData.addDatasetChicago(filteredData);
					} else {
						JFrame frame = new JFrame();
						String message = "The filtered data returned no interactions!";
						JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JFrame frame = new JFrame();
					String message = "Select a Gene List and a CHiCAGO dataset";
					JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		}

		class filterDatasetGeneListOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {

				// Determine the selected datasets
				int selectedChicagoCount = 0;
				int selectedGeneListCount = 0;
				String selectedChicago = "";
				String selectedGeneList = "";

				TreePath[] treePaths = guiDataTree.tree.getSelectionPaths();

				if (treePaths != null) { // Check datasets have been selected
					for (int i = 0; i < treePaths.length; i++) {
						String nodeInfo = treePaths[i].toString();

						System.out.println("NodeInfo: " + nodeInfo);
						String[] nodeInfoElements = nodeInfo.split(", ");

						if (nodeInfoElements.length < 3) { // Check Gene list
															// and CHiCAGO file
															// added
							break;
						}

						String dataType = nodeInfoElements[1];
						String dataSet = nodeInfoElements[2];
						dataSet = dataSet.substring(0, (dataSet.length() - 1));

						if (dataType.equals("CHiCAGO")) {
							selectedChicagoCount++;
							selectedChicago = dataSet;
						} else if (dataType.equals("GeneLists")) {
							selectedGeneListCount++;
							selectedGeneList = dataSet;
						}
					}
				}

				if ((selectedChicagoCount == 1) && (selectedGeneListCount == 1)) {
					dialogBox.append(
							"\nFiltering CHiCAGO dataset " + selectedChicago + " with gene list " + selectedGeneList);

					// Filter the data
					DatasetChicago filteredData = CeersPower.allData.getDatasetChicago(selectedChicago)
							.filterByGene(CeersPower.allData.getDatasetGeneList(selectedGeneList));

					if (filteredData.getInteractions() > 0) { // Add the new
																// filtered
																// dataset
						guiDataTree.addNode("CHiCAGO", filteredData.getName());
						CeersPower.allData.addDatasetChicago(filteredData);
					} else {
						JFrame frame = new JFrame();
						String message = "The filtered data returned no interactions!";
						JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JFrame frame = new JFrame();
					String message = "Select a Gene List and a CHiCAGO dataset";
					JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		}

		class importGeneListOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				String infile = selectFileDialog();

				if (infile != null) {
					try {
						GeneList newGeneList = ImportExport.importGeneListData(infile);
						CeersPower.allData.addDatasetGeneList(newGeneList);
						guiDataTree.addNode("GeneLists", newGeneList.getName());

					} catch (IOException e) {
						JFrame frame = new JFrame("JOptionPane showMessageDialog example");
						JOptionPane.showMessageDialog(frame, "Could not read " + infile, "Import Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		class importFragmentListOptionListener implements ActionListener {

			public void actionPerformed(ActionEvent event) {
				String infile = selectFileDialog();

				if (infile != null) {
					try {
						DatasetFragments newDatasetFragments = ImportExport.importFragmentListData(infile);
						CeersPower.allData.addDatasetFragments(newDatasetFragments);
						guiDataTree.addNode("FragmentsLists", newDatasetFragments.getName());

					} catch (IOException e) {
						JFrame frame = new JFrame("JOptionPane showMessageDialog example");
						JOptionPane.showMessageDialog(frame, "Could not read " + infile, "Import Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}

		}

		class importChicagoMatrixOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				String infile = selectFileDialog();

				// Import data
				if (infile != null) {
					try {
						DatasetChicago[] newDatasets = ImportExport.importChicagoData(infile);

						for (int i = 0; i < newDatasets.length; i++) {
							CeersPower.allData.addDatasetChicago(newDatasets[i]);
							String newChicagoDatasetName = newDatasets[i].getName();

							// Add new dataset to JTree
							guiDataTree.addNode("CHiCAGO", newChicagoDatasetName);
						}

					} catch (IOException e) {
						JFrame frame = new JFrame("JOptionPane showMessageDialog example");
						JOptionPane.showMessageDialog(frame, "Could not read " + infile, "Import Error",
								JOptionPane.ERROR_MESSAGE);
					} catch (NumberFormatException nf) {
						JFrame frame = new JFrame("JOptionPane showMessageDialog example");
						JOptionPane.showMessageDialog(frame, "Could not read " + infile, "Import Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		class aboutOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				JFrame frame = new JFrame();
				String version = CeersPower.getVersion();
				String message = "CeersPower v" + version
						+ "\n(C) 2018 Steven Wingett, Simon Andrews, The Babraham Institute, Cambridge, UK\n";
				JOptionPane.showMessageDialog(frame, message, "Ceer's Power", JOptionPane.PLAIN_MESSAGE);
			}
		}

		class datasetStatisticsOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				// Determine the selected datasets
				if (guiDataTree.tree.getSelectionPaths() == null) {
					JFrame frame = new JFrame();
					String message = "Select 1 dataset";
					JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				int countSelectedNodes = guiDataTree.tree.getSelectionCount();
				String nodeInfo = guiDataTree.tree.getSelectionPath().toString();
				String[] nodeInfoElements = nodeInfo.split(", ");

				if ((countSelectedNodes == 1) && (nodeInfoElements.length == 3)) {
					String dataType = nodeInfoElements[1];
					String dataSet = nodeInfoElements[2];
					dataSet = dataSet.substring(0, (dataSet.length() - 1));
					System.out.println(dataType);
					System.out.println(dataSet);

					if (dataType.equals("CHiCAGO")) { // Analyse the dataset
						dialogBox.append("\n" + CeersPower.allData.getDatasetChicago(dataSet).getSummary());
					} else if (dataType.equals("GeneLists")) {
						dialogBox.append("\n" + CeersPower.allData.getDatasetGeneList(dataSet).getSummary());
					} else if (dataType.equals("FragmentsLists")) {
						dialogBox.append("\n" + CeersPower.allData.getDatasetFragmentList(dataSet).getSummary());
					}

				} else {
					JFrame frame = new JFrame();
					String message = "Select 1 dataset";
					JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		class exportWashUOptionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				// Determine the selected datasets
				boolean parametersOkFlag = false;

				if (guiDataTree.tree.getSelectionPaths() != null) { // Check
																	// datasets
																	// have been
																	// selected)

					int countSelectedNodes = guiDataTree.tree.getSelectionCount();
					String nodeInfo = guiDataTree.tree.getSelectionPath().toString();
					String[] nodeInfoElements = nodeInfo.split(", ");

					if ((countSelectedNodes == 1) && (nodeInfoElements.length == 3)) {
						String dataType = nodeInfoElements[1];
						String dataSet = nodeInfoElements[2];
						dataSet = dataSet.substring(0, (dataSet.length() - 1));

						if (dataType.equals("CHiCAGO")) {
							parametersOkFlag = true;

							// Open output file
							String outputFilename = saveFileDialog();

							// Write WashU text to file
							ImportExport.writeArrayOutfile(outputFilename,
									CeersPower.allData.getDatasetChicago(dataSet).convertWashU());
						}
					}
				}

				if (parametersOkFlag == false) {
					JFrame frame = new JFrame();
					String message = "Select 1 CHiCAGO dataset";
					JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		// The GUI layout
		// Build the menu bar
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menuBar = new JMenuBar();

		JMenu menuImport = new JMenu("Import");
		JMenuItem importChicagoMatrix = new JMenuItem("CHiCAGO Peak Matrix");
		menuImport.add(importChicagoMatrix);
		importChicagoMatrix.addActionListener(new importChicagoMatrixOptionListener());
		JMenuItem importChicagoSeqmonk = new JMenuItem("CHiCAGO SeqMonk Format File");
		menuImport.add(importChicagoSeqmonk);
		importChicagoSeqmonk.addActionListener(new importChicagoSeqmonkOptionListener());
		JMenuItem importGeneList = new JMenuItem("Gene List");
		menuImport.add(importGeneList);
		importGeneList.addActionListener(new importGeneListOptionListener());
		JMenuItem importFragmentList = new JMenuItem("Fragment List");
		importFragmentList.addActionListener(new importFragmentListOptionListener());
		menuImport.add(importFragmentList);

		JMenu menuAnalysis = new JMenu("Analysis");
		JMenuItem datasetStatistics = new JMenuItem("Dataset Statistics");
		JMenuItem datasetOverlaps = new JMenuItem("Find common interactions");
		menuAnalysis.add(datasetStatistics);
		menuAnalysis.add(datasetOverlaps);

		JMenu menuPlot = new JMenu("Plot");
		JMenuItem datasetPlotHicDistances = new JMenuItem("Plot HiC Distance Histogram");
		JMenuItem datasetPlotScores = new JMenuItem("Plot CHiCAGO Scores Histogram");
		menuPlot.add(datasetPlotHicDistances);
		menuPlot.add(datasetPlotScores);

		JMenu menuFilter = new JMenu("Filter");
		JMenuItem filterDatasetGeneList = new JMenuItem("Filter data with gene list");
		JMenuItem filterDatasetFragmentList = new JMenuItem("Filter data with fragment list");
		menuFilter.add(filterDatasetGeneList);
		menuFilter.add(filterDatasetFragmentList);

		datasetStatistics.addActionListener(new datasetStatisticsOptionListener());
		filterDatasetGeneList.addActionListener(new filterDatasetGeneListOptionListener());
		filterDatasetFragmentList.addActionListener(new filterDatasetFragmentListOptionListener());
		datasetOverlaps.addActionListener(new ChicagoOverlapsOptionListener());
		datasetPlotHicDistances.addActionListener(new plotHicDistancesListener());
		datasetPlotScores.addActionListener(new plotChicagoScoresOptionListener());

		JMenu menuExport = new JMenu("Export");
		JMenuItem exportWashU = new JMenuItem("WashU Browser");
		menuExport.add(exportWashU);
		exportWashU.addActionListener(new exportWashUOptionListener());

		JMenu menuHelp = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		menuHelp.add(about);

		about.addActionListener(new aboutOptionListener());

		menuBar.add(menuImport);
		menuBar.add(menuAnalysis);
		menuBar.add(menuPlot);
		menuBar.add(menuFilter);
		menuBar.add(menuExport);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);

		// Tools
		JPanel toolBox = new JPanel();
		this.getContentPane().add(BorderLayout.NORTH, toolBox);

		// Sliders
		JPanel sliders = new JPanel();
		GridLayout sliderLayout = new GridLayout(0, 1);
		sliders.setLayout(sliderLayout);

		this.getContentPane().add(BorderLayout.EAST, sliders);

		// Dialog display
		dialogBox = new JTextArea(20, 20);
		dialogBox.setEditable(false);
		JScrollPane dialogScrollPane = new JScrollPane(dialogBox);
		this.getContentPane().add(BorderLayout.SOUTH, dialogScrollPane);

		// JTrees
		this.getContentPane().add(BorderLayout.WEST, guiDataTree);

		this.setSize(800, 800);
		this.setVisible(true);

		// Graphs
		graphPanel = new JPanel();
		graphPanel.setLayout(new BorderLayout());
		this.getContentPane().add(BorderLayout.CENTER, graphPanel);
		graphPanel.setVisible(true);

	}

	// GUI methods
	public String selectFileDialog() {
		// Choose file
		String fileName;
		final JFileChooser fc = new JFileChooser();
		int response = fc.showOpenDialog(null);
		if (response == JFileChooser.APPROVE_OPTION) {
			fileName = fc.getSelectedFile().toString();
			System.out.println(fileName);
		} else {
			fileName = null;
		}
		return fileName;
	}

	public String saveFileDialog() {
		// Choose file
		String fileName;
		final JFileChooser fc = new JFileChooser();
		int response = fc.showSaveDialog(null);
		if (response == JFileChooser.APPROVE_OPTION) {
			fileName = fc.getSelectedFile().toString();
		} else {
			fileName = "The file open operation was cancelled.";
		}
		return fileName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
