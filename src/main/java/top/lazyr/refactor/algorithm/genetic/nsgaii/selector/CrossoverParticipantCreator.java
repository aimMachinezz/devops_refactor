package top.lazyr.refactor.algorithm.genetic.nsgaii.selector;

import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;

import java.util.List;

@FunctionalInterface
public interface CrossoverParticipantCreator {
	List<Chromosome> create(Population population);
}
