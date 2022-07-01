package top.lazyr.refactor.algorithm.random;


import top.lazyr.refactor.algorithm.random.model.Solution;
import top.lazyr.refactor.algorithm.random.objectivefunction.AbstractObjectiveFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/12
 */
public class Service {

    public static void sortFrontWithCrowdingDistance(List<Solution> paretoSolutions, int front) {
        int frontStartIndex = -1;
        int frontEndIndex = -1;
        List<Solution> frontToSort = new ArrayList<>();

        for(int i = 0; i < paretoSolutions.size(); i++)
            if(paretoSolutions.get(i).getRank() == front) {
                frontStartIndex = i;
                break;
            }

        if((frontStartIndex == -1) || (frontStartIndex == (paretoSolutions.size() - 1)) || (paretoSolutions.get(frontStartIndex + 1).getRank() != front))
            return;

        for(int i = frontStartIndex + 1; i < paretoSolutions.size(); i++)
            if(paretoSolutions.get(i).getRank() != front) {
                frontEndIndex = i - 1;
                break;
            } else if(i == (paretoSolutions.size() - 1))
                frontEndIndex = i;

        for(int i = frontStartIndex; i <= frontEndIndex; i++)
            frontToSort.add(paretoSolutions.get(i));

        frontToSort.sort(Collections.reverseOrder(Comparator.comparingDouble(Solution::getCrowdingDistance)));

        for(int i = frontStartIndex; i <= frontEndIndex; i++)
            paretoSolutions.set(i, frontToSort.get(i - frontStartIndex));
    }

    public static void calculateObjectiveValues(Solution refactors, List<AbstractObjectiveFunction> objectives) {
        for(int i = 0; i < objectives.size(); i++)
            refactors.addObjectiveValue(i, objectives.get(i).getValue(refactors));
    }

    public static boolean populaceHasUnsetRank(List<Solution> solutions) {
        for(Solution solution : solutions)
            if(solution.getRank() == -1)
                return true;
        return false;
    }

    public static void normalizeSortedObjectiveValues(List<Solution> solutions, int objectiveIndex) {

        double actualMin = solutions.get(0).getObjectiveValues().get(objectiveIndex);
        double actualMax = solutions.get(solutions.size() - 1).getObjectiveValues().get(objectiveIndex);

        for(Solution solution : solutions)
            solution.setNormalizedObjectiveValue(
                    objectiveIndex,
                    top.lazyr.refactor.algorithm.genetic.nsgaii.Service.minMaxNormalization(
                            solution.getObjectiveValues().get(objectiveIndex),
                            actualMin,
                            actualMax
                    )
            );
    }



}
