/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.video.VideosDashboardResponse;
import com.mgdsw.sdss.dto.video.VideosResponse;
import com.mgdsw.sdss.entity.challenge.ChallengeVideo;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.repository.challenge.ChallengeVideoRepository;
import com.mgdsw.sdss.service.ChallengeVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class ChallengeVideoServiceImpl extends AbstractBaseServiceImpl<Integer, ChallengeVideo> implements ChallengeVideoService {

    private final ChallengeVideoRepository challengeVideoRepository;

    @Override
    protected ChallengeVideoRepository repository() {
        return challengeVideoRepository;
    }

    @Override
    @Transactional
    public void create(List<String> linkList, Challenges challenges) {
        List<ChallengeVideo> challengeVideoList =
                linkList.stream().map(link -> ChallengeVideo.builder().challenges(challenges).link(link).build()).collect(Collectors.toList());
        challengeVideoRepository.saveAll(challengeVideoList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideosDashboardResponse> retrieveAllYoutubeVideosByChallengeId(Integer challengeId) {
        if (Objects.nonNull(challengeId)) {
            // Retrieve videos for the specific challenge
            List<VideosResponse> videos = this.findYoutubeVideosByChallenge(challengeId);
            // Return a single response for the given challengeId
            return Collections.singletonList(VideosDashboardResponse.builder()
                    .videosResponseList(videos)
                    .challengeId(challengeId)
                    .build());
        } else {
            // Use a Map to store unique VideosDashboardResponse entries by challengeId
            Map<Integer, VideosDashboardResponse> responseMap = new HashMap<>();
            List<ChallengeVideo> challengeVideoList = challengeVideoRepository.findAll();

            // Process each ChallengeVideo and group by unique challengeId
            challengeVideoList.forEach(video -> {
                Integer vidChallengeId = (video.getChallenges() != null) ? video.getChallenges().getId() : null;
                if (vidChallengeId != null && !responseMap.containsKey(vidChallengeId)) {
                    // If this challengeId hasn't been added to the map, create a new response
                    List<VideosResponse> videoResponses = this.findYoutubeVideosByChallenge(vidChallengeId);
                    responseMap.put(vidChallengeId, VideosDashboardResponse.builder()
                            .challengeId(vidChallengeId)
                            .videosResponseList(videoResponses)
                            .build());
                }
            });

            // Convert Map values to List to get unique responses by challengeId
            return new ArrayList<>(responseMap.values());
        }
    }

    @Override
    @Transactional
    public void removeAllByChallenge(Challenges challengeNeedToRemove) {
        challengeVideoRepository.deleteByChallenges(challengeNeedToRemove);
    }

    private List<VideosResponse> findYoutubeVideosByChallenge(Integer challengeId) {
        return challengeVideoRepository.findChallengeVideosByChallengeId(challengeId)
                .stream().map(video -> modelMapper.map(video, VideosResponse.class)).collect(Collectors.toList());
    }
}