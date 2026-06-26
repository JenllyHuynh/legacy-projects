package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.OrganizerDTO;
import vn.edu.fpt.model.entity.Organizer;
import vn.edu.fpt.repository.OrganizerRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizerService {

    private final OrganizerRepo organizerRepo;

    public OrganizerService(OrganizerRepo organizerRepo) {
        this.organizerRepo = organizerRepo;
    }

    public List<Organizer> getAllOrganizer() {
        return organizerRepo.findAll();
    }

    public Optional<Organizer> getOrganizerById(Integer id) {
        return organizerRepo.findById(id);
    }


    public int createOrganizer(OrganizerDTO dto) {

        Organizer organizer = new Organizer();

        organizer.setName(dto.getName());
        organizer.setLogoUrl(dto.getLogoUrl());
        organizer.setDescription(dto.getDescription());
        organizer.setEmail(dto.getEmail());
        organizer.setPhone(dto.getPhone());
        organizer.setWebsite(dto.getWebsite());
        organizer.setStatus(dto.getStatus());
        organizer.setCreatedAt(LocalDateTime.now());

        organizerRepo.save(organizer);
        return organizer.getId();
    }

}
