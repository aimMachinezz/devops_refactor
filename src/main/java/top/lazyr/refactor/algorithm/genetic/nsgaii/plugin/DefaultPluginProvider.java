package top.lazyr.refactor.algorithm.genetic.nsgaii.plugin;


import top.lazyr.refactor.algorithm.genetic.nsgaii.Service;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.childpopulationproducer.ChildPopulationProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.populationproducer.PopulationProducer;

import java.util.ArrayList;
import java.util.List;

public class DefaultPluginProvider {

	public static PopulationProducer defaultPopulationProducer() {
		return (populationSize, chromosomeLength, geneticCodeProducer, fitnessCalculator) -> {

			List<Chromosome> populace = new ArrayList<>();

			for(int i = 0; i < populationSize; i++)
				populace.add(
					new Chromosome(
						geneticCodeProducer.produce(chromosomeLength)
					)
				);

			return new Population(populace);
		};
	}

	public static ChildPopulationProducer defaultChildPopulationProducer() {
		return (parentPopulation, crossover, mutation, populationSize) -> {

			List<Chromosome> populace = new ArrayList<>();

			while(populace.size() < populationSize)
				if((populationSize - populace.size()) == 1)
					populace.add(
						mutation.perform(
							Service.crowdedBinaryTournamentSelection(parentPopulation)
						)
					);
				else
					for(Chromosome chromosome : crossover.perform(parentPopulation))
						populace.add(mutation.perform(chromosome));

			return new Population(populace);
		};
	}
}
