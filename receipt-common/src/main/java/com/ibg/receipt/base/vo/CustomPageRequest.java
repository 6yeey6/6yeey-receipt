package com.ibg.receipt.base.vo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * @Date 2020-03-04
 * 注意：该分页工具类只用于前端调用使用，如果是服务内部逻辑需要分页查询，如 Slice<Loan> = findxxxx(String xx, Pageable pageable)
 * 当需要根据Slice.nextPageable()循环查询下一页时，清勿使用CustomPageRequest作为Pagebale入参。因为其构造方法会将当前页减一，此时
 * 循环获取下一页时传入的当前页永远是第一页，会造成程序死循环。
 */
public class CustomPageRequest extends PageRequest {
    private static final long serialVersionUID = -4541509938956089562L;
    private final Sort sort;

    public CustomPageRequest(int page, int size) {
        this(page, size, (Sort) null);
    }

    public CustomPageRequest(int page, int size, Direction direction, String... properties) {
        this(page, size, new Sort(direction, properties));
    }

    public CustomPageRequest(int page, int size, Sort sort) {
        super(page - 1, size);
        this.sort = sort;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return new PageRequest(this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    @Override
    public PageRequest previous() {
        return this.getPageNumber() == 0 ? this
                : new PageRequest(this.getPageNumber() - 1, this.getPageSize(), this.getSort());
    }

    @Override
    public Pageable first() {
        return new PageRequest(0, this.getPageSize(), this.getSort());
    }
}
