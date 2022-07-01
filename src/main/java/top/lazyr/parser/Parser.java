package top.lazyr.parser;

import top.lazyr.architecture.diagram.Graph;

/**
 * @author lazyr
 * @created 2022/1/25
 */
public interface Parser {
    Graph parse(String path);
}
