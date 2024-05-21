package com.dragunov.schedulerservice.dto;

import lombok.Data;

@Data
public class VacancyImportScheduledTaskDto {

    private String query;

    private Integer pageIndex;

    private Integer pageSize;

}
