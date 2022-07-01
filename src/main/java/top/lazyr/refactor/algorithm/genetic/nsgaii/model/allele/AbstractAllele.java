package top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele;

public abstract class AbstractAllele {

	protected final Object gene;

	public AbstractAllele(Object gene) {
		this.gene = gene;
	}

	public abstract Object getGene();
	public abstract AbstractAllele getCopy();
}
