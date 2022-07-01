package top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele;

public class BooleanAllele extends AbstractAllele {

	public BooleanAllele(boolean gene) {
		super(gene);
	}

	@Override
	public Boolean getGene() {
		return (boolean) this.gene;
	}

	@Override
	public AbstractAllele getCopy() {
		return new BooleanAllele((boolean) this.gene);
	}

	@Override
	public String toString() {
		return ((Boolean) this.gene ? "1" : "0");
	}
}
