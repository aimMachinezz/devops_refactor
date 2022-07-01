package top.lazyr.refactor.generator;

import top.lazyr.constant.RefactorConstant;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lazyr
 * @created 2022/1/28
 */
public abstract class RefactorListGenerator {

    /**
     * 随机生成一个重构操作
     * @return
     */
    public abstract String generateOne(int r);

    /**
     * 在refactors之后随机生成一个重构操作
     * @return
     */
    public abstract String generateOne(List<String> refactors, int r);

    /**
     * 生成r个互不冲突的重构操作
     * @param r
     * @return
     */
    public abstract List<String> generateList(int r);

    /**
     * 返回重构方案生成方法简介
     * @return
     */
    public abstract String getIntroduction();

    /**
     * 若refactor中重构的文件和refactors中要重构的文件已经重复，则认为refactor无效，返回false;
     * 否则返回true
     * @param refactors
     * @param refactor
     * @return
     */
    protected boolean refactorValidate(List<String> refactors, String refactor) {
        if (refactors == null) {
            return false;
        }
        if (refactors.size() == 0) {
            return true;
        }

        // 已经被重构的文件
        Set<String> existedRefactoredFileIds = new HashSet<>();
        for (String r : refactors) {
            Set<String> fileIds = getFileIdsFromRefactor(r);
            existedRefactoredFileIds.addAll(fileIds);
        }

        Set<String> fileIds = getFileIdsFromRefactor(refactor);

        boolean existed = false;
        for (String fileId : fileIds) {
            if (existedRefactoredFileIds.contains(fileId)) {
                existed = true;
            }
            existedRefactoredFileIds.add(fileId);
        }
        return !existed;
    }

    /**
     * 若refactor和refactors中无一样的重构操作则认为有效
     * @param refactors
     * @param refactor
     * @return
     */
    protected boolean refactorValidate2(List<String> refactors, String refactor) {
        if (refactors == null) {
            return false;
        }
        if (refactors.size() == 0) {
            return true;
        }
        return !refactors.contains(refactor);
    }

    /**
     * 从重构格式字符串中抽取待重构的文件列表
     * @param refactor
     * @return
     */
    private Set<String> getFileIdsFromRefactor(String refactor) {
        String[] actions = refactor.split(RefactorConstant.ACTION_SEPARATOR);
        Set<String> fileIds = new HashSet<>();
        switch (actions[0]) {
            case RefactorConstant.MOVE_FILE:
                fileIds.add(actions[2]);
                break;
            case RefactorConstant.EXTRACT_COMPONENT:
                String[] fileComponentIds = actions[1].split(RefactorConstant.CONTENT_SEPARATOR);
                for (String fileComponentId : fileComponentIds) {
                    fileIds.add(fileComponentId.split(RefactorConstant.BELONG_SEPARATOR)[0]);
                }
                break;
            case RefactorConstant.MOVE_FILES:
                String[] fileSourceTargetIds = actions[1].split(RefactorConstant.CONTENT_SEPARATOR);
                for (String fileSourceTargetId : fileSourceTargetIds) {
                    fileIds.add(fileSourceTargetId.split(RefactorConstant.BELONG_SEPARATOR)[0]);
                }
                break;
        }
        return fileIds;
    }


    /**
     * 生成moveFile格式原子操作字符串
     * 生成格式: moveFile,sourceComponentNodeId,fileNodeId,targetComponentNodeId
     * - 若fileNodeId为""，则返回""
     * @param sourceComponentNodeId
     * @param fileNodeId
     * @param targetComponentNodeId
     * @return
     */
    public static String moveFile(String sourceComponentNodeId, String fileNodeId, String targetComponentNodeId) {
        if (fileNodeId.equals("")) {
            return "";
        }
        StringBuilder moveFile = new StringBuilder();
        moveFile.append(RefactorConstant.MOVE_FILE +
                RefactorConstant.ACTION_SEPARATOR +
                sourceComponentNodeId +
                RefactorConstant.ACTION_SEPARATOR +
                fileNodeId +
                RefactorConstant.ACTION_SEPARATOR +
                targetComponentNodeId);
        return moveFile.toString();
    }

    /**
     * 生成moveFiles格式原子操作字符串
     * moveFiles,fileNodeId1>sourceComponentNodeId1>targetComponentNodeId1;fileNodeId2>sourceComponentNodeId2>targetComponentNodeId2
     * @param fileSourceComponentIds
     * @param fileTargetComponentIds
     * @return
     */
    public static String moveFiles(Map<String, String> fileSourceComponentIds, Map<String, String> fileTargetComponentIds) {
        if (fileSourceComponentIds == null || fileSourceComponentIds.size() == 0 ||
                fileTargetComponentIds == null || fileTargetComponentIds.size() == 0 ||
                fileSourceComponentIds.size() != fileTargetComponentIds.size()) {
            return "";
        }
        StringBuilder moveFiles = new StringBuilder();
        moveFiles.append(RefactorConstant.MOVE_FILES +
                RefactorConstant.ACTION_SEPARATOR);
        int i = 0;
        for (String fileId : fileSourceComponentIds.keySet()) {
            moveFiles.append(fileId + RefactorConstant.BELONG_SEPARATOR
                    + fileSourceComponentIds.get(fileId) + RefactorConstant.BELONG_SEPARATOR
                    + fileTargetComponentIds.get(fileId));
            if (i++ != fileSourceComponentIds.size() - 1) {
                moveFiles.append(RefactorConstant.CONTENT_SEPARATOR);
            }
        }
        return moveFiles.toString();
    }

    /**
     * 生成extractComponent格式原子操作字符串
     * 生成格式: extractComponent,fileNodeId1>sourceComponentNodeId1;fileNodeId2>sourceComponentNodeId2,from_sourceComponentNodeId
     * - 若fileNames为空，则返回""
     * - 对同一个sourceComponentNodeId每次抽取到的新组件名都是from_sourceComponentNodeId
     * @param sourceComponentNodeId
     * @param fileComponentIds
     * @return
     */
    public static String extractComponent(String sourceComponentNodeId, Map<String, String> fileComponentIds) {
        if (fileComponentIds == null || fileComponentIds.size() == 0) {
            return "";
        }
        StringBuilder extractComponent = new StringBuilder();
        extractComponent.append(RefactorConstant.EXTRACT_COMPONENT +
                RefactorConstant.ACTION_SEPARATOR);

        int i = 0;
        for (String fileId : fileComponentIds.keySet()) {
            extractComponent.append(fileId + RefactorConstant.BELONG_SEPARATOR + fileComponentIds.get(fileId));
            if (i++ != fileComponentIds.size() - 1) {
                extractComponent.append(RefactorConstant.CONTENT_SEPARATOR);
            }
        }

        extractComponent.append(RefactorConstant.ACTION_SEPARATOR + newComponentNodeId(sourceComponentNodeId));
        return extractComponent.toString();
    }

    /**
     * a.b.c[SEPARATOR][level][SEPARATOR][from] => a.b.from_c[SEPARATOR][level][SEPARATOR][from]
     * @param packageName
     * @return
     */
    private static String newComponentNodeId(String packageName) {
        // TODO:考虑packageName不合法的情况，如packageName=""
        if (!packageName.contains(".")) {
            return "from_" + packageName;
        }
        String prefix = packageName.substring(0, packageName.lastIndexOf("."));
        String currentPackageName = packageName.substring(packageName.lastIndexOf(".") + 1);
        return prefix + ".from_" + currentPackageName;
    }

}
