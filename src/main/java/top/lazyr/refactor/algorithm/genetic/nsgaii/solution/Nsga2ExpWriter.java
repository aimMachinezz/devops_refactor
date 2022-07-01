package top.lazyr.refactor.algorithm.genetic.nsgaii.solution;

import cn.hutool.poi.excel.ExcelUtil;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.constant.ConsolePrinter;
import top.lazyr.io.output.ExcelOutPut;
import top.lazyr.io.output.TxtOutput;
import top.lazyr.refactor.algorithm.genetic.nsgaii.Configuration;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.RefactorChromosome;
import top.lazyr.smell.detector.cyclicdependency.CyclicDependencyDetector;
import top.lazyr.smell.detector.hublikedependency.HubLikeDependencyDetector;
import top.lazyr.smell.detector.unstabledependency.UnstableDependencyDetector;
import top.lazyr.util.ObjectiveUtil;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author lazyr
 * @created 2022/2/16
 */
public class Nsga2ExpWriter {
    private static DecimalFormat format = new DecimalFormat("#0.0000");

    public static void write(List<Chromosome> solutions, int generation, Configuration configuration) {
        // 1、指标数据
        List<List<String>> indicatorInfos = new ArrayList<>();
        if (generation == 100) {
            indicatorInfos.add(Arrays.asList("枢纽型异味消除率", "不稳定异味消除率", "枢纽型&不稳定异味消除率", "环依赖异味消除率", "总异味消除率", "内聚提升率", "耦合提升率"));
        }

        Graph originGraph = ((RefactorChromosome) solutions.get(0)).getOriginGraph();
        // 获取初始异味数据
        Set<Node> originHLNodes = HubLikeDependencyDetector.detect(originGraph).keySet();
        Set<Node> originUDNodes = UnstableDependencyDetector.detect(originGraph).keySet();
        Set<Node> originCDNodes = new HashSet<>(CyclicDependencyDetector.detect(originGraph));
        double originHLCount =  originHLNodes.size();
        double originUDCount =  originUDNodes.size();
        double originCDCount =  originCDNodes.size();
        double originHLUDSmellCount = originHLCount + originUDCount;
        double originTotalSmellCount = originHLCount + originUDCount + originCDCount;
        // 获取初始内聚耦合数据
        double originCohesion = ObjectiveUtil.calCohesion(originGraph);
        double originCoupling = ObjectiveUtil.calCoupling(originGraph);


        // 2、准确度数据
        List<List<String>> accuracyInfos = new ArrayList<>();
        if (generation == 100) {
            accuracyInfos.add(Arrays.asList("TP", "FN", "TN", "FP", "sensitivity", "specificity"));
        }
        Set<Node> systemSmellComponentNodes = new HashSet<>();
        Set<Node> systemComponentNodes = new HashSet<>(originGraph.filterSystemComponentNodes());
        // 初始化项目内异味Node
        if (configuration.isOptimizedHL()) {
            systemSmellComponentNodes.addAll(originHLNodes);
        }
        if (configuration.isOptimizedUD()) {
            systemSmellComponentNodes.addAll(originUDNodes);
        }
        if (configuration.isOptimizedCD()) {
            systemSmellComponentNodes.addAll(originCDNodes);
        }
        List<String> systemComponentNodeIds = GraphManager.nodes2Ids(systemComponentNodes); // 初始项目内节点
        List<String> systemSmellComponentNodeIds = GraphManager.nodes2Ids(systemSmellComponentNodes); // 异味节点ID



        // 3、pareto近似最优解重构操作列表
        List<Chromosome> paretoOptimalSolutions = new ArrayList<>();

        indicatorInfos.add(Arrays.asList("代数: " + generation));
        accuracyInfos.add(Arrays.asList("代数: " + generation));

        for (int i = 0; i < solutions.size(); i++) {
            if (solutions.get(i).getRank() != 1) {
                continue;
            }

            Graph refactoredGraph = ((RefactorChromosome) solutions.get(i)).getPhenotype();
            // 异味消除率
            List<String> indicatorInfo = buildIndicatorInfo(refactoredGraph,
                                                originHLCount,
                                                originUDCount,
                                                originCDCount,
                                                originHLUDSmellCount,
                                                originTotalSmellCount,
                                                originCohesion,
                                                originCoupling);
            indicatorInfos.add(indicatorInfo);


            // 准确度
            List<String> accuracyInfo = buildAccuracyInfo(originGraph,
                                                    systemComponentNodeIds,
                                                    systemSmellComponentNodeIds,
                                                    refactoredGraph);
            accuracyInfos.add(accuracyInfo);
            // pareto最优解
            paretoOptimalSolutions.add(solutions.get(i));
        }


        List<List<String>> frontInfos = buildFrontInfo(paretoOptimalSolutions, generation);
        String refactorsInfo = buildRefactorInfo(paretoOptimalSolutions, generation);

        ExcelOutPut.append2Excel(configuration.getOutputCatalog() + "indicators_" + configuration.getCurrentExpTime() + ".xlsx", "data", indicatorInfos);
        ExcelOutPut.append2Excel(configuration.getOutputCatalog() + "accuracies_" + configuration.getCurrentExpTime() + ".xlsx", "data", accuracyInfos);
        ExcelOutPut.append2Excel(configuration.getOutputCatalog() + "fronts_" + configuration.getCurrentExpTime() + ".xlsx", "data", frontInfos);
        TxtOutput.append2File(configuration.getOutputCatalog() + "refactors_" + configuration.getCurrentExpTime() + ".txt", refactorsInfo);
    }

    private static String buildRefactorInfo(List<Chromosome> paretoOptimalSolutions, int generation) {
        StringBuilder refactorInfo = new StringBuilder();
        refactorInfo.append(ConsolePrinter.createTitle(generation + "") + "\r\n");
        for (Chromosome paretoOptimalSolution : paretoOptimalSolutions) {
            List<String> refactors = ((RefactorChromosome)paretoOptimalSolution).getRefactors();
            for (String refactor : refactors) {
                refactorInfo.append(refactor + "\r\n");
            }
            refactorInfo.append("\r\n");
        }
        return refactorInfo.toString();
    }

    private static List<List<String>> buildFrontInfo(List<Chromosome> paretoOptimalSolutions, int generation) {
        List<List<String>> solutionInfos = new ArrayList<>();
        if (generation == 100) {
            solutionInfos.add(Arrays.asList("RANK", "value1", "value2", "value3"));
        }
        solutionInfos.add(Arrays.asList("代数: " + generation));
        List<List<Double>> vectors = solution2Vectors(paretoOptimalSolutions);
        for (int i = 0; i < vectors.size(); i++) {
            List<String> solutionInfo = new ArrayList<>();
            List<Double> vector = vectors.get(i);
            solutionInfo.add(paretoOptimalSolutions.get(i).getRank() + "");
            for (int j = 0; j < vector.size(); j++) {
                solutionInfo.add(vector.get(j) + "");
            }
            solutionInfos.add(solutionInfo);
        }
        return solutionInfos;
    }


    /**
     * 将一组解solutions转换为解向量
     * - 若solutions为null或size=0，则返回null
     * @param solutions
     * @return
     */
    private static List<List<Double>> solution2Vectors(List<Chromosome> solutions) {
        if (solutions == null || solutions.size() == 0) {
            return null;
        }
        List<List<Double>> vectors = new ArrayList<>();
        for (int i = 0; i < solutions.size(); i++) {
            List<Double> vector = solutions.get(i).getObjectiveValues();
            vectors.add(vector);
        }
        return vectors;
    }

    private static List<String> buildAccuracyInfo(Graph originGraph,
                                            List<String> systemComponentNodeIds,
                                            List<String> systemSmellComponentNodeIds,
                                            Graph refactoredGraph) {
        List<String> accuracyInfo = new ArrayList<>();
        int TP = 0; // 有异味并被重构
        int FN = 0; // 有异味未被重构
        int TN = 0; // 没有异味且没被重构
        int FP = 0; // 没有异味但被重构
        for (String systemComponentNodeId : systemComponentNodeIds) { // 遍历所有项目内组件节点ID
            if (isRefactored(originGraph, originGraph.findNodeById(systemComponentNodeId),
                                refactoredGraph, refactoredGraph.findNodeById(systemComponentNodeId))) { // 若被重构
                if (systemSmellComponentNodeIds.contains(systemComponentNodeId)) { // 有异味且被重构
                    TP++;
                } else { // 无异味且被重构
                    FP++;
                }
            } else { // 若未被重构
                if (systemSmellComponentNodeIds.contains(systemComponentNodeId)) { // 有异味未重构
                    FN++;
                } else { // 无异味未重构
                    TN++;
                }
            }
        }

        double sensitivity = ((double) TP / (TP + FN));
        double specificity = ((double) TN / (TN + FP));
        accuracyInfo.add(TP + "");
        accuracyInfo.add(FN + "");
        accuracyInfo.add(TN + "");
        accuracyInfo.add(FP + "");
        accuracyInfo.add(format.format(sensitivity));
        accuracyInfo.add(format.format(specificity));
        return accuracyInfo;
    }

    private static List<String> buildIndicatorInfo(Graph refactoredGraph,
                                                   double originHLCount,
                                                   double originUDCount,
                                                   double originCDCount,
                                                   double originHLUDSmellCount,
                                                   double originTotalSmellCount,
                                                   double originCohesion,
                                                   double originCoupling) {
        List<String> indicatorInfo = new ArrayList<>();
        // 异味消除率
        Set<Node> refactoredHLNodes = HubLikeDependencyDetector.detect(refactoredGraph).keySet();
        Set<Node> refactoredUDNodes = UnstableDependencyDetector.detect(refactoredGraph).keySet();
        Set<Node> refactoredCDNodes = new HashSet<>(CyclicDependencyDetector.detect(refactoredGraph));
        double refactoredHLCount =  refactoredHLNodes.size();
        double refactoredUDCount =  refactoredUDNodes.size();
        double refactoredCDCount =  refactoredCDNodes.size();
        double refactoredHLUDSmellCount =  refactoredHLCount + refactoredUDCount;
        double refactoredTotalSmellCount = refactoredHLCount + refactoredUDCount + refactoredCDCount;
        double hlER = 1 + ((originHLCount - refactoredHLCount) / originHLCount);
        double udER = 1 + ((originUDCount - refactoredUDCount) / originUDCount);
        double cdER = 1 + ((originCDCount - refactoredCDCount) / originCDCount);
        double totalHLUDER = 1 + ((originHLUDSmellCount - refactoredHLUDSmellCount) / originHLUDSmellCount);
        double totalSmellER = 1 + ((originTotalSmellCount - refactoredTotalSmellCount) / originTotalSmellCount);
        indicatorInfo.add(format.format(hlER));
        indicatorInfo.add(format.format(udER));
        indicatorInfo.add(format.format(totalHLUDER));
        indicatorInfo.add(format.format(cdER));
        indicatorInfo.add(format.format(totalSmellER));

        // 内聚耦合提升率
        double refactoredCohesion = ObjectiveUtil.calCohesion(refactoredGraph);
        double refactoredCoupling = ObjectiveUtil.calCoupling(refactoredGraph);
        double cohesionImprove = 1 + ((refactoredCohesion - originCohesion)/ originCohesion);
        double couplingImprove = 1 + ((originCoupling - refactoredCoupling)/ originCoupling);
        indicatorInfo.add(format.format(cohesionImprove));
        indicatorInfo.add(format.format(couplingImprove));

        return indicatorInfo;
    }

    /**
     * 判断一个组件是否被重构，只有一个组件中的文件被移走时，才算被重构
     * - 若被重构，则返回true，否则，返回false
     * @param originGraph
     * @param originComponentNode
     * @param refactoredGraph
     * @param refactoredComponentNode
     * @return
     */
    public static boolean isRefactored(Graph originGraph, Node originComponentNode, Graph refactoredGraph,  Node refactoredComponentNode) {
        List<Node> originSubFileNodes = GraphManager.findSubFileNodes(originGraph, originComponentNode);
        List<String> originSubFileNodeIds = GraphManager.nodes2Ids(originSubFileNodes);

        List<Node> refactoredSubFileNodes = GraphManager.findSubFileNodes(refactoredGraph, refactoredComponentNode);
        List<String> refactoredSubFileNodeIds = GraphManager.nodes2Ids(refactoredSubFileNodes);

        if (originSubFileNodeIds != null && refactoredSubFileNodeIds == null) { // 初始有文件&&重构后无文件(表示文件被移走)
            return true;
        }

        if ((originSubFileNodeIds == null && refactoredSubFileNodeIds != null)) { // 初始无文件&&重构后有文件(表示文件被移进来)
            return false;
        }

        if (originSubFileNodeIds == null && refactoredSubFileNodeIds == null) { // 初始无文件&&重构后无文件(表示无文件移动)
            return false;
        }

        for (String originSubFileNodeId : originSubFileNodeIds) {
            if (!refactoredSubFileNodeIds.contains(originSubFileNodeId)) { // 若重构后的组件中不包含初始组件中的文件，则表示有文件被移走，该组件被重构
                return true;
            }
        }
        return false;
    }
}
