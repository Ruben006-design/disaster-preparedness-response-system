package com.disasterresponse.dto;

import java.util.Collections;
import java.util.List;

/**
 * Standard pagination response envelope matching Spring Data Page JSON structure.
 * Guarantees zero breakage for React frontend data tables and paginators.
 */
public class PageResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private boolean first;
    private boolean last;
    private boolean empty;

    public PageResponse() {
        this.content = Collections.emptyList();
    }

    public PageResponse(List<T> content, long totalElements, int page, int size) {
        this.content = content != null ? content : Collections.emptyList();
        this.totalElements = totalElements;
        this.number = page;
        this.size = size > 0 ? size : 10;
        this.totalPages = this.size > 0 ? (int) Math.ceil((double) totalElements / this.size) : 0;
        this.first = page == 0;
        this.last = page >= Math.max(0, this.totalPages - 1);
        this.empty = this.content.isEmpty();
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
