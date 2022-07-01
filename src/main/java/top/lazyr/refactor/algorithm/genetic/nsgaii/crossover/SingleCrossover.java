package top.lazyr.refactor.algorithm.genetic.nsgaii.crossover;


import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.AbstractAllele;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.RefactorChromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;
import top.lazyr.refactor.algorithm.genetic.nsgaii.selector.CrossoverParticipantCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 单点交叉
 * - 若交叉，则只返回一个子代
 * - 若不交叉，则返回两个父本副本
 * @author lazyr
 * @created 2022/1/29
 */
public class SingleCrossover extends AbstractCrossover {

    public SingleCrossover(CrossoverParticipantCreator crossoverParticipantCreator, float crossoverProbability) {
        super(crossoverParticipantCreator);
        this.crossoverProbability = crossoverProbability;
        this.crossoverTitle = "单点交叉算子";
    }

    /**
     * 若进行交叉，则返回一个子代(子代initGraph已经有初始值，表现型为null)
     * 若不进行交叉，则返回两个父代的副本(两个父代副本initGraph已经有初始值，表现型为null)
     * @param population
     * @return
     */
    @Override
    public List<Chromosome> perform(Population population) {
        List<Chromosome> children = new ArrayList<>();
        List<Chromosome> parents = this.crossoverParticipantCreator.create(population);

        if (this.shouldPerformCrossover()) {
//            System.out.println("交叉");
            children.add(crossover(parents.get(0), parents.get(1)));
        } else {
            children.add(parents.get(0).getCopy());
            children.add(parents.get(1).getCopy());
        }
        return children;
    }

    /**
     * parent1和parent2交叉，返回一个子代(此时还表现型为null)
     * @param parent1
     * @param parent2
     * @return
     */
    private Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        // 获取交叉点，1 <= point < parent1.getLength() - 1
        int point = ThreadLocalRandom.current().nextInt(1, parent1.getLength() - 1);
        // 获取交叉基因
        List<AbstractAllele> childAlleles = new ArrayList<>();
        if (ThreadLocalRandom.current().nextDouble() <= 0.5) { // 结合parent1的上半部分和parent2的下半部分
            fillAlleles(0, point, childAlleles, parent1);
            fillAlleles(point, parent2.getLength(), childAlleles, parent2);
        } else { // 结合parent2的上半部分和parent1的下半部分
            fillAlleles(0, point, childAlleles, parent2);
            fillAlleles(point, parent1.getLength(), childAlleles, parent1);
        }
        return new RefactorChromosome(childAlleles, ((RefactorChromosome)parent1).getOriginGraph());
    }


    private void fillAlleles(int origin, int bound, List<AbstractAllele> childAlleles, Chromosome parent) {
        if (bound > parent.getLength() || origin >= bound || origin < 0 || origin >= parent.getLength() || bound < 1) {
            return;
        }
        for (int i = origin; i < bound; i++) {
//            childAlleles.add(new RefactorAllele((String) parent.getAllele(i).getGene()));
            childAlleles.add(parent.getAllele(i));
        }
    }

}
