package com.e1i5.mentaltal.entity;

import com.e1i5.mentaltal.audit.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Transient
    private Long mid;

    private String nickName;

    @Column(nullable = false, length = 256)
    private String title;

    @Column(nullable = false, columnDefinition = "Text")
    private String content;

    private String tags;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private long voteCount;  // 공감수 (좋아요)

    @Column(columnDefinition = "integer default 0")
    private long commentCount;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    public void addMember(Member member) {
        if ( this.member != null) {
            this.member.getBoards().remove(this);
        }
        this.member = member;
        this.member.getBoards().add(this);
    }

    public void plusCommentCount() {
        commentCount++;
    }

    public void minusCommentCount () {commentCount--;}
}
