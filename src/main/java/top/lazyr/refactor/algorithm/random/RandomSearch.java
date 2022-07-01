package top.lazyr.refactor.algorithm.random;


import top.lazyr.constant.ConsolePrinter;
import top.lazyr.refactor.algorithm.genetic.nsgaii.NSGA2;
import top.lazyr.refactor.algorithm.random.model.Solution;
import top.lazyr.refactor.algorithm.random.solution.RandomExpWriter;
import top.lazyr.refactor.generator.RefactorListGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/12
 */
public class RandomSearch {
    public static final int DOMINANT = 1;
    public static final int INFERIOR = 2;
    public static final int NON_DOMINATED = 3;

    private final RandomConfiguration randomConfiguration;

    public RandomSearch(RandomConfiguration randomConfiguration) {
        this.randomConfiguration = randomConfiguration;
    }


    public List<Solution> run() {
        List<Solution> parents = generateSolutions(this.randomConfiguration.getRefactorListGenerator());
        List<Solution> children = generateSolutions(this.randomConfiguration.getRefactorListGenerator());
        for (int generation = 100; generation <= randomConfiguration.getMaxGenerationNum(); generation += 100) {
            parents = this.getChildFromCombinedPopulation(
                    this.prepareSolutions(combineSolutions(parents, children))
            );
            children = this.prepareSolutions(
                    generateSolutions(this.randomConfiguration.getRefactorListGenerator())
            );
            RandomExpWriter.write(parents, generation, randomConfiguration);
            ConsolePrinter.printTitle("代数: " + generation);
        }
        
        return parents;
    }

    private List<Solution> combineSolutions(List<Solution> parents, List<Solution> children) {
        parents.addAll(children);
        return parents;
    }


    private List<Solution> prepareSolutions(List<Solution> solutions) {
        // 对所有解进行非支配排序
        this.fastNonDominatedSort(solutions);
        // 计算所有解拥挤度值
        this.crowdingDistanceAssignment(solutions);
        // 根据非支配值对所有解进行排序
        solutions.sort(Comparator.comparingInt(Solution::getRank));
        return solutions;
    }

    public List<Solution> generateSolutions(RefactorListGenerator refactorListGenerator) {
        List<Solution> solutions = new ArrayList<>();
        for (int i = 0; i < this.randomConfiguration.getPopulationSize(); i++) {
            List<String> refactors = refactorListGenerator.generateList(this.randomConfiguration.getRefactorNum());
            Solution solution = new Solution(refactors, this.randomConfiguration.getOriginGraph());
            // 计算目标函数值
            Service.calculateObjectiveValues(solution, this.randomConfiguration.objectives);
            solutions.add(solution);
        }
        return solutions;
    }

    public void fastNonDominatedSort(List<Solution> solutions) {
        // 充值solution，设置dominatedCount=0，dominatedChromosomes=new ArrayList<>()和rank=-1
        for(Solution solution : solutions)
            solution.reset();

        for(int i = 0; i < solutions.size() - 1; i++) {
            for (int j = i + 1; j < solutions.size(); j++)
                switch (this.dominates(solutions.get(i), solutions.get(j))) {
                    // 第一个支配第二个
                    case NSGA2.DOMINANT:
                        solutions.get(i).addDominatedChromosome(solutions.get(j));
                        solutions.get(j).incrementDominatedCount(1);
                        break;
                    // 第二个支配第一个
                    case NSGA2.INFERIOR:
                        solutions.get(i).incrementDominatedCount(1);
                        solutions.get(j).addDominatedChromosome(solutions.get(i));
                        break;
                    // 互相都不支配
                    case NSGA2.NON_DOMINATED:
                        break;
                }

            // 若这个个体，没有被支配的个体，则设置rank为1
            if(solutions.get(i).getDominatedCount() == 0)
                solutions.get(i).setRank(1);
        }

        // 补充最后一个个体的rank
        if(solutions.get(solutions.size() - 1).getDominatedCount() == 0)
            solutions.get(solutions.size() - 1).setRank(1);

        // 若种群中存在个体不是非支配个体
        while(Service.populaceHasUnsetRank(solutions)) {
            solutions.forEach(solution -> {
                // 获取非支配个体
                if(solution.getRank() != -1)
                    //遍历每个非支配个体 支配的个体
                    solution.getDominatedSolutions().forEach(dominatedChromosome -> {
                        if(dominatedChromosome.getDominatedCount() > 0) {
                            // 减少被支配个体的dominatedCount
                            dominatedChromosome.incrementDominatedCount(-1);
                            // 若已经为0，则认为是当前非支配个体的dominatedCount+1
                            if(dominatedChromosome.getDominatedCount() == 0)
                                dominatedChromosome.setRank(solution.getRank() + 1);
                        }
                    });
            });
        }
    }

    public int dominates(Solution solution1, Solution solution2) {

        if(this.isDominant(solution1, solution2)) return NSGA2.DOMINANT;
        else if(this.isDominant(solution2, solution1)) return NSGA2.INFERIOR;
        else return NSGA2.NON_DOMINATED;
    }

    public boolean isDominant(Solution solution1, Solution solution2) {

        boolean atLeastOneIsBetter = false;

        for(int i = 0; i < this.randomConfiguration.objectives.size(); i++)
            if(solution1.getObjectiveValues().get(i) < solution2.getObjectiveValues().get(i))
                return false;
            else if(solution1.getObjectiveValues().get(i) > solution2.getObjectiveValues().get(i))
                atLeastOneIsBetter = true;

        return atLeastOneIsBetter;
    }

    public void crowdingDistanceAssignment(List<Solution> solutions) {

        int size = solutions.size();

        for(int i = 0; i < this.randomConfiguration.objectives.size(); i++) {

            int iFinal = i;

            solutions.sort(Collections.reverseOrder(Comparator.comparingDouble(c -> c.getObjectiveValue(iFinal))));
            Service.normalizeSortedObjectiveValues(solutions, i);
            solutions.get(0).setCrowdingDistance(Double.MAX_VALUE);
            solutions.get(solutions.size() - 1).setCrowdingDistance(Double.MAX_VALUE);

            double maxNormalizedObjectiveValue = selectMaximumNormalizedObjectiveValue(solutions, i);
            double minNormalizedObjectiveValue = selectMinimumNormalizedObjectiveValue(solutions, i);

            for(int j = 1; j < size; j++)
                if(solutions.get(j).getCrowdingDistance() < Double.MAX_VALUE) {

                    double previousChromosomeObjectiveValue = solutions.get(j - 1).getNormalizedObjectiveValues().get(i);
                    double nextChromosomeObjectiveValue = solutions.get(j + 1).getNormalizedObjectiveValues().get(i);
                    double objectiveDifference = nextChromosomeObjectiveValue - previousChromosomeObjectiveValue;
                    double minMaxDifference = maxNormalizedObjectiveValue - minNormalizedObjectiveValue;

                    solutions.get(j).setCrowdingDistance(
                            top.lazyr.refactor.algorithm.genetic.nsgaii.Service.roundOff(
                                    solutions.get(j).getCrowdingDistance() +
                                            (objectiveDifference / minMaxDifference),
                                    4
                            )
                    );
                }
        }
    }

    public double selectMaximumNormalizedObjectiveValue(List<Solution> solutions, int objectiveIndex) {

        double result = solutions.get(0).getNormalizedObjectiveValues().get(objectiveIndex);

        for(Solution solution : solutions)
            if(solution.getNormalizedObjectiveValues().get(objectiveIndex) > result)
                result = solution.getNormalizedObjectiveValues().get(objectiveIndex);

        return result;
    }

    public double selectMinimumNormalizedObjectiveValue(List<Solution> solutions, int objectiveIndex) {

        double result = solutions.get(0).getNormalizedObjectiveValues().get(objectiveIndex);

        for(Solution solution : solutions)
            if(solution.getNormalizedObjectiveValues().get(objectiveIndex) < result)
                result = solution.getNormalizedObjectiveValues().get(objectiveIndex);

        return result;
    }

    public List<Solution> getParetoSolutions(List<Solution> solutions) {
        if (solutions.size() <= this.randomConfiguration.getPopulationSize()) { // 若解的个数未达到预定个数，则直接返回
            return solutions;
        }

        // 获取第 paretoSolutionNum - 1 个解的非支配值
        int lastFrontToConsider = solutions.get(this.randomConfiguration.getPopulationSize() - 1).getRank();
        List<Solution> childPopulace = new ArrayList<>();

        // 若 第 paretoSolutionNum 和 第 paretoSolutionNum-1 个个体非支配值相等
        if(solutions.get(this.randomConfiguration.getPopulationSize()).getRank() == lastFrontToConsider)
            // 对最后一层非支配值进行拥挤度排序
            Service.sortFrontWithCrowdingDistance(solutions, lastFrontToConsider);

        // 按非支配值和拥挤度值排序，添加 paretoSolutionNum 个解
        for(int i = 0; i < this.randomConfiguration.getPopulationSize(); i++)
            childPopulace.add(solutions.get(i));

        return childPopulace;
    }

    public List<Solution> getChildFromCombinedPopulation(List<Solution> combinedSolutions) {

        int lastFrontToConsider = combinedSolutions.get(this.randomConfiguration.getPopulationSize() - 1).getRank();
        List<Solution> children = new ArrayList<>();

        if(combinedSolutions.get(this.randomConfiguration.getPopulationSize()).getRank() == lastFrontToConsider)
            Service.sortFrontWithCrowdingDistance(combinedSolutions, lastFrontToConsider);

        for(int i = 0; i < this.randomConfiguration.getPopulationSize(); i++)
            children.add(combinedSolutions.get(i));

        return children;
    }
}
