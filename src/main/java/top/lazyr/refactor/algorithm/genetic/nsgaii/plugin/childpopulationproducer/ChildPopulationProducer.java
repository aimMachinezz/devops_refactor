package top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.childpopulationproducer;


import top.lazyr.refactor.algorithm.genetic.nsgaii.crossover.AbstractCrossover;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;
import top.lazyr.refactor.algorithm.genetic.nsgaii.mutation.AbstractMutation;

/**
 * The ChildPopulationProducer interface is used to produce a population of chromosomes for the algorithm to use.
 * It has only one method, `produce()` that produces a child population from a provided parent population. For every child
 * population, a fixed amount of chromosomes is created by executing the provided `crossover` and `mutation` operators on the parent
 * population. This interface is used to generate every child population at each generation. Note that the initial parent population
 * is generated using the `PopulationProducer` interface.
 */
@FunctionalInterface
public interface ChildPopulationProducer {
	/**
	 * This method generates a child `Population`, which is a collection of `Chromosomes` equal to provided `populationSize`, from the
	 * given `parentPopulation`. The child chromosomes are created by executing the provided `crossover` and `mutation` operators on
	 * the `parentPopulation`. A new `Population` is hence created.
	 * @param parentPopulation the population from which the child population is to be generated.
	 * @param crossover the crossover operator which is a concrete implementation of `AbstractCrossover`.
	 * @param mutation the mutation operator which is a concrete implementation of `AbstractMutation`.
	 * @param populationSize the size of the population, i.e., the number of child chromosomes to be created.
	 * @return a new child `Population`, with `populationSize` number of chromosomes.
	 */
	Population produce(Population parentPopulation, AbstractCrossover crossover, AbstractMutation mutation, int populationSize);
}
