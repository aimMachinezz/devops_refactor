package top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction;


import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator.FitnessCalculator;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator.FitnessCalculatorProvider;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveProvider {

	public static List<AbstractObjectiveFunction> provideSCHObjectives(int chromosomeLength) {
		return  ObjectiveProvider.provideSCHObjectives(
			FitnessCalculatorProvider.normalizedGeneticCodeValue(
				0,
				Math.pow(2, chromosomeLength) - 1,
				0,
				2
			)
		);
	}

	public static List<AbstractObjectiveFunction> provideSCHObjectives(FitnessCalculator fitnessCalculator) {

		List<AbstractObjectiveFunction> objectives = new ArrayList<>();

		objectives.add(new SCH_1(fitnessCalculator));
		objectives.add(new SCH_2(fitnessCalculator));

		return objectives;
	}

	public static List<AbstractObjectiveFunction> provideZDTObjectives() {

		List<AbstractObjectiveFunction> objectives = new ArrayList<>();

		objectives.add(new ZDT1_1());
		objectives.add(new ZDT1_2());

		return objectives;
	}

}
