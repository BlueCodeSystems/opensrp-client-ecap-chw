package org.smartregister.chw.core.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class VisitSummary {
    private String visitType;
    private Date visitDate;
    private Date dateCreated;
    private String baseEntityID;
}
