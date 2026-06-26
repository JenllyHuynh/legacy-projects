package vn.edu.fpt.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.entity.DiscountCode;
import vn.edu.fpt.model.entity.Venue;
import vn.edu.fpt.repository.VenueRepo;
import vn.edu.fpt.util.PageSetting;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VenueService {

    private final VenueRepo venueRepo;

    public VenueService(VenueRepo venueRepo) {
        this.venueRepo = venueRepo;
    }

    // Lấy dữ liệu
    public List<Venue> getAll() { return this.venueRepo.findAll();}

    public Page<Venue> getVenueByPage(int page) {
        Pageable pageable = PageRequest.of(page,
                PageSetting.SIZE_OF_EACH_VENUE);

        return venueRepo.findAll(pageable);
    }

    public Page<Venue> getVenueByName(String name, int page) {
        Pageable pageable = PageRequest.of(page,
                PageSetting.SIZE_OF_EACH_VENUE);

        return venueRepo.findVenueByName(name, pageable);
    }

    public List<String> getFilterVenues() {
        return venueRepo.getAllCities();
    }

    // Chỉnh sửa dữ liệu
    @Transactional
    public void deleteVenue(int id) {
            this.venueRepo.deleteById(id);
    }

    @Transactional
    public int saveVenue(Venue venue) {
        try {
            venueRepo.save(venue);
            return 1;
        } catch (Exception e) {
            System.out.println("save Venue fail");
            System.out.println(e.getMessage());
            return 0;
        }
    }

    @Transactional
    public Venue findVenueById(int venueId) {
        return venueRepo.findById(venueId);
    }

    public Optional<Venue> getVenueById(Integer id) {
        return venueRepo.findById(id);
    }

    @Transactional
    public int updateVenue(Venue newVenue) {
        Venue venue = venueRepo.findById((int)newVenue.getId());
        venue.update(newVenue);
        return 1;
    }

    @Transactional
    public void deactive(int id) {
        try {
            Venue venue = venueRepo.findById(id);
            venue.setIsActive(!venue.getIsActive());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public String checkSize(int capacity) {
        if (capacity <= 0) {
            System.out.println("Capacity is invalid");
            return "Invalid capacity";
        } else if (capacity < 10) {
            System.out.println("Size of venue is too small");
            return "Too small";
        } else if (capacity < 100) {
            System.out.println("Size of venue is small");
            return "Small";
        } else if (capacity < 500) {
            System.out.println("Size of venue is medium");
            return "Medium";
        } else if (capacity <= 5000) {
            System.out.println("Size of venue is large");
            return "Large";
        } else {
            System.out.println("Size of venue is Mega");
            return "Mega";
        }
    }

    public Page<Venue> findVenuesWithCriteria(String filter, String name, int page) {
        Pageable pageable = PageRequest.of(page,
                PageSetting.SIZE_OF_EACH_DISCOUNT,
                 Sort.by("id").descending());
            switch (filter) {
                case "all":
                    if (name == null) {
                        return venueRepo.findAll(pageable);
                    } else {
                        return venueRepo.findVenueByName(name, pageable);
                    }
                case "isActive":
                    if (name == null) {
                        return venueRepo.findVenueByIsActive(pageable);
                    } else {
                        return venueRepo.findVenueByIsActiveAndName(name, pageable);
                    }
                case "inActive":
                    if (name == null) {
                        return venueRepo.findVenueByInActive(pageable);
                    } else {
                        return venueRepo.findVenueByInActiveAndName(name, pageable);
                    }
                default:
                    return null;
            }
        }

}
