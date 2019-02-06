package uk.ac.babraham;

import java.awt.Component;
import java.awt.FontMetrics;
import java.text.DecimalFormat;

public class AxisScale {

	private double min;
	private double max;
	private double starting;
	private double interval;
	private DecimalFormat df = null;

	private int xSpace;

	public AxisScale(double min, double max, Component comp) {

		if (Double.isNaN(min) || Double.isInfinite(min)) {
			throw new IllegalArgumentException("Min value in axis scale was " + min + " that's going to end badly");
		}

		if (Double.isNaN(max) || Double.isInfinite(max)) {
			throw new IllegalArgumentException("Max value in axis scale was " + max + " that's going to end badly");
		}

		this.min = min;
		this.max = max;

		if (max <= min) {
			starting = min;
			interval = 1;
			return;
		}

		double base = 1;

		while (base > (max - min)) {
			base /= 10;
		}

		double[] divisions = new double[] { 0.1, 0.2, 0.25, 0.5 };

		OUTER: while (true) {

			for (int d = 0; d < divisions.length; d++) {
				double tester = base * divisions[d];
				if (((max - min) / tester) <= 10) {
					interval = tester;
					break OUTER;
				}
			}

			base *= 10;

		}

		// Now we work out the first value to be plotted
		int basicDivision = (int) (min / interval);

		double testStart = basicDivision * interval;

		if (testStart < min) {
			testStart += interval;
		}

		starting = testStart;

		// Finally we work out the width needed to draw the numbers for this
		// scale. We can't tell which is largest so we measure them all.
		FontMetrics metrics = comp.getGraphics().getFontMetrics();
		xSpace = 0;

		double current = getStartingValue();

		while (current <= getMax()) {
			int width = metrics.stringWidth(format(current));
			if (width > xSpace)
				xSpace = width;
			current += getInterval();
		}
	}

	public String format(double number) {
		if (df == null) {
			if (interval == (int) interval) {
				df = new DecimalFormat("#");
			} else {
				String stringInterval = "" + interval;
				// Find the number of decimal places
				int dp = stringInterval.length() - (stringInterval.indexOf(".") + 1);
				StringBuffer formatBuffer = new StringBuffer();
				formatBuffer.append("#.");
				for (int i = 0; i < dp; i++) {
					formatBuffer.append("#");
				}
				df = new DecimalFormat(formatBuffer.toString());
			}
		}

		return df.format(number);
	}

	public double getStartingValue() {
		return starting;
	}

	public double getInterval() {
		return interval;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public int getXSpaceNeeded() {
		return xSpace;
	}

}
