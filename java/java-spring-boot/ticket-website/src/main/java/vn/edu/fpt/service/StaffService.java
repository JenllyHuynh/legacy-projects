package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.entity.Staff;
import vn.edu.fpt.repository.StaffRepo;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepo staffRepo;

    public StaffService(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    public Staff getStaffByEmail(String email) {
        return staffRepo.getStaffByEmail(email);
    }

    public List<Staff> getAllStaff() {return staffRepo.findAll(); }

    public Page<Staff> getPageStaffByStatus(
            int pageNumber,
            int pageSize,
            Boolean isActive
    ) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return staffRepo.getPageStaffByStatus(
                isActive,
                pageable
        );
    }

    @Transactional
    public void changeStatus(Integer id, boolean active) {

        Staff staff = staffRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found"));

        staff.setIsActive(active);
    }

    public Optional<Staff> getStaffById(String email) {
        return staffRepo.findByEmail(email);
    }
}
