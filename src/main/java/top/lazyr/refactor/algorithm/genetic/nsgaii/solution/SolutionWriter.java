package top.lazyr.refactor.algorithm.genetic.nsgaii.solution;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.writer.ExcelModelWriter;
import top.lazyr.io.output.TxtOutput;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.RefactorChromosome;
import top.lazyr.util.FileUtil;

import java.util.List;
import java.util.Set;

/**
 * @author lazyr
 * @created 2022/2/6
 */
public class SolutionWriter {
    /**
     *
     * @param paretoOptimalSolutions
     * @param num
     */
    private static void write(RefactorChromosome paretoOptimalSolutions, int num, String outputPath) {
        Graph phenotype = paretoOptimalSolutions.getPhenotype();
        List<String> refactors = paretoOptimalSolutions.getRefactors();
        TxtOutput.append2File(outputPath + "重构建议" + num + ".txt",
                            refactors.toString().replace("[", "")
                            .replace("]", "")
                            .replace(", ", "\n"));
        ExcelModelWriter.write(phenotype, outputPath + "重构后源代码模型图" + num + ".xlsx");
    }

    public static void write(List<Chromosome> paretoOptimalSolutions, String outputPath) {
        for (int i = 0; i < paretoOptimalSolutions.size(); i++) {
            write((RefactorChromosome) paretoOptimalSolutions.get(i), i, outputPath);
        }
        Graph originGraph = ((RefactorChromosome)paretoOptimalSolutions.get(0)).getOriginGraph();
//
//        Set<Node> originHLNodes = HubLikeDependencyDetector.detect(originGraph).keySet();
//        Set<Node> originUDNodes = UnstableDependencyDetector.detect(originGraph).keySet();
//        Set<Node> originCDNodes = new HashSet<>(CyclicDependencyDetector.detect(originGraph));
//
//        Set<Node> originSmellNodes = new HashSet<>();
//        originSmellNodes.addAll(originHLNodes);
//        originSmellNodes.addAll(originUDNodes);
//        originSmellNodes.addAll(originCDNodes);
//
//
//        double originCohesion= ObjectiveUtil.calCohesion(originGraph);
//        double originCoupling = ObjectiveUtil.calCoupling(originGraph);
//
//        DecimalFormat format = new DecimalFormat("#0.000");
//        StringBuilder effect = new StringBuilder();
//        for (int i = 0; i < paretoOptimalSolutions.size(); i++) {
//            Graph refactoredGraph = ((RefactorChromosome)paretoOptimalSolutions.get(i)).getPhenotype();
//            Set<Node> refactoredHLNodes = HubLikeDependencyDetector.detect(refactoredGraph).keySet();
//            Set<Node> refactoredUDNodes = UnstableDependencyDetector.detect(refactoredGraph).keySet();
//            Set<Node> refactoredCDNodes = new HashSet<>(CyclicDependencyDetector.detect(refactoredGraph));
//
//            Set<Node> refactoredSmellNodes = new HashSet<>();
//            refactoredSmellNodes.addAll(refactoredHLNodes);
//            refactoredSmellNodes.addAll(refactoredUDNodes);
//            refactoredSmellNodes.addAll(refactoredCDNodes);
//            effect.append(ConsoleConstant.SEPARATOR + "第" + i + "个pareto最优解" + ConsoleConstant.SEPARATOR + "\n");
//            effect.append(
//                    "重构前异味组件数: " + (originSmellNodes.size()) +
//                            ", 重构后异味组件数: " + (refactoredSmellNodes.size()) + "\n" +
//                            "重构前总异味数: " + (originHLNodes.size() + originUDNodes.size() + originCDNodes.size()) +
//                            ", 重构后总异味数: " + (refactoredHLNodes.size() + refactoredUDNodes.size() + refactoredCDNodes.size()) + "\n" +
//                            buildSmellEffect(originHLNodes, refactoredHLNodes, "枢纽型异味") + "\n" +
//                            buildSmellEffect(originUDNodes, refactoredUDNodes, "不稳定异味") + "\n" +
//                            buildSmellEffect(originCDNodes, refactoredCDNodes, "环依赖异味") + "\n"
//            );
//            double refactoredCohesion = ObjectiveUtil.calCohesion(refactoredGraph);
//            effect.append("重构前内聚性: " + format.format(originCohesion) + ", 重构后内聚性: " + format.format(refactoredCohesion) + ", 改善率： " + format.format((refactoredCohesion / originCohesion)) + "\n");
//            double refactoredCoupling = ObjectiveUtil.calCoupling(refactoredGraph);
//            effect.append("重构前耦合度: " + format.format(originCoupling) + ", 重构后耦合度: " + format.format(refactoredCoupling) + ", 改善率： " + format.format((originCoupling / refactoredCoupling)) + "\n");
//        }

//        FileUtil.write2File(outputPath + "重构提升效果.txt", effect.toString());
//        System.out.println("pareto最优解写入成功...");
//        System.out.println(effect.toString());
    }

    private static String buildSmellEffect(Set<Node> originSmellNodes, Set<Node> refactoredSmellNodes, String smellName) {
        StringBuilder smellRefactorInfo = new StringBuilder();
        smellRefactorInfo.append("重构前" + smellName + "数: " + originSmellNodes.size() + ", 重构后" + smellName + "数: " + refactoredSmellNodes.size());
        return smellRefactorInfo.toString();
    }

}
