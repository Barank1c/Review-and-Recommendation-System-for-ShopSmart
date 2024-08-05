package hbg.rrssbackend.service;

import hbg.rrssbackend.model.CPComments;
import hbg.rrssbackend.repository.CPCommentsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CPCommentsService {
    private final CPCommentsRepository cpCommentsRepository;
    public CPCommentsService(CPCommentsRepository cpCommentsRepository) {
        this.cpCommentsRepository = cpCommentsRepository;
    }

    public List<CPComments> getPostCommentsByPage(long postId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return cpCommentsRepository.findPostCommentsByPage(postId, pageSize, offset);
    }

    public void deleteCommentById(Long commentId) {
        cpCommentsRepository.deleteByCommentId(commentId);
    }

    public long countCommentsByPostId(Long cpId) {
        return cpCommentsRepository.countCommentsByPostId(cpId);
    }


}
