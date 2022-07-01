package top.lazyr.constant;

/**
 * @author lazyr
 * @created 2022/1/6
 */
public interface RefactorConstant {
    /**
     * 移动一个文件到一个项目内的组件中
     */
    String MOVE_FILE = "moveFile";

    /**
     * 同时移动多个文件到项目内的多个组件中
     */
    String MOVE_FILES = "moveFiles";

    /**
     * 抽取一组文件到一个新的组件中
     */
    String EXTRACT_COMPONENT = "extractComponent";
    /**
     * 行为分割符
     */
    String ACTION_SEPARATOR = ",";

    /**
     * 操作分隔符
     * extractComponent,fileNodeId1>sourceComponentNodeId1;fileNodeId2>sourceComponentNodeId2,from_sourceComponentNodeId
     * 表示
     * 将 sourceComponentNodeId1组件内的fileNodeId1 和 sourceComponentNodeId2组件下的fileNodeId2 抽取到from_sourceComponentNodeId组件中
     * moveFiles,fileNodeId1>sourceComponentNodeId1>targetComponentNodeId1;fileNodeId2>sourceComponentNodeId2>targetComponentNodeId2
     */
    String CONTENT_SEPARATOR = ";";
    String BELONG_SEPARATOR = ">";

}
