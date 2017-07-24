package de.digitalcollections.commons.springdata.domain;

import de.digitalcollections.core.model.api.paging.PageRequest;
import de.digitalcollections.core.model.api.paging.Sorting;
import de.digitalcollections.core.model.impl.paging.PageRequestImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableConverter {

  public static PageRequest convert(Pageable pageable) {
    if (pageable == null) {
      return null;
    }
    int pageNumber = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    final Sort sort = pageable.getSort();
    Sorting sorting = SortConverter.convert(sort);

    PageRequest page = new PageRequestImpl(pageNumber, pageSize, sorting);
    return page;
  }

  public static Pageable convert(PageRequest pageRequest) {
    if (pageRequest == null) {
      return null;
    }
    int pageNumber = pageRequest.getPageNumber();
    int pageSize = pageRequest.getPageSize();

    final Sorting sorting = pageRequest.getSorting();
    Sort sort = SortConverter.convert(sorting);

    Pageable pageable = new org.springframework.data.domain.PageRequest(pageNumber, pageSize, sort);
    return pageable;
  }
}