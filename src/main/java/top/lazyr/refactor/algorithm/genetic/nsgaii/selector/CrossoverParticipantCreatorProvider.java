package top.lazyr.refactor.algorithm.genetic.nsgaii.selector;


import top.lazyr.refactor.algorithm.genetic.nsgaii.Service;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;

import java.util.ArrayList;
import java.util.List;

public class CrossoverParticipantCreatorProvider {

	public static CrossoverParticipantCreator selectByBinaryTournamentSelection() {
		return population -> {

			List<Chromosome> selected = new ArrayList<>();

			Chromosome parent1 = Service.crowdedBinaryTournamentSelection(population);
			Chromosome parent2 = Service.crowdedBinaryTournamentSelection(population);

			while(parent1.identicalGeneticCode(parent2))
				parent2 = Service.crowdedBinaryTournamentSelection(population);

			selected.add(0, parent1);
			selected.add(1, parent2);

			return selected;
		};
	}
}
