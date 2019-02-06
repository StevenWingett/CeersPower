package uk.ac.babraham;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Utilities {

	// Converts a float array to an int array
	public static float[] intArray2FloatArray(int[] intArray) {
		float[] floatArray = new float[intArray.length];

		for (int i = 0; i < intArray.length; i++) {
			floatArray[i] = (float) intArray[i];
		}

		return floatArray;
	}

	// Rename name to handle SeqMonk formatting
	public static String formatSeqmonkGeneName(String seqMonkGeneName) {

		seqMonkGeneName = seqMonkGeneName.replace("\"", ""); // Observing speech
																// marks in gene
																// names -
																// remove these

		String[] nameArr = seqMonkGeneName.split(",");
		String name = nameArr[0];
		String[] nameArr2 = name.split("-");
		String name2;

		if (nameArr2.length > 1) {
			String[] nameArr3 = Arrays.copyOfRange(nameArr2, 0, nameArr2.length - 1);
			name2 = String.join("-", nameArr3);
		} else {
			name2 = String.join("-", nameArr2);
		}
		return (name2);
	}

	public static HashMap<String, Boolean[]> findChicagoCommonInteractions(ArrayList<DatasetChicago> chicagoDatasets) {
		HashMap<String, Boolean[]> commonInteractionsTracker = new HashMap();
		Boolean[] initialisedArray = new Boolean[chicagoDatasets.size()];

		// Return an empty dataset as no common interactions found
		if (chicagoDatasets.size() < 2) {
			return commonInteractionsTracker;
		}

		// Set all values in the array to false
		for (int i = 0; i < initialisedArray.length; i++) {
			initialisedArray[i] = false;
		}

		// Create hashmap <String id> = Boolean[], where the array is true of
		// false for the presence of each interaction in each dataset
		for (int i = 0; i < chicagoDatasets.size(); i++) {
			DatasetChicago chicagoDataset = chicagoDatasets.get(i);
			String[] interactionsSummary = chicagoDataset.getInteractionCoordinates();
			HashMap<String, Boolean[]> interactionsToAdd = new HashMap<String, Boolean[]>(); // Add
																								// these
																								// to
																								// commonInteractionsTracker
																								// AFTER
																								// iterating
																								// through
																								// commonInteractionsTracker

			for (int j = 0; j < interactionsSummary.length; j++) {
				String myInteraction = interactionsSummary[j];
				Boolean[] updatedArray; // This may be a modified or new array
				// System.out.println(myInteraction);

				if (commonInteractionsTracker.containsKey(myInteraction)) { // Interaction
																			// encountered
																			// before
					updatedArray = commonInteractionsTracker.get(myInteraction); // Use
																					// existing
																					// array
					updatedArray[i] = true;
					commonInteractionsTracker.put(myInteraction, updatedArray);
				} else {
					updatedArray = initialisedArray.clone(); // Use new array
					updatedArray[i] = true;
					interactionsToAdd.put(myInteraction, updatedArray);
				}
			}

			// Now Add the new interactions
			for (String interactionToAdd : interactionsToAdd.keySet()) {
				commonInteractionsTracker.put(interactionToAdd, interactionsToAdd.get(interactionToAdd));
			}
		}
		return commonInteractionsTracker;
	}

}