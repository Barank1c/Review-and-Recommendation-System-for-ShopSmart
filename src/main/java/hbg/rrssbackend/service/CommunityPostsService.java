package hbg.rrssbackend.service;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CommunityPosts;
import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.repository.CommunityPostsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityPostsService {
    private final CommunityPostsRepository communityPostsRepository;

    public CommunityPostsService(CommunityPostsRepository communityPostsRepository) {
        this.communityPostsRepository = communityPostsRepository;
    }

    public Optional<CommunityPosts> getCommunityPostById(long cpId) {
        return communityPostsRepository.findById(cpId);
    }

    public List<CommunityPosts> getCommunityPostsByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityPostsRepository.findCommunityPostsByPage(pageSize, offset);
    }

    public CommunityPosts saveCommunityPost(CommunityPosts communityPosts) {
        return communityPostsRepository.save(communityPosts);
    }

    public void deleteCommunityPost(Long cpId) {
        communityPostsRepository.deleteById(cpId);
    }

    public long countTotalPosts() {
        return communityPostsRepository.countAllPosts();
    }






}
