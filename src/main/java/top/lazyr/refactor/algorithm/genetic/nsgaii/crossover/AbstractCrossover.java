package top.lazyr.refactor.algorithm.genetic.nsgaii.crossover;



import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;
import top.lazyr.refactor.algorithm.genetic.nsgaii.selector.CrossoverParticipantCreator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lazyr
 * @created 2022/1/30
 */
public abstract class AbstractCrossover {
    protected String crossoverTitle = "默认交叉算子";

    protected final CrossoverParticipantCreator crossoverParticipantCreator;

    protected float crossoverProbability = 0.7f;


    public AbstractCrossover(CrossoverParticipantCreator crossoverParticipantCreator) {
        this.crossoverParticipantCreator = crossoverParticipantCreator;
    }

    public abstract List<Chromosome> perform(Population population);

    public boolean shouldPerformCrossover() {
        return ThreadLocalRandom.current().nextFloat() <= this.crossoverProbability;
    }

    @Override
    public String toString() {
        return crossoverTitle;
    }
}
