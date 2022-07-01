package top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator;


import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;

@FunctionalInterface
public interface FitnessCalculator {
	double calculate(Chromosome chromosome);
}
