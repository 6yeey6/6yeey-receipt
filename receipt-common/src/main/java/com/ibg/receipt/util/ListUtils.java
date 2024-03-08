package com.ibg.receipt.util;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * List 工具类
 */
@Slf4j
public class ListUtils {

    /**
     * 对List进行分组，返回List<List>
     *
     * @param orginLists
     *            原始list
     * @param pageNum
     *            每页数
     * @return
     */
    public static <T> List<List<T>> spileList(List<T> orginLists, int pageNum) {

        List<List<T>> list = new ArrayList<List<T>>();
        int size = orginLists.size();
        int shard = (orginLists.size()) % pageNum == 0 ? (orginLists.size() / pageNum)
                : (orginLists.size() / pageNum + 1);
        log.info("List分批 总数：{} 每页数量: {} ", size, pageNum);
        for (int j = 1; j <= shard; j++) {
            int start = (j - 1) * pageNum;
            int end = j * pageNum >= size ? size : j * pageNum;
            list.add(orginLists.subList(start, end));
            log.info("第 {} 批 start {} end {} ", j, start, end);
        }
        log.info("List 分批 分页总数{}： 每页数量: {} 总数:{}", shard, pageNum, size);

        return list;
    }

    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        while (list.size() <= 10000) {
            list.add("1");
        }

        List x = spileList(list, 5000);

    }

}
