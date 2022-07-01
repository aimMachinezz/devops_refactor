package top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele;


/**
 * @author lazyr
 * @created 2022/1/29
 */
public class RefactorAllele extends AbstractAllele {
    public RefactorAllele(String gene) {
        super(gene);
    }

    @Override
    public String getGene() {
        return (String) gene;
    }

    @Override
    public AbstractAllele getCopy() {
        return new RefactorAllele(String.valueOf(gene));
    }

    @Override
    public String toString() {
        return (String)gene;
    }
}
