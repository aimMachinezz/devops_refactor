package top.lazyr.refactor.algorithm.genetic.singleovjective;


import top.lazyr.refactor.algorithm.genetic.singleovjective.model.Individual;
import top.lazyr.refactor.generator.RefactorListGenerator;
import top.lazyr.ui.LineChart;
import top.lazyr.constant.RefactorConstant;

import java.util.*;

/**
 * @author lazyr
 * @created 2022/1/7
 */
public class GeneticAlgorithm {
    /* 种群大小 */
    private int N;
    /* 解的大小 */
    private int R;
    /* 演进代数 */
    private int T;
    /* 解的个数 */
    private int M;

    private GeneticRule geneticRule;
    private RefactorListGenerator refactorList;
    private IndividualGenerator generator;


    public GeneticAlgorithm(int N, int R, int T, int M, GeneticRule geneticRule, RefactorListGenerator refactorList, IndividualGenerator generator) {
        this.N = N;
        this.R = R;
        this.T = T;
        this.M = M;
        this.geneticRule = geneticRule;
        this.refactorList = refactorList;
        this.generator = generator;
    }

    /**
     * 直接保留下一代，记录所有代中最好的个体
     * @return
     */
    public List<Individual> run1() {
        LineChart lineChart1 = new LineChart("适应度", "代数", "适应度值");
        LineChart lineChart2 = new LineChart("重构操作", "代数", "操作次数");
        lineChart1.show();
        lineChart2.show();

        List<Individual> bestIndividuals = new ArrayList<>();
        // 1、初始化群体
        List<Individual> individuals = initPopulation();
        // 2、迭代T次
        List<Double> populationFitness = new ArrayList<>();
        List<Double> bestFitness = new ArrayList<>();
        List<Double> succeedNum = new ArrayList<>();
        List<Double> failedNum = new ArrayList<>();
        List<String> x = new ArrayList<>();
        for (int i = 0; i < T; i++) {
            // ===========直接保留下一代，记录所有代中最好的个体========================
            // 1、更新解
            bestIndividuals = refreshBestIndividuals(individuals, bestIndividuals);
            double populationAverFitness = averFitness(individuals);
            double bestAverFitness = averFitness(bestIndividuals);
            double averSucceedSNum = averSucceedNum(individuals);
            double averFailedSNum = averFailedNum(individuals);

            lineChart1.addDate("群体适应度平均值", i + "", populationAverFitness);
            lineChart1.addDate("最佳个体平均适应度值", i + "", bestAverFitness);
            lineChart2.addDate("成功操作类平均个数", i + "", averSucceedSNum);
            lineChart2.addDate("失败操作类平均个数", i + "", averFailedSNum);
            x.add(i + "");
            populationFitness.add(populationAverFitness);
            bestFitness.add(bestAverFitness);
            succeedNum.add(averSucceedSNum);
            failedNum.add(averFailedSNum);
            // 2、生成下一代
            individuals = geneticRule.evolve(individuals);

        }

//        TxtOutput.write2File("exp/fitness.txt", x.toString().replaceAll(", ", "','").replace("[", "['").replace("]", "'") + "\n");
//        TxtOutput.append2File("exp/fitness.txt", populationFitness.toString() + "\n");
//        TxtOutput.append2File("exp/fitness.txt", bestFitness.toString());
//        TxtOutput.write2File("exp/actionNum.txt", x.toString() + "\n");
//        TxtOutput.append2File("exp/actionNum.txt", succeedNum.toString() + "\n");
//        TxtOutput.append2File("exp/actionNum.txt", failedNum.toString());
        return bestIndividuals;
    }

    /**
     * 保留前N个最好的为下一代
     * @return
     */
    public List<Individual> run2() {
        LineChart lineChart1 = new LineChart("适应度", "代数", "适应度值");
        LineChart lineChart2 = new LineChart("重构操作", "代数", "操作次数");
        lineChart1.show();
        lineChart2.show();

        List<Individual> bestIndividuals = new ArrayList<>(M);
        // 1、初始化群体
        List<Individual> individuals = initPopulation();
        // 2、迭代T次
        for (int i = 0; i < T; i++) {
            // ===========保留前N个最好的为下一代========================
            // 1、获取下一代
            List<Individual> nextIndividuals = geneticRule.evolve(individuals);
            lineChart2.addDate("成功操作类平均个数", i + "", averSucceedNum(nextIndividuals));
            lineChart2.addDate("失败操作类平均个数", i + "", averFailedNum(nextIndividuals));
            // 2、取最后的前N个
            individuals = refreshBestIndividuals(individuals, nextIndividuals);
            lineChart1.addDate("群体适应度平均值", i + "", averFitness(individuals));
        }

        return individuals;
    }

    private double averFailedNum(List<Individual> individuals) {
        double failedSum = 0;
        for (Individual individual : individuals) {
            failedSum += individual.getFailedNum();
        }
        return failedSum / individuals.size();
    }

    private double averSucceedNum(List<Individual> individuals) {
        double succeedSum = 0;
        for (Individual individual : individuals) {
            succeedSum += individual.getSucceedNum();
        }
        return succeedSum / individuals.size();
    }

    private double averFitness(List<Individual> bestIndividuals) {
        double score = 0d;
        for (Individual bestIndividual : bestIndividuals) {
            score += bestIndividual.getScore();
        }
        return score/bestIndividuals.size();
    }

    /**
     * 比较population和bestIndividuals，选取适应度值最高的M个个体
     * TODO: 待优化
     * @param individuals
     * @param bestIndividuals
     */
    private List<Individual> refreshBestIndividuals(List<Individual> individuals, List<Individual> bestIndividuals) {
//        System.out.println("更新最优解==========================");
        // individuals按从高到低排序
        List<Individual> temp = new ArrayList<Individual>();
//        temp.addAll(uniqueIndividuals(individuals, bestIndividuals));
        temp.addAll(individuals);
        temp.addAll(uniqueIndividuals(bestIndividuals, individuals));

        temp.sort(new Comparator<Individual>() {
            @Override
            public int compare(Individual i1, Individual i2) {
                double d1 = i1.getScore();
                double d2 = i2.getScore();
                double dis = 1e-6;

                return Math.abs(d1-d2) < dis ? 0 : d2 - d1 < 0 ? -1 : 1 ;
            }
        });
        List<Individual> result = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            result.add(temp.get(i));
        }
        return result;
    }


    private boolean individualEqual(Individual individual1, Individual individual2) {
        List<String> genotype1 = individual1.getGenotype();
        List<String> genotype2 = individual2.getGenotype();
        for (int i = 0; i < genotype1.size(); i++) {
            if (!genotype1.get(i).equals(genotype2.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回individuals1独立的数据（individuals1中有，但individuals2没有的数据）
     * @param individuals1
     * @param individuals2
     * @return
     */
    private List<Individual> uniqueIndividuals(List<Individual> individuals1, List<Individual> individuals2) {
        List<Individual> temp = new ArrayList<>();
        for (Individual individual1 : individuals1) {
            boolean isExisted = false;
            for (Individual individual2 : individuals2) {
                if (individualEqual(individual1, individual2)) {
                    isExisted = true;
                    break;
                }
            }
            if (!isExisted) {
                temp.add(individual1);
            }
        }
        return temp;
    }

    private List<Individual> initPopulation(){
        List<Individual> individuals = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            List<String> refactors = new ArrayList<>();
            for (int j = 0; j < R; j++) {
                String refactor = refactorList.generateOne(R);
                while (!refactorValidate(refactors, refactor)) { // 若无效，则继续随机生成，直到有效为止
                    refactor = refactorList.generateOne(R);
                }
                refactors.add(refactor);
            }
            Individual individual = generator.generate(refactors);
            individuals.add(individual);
        }
        return individuals;
    }

    /**
     * 若refactor中重构的文件和refactors中要重构的文件已经重复，则认为refactor无效，返回false;
     * 否则返回true
     * @param refactors
     * @param refactor
     * @return
     */
    private boolean refactorValidate(List<String> refactors, String refactor) {
        if (refactors == null) {
            return false;
        }
        if (refactors.size() == 0) {
            return true;
        }
        Set<String> existedFileNodeId = new HashSet<>();
        for (String r : refactors) {
            String fileNodeId = r.split(RefactorConstant.ACTION_SEPARATOR)[2];
            existedFileNodeId.add(fileNodeId);
        }

        String fileNodeId = refactor.split(RefactorConstant.ACTION_SEPARATOR)[2];

        return !existedFileNodeId.contains(fileNodeId);
    }
}
