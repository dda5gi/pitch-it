package com.alppano.speakon.domain.resume.entity;

import com.alppano.speakon.common.entity.BaseTimeEntity;
import com.alppano.speakon.domain.datafile.entity.DataFile;
import com.alppano.speakon.domain.interview_join.entity.InterviewJoin;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Resume extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "interview_join_id", nullable = false, unique = true)
    private InterviewJoin interviewJoin;

    @OneToOne
    @JoinColumn(name = "data_file_id")
    private DataFile dataFile;

}
