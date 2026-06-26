package vn.edu.fpt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.entity.DiscountCode;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.repository.DiscountCodeRepo;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.util.PageSetting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@EnableScheduling
@Service
public class DiscountCodeService {
    private static final Logger logger = LoggerFactory.getLogger(DiscountCodeService.class);

    @Autowired
    DiscountCodeRepo discountCodeRepo;
    @Autowired
    EventRepo eventRepo;

    //Hàm tính toán
    public int getTotalActiveDiscount(List<DiscountCode> discountCodes) {
        int total = 0;
        for (DiscountCode dc: discountCodes) {
            if (dc.getIsActive()) {
                total += 1;
            }
        }
        System.out.println("số lượng active discounts là: " + total);
        return total;
    }

    public double applyDiscount(String discountType, double value, double originalFee) {
        double finalFee = originalFee;
        switch (discountType) {
            case "Percentage":
                if (value >= 1 && value < 100) {
                    finalFee = originalFee  * (1 - value/100);
                } else {
                    System.out.println("Invalid value! value must be greater or equal than 1 and smaller or equal 100");
                    return finalFee;
                }
                break;
            case "FixedAmount":
                if (value >= 1) {
                    if (value <= originalFee) {
                        finalFee = originalFee - value;
                    } else {
                        finalFee = 0;
                    }
                } else {
                    System.out.println("Invalid value! value must be greater or equal than 1");
                    return finalFee;
                }
                break;
            default:
                System.out.println("Invalid discountType");
                return finalFee;
        }
        System.out.println("Fee after apply discount: " + finalFee);
        return finalFee;
    }

    public boolean checkLength(String code) {
        if (code.length() >= 8) {
            System.out.println("Valid code");
            return true;
        } else {
            System.out.println("Invalid code");
            return false;
        }
    }

    public boolean canUseDiscount(int usedCount, int maxUses) {
        if (maxUses <= 0) {
            System.out.println("Invalid maxUses");
            return false;
        }
        if (usedCount < 0) {
            System.out.println("Invalid useCount");
            return false;
        }
        if (usedCount >= maxUses) {
            System.out.println("Can not use discount");
            return false;
        } else {
            System.out.println("Can use discount");
            return true;
        }
    }


    //Lấy dữ liệu
    public List<DiscountCode> getAll() {
        return discountCodeRepo.findAll();
    }

    public Page<DiscountCode> getDiscountPage(int page) {
        Pageable pageable = PageRequest.of(page,
                PageSetting.SIZE_OF_EACH_DISCOUNT);
        return discountCodeRepo.findAll(pageable);
    }

    public Page<DiscountCode> findDiscountsWithCriteria(String discountType, String filter, String code, int page, int staffId) {
        Pageable pageable = PageRequest.of(page,
                PageSetting.SIZE_OF_EACH_DISCOUNT,
                Sort.by("id").descending());
        if (discountType.equals("all")) {
            switch (filter) {
                case "all":
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findAllByEvent_EventStaffAssignments_staffId(staffId, pageable);
                    } else {
                        return discountCodeRepo.findDiscountByCode(staffId, code, pageable);
                    }
                case "isActive":
                    if (code == null || code.isEmpty()) {
                        System.err.println(1);
                        return discountCodeRepo.findDiscountByIsActive(staffId, pageable);
                    } else {
                        System.err.println(2);
                        return discountCodeRepo.findDiscountByIsActiveAndCode(staffId, code, pageable);
                    }
                case "inactive":
                    if (code == null || code.isEmpty()) {
                        System.err.println(1);
                        return discountCodeRepo.findDiscountByInactive(staffId, pageable);
                    } else {
                        System.err.println(2);
                        return discountCodeRepo.findDiscountByInactiveAndCode(staffId, code, pageable);
                    }
                case "expried":
                    LocalDateTime now = LocalDateTime.now();
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findExpiredDiscounts(staffId, now, pageable);
                    } else {
                        return discountCodeRepo.findExpiredDiscountsAndCode(staffId, code, now, pageable);
                    }
                default:
                    return null;
            }
        } else if (discountType.equals("percentage")) {
            switch (filter) {
                case "all":
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findByDiscountType(staffId, pageable, discountType);
                    } else {
                        return discountCodeRepo.findPercentageDiscountByCode(staffId, code, pageable);
                    }
                case "isActive":
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findPercentageDiscountByIsActive(staffId, pageable);
                    } else {
                        return discountCodeRepo.findPercentageDiscountByIsActiveAndCode(staffId, code, pageable);
                    }
                case "inactive":
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findPercentageDiscountByInactive(staffId, pageable);
                    } else {
                        return discountCodeRepo.findPercentageDiscountByInactiveAndCode(staffId, code, pageable);
                    }
                case "expried":
                    LocalDateTime now = LocalDateTime.now();
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findPercentageExpiredDiscounts(staffId, now, pageable);
                    } else {
                        return discountCodeRepo.findPercentageExpiredDiscountsAndCode(staffId, code, now, pageable);
                    }
                default:
                    return null;
            }
        } else if (discountType.equals("fixedAmount")) {
            switch (filter) {
                case "all":
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findByDiscountType(staffId, pageable, discountType);
                    } else {
                        return discountCodeRepo.findfixedAmountDiscountByCode(staffId, code, pageable);
                    }
                case "isActive":
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findfixedAmountDiscountByIsActive(staffId, pageable);
                    } else {
                        return discountCodeRepo.findfixedAmountDiscountByIsActiveAndCode(staffId, code, pageable);
                    }
                case "inactive":
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findfixedAmountDiscountByInactive(staffId, pageable);
                    } else {
                        return discountCodeRepo.findfixedAmountDiscountByInactiveAndCode(staffId, code, pageable);
                    }
                case "expried":
                    LocalDateTime now = LocalDateTime.now();
                    if (code == null || code.isEmpty()) {
                        return discountCodeRepo.findfixedAmountExpiredDiscounts(staffId, now, pageable);
                    } else {
                        return discountCodeRepo.findfixedAmountExpiredDiscountsAndCode(staffId, code, now, pageable);
                    }
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public DiscountCode getDiscountCodeById(int id) {
        DiscountCode dc = discountCodeRepo.findDiscountById(id);
        return dc;
    }

    public List<DiscountCode> getDiscountCodeByOrganizerId(Integer OrganizerId) {
        return discountCodeRepo.findByEvent_Organizer_Id(OrganizerId);
    }

    //chỉnh sửa hoặc thêm dữ liệu
    @Transactional
    public int editDiscountCode(DiscountCode discountCode, Integer eventId ) {
        DiscountCode dc = discountCodeRepo.findDiscountById(discountCode.getId());
        if (dc == null) {
            return -1;
        }
        Event event = eventRepo.getEventById(eventId);
        dc.setEvent(event);
        dc.updateDiscountCode(discountCode);
        return 1;
    }

    @Transactional
    public int deleteDiscountCode(int discountCodeId) {
        discountCodeRepo.deleteById(discountCodeId);
        return 1;
    }

    @Transactional
    public int newDiscount(DiscountCode discountCode, int eventId) {
        try {
            Optional<Event> event = eventRepo.findById(eventId);
            discountCode.setEvent(event.orElse(null));
            discountCode.setUsedCount(0);
            // Set dat truoc = 0
            discountCode.setReservedCount(0);
            discountCodeRepo.save(discountCode);
            return 1;
        } catch (DataIntegrityViolationException ex) {
            return 0;
        }
    }

    @Transactional
    public void deactiveCode(Integer discountCodeId) {
        try {
            DiscountCode discountCode = discountCodeRepo.findDiscountById(discountCodeId);
            discountCode.setIsActive(!discountCode.getIsActive());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<DiscountCode> getListDiscountOfEvent(Event event) {
        return discountCodeRepo.getAllByEvent(event);
    }

    public List<DiscountCode> getAvailableDiscountByEventIds(List<Integer> eventId) {
        return discountCodeRepo.getAvailableDiscountByEventIds(eventId);
    }

    public Optional<DiscountCode> getDiscountOptCodeById(int id) {
        return discountCodeRepo.findById(id);
    }

    // Đặt chỗ discount code
    @Transactional
    public boolean reserveDiscount(Integer discountId) {

        int updated = discountCodeRepo.reserveDiscount(discountId);

        if (updated == 1) {
            return true;
        }
        return false;
    }

    // Tăng used count
    @Transactional
    public void paymentSuccess(Integer discountId) {
        discountCodeRepo.confirmUsage(discountId);
    }

    /// Giảm đặt chỗ : tương tự payment thành công. Dùng cho xóa discount trong checkout code cũng được
    @Transactional
    public void paymentFailOrNotPayment(Integer discountId) {
        discountCodeRepo.releaseReservation(discountId);
    }

    @Transactional
    public void deleteDiscountInCheckOut(Integer discountId) {
        discountCodeRepo.deleteDiscountInCheckOut(discountId);

    }

    @Scheduled(cron = "*/30 * * * * *")
    public void checkAndDisableDiscounts() {
        int updatedCount = discountCodeRepo.updateExpiredCode();
        logger.info("check expired code");
        if (updatedCount > 0) {
            logger.info("Đã vô hiệu hóa " + updatedCount + " mã giảm giá hết hạn/hết lượt.");
        }
    }

}
