package top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.populationproducer;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.AbstractAllele;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.RefactorChromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator.FitnessCalculator;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.geneticcodeproducer.GeneticCodeProducer;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始种群生成器
 * @author lazyr
 * @created 2022/1/29
 */
public class RefactorPopulationProducer implements PopulationProducer {
    private Graph graph;

    public RefactorPopulationProducer(Graph graph) {
        this.graph = graph;
    }

    /**
     * geneticCodeProducer可以产生一个长度为chromosomeLength的染色体
     * 初始化populationSize个染色体和表现型为null的个体（initGraph为初始源代码模型）
     * 返回一个有populationSize个个体的群体
     * @param populationSize
     * @param chromosomeLength
     * @param geneticCodeProducer
     * @param fitnessCalculator
     * @return
     */
    @Override
    public Population produce(int populationSize, int chromosomeLength, GeneticCodeProducer geneticCodeProducer, FitnessCalculator fitnessCalculator) {
        List<Chromosome> chromosomes = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<? extends AbstractAllele> alleles = geneticCodeProducer.produce(chromosomeLength);
            chromosomes.add(new RefactorChromosome(alleles, graph));
        }
        return new Population(chromosomes);
    }

    @Override
    public String toString() {
        return "一次性生成";
    }
}
