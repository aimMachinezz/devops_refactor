package top.lazyr.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;

/**
 * @author lazyr
 * @created 2022/1/30
 */
public class LineChart {
    /* 折线图的标题 */
    private String title;
    /* 横坐标名字 */
    private String xAxisName;
    /* 纵坐标名字 */
    private String yAxisName;
    /* 折线图的数据 */
    private DefaultCategoryDataset dataset;
    /* 折线图表 */
    private JFreeChart chart;


    public LineChart(String title, String xAxisName, String yAxisName, DefaultCategoryDataset dataset) {
        this.title = title;
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        this.dataset = dataset;
    }

    public LineChart(String title, String xAxisName, String yAxisName) {
        this(title, xAxisName, yAxisName, new DefaultCategoryDataset());
    }

    public void show() {
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setLargeFont(new Font("Times-Roman", Font.BOLD, 15));
        mChartTheme.setExtraLargeFont(new Font("Times-Roman", Font.PLAIN, 15));
        mChartTheme.setRegularFont(new Font("Times-Roman", Font.PLAIN, 8));
        ChartFactory.setChartTheme(mChartTheme);
        chart = ChartFactory.createLineChart(
                title,//图名字
                xAxisName,//横坐标
                yAxisName,//纵坐标
                dataset,//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器
                true);// 是否生成超链接

        CategoryPlot mPlot = (CategoryPlot)chart.getPlot();
        mPlot.setBackgroundPaint(Color.LIGHT_GRAY);
        mPlot.setRangeGridlinePaint(Color.BLUE);//背景底部横虚线
        mPlot.setOutlinePaint(Color.RED);//边界线
        CategoryAxis categoryaxis = mPlot.getDomainAxis(); // 横轴上的 Label倾斜90
        categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        ChartFrame mChartFrame = new ChartFrame(title, chart);
        mChartFrame.pack();
        mChartFrame.setVisible(true);
    }

    public void addDate(String lineName, String xAxis, double value) {
        this.dataset.addValue(value, lineName, xAxis);
    }

    public JFreeChart getJFreeChart() {
        return chart;
    }
}
