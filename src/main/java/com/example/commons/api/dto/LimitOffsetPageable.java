package com.example.commons.api.dto;

import java.util.Optional;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class LimitOffsetPageable implements Pageable {

    @Min(value = 0)
    private Integer offset;

    @Min(value = 1)
    @Max(value = 100)
    private Integer limit;

    public LimitOffsetPageable(Integer limit, Integer offset) {
        this.offset = Optional.ofNullable(offset).orElse(0);
        this.limit = Optional.ofNullable(limit).orElse(50);
    }

    @Override
    public int getPageNumber() {
        return this.offset / this.limit;
    }

    @Override
    public int getPageSize() {
        return this.limit;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }

    @Override
    public Pageable next() {
        return new LimitOffsetPageable(this.getPageSize(), (int) this.getOffset() + this.getPageSize());
    }

    public Pageable previous() {
        return this.hasPrevious()
            ? new LimitOffsetPageable(this.getPageSize(), (int) this.getOffset() - this.getPageSize()) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }

    @Override
    public Pageable first() {
        return new LimitOffsetPageable(this.getPageSize(), 0);
    }

    @Override
    public boolean hasPrevious() {
        return this.offset > this.limit;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new LimitOffsetPageable(this.limit, this.limit * (pageNumber - 1));
    }

}
