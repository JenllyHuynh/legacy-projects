package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.entity.Role;
import vn.edu.fpt.repository.RoleRepo;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public List<Role> getAll() {
        return roleRepo.findAll();
    }

    public Optional<Role> getRoleById(int id) {
        return roleRepo.findById(id);
    }
}
