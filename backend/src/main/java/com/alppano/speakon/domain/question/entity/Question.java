package com.alppano.speakon.domain.question.entity;

import com.alppano.speakon.domain.feedback.entity.Feedback;
import com.alppano.speakon.domain.interview_join.entity.InterviewJoin;
import com.alppano.speakon.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 200, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "interview_join_id", nullable = false)
    private InterviewJoin interviewJoin;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writer;

    @Builder.Default
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();

    public void setInterviewJoin(InterviewJoin interviewJoin) {
        if (this.interviewJoin != null) {
            this.interviewJoin.getQuestions().remove(this);
        }

        this.interviewJoin = interviewJoin;
        this.interviewJoin.getQuestions().add(this);
    }

}