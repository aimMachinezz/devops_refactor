package top.lazyr.refactor.algorithm.genetic.singleovjective.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.lazyr.architecture.diagram.Graph;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/7
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Individual {
    /* genotype */
    private List<String> genotype;
    /* phenotype */
    private Graph phenotype;

    private double score;

    private int failedNum;

    private int succeedNum;


}
