package top.lazyr.refactor.algorithm.genetic.nsgaii.mutation;


import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.AbstractAllele;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.BooleanAllele;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;

import java.util.ArrayList;
import java.util.List;

public class SinglePointMutation extends AbstractMutation {

	private static final String BOOLEAN_ALLELE_INSTANCE_ERROR = "SinglePointMutation works with BooleanAllele only. "		+
																"Please implement your own Mutation class by extending "	+
																"the AbstractMutation class to get your desired results.";

	public SinglePointMutation() {
		super();
	}

	public SinglePointMutation(float mutationProbability) {
		super(mutationProbability);
	}

	@Override
	public Chromosome perform(Chromosome chromosome) {

		for(AbstractAllele allele : chromosome.getGeneticCode())
			if(!(allele instanceof BooleanAllele))
				throw new UnsupportedOperationException(SinglePointMutation.BOOLEAN_ALLELE_INSTANCE_ERROR);

		List<BooleanAllele> booleanGeneticCode = new ArrayList<>();

		for(int i = 0; i < chromosome.getLength(); i++)
			booleanGeneticCode.add(
				i, new BooleanAllele(
					this.shouldPerformMutation()								?
						!((BooleanAllele) chromosome.getAllele(i)).getGene()	:
						((BooleanAllele) chromosome.getAllele(i)).getGene()
				)
			);

		return new Chromosome(new ArrayList<>(booleanGeneticCode));
	}
}
