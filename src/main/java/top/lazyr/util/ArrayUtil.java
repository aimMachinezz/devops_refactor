package top.lazyr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lazyr
 * @created 2021/11/26
 */
public class ArrayUtil {
    /**
     * 将数组转化为List
     * - 若arr为null，则返回null
     * - 若arr的length=0, 则返回size=0的List
     * @param arr
     * @param <E>
     * @return
     */
    public static <E> List<E> arr2List(E[] arr) {
        if (arr == null) {
            return null;
        }
        List<E> list = new ArrayList<>();
        for (E e : arr) {
            list.add(e);
        }
        return list;
    }


    /**
     * 将Set转换为List
     * @param set
     * @param <E>
     * @return
     */
    public static <E> List<E> set2List(Set<E> set) {
        if (set == null) {
            return null;
        }
        List<E> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }

    /**
     * 将Map中的所有value转换为List
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> List<V> map2List(Map<K, V> map) {
        if (map == null) {
            return null;
        }
        List<V> list = new ArrayList<>();
        for (K key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    /**
     * 将 list2 的start到end-1个元素按顺序添加到list1中
     * @param list1
     * @param list2
     * @param start
     * @param end
     * @param <E>
     */
    public static <E> void moveArr2Arr(List<E> list1, List<E> list2, int start, int end) {
        if (list2.size() < end || start >= end || start < 0) {
            return;
        }
        for (int i = start; i < end; i++) {
            list1.add(list2.get(i));
        }
    }


    /**
     * 返回list1和list2的diff结果
     * [
     *      // 第一个List存放list1中不存在，但list2中存在的元素
     *      [],
     *      // 第二个List存放list1中存在，但list2中不存在的元素
     *      [],
     *      // 第三个List存放list1和list2中共同存在的元素
     *      []
     * ]
     * @param list1
     * @param list2
     * @return
     */
    public static <T> List<List<T>> diff(List<T> list1, List<T> list2) {
        List<List<T>> diffResult = new ArrayList<>();
        // 存放list1中不存在，但list2中存在的元素
        List<T> moved = new ArrayList<>();
        // 存放list1中存在，但list2中不存在的元素
        List<T> created = new ArrayList<>();
        // 存放list1和list2中共同存在的元素
        List<T> noRefactored = new ArrayList<>();

        diffResult.add(moved);
        diffResult.add(created);
        diffResult.add(noRefactored);
        if (list1 == null && list2 == null) {
            return diffResult;
        } else if (list1 == null && list2 != null) {
            created.addAll(list2);
            return diffResult;
        } else if (list1 != null && list2 == null) {
            moved.addAll(list1);
            return diffResult;
        }

        for (T originFile : list1) {
            if (!list2.contains(originFile)) {
                moved.add(originFile);
            } else {
                noRefactored.add(originFile);
            }
        }
        for (T refactoredFile : list2) {
            if (!list1.contains(refactoredFile)) {
                created.add(refactoredFile);
            }
        }

        return diffResult;
    }

}
