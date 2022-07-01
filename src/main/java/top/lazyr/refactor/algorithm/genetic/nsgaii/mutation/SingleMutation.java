package top.lazyr.refactor.algorithm.genetic.nsgaii.mutation;

import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.AbstractAllele;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.RefactorAllele;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.RefactorChromosome;
import top.lazyr.refactor.generator.RefactorListGenerator;
import top.lazyr.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lazyr
 * @created 2022/1/29
 */
public class SingleMutation extends AbstractMutation {

    private RefactorListGenerator refactorList;

    private static final String REFACTOR_ALLELE_INSTANCE_ERROR = "SingleMutation works with RefactorAllele only. ";

    public SingleMutation(float mutationProbability, RefactorListGenerator refactorList) {
        this.mutationProbability = mutationProbability;
        this.refactorList = refactorList;
        this.mutationTitle = "单点变异算子";
    }

    /**
     * 返回一个变异/没变异的染色体（此时initGraph已经有初始值，表现型为null）
     * @param chromosome
     * @return
     */
    @Override
    public Chromosome perform(Chromosome chromosome) {
        for(AbstractAllele allele : chromosome.getGeneticCode()) {
            if(!(allele instanceof RefactorAllele)) {
                throw new UnsupportedOperationException(SingleMutation.REFACTOR_ALLELE_INSTANCE_ERROR);
            }
        }

        if (shouldPerformMutation()) {
//            System.out.println("变异");
            int point = ThreadLocalRandom.current().nextInt(chromosome.getLength());
            List<String> originRefactors = ((RefactorChromosome) chromosome).getRefactors();
            List<String> currentRefactors = new ArrayList<>();
            ArrayUtil.moveArr2Arr(currentRefactors, originRefactors, 0, point - 1);
            String refactor = refactorList.generateOne(currentRefactors, chromosome.getLength());
            chromosome.setAllele(point, new RefactorAllele(refactor));
        }
        return chromosome;
    }
}
