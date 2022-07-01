package top.lazyr.refactor.algorithm.genetic.nsgaii.solution;


import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/8
 */
public class SolutionFilter {

    public static List<Chromosome> filter(List<Chromosome> paretoOptimalSolutions) {
        List<Chromosome> filterSolutions = new ArrayList<>();
        for (Chromosome paretoOptimalSolution : paretoOptimalSolutions) {
            if (paretoOptimalSolution.getRank() == 1) {
                filterSolutions.add(paretoOptimalSolution);
            }
            System.out.print(paretoOptimalSolution.getRank() + " ");
        }
        System.out.println("solutionFilter");
        return filterSolutions;
    }
}
