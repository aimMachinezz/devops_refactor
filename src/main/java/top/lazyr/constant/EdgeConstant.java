package top.lazyr.constant;

/**
 * 软件制品图
 * @author lazyr
 * @created 2022/1/26
 */
public interface EdgeConstant {
    /**
     * 表示文件与文件之间的依赖关系 or 组件与组件之间的依赖关系
     * - 若文件A中依赖文件B中，则 文件A ==DEPEND==> 文件B
     * - 若组件A中的文件调用了组件B中的文件，则 组件A ==DEPEND==> 组件B
     */
    String DEPEND = "DEPEND";

    /**
     * 表示文件与组件的从属关系
     * - 若文件A属于组件B，则 文件A ==BELONG==> 组件B
     */
    String BELONG = "BELONG";

    /* 在查询时使用：获取所有类型的边 */
    String ALL = "ALL";
}
