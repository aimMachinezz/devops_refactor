package top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator;


import top.lazyr.refactor.algorithm.genetic.nsgaii.Service;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.BooleanAllele;

import java.util.stream.Collectors;

public class FitnessCalculatorProvider {

	private static final String NON_BOOLEAN_ALLELE_UNSUPPORTED = "FitnessCalculator.normalizedGeneticCodeValue does not work with "			+
																"AbstractAllele type other than BooleanAllele. If you are implementing "	+
																"a different type of AbstractAllele object use your own implementation "	+
																"of FitnessCalculator.";

	public static FitnessCalculator normalizedGeneticCodeValue(double actualMin, double actualMax, double normalizedMin, double normalizedMax) {
		return chromosome -> {

			if(!(chromosome.getAllele(0) instanceof BooleanAllele))
				throw new UnsupportedOperationException(FitnessCalculatorProvider.NON_BOOLEAN_ALLELE_UNSUPPORTED);

			return Service.getNormalizedGeneticCodeValue(
					chromosome.getGeneticCode().stream().map(e -> (BooleanAllele) e).collect(Collectors.toList()),
					actualMin,
					actualMax,
					normalizedMin,
					normalizedMax
			);
		};
	}
}
