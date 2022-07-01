package top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.childpopulationproducer;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.refactor.algorithm.genetic.nsgaii.Service;
import top.lazyr.refactor.algorithm.genetic.nsgaii.crossover.AbstractCrossover;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;
import top.lazyr.refactor.algorithm.genetic.nsgaii.mutation.AbstractMutation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/29
 */
public class RefactorChildPopulationProducer implements ChildPopulationProducer {
    private Graph graph;

    public RefactorChildPopulationProducer(Graph graph) {
        this.graph = graph;
    }

    @Override
    public Population produce(Population parentPopulation, AbstractCrossover crossover, AbstractMutation mutation, int populationSize) {
        List<Chromosome> populace = new ArrayList<>();
//        ConsoleConstant.printTitle("产生子代");
        while(populace.size() < populationSize)
            if((populationSize - populace.size()) == 1){
                populace.add(
                        mutation.perform(
                                Service.crowdedBinaryTournamentSelection(parentPopulation).getCopy()
                        )
                );
//                System.out.println("变异");
            }
            else
                for(Chromosome chromosome : crossover.perform(parentPopulation)) {
                    populace.add(mutation.perform(chromosome));
//                    System.out.println("交叉");
                }

        
        return new Population(populace);
    }

    @Override
    public String toString() {
        return "常规子代生成器";
    }
}
