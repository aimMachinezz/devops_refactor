package top.lazyr.constant;

/**
 * @author lazyr
 * @created 2022/1/26
 */
public interface NodeConstant {
    /* 对应from属性 */
    String FROM_SYSTEM = "SYSTEM";
    String FROM_NON_SYSTEM = "NON_SYSTEM";
    /* 在查询时使用：获取所有来源的Node */
    String FROM_ALL = "ALL";

    /* 对应level属性 */
    String LEVEL_COMPONENT = "COMPONENT";
    String LEVEL_FILE = "FILE";
    /* 在查询时使用：获取所有粒度的Node */
    String LEVEL_ALL = "ALL";

    /* 用于组合name、level、from的分隔符 */
    String SEPARATOR = " ~ ";

}
