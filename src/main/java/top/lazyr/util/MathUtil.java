package top.lazyr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author lazyr
 * @created 2022/1/5
 */
public class MathUtil {
    private static Random random = new Random();

    /**
     * 返回所有0 - n的排列组合可能
     * @param n
     * @return
     */
    public static List<List<Integer>> combine(int n) {
        List<List<Integer>> combineList = new ArrayList<>();
        for (int i = 1; i <= n; i++) { // i为
            List<List<Integer>> combine = combine(new ArrayList<>(), new ArrayList<>(), n - 1, i);
            combineList.addAll(combine);

        }

        return combineList;
    }

    private static List<List<Integer>> combine(List<Integer> temp, List<List<Integer>> ans, int n, int k) {
        dfs(temp, ans, 0, n, k);
        return ans;
    }

    private static void dfs(List<Integer> temp, List<List<Integer>> ans, int cur, int n, int k) {
        // 剪枝：temp 长度加上区间 [cur, n] 的长度小于 k，不可能构造出长度为 k 的 temp
        if (temp.size() + (n - cur + 1) < k) {
            return;
        }
        // 记录合法的答案
        if (temp.size() == k) {
            ans.add(new ArrayList<Integer>(temp));
            return;
        }
        // 考虑选择当前位置
        temp.add(cur);
        dfs(temp, ans,cur + 1, n, k);
        temp.remove(temp.size() - 1);
        // 考虑不选择当前位置
        dfs(temp, ans,cur + 1, n, k);
    }


    /**
     * 返回一个随机整数x (1 <= x <= n)
     * @param n
     * @return
     */
    public static int randomFromOne2N(int n) {
        return random.nextInt(n) + 1;
    }

    /**
     * 返回一个随机整数x (0 <= x < n)
     * @param n
     * @return
     */
    public static int randomFromZero(int n) {
        return random.nextInt(n);
    }


}
