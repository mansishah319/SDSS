/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.variety;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.entity.challenge.Challenges;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tweets")
public class Tweets extends BaseEntity<Integer> {

    private String tweetId;

    @Column(name = "tweet_date")
    @CreationTimestamp
    private LocalDateTime tweetDate;

    private Integer hashtagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    public Challenges challenges;

    @Column(length = 1000)
    private String content;

    private String hashtag;
    private String otherHashtags;
    private String media;
    private String username;
    private String name;
    private String profileImageUrl;

    private Integer friendsCount;
    private Integer statusesCount;
    private Integer userFollowers;
    private Integer retweetCount;
    private Integer favoriteCount;
}
