package com.ibg.receipt.base.vo;

import com.ibg.receipt.util.BeanMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVo<T> implements Serializable {

    private static final long serialVersionUID = 1266795568475950859L;

    private Integer pageNum;

    private Integer pageSize;

    private Long total;

    private List<T> list;

    public PageVo(Page<?> page, Class<T> clazz, Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (page == null) {
            this.total = 0L;
            this.list = Collections.emptyList();
        } else {
            this.total = page.getTotalElements();
            this.list = BeanMapper.mapList(page.getContent(), clazz);
        }
    }

}
