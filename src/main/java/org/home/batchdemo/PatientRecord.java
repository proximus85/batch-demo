package org.home.batchdemo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class PatientRecord implements Serializable {
    private final String firstName;
}
