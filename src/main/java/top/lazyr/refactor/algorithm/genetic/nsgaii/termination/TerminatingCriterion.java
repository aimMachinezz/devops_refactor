package top.lazyr.refactor.algorithm.genetic.nsgaii.termination;


import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;

@FunctionalInterface
public interface TerminatingCriterion {
	boolean shouldRun(Population population, int generationCount, int maxGenerations);
}
