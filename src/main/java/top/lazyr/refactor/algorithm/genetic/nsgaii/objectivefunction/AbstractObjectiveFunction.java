package top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction;


import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator.FitnessCalculator;

public abstract class AbstractObjectiveFunction {

	protected String objectiveFunctionTitle = "Objective Title Not Implemented";
	protected FitnessCalculator fitnessCalculator;

	public AbstractObjectiveFunction() {}

	public AbstractObjectiveFunction(FitnessCalculator fitnessCalculator) {
		this.fitnessCalculator = fitnessCalculator;
	}

	public abstract double getValue(Chromosome chromosome);

	public String getObjectiveTitle() {
		return this.objectiveFunctionTitle;
	}

	@Override
	public String toString() {
		return this.objectiveFunctionTitle;
	}
}
