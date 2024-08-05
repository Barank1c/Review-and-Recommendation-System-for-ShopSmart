package hbg.rrssbackend.service;

import hbg.rrssbackend.model.ApplyRole;
import hbg.rrssbackend.model.Role;
import hbg.rrssbackend.repository.ApplyRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ApplyRoleService {
    private final ApplyRoleRepository applyRoleRepository;
    public ApplyRoleService(ApplyRoleRepository applyRoleRepository) {
        this.applyRoleRepository = applyRoleRepository;
    }

    public ApplyRole findIsApplied(long userId, Role role){
        return applyRoleRepository.findIsApplied(userId,role);
    }

    public Optional<ApplyRole> findById(long arId){
        return applyRoleRepository.findById(arId);
    }

    public void removeById(long arId){
        applyRoleRepository.deleteById(arId);
    }

    public void save(ApplyRole applyRole) {
        applyRoleRepository.save(applyRole);
    }

    @Transactional
    public List<ApplyRole> getApplyRolesByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return applyRoleRepository.getApplyRolesByPage(offset, pageSize);
    }

    public void removeUserRequests(long userId) {
        applyRoleRepository.removeUserRequests(userId);
    }


    public int getApplyRolesCount() {
        return applyRoleRepository.getApplyRolesCount();
    }


}
