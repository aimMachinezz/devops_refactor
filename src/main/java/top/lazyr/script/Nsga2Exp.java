package top.lazyr.script;

import cn.hutool.core.util.StrUtil;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.constant.ConsolePrinter;
import top.lazyr.io.output.TxtOutput;
import top.lazyr.parser.SourceCodeParser;
import top.lazyr.refactor.algorithm.genetic.nsgaii.Configuration;
import top.lazyr.refactor.algorithm.genetic.nsgaii.NSGA2;
import top.lazyr.refactor.algorithm.genetic.nsgaii.crossover.AbstractCrossover;
import top.lazyr.refactor.algorithm.genetic.nsgaii.crossover.SingleCrossover;
import top.lazyr.refactor.algorithm.genetic.nsgaii.mutation.AbstractMutation;
import top.lazyr.refactor.algorithm.genetic.nsgaii.mutation.SingleMutation;
import top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction.*;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.childpopulationproducer.ChildPopulationProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.childpopulationproducer.RefactorChildPopulationProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.geneticcodeproducer.GeneticCodeProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.geneticcodeproducer.RefactorProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.populationproducer.PopulationProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.populationproducer.RefactorPopulationProducer;
import top.lazyr.refactor.algorithm.genetic.nsgaii.selector.ChampionshipSelector;
import top.lazyr.refactor.algorithm.genetic.nsgaii.selector.CrossoverParticipantCreator;
import top.lazyr.refactor.algorithm.genetic.nsgaii.termination.MyTermination;
import top.lazyr.refactor.algorithm.genetic.nsgaii.termination.TerminatingCriterion;
import top.lazyr.refactor.generator.*;
import top.lazyr.script.model.ExpParam;
import top.lazyr.script.service.ExpParamService;
import top.lazyr.smell.detector.cyclicdependency.CyclicDependencyDetector;
import top.lazyr.smell.detector.hublikedependency.HubLikeDependencyDetector;
import top.lazyr.smell.detector.unstabledependency.UnstableDependencyDetector;
import top.lazyr.util.FileUtil;
import top.lazyr.util.ObjectiveUtil;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lazyr
 * @created 2022/2/16
 */
public class Nsga2Exp {

    public static void exp(String configFilePath) {
        List<ExpParam> unFinishedExpParams = ExpParamService.getUnFinishedExpParams(configFilePath);
        for (ExpParam unFinishedExpParam : unFinishedExpParams) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // 创建数据生成目录字符串
            String outputCatalog = createOutputCatalog(dateFormat.format(date), unFinishedExpParam);
            // 创建目录（若已存在，则不再创建）
            FileUtil.createCatalog(outputCatalog);
            // 删除上次未完成的实验数据，并返回已完成的实验次数
            int finishedExpTime = ExpParamService.clear(outputCatalog);

            Long start = System.currentTimeMillis();
            refactor(unFinishedExpParam, finishedExpTime + 1, outputCatalog);
            Long end = System.currentTimeMillis();
            System.out.println("用时: " + ((end - start) / (1000 * 60)) + "分");
            ExpParamService.finishExpParam(unFinishedExpParam, finishedExpTime + 1, outputCatalog);

        }

    }

    private static void refactor(ExpParam expParam, int currentExpTime, String outputCatalog) {
        // 1、获取初始源代码模型
        SourceCodeParser sourceCodeParser = new SourceCodeParser();
        Graph originGraph = sourceCodeParser.parse(expParam.getProjectPath());
        // 保存输出信息
        StringBuilder configurationInfo = new StringBuilder();
        // 2、检测初始异味
        int originHLNum = 0;
        int originUDNum = 0;
        int originCDNum = 0;
        int totalSmellNum = 0;
        int totalSmellComponentNum = 0;
        int totalSmellFileNum = 0;
        Set<Node> smellComponentNodes = new HashSet<>(); // 异味组件
        configurationInfo.append(ConsolePrinter.createTitle("异味信息") + "\r\n");
        if (expParam.isOptimizedHL()) {
            Set<Node> originHLNodes = HubLikeDependencyDetector.detect(originGraph).keySet();
            smellComponentNodes.addAll(originHLNodes);
            originHLNum = originHLNodes.size();
            configurationInfo.append("枢纽型异味: " + originHLNum + "\r\n");
        }
        if (expParam.isOptimizedUD()) {
            Set<Node> originUDNodes = UnstableDependencyDetector.detect(originGraph).keySet();
            smellComponentNodes.addAll(originUDNodes);
            originUDNum = originUDNodes.size();
            configurationInfo.append("不稳定异味: " + originUDNum + "\r\n");
        }
        if (expParam.isOptimizedCD()) {
            Set<Node> originCDNodes = new HashSet<>(CyclicDependencyDetector.detect(originGraph));
            smellComponentNodes.addAll(originCDNodes);
            originCDNum = originCDNodes.size();
            configurationInfo.append("环依赖异味: " + originCDNum + "\r\n");
        }
        totalSmellNum = originHLNum + originUDNum + originCDNum;
        totalSmellComponentNum = smellComponentNodes.size();
        totalSmellFileNum = findSubFiles(originGraph, smellComponentNodes);
        configurationInfo.append("异味总个数: " + totalSmellNum + "\r\n");
        configurationInfo.append("异味组件个数: " + totalSmellComponentNum + "\r\n");
        configurationInfo.append("异味文件个数: " + totalSmellFileNum + "\r\n");
        // 3、设置重构操作列表生成方案
        RefactorListGenerator refactorListGenerator = null; // 重构操作列表生成器
        switch (expParam.getPlanNum()) {
            case 1:
                refactorListGenerator = new RefactorListGenerator1(originGraph, new ArrayList<>(smellComponentNodes));
                break;
            case 2:
                refactorListGenerator = new RefactorListGenerator2(originGraph, new ArrayList<>(smellComponentNodes));
                break;
            case 3:
                refactorListGenerator = new RefactorListGenerator3(originGraph, new ArrayList<>(smellComponentNodes));
                break;
            case 4:
                refactorListGenerator = new RefactorListGenerator4(originGraph, new ArrayList<>(smellComponentNodes));
                break;
            default:
                System.out.println("无效重构操作列表生成方案, 请选择正确的重构操作列表生成方案");
                return;
        }
        configurationInfo.append(ConsolePrinter.createTitle("耦合&&内聚指标") + "\r\n");
        // 设置目标函数
        List<AbstractObjectiveFunction> objectiveFunctions = new ArrayList<>();
        // 添加异味消除率优化目标
        objectiveFunctions.add(new SmellEliminationRate(totalSmellNum, expParam.isOptimizedHL(), expParam.isOptimizedUD(), expParam.isOptimizedCD()));

        // 添加内聚指标优化目标
        if (expParam.isOptimizedCohesion()) {
            double originCohesion = ObjectiveUtil.calCohesion(originGraph);
            objectiveFunctions.add(new Cohesion1(originCohesion));
            configurationInfo.append("内聚指标: " + originCohesion + "\r\n");
        }
        // 添加耦合指标优化目标
        if (expParam.isOptimizedCoupling()) {
            double originCoupling = ObjectiveUtil.calCoupling(originGraph);
            objectiveFunctions.add(new Coupling1(originCoupling));
            configurationInfo.append("耦合指标: " + originCoupling + "\r\n");
        }
        if (!(expParam.isOptimizedCohesion() || expParam.isOptimizedCoupling())) { // 此时认为为单目标优化算法
            objectiveFunctions.add(new Placeholder());
            configurationInfo.append("占位指标: 0\r\n");
        }
        Configuration configuration = new Configuration(objectiveFunctions);
        // 遗传算法
        // 设置种群生成器
        PopulationProducer populationProducer = new RefactorPopulationProducer(originGraph); // 初始种群生成器
        GeneticCodeProducer geneticCodeProducer = new RefactorProducer(refactorListGenerator); // 基因型生成器
        ChildPopulationProducer childPopulationProducer = new RefactorChildPopulationProducer(originGraph); // 子代生成器
        configuration.setPopulationProducer(populationProducer);
        configuration.setGeneticCodeProducer(geneticCodeProducer);
        configuration.setChildPopulationProducer(childPopulationProducer);
        // 设置参数
        configuration.setPopulationSize(expParam.getPopulationSize()); // 种群大小
        configuration.setMaxGenerations(expParam.getMaxGenerationCount()); // 演进代数
        configuration.setChromosomeLength((int) (expParam.getChromosomeLengthRatio() * totalSmellFileNum)); // 基因型长度
        // 设置算子
        CrossoverParticipantCreator selector = new ChampionshipSelector(); // 选择算子: 锦标赛选择策略
        AbstractCrossover crossover = new SingleCrossover(selector, expParam.getCrossoverProbability()); // 交叉算子：单点交叉算子
        AbstractMutation mutation = new SingleMutation(expParam.getMutationProbability(), refactorListGenerator); // 变异算子：单点变异
        configuration.setCrossover(crossover);
        configuration.setMutation(mutation);
        // 设置终止条件
        TerminatingCriterion terminatingCriterion = new MyTermination(configuration); // 终止条件：迭代到最大代数时终止
        configuration.setTerminatingCriterion(terminatingCriterion);
        // 设置实验过程数据（输出目录）
        configuration.setOutputCatalog(outputCatalog);
        configuration.setCurrentExpTime(currentExpTime);
        configuration.setOptimizedHL(expParam.isOptimizedHL());
        configuration.setOptimizedUD(expParam.isOptimizedUD());
        configuration.setOptimizedCD(expParam.isOptimizedCD());


        configurationInfo.append(ConsolePrinter.createTitle("NSGA2算法参数") + "\r\n");
        configurationInfo.append("crossoverProbability : " + expParam.getCrossoverProbability() + "\r\n");
        configurationInfo.append("mutationProbability : " + expParam.getMutationProbability() + "\r\n");


        configurationInfo.append(configuration.toString());

        TxtOutput.write2File(outputCatalog + "实验详细数据.txt", configurationInfo.toString());

        System.out.println(configurationInfo.toString()); // 输出配置信息

        // 运行NSGA2多目标优化算法
        NSGA2 nsga2 = new NSGA2(configuration);
        nsga2.run();
    }

    private static String createOutputCatalog(String day, ExpParam expParam) {
        String optimizationObjectives = (expParam.isOptimizedHL() ? "hl_" : "") +
                (expParam.isOptimizedUD() ? "ud_" : "") +
                (expParam.isOptimizedCD() ? "cd_" : "") +
                (expParam.isOptimizedCohesion() ? "cohesion_" : "") +
                (expParam.isOptimizedCoupling() ? "coupling_" : "");
        optimizationObjectives = StrUtil.strip(optimizationObjectives, "_");
        String outputCatalog = day + "/" + expParam.getProjectName() + "/" + optimizationObjectives +
                "/plan" + expParam.getPlanNum() + "/" +
                expParam.getPopulationSize() + "_" +
                expParam.getCrossoverProbability() + "_" +
                expParam.getMutationProbability() + "_" +
                expParam.getChromosomeLengthRatio() + "/";
        return outputCatalog;
    }




    /**
     * 获取所有componentNodes中的文件Node个数
     * @param componentNodes
     * @return
     */
    private static int findSubFiles(Graph graph, Set<Node> componentNodes) {
        int subFileNum  = 0;
        for (Node componentNode : componentNodes) {
            List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, componentNode);
            subFileNum += subFileNodes == null ? 0 : subFileNodes.size();
        }
        return subFileNum;
    }



}
