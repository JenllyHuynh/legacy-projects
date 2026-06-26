package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.entity.Comment;
import vn.edu.fpt.repository.CommentRepo;

import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepo commentRepo;

    public CommentService(CommentRepo commentRepo) {
        this.commentRepo = commentRepo;
    }

    public boolean hasCommented(int customerId, int eventId) {
        return commentRepo.hasCommented(customerId, eventId);
    }

    public void save(Comment comment) {
        commentRepo.save(comment);
    }

    public Double getAverageRating(int id) {
        return commentRepo.getAverageRating(id);
    }

    public Double getAvgOfStart(int eventId, int start) {
        Long total = commentRepo.getTotalComments(eventId);
        Long count = commentRepo.getCountOfStart(eventId, start);
        Double percent = total > 0 ? (count * 1.0 / total) : 0.0;
        return percent;
    }

    public Optional<Comment> getById(int id) {
        return commentRepo.findById(id);
    }

    public void delete(Comment comment) {
        commentRepo.delete(comment);
    }
}
