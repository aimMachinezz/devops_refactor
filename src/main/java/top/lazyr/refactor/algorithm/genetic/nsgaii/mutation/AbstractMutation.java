package top.lazyr.refactor.algorithm.genetic.nsgaii.mutation;



import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractMutation {
	protected String mutationTitle = "默认变异算子";
	protected float mutationProbability = 0.03f;

	public AbstractMutation() {}

	public AbstractMutation(float mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	public abstract Chromosome perform(Chromosome chromosome);

	public boolean shouldPerformMutation() {
		return ThreadLocalRandom.current().nextFloat() <= this.mutationProbability;
	}

	@Override
	public String toString() {
		return this.mutationTitle;
	}
}
