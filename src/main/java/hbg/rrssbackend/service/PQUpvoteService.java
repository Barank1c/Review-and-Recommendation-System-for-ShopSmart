package hbg.rrssbackend.service;

import hbg.rrssbackend.model.PQUpvote;
import hbg.rrssbackend.model.RRUpvote;
import hbg.rrssbackend.repository.PQUpvoteRepository;
import org.springframework.stereotype.Service;

@Service
public class PQUpvoteService {
    private final PQUpvoteRepository pqUpvoteRepository;

    public PQUpvoteService(PQUpvoteRepository pqUpvoteRepository) {
        this.pqUpvoteRepository = pqUpvoteRepository;
    }

    public boolean isQUpvotedByUser(long userId, long pqId) {
        PQUpvote pqUpvote = pqUpvoteRepository.isQUpvotedByUser(userId,pqId);
        return pqUpvote != null;
    }

    public Integer getTotalUpvotesOfQ(long pqId){
        return pqUpvoteRepository.getTotalUpvotesOfQ(pqId);
    }

    public void upvoteQ(long userId,long pqId){
        pqUpvoteRepository.upvoteQ( userId, pqId);
    }

    public void unvoteQ(long userId,long pqId){
        pqUpvoteRepository.unvoteQ( userId, pqId);
    }
}
