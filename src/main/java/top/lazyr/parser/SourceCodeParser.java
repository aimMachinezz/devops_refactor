package top.lazyr.parser;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lazyr.constant.NodeConstant;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.parser.manager.CtClassManager;
import top.lazyr.util.FileUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 缺点：可能会缺少某些继承/实现涉及到的组件和文件
 * 原因：只保留了依赖关系
 * @author lazyr
 * @created 2022/1/25
 */
public class SourceCodeParser implements Parser {
    private static Logger logger = LoggerFactory.getLogger(SourceCodeParser.class);
    private CtClassManager ctClassManager;
    private Graph graph;

    public SourceCodeParser() {
        this.ctClassManager = CtClassManager.getCtClassManager();
    }

    @Override
    public Graph parse(String sourceCodePath) {
        this.graph = new Graph();
        List<CtClass> ctClasses = extractCtClass(sourceCodePath);
        extractSystemFileNodes(ctClasses);
        extractSystemComponentNodes(ctClasses);
        buildFileDepend(ctClasses);
        buildFileBelong();
        buildComponentDepend();
        return graph;
    }

    private void buildComponentDepend() {
        List<Node> componentNodes = graph.getComponentNodes();
        for (Node componentNode : componentNodes) {
            List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, componentNode);
            if (subFileNodes == null) {
//                System.out.println(componentNode + " 无子文件节点");
                continue;
            }
            for (Node subFileNode : subFileNodes) {
                List<Node> dependFileNodes = GraphManager.findEfferentNodes(graph, subFileNode);
                if (dependFileNodes == null) { // 无依赖的文件
//                    System.out.println(componentNode + " 子文件无依赖文件");
                    continue;
                }
                for (Node dependFileNode : dependFileNodes) {
                    Node dependComponentNode = GraphManager.findBelongComponent(graph, dependFileNode);
                    if (dependComponentNode == null) { // 若依赖组件不存在，则表示之前处理有错误
                        logger.error("the component of file node({}) does not exist.", dependFileNode);
//                        System.out.println("the component of file node(" + dependFileNode + ") does not exist.");
                        continue;
                    }
                    if (componentNode.equals(dependComponentNode)) { // 若自己依赖自己，则不处理
                        continue;
                    }
//                    System.out.println(componentNode + " => " + dependComponentNode);
                    graph.addDepend(componentNode, dependComponentNode);
                }
            }
        }
    }

    private void buildFileBelong() {
        List<Node> fileNodes = graph.getFileNodes();
        for (Node fileNode : fileNodes) {
            String fileName = fileNode.getName();
            String componentName = extractComponentName(fileName);
            Node componentNode = graph.findNodeById(componentName +
                                                                NodeConstant.SEPARATOR +
                                                                NodeConstant.LEVEL_COMPONENT +
                                                                NodeConstant.SEPARATOR +
                                                                (fileNode.isSystem() ? NodeConstant.FROM_SYSTEM :
                                                                        NodeConstant.FROM_NON_SYSTEM));
            if (componentNode == null) {
                componentNode = fileNode.isSystem() ?
                                        GraphManager.buildSystemComponentNode(componentName) :
                                        GraphManager.buildNonSystemComponentNode(componentName);
                graph.addComponentNode(componentNode);
            }

            graph.updateBelong(fileNode, componentNode);

        }
    }

    /**
     * 从源码文件提取CtClass对象
     * @param sourceCodePath
     * @return
     */
    public List<CtClass> extractCtClass(String sourceCodePath) {
        List<String> filesAbsolutePath = FileUtil.getFilesAbsolutePath(sourceCodePath, ".class");
        List<CtClass> ctClasses = new ArrayList<>();
        for (String fileAbsolutePath : filesAbsolutePath) {
            CtClass ctClass = ctClassManager.getOuterCtClass(fileAbsolutePath);
            ctClasses.add(ctClass);
        }
        return ctClasses;
    }

    /**
     * 从项目源码提取项目内组件Node，若组件内无文件，则这个组件Node不会被提取
     * @param ctClasses
     * @return
     */
    private void extractSystemComponentNodes(List<CtClass> ctClasses) {
        Set<String> componentNames = new HashSet<>();
        for (CtClass ctClass : ctClasses) {
            componentNames.add(ctClass.getPackageName());
        }
        for (String componentName : componentNames) {
            Node componentNode = GraphManager.buildSystemComponentNode(componentName);
            graph.addComponentNode(componentNode);
        }
    }


    /**
     * 将ctClasses转换系统内文件节点
     * @param ctClasses
     * @return
     */
    private void extractSystemFileNodes(List<CtClass> ctClasses) {
        for (CtClass ctClass : ctClasses) {
            Node fileNode = GraphManager.buildSystemFileNode(ctClass.getName());
            graph.addFileNode(fileNode);
        }
    }
    
    /**
     * 构建文件与文件之间的依赖关系（详细构建规则看内部类FileDepend中注释）
     */
    private void buildFileDepend(List<CtClass> ctClasses) {
        Set<String> innerClassNodes = new HashSet<>();
        for (CtClass ctClass : ctClasses) {
            String className = ctClass.getName();
            if (className.contains("$")) {
                innerClassNodes.add(className);
            }
            try {
                ctClass.instrument(new FileDepend(className));
            } catch (CannotCompileException e) {
                logger.error("build depend edge failed: err = " + e.getMessage());
            }
        }

        // 删除内部类节点
        for (String innerClassNode : innerClassNodes) {
            graph.removeFileNode(innerClassNode +
                                            NodeConstant.SEPARATOR +
                                            NodeConstant.LEVEL_FILE +
                                            NodeConstant.SEPARATOR +
                                            NodeConstant.FROM_SYSTEM);
        }
    }
    
    private class FileDepend extends ExprEditor {
        /* 会将内部类转换为外部类名 */
        private String sourceClassName;
        /* sourceClassName对应的外部类Java文件 */
        private Node sourceFileNode;
        /**
         * 使用该类构建依赖关系的sourceClassName都为项目内的类，则默认为Node已创建
         * - 若未创建，则记录为error
         * - 若sourceClassName为内部类的名字且外部类Node未创建，则记录为error
         * - 若sourceClassName则将其转换为外部类的名字
         * @param sourceClassName
         */
        public FileDepend(String sourceClassName) {
            this.sourceClassName = extractOuterClassName(sourceClassName);
            this.sourceFileNode = graph.findNodeById(this.sourceClassName +
                                                    NodeConstant.SEPARATOR +
                                                    NodeConstant.LEVEL_FILE +
                                                    NodeConstant.SEPARATOR +
                                                    NodeConstant.FROM_SYSTEM);
        }

        /**
         * 当 类A调用类B的方法 时触发
         * - 若 类B == 类A，则不认为类A依赖类B；
         * - 若 类B 为 类A的内部类，则不认为类A依赖类B
         * - 若 类B 为 类A的外部类，则不认为类A依赖类B
         * - 若 类A为内部类， 类B不为内部类，则认为类A的外部类依赖类B
         * - 若 类A为内部类，类B为内部类，则认为类A的外部类依赖类B的外部类
         * - 若 类A为普通类，类B为内部类，则认为类A依赖类B的外部类
         * - 若 类B为基本数据类型数组，
         *   - 若调用的为clone方法等数组自己实现的方法，则认为类A依赖类B的包装类
         *   - 否则认为类A依赖Object（编译原理导致的）
         * - 若 类B为普通类型数组，则认为类A依赖类B
         * @param m
         * @throws CannotCompileException
         */
        @Override
        public void edit(MethodCall m) throws CannotCompileException {
            String dependClassName = m.getClassName();
            logger.debug("class({}) call the method({}) of class({}).", this.sourceClassName, m.getMethodName(), dependClassName);
            buildDepend(dependClassName);
        }

        /**
         * 当 类A使用super()调用父类B的构造器 时触发（只会调用父类），暂时废弃
         * @param c
         * @throws CannotCompileException
         */
        @Override
        public void edit(ConstructorCall c) throws CannotCompileException {
            logger.debug("class({}) use the constructor({}) parent class({}).", sourceClassName, c.getMethodName(), c.getClassName());
//            buildDepend(c.getClassName());
        }

        /**
         * 当 类A调用类B的成员变量 时触发
         * - 若成员变量为基本数据类型，则不认为类A依赖类B（编译后会直接将基本数据类型赋值）
         * - 若成员变量为普通类型，则认为类A依赖类B
         * @param f
         * @throws CannotCompileException
         */
        @Override
        public void edit(FieldAccess f) throws CannotCompileException {
            logger.debug("class({}) use the field({}) of class({}).", sourceClassName, f.getFieldName(), f.getClassName());
            buildDepend(f.getClassName());
        }

        /**
         * 当 类A使用new初始化普通类B 时触发
         * - 则认为类A依赖类B
         * @param e
         * @throws CannotCompileException
         */
        @Override
        public void edit(NewExpr e) throws CannotCompileException {
            logger.debug("class({}) new class({}).", sourceClassName, e.getClassName());
            buildDepend(e.getClassName());
        }

        /**
         * 当 类A使用new初始化数组 时触发
         * - 若数组的类型为基本数据类型，则将其转换为对应的包装类B，则认为类A依赖类B
         * - 若数组的类型为普通类型B，则认为类A依赖类B
         * @param a
         * @throws CannotCompileException
         */
        @Override
        public void edit(NewArray a) throws CannotCompileException {
            String dependClassName = "";
            try {
                dependClassName =  unboxing(a.getComponentType().getName());
            } catch (NotFoundException e) {
                dependClassName = e.getMessage();
                logger.error("init array failed: err = " + e.getMessage());
            }
            logger.debug("class({}) new array({}).", sourceClassName, dependClassName);
            buildDepend(dependClassName);
        }

        /**
         * 当 类A中使用类B进行类型转换 时触发
         * - 若类B为基本数据类型，则不认为类A依赖类B的包装类（直接编译成类B）
         * - 若类B为普通类型，则认为类A依赖类B
         * @param c
         * @throws CannotCompileException
         */
        @Override
        public void edit(Cast c) throws CannotCompileException {
            String dependClassName = "";
            try {
                dependClassName = c.getType().getName();
            } catch (NotFoundException e) {
                dependClassName = e.getMessage();
            }
            logger.debug("in the class({}), cast class({}).", sourceClassName, dependClassName);
            buildDepend(dependClassName);
        }

        /**
         * 当 类A中使用try-catch语句时触发
         * - 当使用throws语句抛出异常类B时，则认为类A依赖类B
         * @param h
         * @throws CannotCompileException
         */
        @Override
        public void edit(Handler h) throws CannotCompileException {
            CtClass[] throwsCtClasses = h.mayThrow();
            for (CtClass throwsCtClass : throwsCtClasses) {
                buildDepend(throwsCtClass.getName());
            }
        }

        /**
         * 类A为sourceClassName的外部类，类B为dependClassName的外部类
         * - 若 类B == 类A，则不认为类A依赖类B；
         * - 若 类B 为 类A的内部类，则不认为类A依赖类B
         * - 若 类B 为 类A的外部类，则不认为类A依赖类B
         * - 若 类A为内部类， 类B不为内部类，则认为类A的外部类依赖类B
         * - 若 类A为内部类，类B为内部类，则认为类A的外部类依赖类B的外部类
         * - 若 类A为普通类，类B为内部类，则认为类A依赖类B的外部类
         * - 若 类B为基本数据类型数组，
         *   - 若调用的为clone方法等数组自己实现的方法，则认为类A依赖类B的包装类
         *   - 否则认为类A依赖Object（编译原理导致的）
         * - 若 类B为普通类型数组，则认为类A依赖类B
         * - 若类A不存在，则记录为异常
         * @param dependClassName
         */
        public void buildDepend(String dependClassName) {
            if (this.sourceFileNode == null) { // 若类A不存在，则记录为异常
                logger.error("the class({}) is not existed.", sourceClassName);
                return;
            }
            // 一定非空
            Node dependFileNode = extractFileNode(dependClassName);
            if (dependFileNode.equals(this.sourceFileNode)) { // 若自己依赖自己，则不做处理
                return;
            }
            graph.addDepend(sourceFileNode, dependFileNode);
        }

        /**
         * - 若className为int、float等基本数据类型，则返回对应的包装类型
         * - 若className为数组，则返回数组的类型
         * - 若className为内部类，则返回其外部类Node
         * 若处理后的className对应的文件粒度Node不存在，则默认创建 项目外的的文件粒度的Node 和对应的并添加到graph中后返回
         * TODO: 未判断依赖的节点的外部类是否存在，若不存在则可能出现是通过工具直接生成的内部类
         * @param className
         * @return
         */
        public Node extractFileNode(String className) {
            String outerClassName = arr2Class(extractOuterClassName(unboxing(className)));
            // 获取系统内File粒度Node
            Node fileNode = graph.findNodeById(outerClassName  +
                                                        NodeConstant.SEPARATOR +
                                                        NodeConstant.LEVEL_FILE +
                                                        NodeConstant.SEPARATOR +
                                                        NodeConstant.FROM_SYSTEM);

            if (fileNode == null) { // 若为null，则表示不是系统内的Node
                fileNode = graph.findNodeById(outerClassName  +
                                                        NodeConstant.SEPARATOR +
                                                        NodeConstant.LEVEL_FILE +
                                                        NodeConstant.SEPARATOR +
                                                        NodeConstant.FROM_NON_SYSTEM);
            }

            if (fileNode == null) { // 若还为null，则表示该系统外Node未添加到Graph中
                fileNode = GraphManager.buildNonSystemFileNode(outerClassName);
                graph.addFileNode(fileNode);
                Node nonSystemComponentNode = GraphManager.buildNonSystemComponentNode(extractComponentName(outerClassName));
                graph.addComponentNode(nonSystemComponentNode);
            }
            return fileNode;
        }
    }


    /**
     * 对类型拆箱、数组提取类名之后再提取包名
     * @param className
     * @return
     */
    private String extractComponentName(String className) {
        className= unboxing(className);
        className = arr2Class(className);

        if (!className.contains(".")) {
            logger.info("className({}) does not contain '.'.", className);
            return "";
        }
        String packageName = className.substring(0, className.lastIndexOf("."));
        return packageName;
    }
    
    /**
     * 若className包含$，则返回外部类
     * 若className不包含$，则不做处理直接返回className
     * @param className
     * @return
     */
    private String extractOuterClassName(String className) {
        if (!className.contains("$")) {
            return className;
        }
        return className.substring(0, className.indexOf("$"));
    }

    /**
     * 若baseType为基本数据类型，则将其转换为包装器类型
     * 若baseType不为基本数据类型，则不做处理
     * @param baseType
     * @return
     */
    private String unboxing(String baseType) {
        String className = baseType;
        switch (baseType) {
            case "byte":
                className = Byte.class.getName();
                break;
            case "short":
                className = Short.class.getName();
                break;
            case "int":
                className = Integer.class.getName();
                break;
            case "long":
                className = Long.class.getName();
                break;
            case "float":
                className = Float.class.getName();
                break;
            case "double":
                className = Double.class.getName();
                break;
            case "char":
                className = Character.class.getName();
                break;
            case "boolean":
                className = Boolean.class.getName();
                break;
        }
        return className;
    }

    /**
     * 若arrName为数组，则返回数组的类型类名
     * - 若数组的类型为基本数据类型则将其转换为包装类再返回
     * - 若数组的类型为普通类则返回普通类
     * 若arrName为普通类，则不做处理直接返回arrName
     * @param arrName
     * @return
     */
    private String arr2Class(String arrName) {
        if (!arrName.contains("[]")) {
            return arrName;
        }
        return unboxing(arrName.substring(0, arrName.indexOf("[")));
    }
}
