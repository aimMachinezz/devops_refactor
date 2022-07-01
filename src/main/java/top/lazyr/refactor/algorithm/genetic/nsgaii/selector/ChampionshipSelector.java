package top.lazyr.refactor.algorithm.genetic.nsgaii.selector;



import top.lazyr.refactor.algorithm.genetic.nsgaii.Service;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/29
 */
public class ChampionshipSelector implements CrossoverParticipantCreator {
    /**
     * 锦标赛选择
     * 从population选择两个个体作为父本返回
     * @param population
     * @return
     */
    @Override
    public List<Chromosome> create(Population population) {
        List<Chromosome> selected = new ArrayList<>();

        Chromosome parent1 = Service.crowdedBinaryTournamentSelection(population);
        Chromosome parent2 = Service.crowdedBinaryTournamentSelection(population);


        int times = 0;
        while(parent1.identicalGeneticCode(parent2) && times < 10) { // 若连续重复10次，还未找到不想等的父本，则不在找寻
            parent2 = Service.crowdedBinaryTournamentSelection(population);
            times++;
        }
        if (times == 10) {
            System.out.println("两个父本重复");
        }

        selected.add(0, parent1);
        selected.add(1, parent2);

        return selected;
    }
}
