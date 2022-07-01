package top.lazyr.refactor.algorithm.genetic.singleovjective;



import top.lazyr.refactor.algorithm.genetic.singleovjective.crossover.Crossover;
import top.lazyr.refactor.algorithm.genetic.singleovjective.model.Individual;
import top.lazyr.refactor.algorithm.genetic.singleovjective.mutation.Mutation;
import top.lazyr.refactor.algorithm.genetic.singleovjective.select.Selector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/8
 */
public class GeneticRule {
    private Selector selector;
    private Crossover crossover;
    private Mutation mutation;
    private IndividualGenerator generator;
    private int N;


    public GeneticRule(Selector selector, Crossover crossover, Mutation mutation, IndividualGenerator generator, int n) {
        this.selector = selector;
        this.crossover = crossover;
        this.mutation = mutation;
        this.generator = generator;
        this.N = n;
    }

    /**
     * 通过individuals，演化返回下一代种群
     * @param individuals
     * @return
     */
    public List<Individual> evolve(List<Individual> individuals) {
        List<Individual> childIndividuals = new ArrayList<>();

        selector.setIndividual(individuals);
        // TODO:  若N为奇数，可能childIndividuals的size会变成N+1
        while (childIndividuals.size() < N) {

            // 1、选择父本
            Individual parent1 = selector.select();
            Individual parent2 = selector.select();
            // 2、交叉
            List<List<String>> childrenGenotype = crossover.cross(parent1.getGenotype(), parent2.getGenotype());


            if (childrenGenotype == null) {
                continue;
            }
            // 3、变异
            for (List<String> childGenotype : childrenGenotype) {
                mutation.mutate(childGenotype);
            }

            // 4、生成新个体
            for (List<String> childGenotype : childrenGenotype) {
                Individual childIndividual = generator.generate(childGenotype);
                childIndividuals.add(childIndividual);
            }
        }

        return childIndividuals;
    }
}
