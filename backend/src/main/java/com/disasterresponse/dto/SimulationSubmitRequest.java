package com.disasterresponse.dto;

public class SimulationSubmitRequest {

    private Long selectedOptionId;

    public SimulationSubmitRequest() {
    }

    public SimulationSubmitRequest(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
