package com.mgdsw.sdss.repository.survey;

/**
 * Aggregated rating values for a single challenge solution.
 */
public interface SolutionRatingProjection {

    Integer getSolutionId();

    Double getAverageRating();

    Long getRatingCount();
}
