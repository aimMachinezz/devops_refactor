package top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.geneticcodeproducer;


import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.AbstractAllele;

import java.util.List;

/**
 * The GeneticCodeProducer interface is used to create a genetic code, which in turn is used to encode a Chromosome
 * for the algorithm to use. It has only one method, `produce`, which takes as its argument, the length of the genetic
 * code to be produced.
 */
@FunctionalInterface
public interface GeneticCodeProducer {
	/**
	 * This method is used to create a list of `AbstractAllele`s which represents the genetic code of a `Chromosome` and has
	 * a size equal to the `length` argument passed as parameter.
	 *
	 * @param length the length of the list of `AbstractAllele`s to be created. This should be the length of the genetic code.
	 * @return a list of `AbstractAlleles` whose size should be equal to the length argument.
	 */
	List<? extends AbstractAllele> produce(int length);
}
