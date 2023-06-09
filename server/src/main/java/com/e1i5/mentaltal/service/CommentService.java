package com.e1i5.mentaltal.service;

import com.e1i5.mentaltal.entity.Board;
import com.e1i5.mentaltal.entity.Comment;
import com.e1i5.mentaltal.entity.CommentVote;
import com.e1i5.mentaltal.repository.CommentRepository;
import com.e1i5.mentaltal.repository.CommentVoteRepository;
import com.e1i5.mentaltal.exception.BusinessLogicException;
import com.e1i5.mentaltal.exception.ExceptionCode;
import com.e1i5.mentaltal.entity.Member;
import com.e1i5.mentaltal.utils.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardService boardService;
    private final MemberService memberService;
    private final CommentVoteRepository commentVoteRepository;
    private final CustomBeanUtils<Comment> beanUtils;


    // 답변 등록
    public Comment createComment(Comment comment) {
        Member member = memberService.findMember(comment.getMid());
        Board board = boardService.findVerifiedBoard(comment.getBid());
        comment.addMember(member);
        comment.addBoard(board);
        board.plusCommentCount();

        return commentRepository.save(comment);

    }

    // 답변 수정
    public Comment updateComment(Comment comment) {
        Comment findComment = findVerifiedComment(comment.getCommentId());
        Comment updatedComment = beanUtils.copyNonNullProperties(comment, findComment);
        //comment : 모든 필드를 저장할 변수
        //destination : 모든 필드를 중 변경한 값만 저장할 변수

        updatedComment.setVoteCount(1);
        return commentRepository.save(updatedComment);
    }

    // 답변 조회
    public Comment findComment(long commentId) {
        Comment findComment = findVerifiedComment(commentId);
        commentRepository.save(findComment);

        return findComment;
    }

    // 답변 전체조회
    @Transactional(readOnly = true)
    public List<Comment> findAllComment() {
        return commentRepository.findAll();
    }

    // 답변 페이지네이션
    @Transactional(readOnly = true)
    public Page<Comment> findComments(int page, int size) {
        return commentRepository.findAll(PageRequest.of(page, size, Sort.by("commentId").descending()));
    }

    // 답변 삭제
    @Transactional
    public void deleteComment(long commentId) {
        Comment verifiedComment = findVerifiedComment(commentId);
        Board board = boardService.findVerifiedBoard(verifiedComment.getBoard().getBoardId());
        board.minusCommentCount();
        commentVoteRepository.deleteAllByComment(verifiedComment);
        commentRepository.delete(verifiedComment);
    }

    @Transactional(readOnly = true)
    public Comment findVerifiedComment(long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        // 답변이 db에 존재하는지 검증
        // orElseThrow : 가져온 값이 null이면 예외
        Comment findComment = optionalComment.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        return findComment;
    }

    // 공감
    public long getVoteCount(long commentId) {
        Comment comment = findVerifiedComment(commentId);
        return comment.getVoteCount();
    }

    /**
     * 공감수 (좋아요)
     * TODO 로그인한 회원만 공감 가능 --> 비회원 공감 버튼 클릭 시 "로그인이 필요합니다."
     * @param commentId
     * @param memberId
     * @param voteCheck
     * @return
     */
    public Comment commentVote(long commentId, long memberId, boolean voteCheck) {
        Member member = memberService.findMember(memberId);

        Comment comment = findVerifiedComment(commentId);
        Optional<CommentVote> findVote = commentVoteRepository.findByCommentAndMember(comment, member);

        // 공감을 클릭하지 않은 경우 (findVote 존재 x)
        if (!findVote.isPresent()) {
            comment.setVoteCount(comment.getVoteCount() + (voteCheck ? 1 : -1));    // 공감을 클릭하면 +1, 한 번 더 클릭하면 -1 (공감 취소)
            commentVoteRepository.save(new CommentVote(voteCheck, comment, member));
            return comment;
        }
        // 공감을 클릭한 이력이 있는 경우
        // vote가 true이면 -1, false이면 1 --> true(1)는 공감이 눌러져 있는 상태이므로 0으로 만들어줌 == 공감 취소
        // fasle(0)는 공감을 누르지 않은 상태이므로 1로 만들어줌 == 공감 처리
        comment.setVoteCount(comment.getVoteCount() + (voteCheck ? -1 : 1));
        commentVoteRepository.delete(findVote.get()); // 공감을 클릭한 이력을 삭제
        return comment;
    }
}
