package com.mgdsw.sdss.dto.surveys.user;

import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 01 Jul 2025
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveyUserParticipationStats implements BaseMarkerDTO {

    private long totalSurveys;
    private long answeredSurveys;
    private long pendingSurveys;
    private double participationRate;
    private long recentActivity;
    private LocalDateTime lastActivityDate;
}