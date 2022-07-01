package top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.geneticcodeproducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.AbstractAllele;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.RefactorAllele;
import top.lazyr.refactor.generator.RefactorListGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/29
 */
public class RefactorProducer implements GeneticCodeProducer {
    private static Logger logger = LoggerFactory.getLogger(RefactorProducer.class);
    /* 用于生成重构操作的生成列表 */
    private RefactorListGenerator refactorListGenerator;

    public RefactorProducer(RefactorListGenerator refactorListGenerator) {
        this.refactorListGenerator = refactorListGenerator;
    }

    @Override
    public List<? extends AbstractAllele> produce(int length) {
        List<AbstractAllele> abstractAlleles = new ArrayList<>();
        List<String> refactors = refactorListGenerator.generateList(length);
        for (int i = 0; i < length; i++) {
            abstractAlleles.add(new RefactorAllele(refactors.get(i)));
        }
        return abstractAlleles;
    }




    @Override
    public String toString() {
        return refactorListGenerator.getIntroduction();
    }
}
