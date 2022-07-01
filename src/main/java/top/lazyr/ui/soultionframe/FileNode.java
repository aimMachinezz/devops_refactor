package top.lazyr.ui.soultionframe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileNode extends DefaultMutableTreeNode {


        //************通用属性****************
        /* 当前节点是否为目录 */
        private boolean directory;
        /* 当前节点的名字 */
        private String name;
        /* 当前节点的完整名字 */
        private String completeName;

        /* 当前节点的重构前传入节点id */
        private List<String> originAfferentIds;
        /* 当前节点的重构前传出节点id */
        private List<String> originEfferentIds;


        //************文件属性****************
        /* 当前文件在原目录中是否被移走 */
        private boolean moved;
        /* 当前文件在现目录中是否是新移动进来的 */
        private boolean created;
        /* 当前文件重构前所在的目录 */
        private String originCatalog;
        /* 当前文件重构后所在的目录 */
        private String refactoredCatalog;


        //************组件属性****************
        /* 当前目录是否为组件 */
        private boolean component;
        /* 当前组件是否修改过 */
        private boolean updated;
        /* 当前组件是否为系统内组件 */
        private boolean system;

        /* 当前组件重构前是否有枢纽型异味 */
        private boolean originHubLike;
        /* 当前组件重构前是否有枢纽型异味 */
        private boolean originCyclic;
        /* 当前组件重构前是否有不稳定依赖异味 */
        private boolean originUnstable;

        /* 当前组件重构后是否有枢纽型异味 */
        private boolean refactoredHubLike;
        /* 当前组件重构后是否有枢纽型异味 */
        private boolean refactoredCyclic;
        /* 当前组件重构后是否有不稳定依赖异味 */
        private boolean refactoredUnstable;

        /* 当前组件的重构后的传入节点id */
        private List<String> refactoredAfferentIds;
        /* 当前组件的重构后的传出节点id */
        private List<String> refactoredEfferentIds;



        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (directory) {
                builder.append("(");
                builder.append(updated ? "update," : "");

                builder.append(originHubLike ? "o_hl," : "");
                builder.append(refactoredHubLike ? "r_hl," : "");

                builder.append(originCyclic ? "o_cd," : "");
                builder.append(refactoredCyclic ? "r_cd," : "");

                builder.append(originUnstable ? "o_ud," : "");
                builder.append(refactoredUnstable ? "r_ud," : "");
                if (builder.lastIndexOf(",") != -1) {
                    builder = builder.deleteCharAt(builder.lastIndexOf(","));
                }
                builder.append(")");
            } else {
                builder.append(".java(");
                builder.append(moved ? "not existed," : "");
                builder.append(created ? "created," : "");
                if (builder.lastIndexOf(",") != -1) {
                    builder = builder.deleteCharAt(builder.lastIndexOf(","));
                }
                builder.append(")");
            }
            return name + (builder.toString().contains("()") ? builder.toString().contains(".java") ? ".java" : "" : builder.toString());
        }
    }
