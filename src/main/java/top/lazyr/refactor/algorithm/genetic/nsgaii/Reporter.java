package top.lazyr.refactor.algorithm.genetic.nsgaii;

import org.jfree.chart.ChartUtils;
import top.lazyr.ui.LineChart;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.population.Population;
import top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction.AbstractObjectiveFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reporter {
	private List<LineChart> lineCharts;
	private List<AbstractObjectiveFunction> objectiveFunctions;
	private String prefix = "./src/main/resources/";
	public String outputCatalog = "";

	public void init(Configuration configuration) {
		objectiveFunctions = configuration.objectives;
		lineCharts = new ArrayList<>();
		for (AbstractObjectiveFunction objectiveFunction : objectiveFunctions) {
			lineCharts.add(new LineChart(objectiveFunction.getObjectiveTitle(), "代", objectiveFunction.getObjectiveTitle()));
		}
		for (LineChart lineChart : lineCharts) {
			lineChart.show();
		}
		this.outputCatalog = configuration.getOutputCatalog();
	}

	public void reportGeneration(Population child, int generation) {
		List<Double> averChildObjectives = averObjectives(child);

		for (int i = 0; i < lineCharts.size(); i++) {
			lineCharts.get(i).addDate(objectiveFunctions.get(i).getObjectiveTitle(), generation + "", averChildObjectives.get(i));
		}


	}

	private List<Double> averObjectives(Population population) {
		List<Chromosome> chromosomes = population.getPopulace();
		int size = chromosomes.size();
		List<Double> averObjectives = Arrays.asList(0d, 0d, 0d, 0d, 0d);
		for (Chromosome chromosome : chromosomes) {
			List<Double> objectiveValues = chromosome.getObjectiveValues();
			for (int i = 0; i < objectiveValues.size(); i++) {
				averObjectives.set(i, averObjectives.get(i) + objectiveValues.get(i));
			}
		}
		for (int i = 0; i < averObjectives.size(); i++) {
			averObjectives.set(i, averObjectives.get(i) / size);
		}
		return averObjectives;
	}

	public void terminate() {
		for (int i = 0; i < lineCharts.size(); i++) {
			String objectiveTitle = objectiveFunctions.get(i).getObjectiveTitle();
			FileOutputStream out = null;
			try {
				File outFile = new File(prefix + outputCatalog + objectiveTitle + ".jpeg");
				if (!outFile.getParentFile().exists()) {
					outFile.getParentFile().mkdirs();
				}
				ChartUtils.saveChartAsJPEG(outFile, lineCharts.get(i).getJFreeChart(), 800, 500);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		System.out.println("实验过程数据图片写入成功...");
	}

}
