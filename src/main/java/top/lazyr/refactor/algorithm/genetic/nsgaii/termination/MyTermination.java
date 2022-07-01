package top.lazyr.refactor.algorithm.genetic.nsgaii.termination;



import top.lazyr.refactor.algorithm.genetic.nsgaii.Configuration;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;
import top.lazyr.refactor.algorithm.genetic.nsgaii.solution.Nsga2ExpWriter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lazyr
 * @created 2022/1/29
 */
public class MyTermination implements TerminatingCriterion {

    private Set<Integer> generationCounts = new HashSet<>(Arrays.asList(100, 200, 300, 500, 1000, 1500, 2000, 2500, 3000));
    private Configuration configuration;

    public MyTermination(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean shouldRun(Population population, int generationCount, int maxGenerations) {
        if (generationCount % 100 == 0) {
           Nsga2ExpWriter.write(population.getPopulace(), generationCount, configuration);
           System.out.println("迭代次数: " + generationCount);
        }
        return generationCount <= maxGenerations;
    }

    @Override
    public String toString() {
        return "迭代到最大代";
    }
}
