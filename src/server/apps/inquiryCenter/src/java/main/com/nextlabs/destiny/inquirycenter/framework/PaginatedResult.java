package com.nextlabs.destiny.inquirycenter.framework;

import java.util.List;

public class PaginatedResult {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List data;
    private String error;

    public PaginatedResult() {
    }

    public PaginatedResult(int draw, int recordsTotal, int recordsFiltered, List data, String error) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
        this.error = error;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public void setData(List data) {
        this.data = data;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getDraw() {
        return draw;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public Object getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
