package com.mgdsw.sdss.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn MAY 2024
 */

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDropDownResponse {

    private Long id;
    private String employeeNumber;
    private String emailAddress;
    private String fullName;
}
