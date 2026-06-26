package vn.edu.fpt.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.TicketTypeDTO;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Ticket;
import vn.edu.fpt.model.entity.TicketType;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.repository.TicketRepo;
import vn.edu.fpt.repository.TicketTypeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketTypeService {

    private static final Logger logger = LoggerFactory.getLogger(TicketTypeService.class);

    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepo eventRepo;
    private final TicketRepo ticketRepo;

    public TicketTypeService(TicketTypeRepository ticketTypeRepository,
                             EventRepo eventRepo,
                             TicketRepo ticketRepo) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.eventRepo = eventRepo;
        this.ticketRepo = ticketRepo;
    }

    private TicketTypeDTO convertToDTO(TicketType ticketType) {
        TicketTypeDTO dto = new TicketTypeDTO();
        dto.setId(ticketType.getId());
        dto.setEventId(ticketType.getEvent().getId());
        dto.setEventTitle(ticketType.getEvent().getTitle());
        dto.setName(ticketType.getName());
        dto.setDescription(ticketType.getDescription());
        dto.setPrice(ticketType.getPrice());
        dto.setQuantity(ticketType.getQuantity());
        dto.setMaxTicketsPerUser(ticketType.getMaxTicketsPerUser());
        dto.setSalesStartDate(ticketType.getSalesStartDate());
        dto.setSalesEndDate(ticketType.getSalesEndDate());
        dto.setActive(ticketType.getIsActive());
        try {
            Integer soldCount = ticketTypeRepository.countSoldTickets(ticketType.getId());
            dto.setSoldTickets(soldCount != null ? soldCount : 0);
        } catch (Exception e) {
            logger.error("Error counting sold tickets for TicketType ID {}: {}", ticketType.getId(), e.getMessage());
            dto.setSoldTickets(0);
        }
        return dto;
    }

    private TicketType convertToEntity(TicketTypeDTO dto) {
        TicketType entity = new TicketType();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setMaxTicketsPerUser(dto.getMaxTicketsPerUser());
        entity.setSalesStartDate(dto.getSalesStartDate());
        entity.setSalesEndDate(dto.getSalesEndDate());
        entity.setIsActive(dto.getActive());
        return entity;
    }

    public List<TicketTypeDTO> getAllTicketTypesByEvent(Integer eventId) {
        List<TicketType> ticketTypes = ticketTypeRepository.findByEventId(eventId);
        return ticketTypes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public TicketTypeDTO getTicketTypeById(Integer id) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketType not found with id: " + id));
        return convertToDTO(ticketType);
    }

    // ---------------------------------------------------------------
    // TẠO TICKET TYPE + TỰ ĐỘNG SINH TICKETS (BR-62, BR-63)
    // ---------------------------------------------------------------
    @Transactional
    public TicketTypeDTO createTicketType(TicketTypeDTO ticketTypeDTO) {
        Event event = eventRepo.findById(ticketTypeDTO.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + ticketTypeDTO.getEventId()));

        // 1. Lưu TicketType
        TicketType ticketType = convertToEntity(ticketTypeDTO);
        ticketType.setEvent(event);
        if (ticketType.getMaxTicketsPerUser() == null) ticketType.setMaxTicketsPerUser(5);
        if (ticketType.getIsActive() == null) ticketType.setIsActive(true);

        TicketType saved = ticketTypeRepository.save(ticketType);
        logger.info("Created TicketType ID: {}, quantity: {}", saved.getId(), saved.getQuantity());

        // 2. Tự động sinh Tickets tương ứng với quantity (BR-62, BR-63)
        autoGenerateTickets(saved);

        return convertToDTO(saved);
    }

    /**
     * Sinh tự động số lượng Ticket theo quantity của TicketType.
     * Mỗi ticket có:
     *   - TicketNumber: {TicketTypeId}-{index} (ví dụ: 5-001, 5-002)
     *   - TicketCodeHash: SHA-256 của UUID random (BR-62, BR-63)
     *   - TicketStatus: Available
     */
    private void autoGenerateTickets(TicketType ticketType) {
        int quantity = ticketType.getQuantity();
        List<Ticket> tickets = new ArrayList<>();

        for (int i = 1; i <= quantity; i++) {
            String ticketCode = UUID.randomUUID().toString(); // BR-62: random unique

            String ticketCodeHash;
            try {
                ticketCodeHash = QrService.sha256(ticketCode); // BR-63: lưu dạng hash
            } catch (Exception e) {
                throw new RuntimeException("Failed to hash ticketCode for ticket " + i, e);
            }

            Ticket ticket = new Ticket();
            ticket.setTicketType(ticketType);
            ticket.setTicketNumber(ticketType.getId() + "-" + String.format("%03d", i));
            ticket.setTicketStatus("Available");
            ticket.setIssuedAt(LocalDateTime.now());
            ticket.setTicketCodeHash(ticketCodeHash);
            // OrderItemId để null — sẽ được gán khi customer mua vé

            tickets.add(ticket);
        }

        ticketRepo.saveAll(tickets);
        logger.info("Auto-generated {} tickets for TicketType ID: {}", quantity, ticketType.getId());
    }

    // ---------------------------------------------------------------
    // CẬP NHẬT TICKET TYPE + ĐỒNG BỘ SỐ LƯỢNG TICKETS
    // ---------------------------------------------------------------
    @Transactional
    public TicketTypeDTO updateTicketType(Integer id, TicketTypeDTO ticketTypeDTO) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketType not found with id: " + id));

        Integer soldTickets = ticketTypeRepository.countSoldTicketsFromOrders(id);
        int sold = (soldTickets != null) ? soldTickets : 0;
        if (ticketTypeDTO.getQuantity() < sold) {
            throw new RuntimeException(String.format(
                    "Cannot reduce quantity to %d. Already sold %d tickets.",
                    ticketTypeDTO.getQuantity(), sold));
        }

        int oldQuantity = ticketType.getQuantity();
        int newQuantity = ticketTypeDTO.getQuantity();

        // Update thông tin TicketType
        ticketType.setName(ticketTypeDTO.getName());
        ticketType.setDescription(ticketTypeDTO.getDescription());
        ticketType.setPrice(ticketTypeDTO.getPrice());
        ticketType.setQuantity(newQuantity);
        ticketType.setMaxTicketsPerUser(ticketTypeDTO.getMaxTicketsPerUser());
        ticketType.setSalesStartDate(ticketTypeDTO.getSalesStartDate());
        ticketType.setSalesEndDate(ticketTypeDTO.getSalesEndDate());
        ticketType.setIsActive(ticketTypeDTO.getActive());

        TicketType updated = ticketTypeRepository.save(ticketType);

        // Đồng bộ số lượng Tickets nếu quantity thay đổi
        if (newQuantity > oldQuantity) {
            // Tăng quantity → sinh thêm tickets
            int addCount = newQuantity - oldQuantity;
            int startIndex = oldQuantity + 1;
            List<Ticket> newTickets = new ArrayList<>();

            for (int i = startIndex; i <= newQuantity; i++) {
                String ticketCode = UUID.randomUUID().toString();
                String ticketCodeHash;
                try {
                    ticketCodeHash = QrService.sha256(ticketCode);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to hash ticketCode", e);
                }
                Ticket ticket = new Ticket();
                ticket.setTicketType(updated);
                ticket.setTicketNumber(updated.getId() + "-" + String.format("%03d", i));
                ticket.setTicketStatus("Available");
                ticket.setIssuedAt(LocalDateTime.now());
                ticket.setTicketCodeHash(ticketCodeHash);
                newTickets.add(ticket);
            }
            ticketRepo.saveAll(newTickets);
            logger.info("Added {} tickets for TicketType ID: {}", addCount, id);

        } else if (newQuantity < oldQuantity) {
            // Giảm quantity → xóa bớt tickets Available (không xóa Sold/CheckedIn)
            int removeCount = oldQuantity - newQuantity;
            List<Ticket> availableTickets = ticketRepo
                    .findByTicketTypeIdAndStatusAvailable(id, removeCount);
            ticketRepo.deleteAll(availableTickets);
            logger.info("Removed {} available tickets for TicketType ID: {}", availableTickets.size(), id);
        }

        logger.info("Updated TicketType ID: {}", id);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteTicketType(Integer id) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketType not found with id: " + id));
        ticketType.setIsActive(false);
        ticketTypeRepository.save(ticketType);
        logger.info("Deactivated TicketType ID: {}", id);
    }

    @Transactional
    public void hardDeleteTicketType(Integer id) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketType not found with id: " + id));

        Integer soldTickets = ticketTypeRepository.countSoldTickets(id);
        if (soldTickets != null && soldTickets > 0) {
            throw new RuntimeException(String.format(
                    "Cannot delete '%s'. Already sold %d ticket%s. Please deactivate instead.",
                    ticketType.getName(), soldTickets, soldTickets > 1 ? "s" : ""));
        }

        if (ticketType.getItems() != null && !ticketType.getItems().isEmpty()) {
            throw new RuntimeException(String.format(
                    "Cannot delete '%s'. It is currently in %d cart(s).",
                    ticketType.getName(), ticketType.getItems().size()));
        }

        try {
            ticketTypeRepository.delete(ticketType);
            logger.info("Hard deleted TicketType ID: {}", id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete due to database constraints: " + e.getMessage());
        }
    }

    @Transactional
    public void activateTicketType(Integer id) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketType not found with id: " + id));
        ticketType.setIsActive(true);
        ticketTypeRepository.save(ticketType);
        logger.info("Activated TicketType ID: {}", id);
    }

    public List<TicketType> getActiveTicketTypes(Integer eventId) {
        return ticketTypeRepository.findByEvent_IdAndIsActiveTrue(eventId);
    }

    public List<TicketType> getAllTicketType() {
        return ticketTypeRepository.findAll();
    }

    public boolean hasAvailableTickets(Integer ticketTypeId) {
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new RuntimeException("TicketType not found"));
        Integer soldTickets = ticketTypeRepository.countSoldTickets(ticketTypeId);
        return ticketType.getQuantity() - (soldTickets != null ? soldTickets : 0) > 0;
    }

    public int getAvailableTicketsCount(Integer ticketTypeId) {
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new RuntimeException("TicketType not found"));
        Integer soldTickets = ticketTypeRepository.countSoldTickets(ticketTypeId);
        return ticketType.getQuantity() - (soldTickets != null ? soldTickets : 0);
    }

    @Transactional
    public void createTicketType(TicketType ticketType) {
        TicketType saved = ticketTypeRepository.save(ticketType);
        autoGenerateTickets(saved); // tự sinh tickets sau khi lưu
    }

    public Integer getAvailableTicketsByTicketType(Integer id) {
        return ticketTypeRepository.countAvailableTickets(id);
    }

    public Optional<TicketType> getTicketTypeOptById(int id) {
        return ticketTypeRepository.findById(id);
    }


}