package top.lazyr.refactor.algorithm.genetic.nsgaii;



import top.lazyr.refactor.algorithm.genetic.nsgaii.crossover.AbstractCrossover;
import top.lazyr.refactor.algorithm.genetic.nsgaii.crossover.UniformCrossover;
import top.lazyr.refactor.algorithm.genetic.nsgaii.mutation.AbstractMutation;
import top.lazyr.refactor.algorithm.genetic.nsgaii.mutation.SinglePointMutation;
import top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction.AbstractObjectiveFunction;
import top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction.ObjectiveProvider;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.DefaultPluginProvider;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.childpopulationproducer.ChildPopulationProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator.FitnessCalculator;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator.FitnessCalculatorProvider;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.geneticcodeproducer.GeneticCodeProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.geneticcodeproducer.GeneticCodeProducerProvider;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.populationproducer.PopulationProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.selector.CrossoverParticipantCreatorProvider;
import top.lazyr.refactor.algorithm.genetic.nsgaii.termination.TerminatingCriterion;
import top.lazyr.refactor.algorithm.genetic.nsgaii.termination.TerminatingCriterionProvider;

import java.util.List;

/**
 * The Configuration class is used to setup the runtime settings of the NSGA-II algorithm.
 * Since a lot of the settings within the algorithm can be changed, this class manages them all.
 * An instance of this class is needed to be setup before creating an instance of
 * NSGA2 class or running the algorithm. Changing settings of an instance of a Configuration class between
 * runs will also reflect immediately on the result. This means that, if required (but almost rarely),
 * the configuration of the algorithm within the same run can be changed dynamically.
 */
public class Configuration {

	public static final String CONFIGURATION_NOT_SETUP = "The NSGA-II configuration object is not setup properly!";
	public static final int DEFAULT_POPULATION_SIZE = 100;
	public static final int DEFAULT_GENERATIONS = 25;
	public static final int DEFAULT_CHROMOSOME_LENGTH = 20;

	public static String FITNESS_CALCULATOR_NULL = "The fitness calculation operation has not been setup. "				+
													"You need to set the AbstractObjectiveFunction#fitnessCalculator "	+
													"with an instance of FitnessCalculator!";

	public List<AbstractObjectiveFunction> objectives;

	private int populationSize;
	private int maxGenerationNum;
	private int chromosomeLength;
	private PopulationProducer populationProducer;
	private ChildPopulationProducer childPopulationProducer;
	private GeneticCodeProducer geneticCodeProducer;
	private AbstractCrossover crossover;
	private AbstractMutation mutation;
	private TerminatingCriterion terminatingCriterion;
	private FitnessCalculator fitnessCalculator;
	private String outputCatalog;
	private boolean optimizedHL;
	private boolean optimizedUD;
	private boolean optimizedCD;
	private int currentExpTime;

	public int getCurrentExpTime() {
		return currentExpTime;
	}

	public void setCurrentExpTime(int currentExpTime) {
		this.currentExpTime = currentExpTime;
	}

	public boolean isOptimizedCD() {
		return optimizedCD;
	}

	public void setOptimizedCD(boolean optimizedCD) {
		this.optimizedCD = optimizedCD;
	}

	public boolean isOptimizedHL() {
		return optimizedHL;
	}

	public void setOptimizedHL(boolean optimizedHL) {
		this.optimizedHL = optimizedHL;
	}

	public boolean isOptimizedUD() {
		return optimizedUD;
	}

	public void setOptimizedUD(boolean optimizedUD) {
		this.optimizedUD = optimizedUD;
	}

	public Configuration() {
		this(
			Configuration.DEFAULT_POPULATION_SIZE,
			Configuration.DEFAULT_GENERATIONS,
			Configuration.DEFAULT_CHROMOSOME_LENGTH
		);
	}

	public Configuration(int populationSize, int generations, int chromosomeLength) {
		this(
			populationSize,
			generations,
			chromosomeLength,
			DefaultPluginProvider.defaultPopulationProducer(),
			DefaultPluginProvider.defaultChildPopulationProducer(),
			GeneticCodeProducerProvider.binaryGeneticCodeProducer(),
			ObjectiveProvider.provideSCHObjectives(chromosomeLength),
			new UniformCrossover(CrossoverParticipantCreatorProvider.selectByBinaryTournamentSelection()),
			new SinglePointMutation(),
			TerminatingCriterionProvider.fixedTerminatingCriterion(),
			false,
			true,
			true
		);
	}

	public Configuration(PopulationProducer populationProducer) {
		this(
			Configuration.DEFAULT_POPULATION_SIZE,
			Configuration.DEFAULT_GENERATIONS,
			Configuration.DEFAULT_CHROMOSOME_LENGTH,
			populationProducer,
			DefaultPluginProvider.defaultChildPopulationProducer(),
			GeneticCodeProducerProvider.binaryGeneticCodeProducer(),
			ObjectiveProvider.provideSCHObjectives(Configuration.DEFAULT_CHROMOSOME_LENGTH),
			new UniformCrossover(CrossoverParticipantCreatorProvider.selectByBinaryTournamentSelection()),
			new SinglePointMutation(),
			TerminatingCriterionProvider.fixedTerminatingCriterion(),
			false,
			true,
			true
		);
	}

	public Configuration(ChildPopulationProducer childPopulationProducer) {
		this(
			Configuration.DEFAULT_POPULATION_SIZE,
			Configuration.DEFAULT_GENERATIONS,
			Configuration.DEFAULT_CHROMOSOME_LENGTH,
			DefaultPluginProvider.defaultPopulationProducer(),
			childPopulationProducer,
			GeneticCodeProducerProvider.binaryGeneticCodeProducer(),
			ObjectiveProvider.provideSCHObjectives(Configuration.DEFAULT_CHROMOSOME_LENGTH),
			new UniformCrossover(CrossoverParticipantCreatorProvider.selectByBinaryTournamentSelection()),
			new SinglePointMutation(),
			TerminatingCriterionProvider.fixedTerminatingCriterion(),
			false,
			true,
			true
		);
	}

	public Configuration(GeneticCodeProducer geneticCodeProducer) {
		this(
			Configuration.DEFAULT_POPULATION_SIZE,
			Configuration.DEFAULT_GENERATIONS,
			Configuration.DEFAULT_CHROMOSOME_LENGTH,
			DefaultPluginProvider.defaultPopulationProducer(),
			DefaultPluginProvider.defaultChildPopulationProducer(),
			geneticCodeProducer,
			ObjectiveProvider.provideSCHObjectives(Configuration.DEFAULT_CHROMOSOME_LENGTH),
			new UniformCrossover(CrossoverParticipantCreatorProvider.selectByBinaryTournamentSelection()),
			new SinglePointMutation(),
			TerminatingCriterionProvider.fixedTerminatingCriterion(),
			false,
			true,
			true
		);
	}

	public Configuration(TerminatingCriterion terminatingCriterion) {
		this(
			Configuration.DEFAULT_POPULATION_SIZE,
			Configuration.DEFAULT_GENERATIONS,
			Configuration.DEFAULT_CHROMOSOME_LENGTH,
			DefaultPluginProvider.defaultPopulationProducer(),
			DefaultPluginProvider.defaultChildPopulationProducer(),
			GeneticCodeProducerProvider.binaryGeneticCodeProducer(),
			ObjectiveProvider.provideSCHObjectives(Configuration.DEFAULT_CHROMOSOME_LENGTH),
			new UniformCrossover(CrossoverParticipantCreatorProvider.selectByBinaryTournamentSelection()),
			new SinglePointMutation(),
			terminatingCriterion,
			false,
			true,
			true
		);
	}

	public Configuration(List<AbstractObjectiveFunction> objectives) {
		this(
			Configuration.DEFAULT_POPULATION_SIZE,
			Configuration.DEFAULT_GENERATIONS,
			Configuration.DEFAULT_CHROMOSOME_LENGTH,
			DefaultPluginProvider.defaultPopulationProducer(),
			DefaultPluginProvider.defaultChildPopulationProducer(),
			GeneticCodeProducerProvider.binaryGeneticCodeProducer(),
			objectives,
			new UniformCrossover(CrossoverParticipantCreatorProvider.selectByBinaryTournamentSelection()),
			new SinglePointMutation(),
			TerminatingCriterionProvider.fixedTerminatingCriterion(),
			false,
			true,
			true
		);
	}

	public Configuration(FitnessCalculator fitnessCalculator) {
		this(
			Configuration.DEFAULT_POPULATION_SIZE,
			Configuration.DEFAULT_GENERATIONS,
			Configuration.DEFAULT_CHROMOSOME_LENGTH,
			DefaultPluginProvider.defaultPopulationProducer(),
			DefaultPluginProvider.defaultChildPopulationProducer(),
			GeneticCodeProducerProvider.binaryGeneticCodeProducer(),
			ObjectiveProvider.provideSCHObjectives(Configuration.DEFAULT_CHROMOSOME_LENGTH),
			new UniformCrossover(CrossoverParticipantCreatorProvider.selectByBinaryTournamentSelection()),
			new SinglePointMutation(),
			TerminatingCriterionProvider.fixedTerminatingCriterion(),
			false,
			true,
			true,
			fitnessCalculator
		);
	}

	public Configuration(int populationSize,
						 int generations,
						 int chromosomeLength,
						 PopulationProducer populationProducer,
						 ChildPopulationProducer childPopulationProducer,
						 GeneticCodeProducer geneticCodeProducer,
						 List<AbstractObjectiveFunction> objectives,
						 AbstractCrossover crossover,
						 AbstractMutation mutation,
						 TerminatingCriterion terminatingCriterion,
						 boolean silent,
						 boolean plotGraph,
						 boolean writeToDisk) {

		this.populationSize = populationSize;
		this.maxGenerationNum = generations;
		this.chromosomeLength = chromosomeLength;
		this.populationProducer = populationProducer;
		this.childPopulationProducer = childPopulationProducer;
		this.geneticCodeProducer = geneticCodeProducer;
		this.objectives = objectives;
		this.crossover = crossover;
		this.mutation = mutation;
		this.terminatingCriterion = terminatingCriterion;
	}

	public Configuration(int populationSize,
						 int generations,
						 int chromosomeLength,
						 PopulationProducer populationProducer,
						 ChildPopulationProducer childPopulationProducer,
						 GeneticCodeProducer geneticCodeProducer,
						 List<AbstractObjectiveFunction> objectives,
						 AbstractCrossover crossover,
						 AbstractMutation mutation,
						 TerminatingCriterion terminatingCriterion,
						 boolean silent,
						 boolean plotGraph,
						 boolean writeToDisk,
						 FitnessCalculator fitnessCalculator) {

		this(populationSize,
				generations,
				chromosomeLength,
				populationProducer,
				childPopulationProducer,
				geneticCodeProducer,
				objectives,
				crossover,
				mutation,
			terminatingCriterion,
				silent,
				plotGraph,
				writeToDisk);
		this.fitnessCalculator = fitnessCalculator;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		if(populationSize < 1)
			throw new UnsupportedOperationException("Population size cannot be less than 1.");
		this.populationSize = populationSize;
	}

	public int getMaxGenerations() {
		return maxGenerationNum;
	}

	public void setMaxGenerations(int generations) {
		if(generations < 1)
			throw new UnsupportedOperationException("Generations cannot be less than 1.");
		this.maxGenerationNum = generations;
	}

	public int getChromosomeLength() {
		return chromosomeLength;
	}

	public void setChromosomeLength(int chromosomeLength) {
		if(chromosomeLength < 1)
			throw new UnsupportedOperationException("Chromosome length cannot be less than 1.");
		this.chromosomeLength = chromosomeLength;
	}

	public PopulationProducer getPopulationProducer() {
		return populationProducer;
	}

	public void setPopulationProducer(PopulationProducer populationProducer) {
		this.populationProducer = populationProducer;
	}

	public ChildPopulationProducer getChildPopulationProducer() {
		return childPopulationProducer;
	}

	public void setChildPopulationProducer(ChildPopulationProducer childPopulationProducer) {
		this.childPopulationProducer = childPopulationProducer;
	}

	public GeneticCodeProducer getGeneticCodeProducer() {
		return geneticCodeProducer;
	}

	public void setGeneticCodeProducer(GeneticCodeProducer geneticCodeProducer) {
		this.geneticCodeProducer = geneticCodeProducer;
	}

	public AbstractCrossover getCrossover() {
		return crossover;
	}

	public void setCrossover(AbstractCrossover crossover) {
		this.crossover = crossover;
	}

	public AbstractMutation getMutation() {
		return mutation;
	}

	public void setMutation(AbstractMutation mutation) {
		this.mutation = mutation;
	}

	public TerminatingCriterion getTerminatingCriterion() {
		return terminatingCriterion;
	}

	public void setTerminatingCriterion(TerminatingCriterion terminatingCriterion) {
		this.terminatingCriterion = terminatingCriterion;
	}

	public FitnessCalculator getFitnessCalculator() {

		if(this.fitnessCalculator == null)
			this.fitnessCalculator = FitnessCalculatorProvider.normalizedGeneticCodeValue(
				0,
				Math.pow(2, this.chromosomeLength) - 1,
				0,
				2
			);

		return this.fitnessCalculator;
	}

	public void setFitnessCalculator(FitnessCalculator fitnessCalculator) {
		this.fitnessCalculator = fitnessCalculator;
	}

	public boolean isSetup() {
		return (
			this.populationSize != 0 				&&
			this.maxGenerationNum != 0 					&&
			this.chromosomeLength != 0 				&&
			this.populationProducer != null 		&&
			this.childPopulationProducer != null	&&
			this.geneticCodeProducer != null 		&&
			this.crossover != null					&&
			this.mutation != null					&&
			this.terminatingCriterion != null			&&
			this.objectives != null		&&
			!this.objectives.isEmpty()
		);
	}

	public void setOutputCatalog(String chartOutputCatalog) {
		this.outputCatalog = chartOutputCatalog;
	}


	@Override
	public String toString() {
		return "Population Size: " 																				+
				this.populationSize 																				+
				"\nGenerations: " 																					+
				this.maxGenerationNum +
				"\nChromosome Length: " 																			+
				this.chromosomeLength				 																+
				"\nPopulation Producer: " 																			+
				this.populationProducer.toString() 																								+
				"\nChild Population Producer: " 																	+
				this.childPopulationProducer.toString()																								+
				"\nGenetic Code Producer: " 																		+
				this.geneticCodeProducer.toString()																								+
				"\nObjectives: " 																					+
				this.objectives.toString() 																								+
				"\nCrossover Operator: " 																			+
				this.crossover.toString() 																								+
				"\nMutation Operator: " 																			+
				this.mutation.toString() 																								+
				"\nGeneration Driver: " 																			+
				this.terminatingCriterion.toString() 																								+
				"\nFitness Calculator: " 																			+
				(this.fitnessCalculator != null ? this.fitnessCalculator.toString() : "无")                         +
				"\nExperimental data output directory: " + this.outputCatalog;
	}

	public String getOutputCatalog() {
		return outputCatalog;
	}
}
