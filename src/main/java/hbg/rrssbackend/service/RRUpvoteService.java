package hbg.rrssbackend.service;

import hbg.rrssbackend.model.RRUpvote;
import hbg.rrssbackend.model.ReviewUpvote;
import hbg.rrssbackend.repository.RRUpvoteRepository;
import org.springframework.stereotype.Service;

@Service
public class RRUpvoteService {
    private final RRUpvoteRepository rrUpvoteRepository;

    public RRUpvoteService(RRUpvoteRepository rrUpvoteRepository){
        this.rrUpvoteRepository = rrUpvoteRepository;
    }

    public boolean isRRUpvotedByUser(long userId, long rrId) {
        RRUpvote rrUpvote = rrUpvoteRepository.isRRUpvotedByUser(userId,rrId);
        return rrUpvote != null;
    }

    public Integer getTotalUpvotesOfRR(long rrId){
        return rrUpvoteRepository.getTotalUpvotesOfRR(rrId);
    }

    public void upvoteRR(long userId,long rrId){
        rrUpvoteRepository.upvoteRR( userId, rrId);
    }

    public void unvoteRR(long userId,long rrId){
        rrUpvoteRepository.unvoteRR( userId, rrId);
    }
}
