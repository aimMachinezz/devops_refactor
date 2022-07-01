package top.lazyr.refactor.algorithm.genetic.singleovjective.writer;

import top.lazyr.constant.RefactorConstant;
import top.lazyr.refactor.algorithm.genetic.singleovjective.model.Individual;

import java.util.*;

/**
 * @author lazyr
 * @created 2022/1/13
 */
public class SolutionWriter implements ResultWriter{

    @Override
    public void write(Individual individual) {
        List<String> refactors = individual.getGenotype();
        Map<String, Set<String>> result = new HashMap<>();
        for (String refactor : refactors) {
            String[] actions = refactor.split(RefactorConstant.ACTION_SEPARATOR);
            Set<String> refactoredClasses = extractClasses(refactor);
            String action = actions[0];
            String refactoredPackage = actions[1];
            String movedPackage = actions[3];
            Set<String> classNames = result.get(action + "-" + refactoredPackage + "-" + movedPackage);
            if (classNames == null) {
                classNames = refactoredClasses;
            } else {
                classNames.addAll(refactoredClasses);
            }
            result.put(action + "-" + refactoredPackage + "-" + movedPackage, classNames);
        }
        for (String key : result.keySet()) {
            System.out.println("=================================");
            String[] actions = key.split("-");
            System.out.println(actions[0] + " from " + actions[1] + " to " + actions[2]);
            Set<String> classNames = result.get(key);
            for (String className : classNames) {
                System.out.println("\t\t" + className);
            }
            System.out.println("=================================");
        }
    }

    @Override
    public void write(List<Individual> individuals) {

    }


    private Set<String> extractClasses(String refactor) {
        String[] actions = refactor.split(RefactorConstant.ACTION_SEPARATOR);
        String[] classArr = actions[2].split(RefactorConstant.CONTENT_SEPARATOR);
        Set<String> classes = new HashSet<>();
        for (String className : classArr) {
            classes.add(className);
        }
        return classes;
    }


}
