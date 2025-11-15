package com.ovvium.services.util.common.domain;

import com.ovvium.services.util.common.domain.adapters.PageableAdapter;
import com.ovvium.services.util.ovvium.base.Preconditions;
import lombok.Getter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Objects;

import static com.ovvium.services.util.common.domain.SimplePage.FIRST_PAGE;


@Getter
@XmlJavaTypeAdapter(PageableAdapter.class)
public class Pageable implements Serializable {

    private static final int MAX_SIZE = 100;
    private static final long serialVersionUID = 8280485938848398236L;
    
    private final int pageNumber;
    private final int pageSize;
    private final Sort sort;

    public Pageable(int size) {
        this(FIRST_PAGE, size);
    }

    public Pageable(int page, int size) {
        this(page, size, null);
    }

    public Pageable(int page, int size, Direction direction, String... properties) {
        this(page, size, new Sort(direction, properties));
    }

    public Pageable(int page, int size, Sort sort) {
        this.pageNumber = Preconditions.check(page, page >= 0 , "Page index must not be less than zero!");
        Preconditions.check(size, size > 0 , "Page pageSize must not be less than or equal to zero!");
        this.pageSize = Math.min(size, MAX_SIZE); // we have MAX_SIZE so we don´t allow to get all results at once
        this.sort = sort;
    }

    public int getOffset() {
        return pageNumber * pageSize;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Pageable)) {
            return false;
        }

        Pageable that = (Pageable) obj;

        boolean pageEqual = this.pageNumber == that.pageNumber;
        boolean sizeEqual = this.pageSize == that.pageSize;

        boolean sortEqual = Objects.equals(this.sort, that.sort);

        return pageEqual && sizeEqual && sortEqual;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + pageNumber;
        result = 31 * result + pageSize;
        result = 31 * result + (null == sort ? 0 : sort.hashCode());

        return result;
    }
}
