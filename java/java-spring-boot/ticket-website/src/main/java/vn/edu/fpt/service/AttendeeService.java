package vn.edu.fpt.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.*;
import vn.edu.fpt.model.entity.Ticket;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.repository.TicketRepo;
import vn.edu.fpt.util.PageSetting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AttendeeService {

    private final TicketRepo ticketRepo;
    private final EventRepo  eventRepo;
    private final EventService eventService;

    public AttendeeService(TicketRepo ticketRepo,
                           EventRepo eventRepo,
                           EventService eventService) {
        this.ticketRepo   = ticketRepo;
        this.eventRepo    = eventRepo;
        this.eventService = eventService;
    }


    // ── Guard: kiểm tra staff có quyền với event này không ───────────────
    public boolean hasAccessToEvent(Integer staffId, Integer eventId) {
        return eventRepo.existsByStaffAndEvent(staffId, eventId);
    }

    // ── Helper: null/empty list → null (không filter) ─────────────────────
    private List<String> normalizeTypes(List<String> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        return types;
    }

    // ATTENDEE MANAGEMENT — list event của staff
    public Page<StaffEventAttendeeDTO> getEventsByStaffFiltered(
            Integer staffId,
            String keyword,
            String status,
            String page
    ) {

        //Parse page
        int pageIndex = 0;
        try {
            pageIndex = Integer.parseInt(page);
            if (pageIndex < 0) pageIndex = 0;
        } catch (Exception ignored) {}

        Pageable pageable = PageRequest.of(pageIndex, PageSetting.SIZE_OF_EACH_PAGE);

        //Normalize dữ liệu
        keyword = (keyword != null && !keyword.isBlank())
                ? keyword.trim()
                : null;

        status = (status != null && !status.isBlank())
                ? status
                : null;

        //Gọi query đã có filter
        Page<StaffEventAttendeeDTO> result =
                eventRepo.getEventsByStaff(staffId, keyword, status, pageable);

        //Fix page overflow
        if (pageIndex >= result.getTotalPages() && result.getTotalPages() > 0) {
            pageIndex = result.getTotalPages() - 1;
            pageable = PageRequest.of(pageIndex, PageSetting.SIZE_OF_EACH_PAGE);

            result = eventRepo.getEventsByStaff(staffId, keyword, status, pageable);
        }

        // 5. SET COUNT
        result.getContent().forEach(event -> {
            long registered = countRegisteredFiltered(event.getEventId(), null);
            long checkedIn  = countCheckedInFiltered(event.getEventId(), null);

            event.setTotalRegistered(registered);
            event.setTotalCheckedIn(checkedIn);
        });

        return result;
    }

    // ATTENDEE LIST
    public Page<AttendeeDTO> getAttendeesByEventFiltered(
            Integer eventId, String keyword, String ticketType,
            String status, String page) {

        String kw   = (keyword    != null && !keyword.isBlank())    ? keyword.trim()    : null;
        String type = (ticketType != null && !ticketType.isBlank()) ? ticketType.trim() : null;
        String st   = (status     != null && !status.isBlank())     ? status.trim()     : null;

        //Parse page
        int pageIndex = 0;
        try {
            pageIndex = Integer.parseInt(page);
            if (pageIndex < 0) pageIndex = 0;
        } catch (Exception ignored) {}

        Pageable pageable = PageRequest.of(pageIndex, PageSetting.SIZE_OF_EACH_PAGE);
        Page<AttendeeDTO> result = ticketRepo.getAttendeesByEventFiltered(eventId, kw, type, st, pageable);
        //Fix page overflow
        if (pageIndex >= result.getTotalPages() && result.getTotalPages() > 0) {
            pageIndex = result.getTotalPages() - 1;
            pageable = PageRequest.of(pageIndex, PageSetting.SIZE_OF_EACH_PAGE);

            result = ticketRepo.getAttendeesByEventFiltered(eventId, kw, type, st, pageable);
        }
        return result;
    }

    public List<String> getDistinctTicketTypes(Integer eventId) {
        return ticketRepo.findDistinctTicketTypesByEvent(eventId);
    }

    public AttendeeDetailDTO getAttendeeDetail(Integer ticketId) {
        return ticketRepo.getAttendeeDetail(ticketId);
    }

    // CHECK-IN
    public void checkIn(Integer ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        ticket.setTicketStatus("CheckedIn");
        ticket.setCheckedInAt(java.time.LocalDateTime.now());
        ticketRepo.save(ticket);
    }

    // CHECK-IN STATUS — dùng chung cho cả management và checkInStatus page
    public Page<AttendeeDTO> getRecentCheckins(Integer eventId, List<String> types, Pageable pageable) {
        List<String> t = normalizeTypes(types);
        if (t == null) {
            return ticketRepo.getRecentCheckins(eventId, pageable);
        }
        return ticketRepo.getRecentCheckinsByTypes(eventId, t, pageable);
    }

    // Đếm Sold + CheckedIn — dùng chung cho management card và checkInStatus
    public long countRegisteredFiltered(Integer eventId, List<String> types) {
        List<String> t = normalizeTypes(types);
        if (t == null) {
            return ticketRepo.countRegisteredByEvent(eventId);
        }
        return ticketRepo.countRegisteredByTypes(eventId, t);
    }

    // Đếm CheckedIn — dùng chung cho management card và checkInStatus
    public long countCheckedInFiltered(Integer eventId, List<String> types) {
        List<String> t = normalizeTypes(types);
        if (t == null) {
            return ticketRepo.countCheckedInByEvent(eventId);
        }
        return ticketRepo.countCheckedInByTypes(eventId, t);
    }

    // DISTRIBUTION (donut chart)
    public List<DistributionDTO> getDistributionFiltered(Integer eventId, List<String> types) {
        List<String> t = normalizeTypes(types);
        List<DistributionDTO> list;
        if (t == null) {
            list = ticketRepo.getDistribution(eventId);
        } else {
            list = ticketRepo.getDistributionByTypes(eventId, t);
        }
        return buildDistribution(list);
    }

    public List<DistributionDTO> getDistribution(Integer eventId) {
        return buildDistribution(ticketRepo.getDistribution(eventId));
    }

    private List<DistributionDTO> buildDistribution(List<DistributionDTO> list) {
        long total = distributionTotal(list);
        for (DistributionDTO item : list) {
            if (total > 0) {
                item.setPercent((item.getCount() * 100.0) / total);
            } else {
                item.setPercent(0);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setColor(generateColor(i, list.size()));
        }
        double circumference = 2 * Math.PI * 40;
        double currentOffset = 0;
        for (DistributionDTO item : list) {
            double dash = (item.getPercent() / 100.0) * circumference;
            item.setDash(dash);
            item.setOffset(-currentOffset);
            currentOffset += dash;
        }
        return list;
    }

    public long distributionTotal(List<DistributionDTO> list) {
        long total = 0;
        for (DistributionDTO item : list) {
            total += item.getCount();
        }
        return total;
    }

    private String generateColor(int index, int totalSize) {
        List<String> palette = List.of(
                // Xanh dương đậm → nhạt
                "#1e3a8a",  // 0 - navy đậm
                "#1d4ed8",  // 1 - blue đậm
                "#2563eb",  // 2 - blue
                "#3b82f6",  // 3 - blue nhạt
                "#60a5fa",  // 4 - blue nhạt hơn
                // Chuyển sang cyan
                "#38bdf8",  // 5 - sky blue
                "#06b6d4",  // 6 - cyan
                "#0891b2",  // 7 - cyan đậm
                // Chuyển sang teal / xanh lá nhạt
                "#0d9488",  // 8 - teal
                "#14b8a6",  // 9 - teal sáng
                "#34d399",  // 10 - emerald nhạt
                // Chuyển sang xanh lá
                "#22c55e",  // 11 - green
                "#84cc16",  // 12 - lime
                // Chuyển sang vàng / cam nhạt
                "#eab308",  // 13 - yellow
                "#f59e0b",  // 14 - amber
                "#f97316",  // 15 - orange
                // Chuyển sang đỏ / hồng
                "#ef4444",  // 16 - red
                "#ec4899",  // 17 - pink
                "#a855f7",  // 18 - purple
                "#7c3aed"   // 19 - violet
        );

        if (index < palette.size()) {
            return palette.get(index);
        }

        // Fallback: HSL xoay vòng nếu vượt quá 20 loại
        float hue = (index * 18) % 360;
        return "hsl(" + hue + ", 70%, 55%)";
    }

    // EXPORT EXCEL
    public byte[] exportAttendeesToExcel(Integer eventId, List<String> selectedColumns) throws IOException {

        List<AttendeeExportDTO> rows  = ticketRepo.findAttendeesForExport(eventId);
        StaffEventAttendeeDTO   event = eventService.getEventSummary(eventId);

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Attendees");

            CellStyle headerStyle   = createHeaderStyle(wb);
            CellStyle dataStyle     = createDataStyle(wb);
            CellStyle altRowStyle   = createAltRowStyle(wb);
            CellStyle checkinStyle  = createCheckinStyle(wb);
            CellStyle pendingStyle  = createPendingStyle(wb);

            CellStyle statsTitleStyle    = createStatsTitleStyle(wb);
            CellStyle eventLabelStyle    = createEventLabelStyle(wb);
            CellStyle eventNameStyle     = createEventNameStyle(wb);
            CellStyle metaLabelStyle     = createMetaLabelStyle(wb);
            CellStyle metaValueStyle     = createMetaValueStyle(wb);
            CellStyle sectionHeaderStyle = createSectionHeaderStyle(wb);
            CellStyle statLabelStyle     = createStatLabelStyle(wb);
            CellStyle statValueStyle     = createStatValueStyle(wb);
            CellStyle statLabelAltStyle  = createStatLabelAltStyle(wb);
            CellStyle statValueAltStyle  = createStatValueAltStyle(wb);
            CellStyle breakdownColStyle  = createBreakdownColStyle(wb);
            CellStyle breakdownLblStyle  = createBreakdownLabelStyle(wb);
            CellStyle breakdownValStyle  = createBreakdownValStyle(wb);
            CellStyle breakdownLblAlt    = createBreakdownLabelAltStyle(wb);
            CellStyle breakdownValAlt    = createBreakdownValAltStyle(wb);
            CellStyle rateGoodStyle      = createRateGoodStyle(wb);
            CellStyle rateMedStyle       = createRateMedStyle(wb);
            CellStyle rateLowStyle       = createRateLowStyle(wb);

            LinkedHashMap<String, String> allCols = new LinkedHashMap<>();
            allCols.put("fullName",     "Full Name");
            allCols.put("email",        "Email");
            allCols.put("phone",        "Phone");
            allCols.put("ticketCode",   "Ticket Number");
            allCols.put("ticketType",   "Ticket Type");
            allCols.put("ticketStatus", "Ticket Status");
            allCols.put("orderStatus",  "Order Status");
            allCols.put("orderDate",    "Registration Date");
            allCols.put("checkinTime",  "Check-in Time");

            List<String> colKeys = new ArrayList<>();
            for (String key : allCols.keySet()) {
                if (selectedColumns.contains(key)) {
                    colKeys.add(key);
                }
            }

            writeDataTable(sheet, rows, colKeys, allCols,
                    headerStyle, dataStyle, altRowStyle,
                    checkinStyle, pendingStyle);

            int sc = colKeys.size() + 2;
            writeStatsTable(sheet, rows, event, sc,
                    statsTitleStyle, eventLabelStyle, eventNameStyle,
                    metaLabelStyle, metaValueStyle,
                    sectionHeaderStyle,
                    statLabelStyle, statValueStyle,
                    statLabelAltStyle, statValueAltStyle,
                    breakdownColStyle,
                    breakdownLblStyle, breakdownValStyle,
                    breakdownLblAlt, breakdownValAlt,
                    rateGoodStyle, rateMedStyle, rateLowStyle);

            sheet.setAutoFilter(new CellRangeAddress(0, rows.size(), 0, colKeys.size() - 1));
            sheet.createFreezePane(0, 1);

            for (int i = 0; i <= sc + 3; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 600);
            }

            wb.setActiveSheet(0);
            wb.write(out);
            return out.toByteArray();
        }
    }

    private void writeDataTable(Sheet sheet,
                                List<AttendeeExportDTO> rows,
                                List<String> colKeys,
                                LinkedHashMap<String, String> allCols,
                                CellStyle headerStyle, CellStyle dataStyle, CellStyle altRowStyle,
                                CellStyle checkinStyle, CellStyle pendingStyle) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Row header = sheet.createRow(0);
        header.setHeightInPoints(20);
        setCell(header, 0, "No.", headerStyle);
        for (int i = 0; i < colKeys.size(); i++) {
            setCell(header, i + 1, allCols.get(colKeys.get(i)), headerStyle);
        }

        for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
            AttendeeExportDTO r     = rows.get(rowIdx);
            Row               row   = sheet.createRow(rowIdx + 1);
            boolean           isAlt = (rowIdx % 2 != 0);
            CellStyle         base  = isAlt ? altRowStyle : dataStyle;

            row.setHeightInPoints(18);
            setCell(row, 0, rowIdx + 1, base);

            for (int i = 0; i < colKeys.size(); i++) {
                String key  = colKeys.get(i);
                Cell   cell = row.createCell(i + 1);
                cell.setCellStyle(base);

                if (key.equals("fullName")) {
                    cell.setCellValue(nvl(r.getFullName()));
                } else if (key.equals("email")) {
                    cell.setCellValue(nvl(r.getEmail()));
                } else if (key.equals("phone")) {
                    cell.setCellValue(nvl(r.getPhone()));
                } else if (key.equals("ticketCode")) {
                    cell.setCellValue(nvl(r.getTicketCode()));
                } else if (key.equals("ticketType")) {
                    cell.setCellValue(nvl(r.getTicketType()));
                } else if (key.equals("orderStatus")) {
                    cell.setCellValue(nvl(r.getOrderStatus()));
                } else if (key.equals("orderDate")) {
                    cell.setCellValue(r.getOrderDate() != null ? r.getOrderDate().format(dtf) : "");
                } else if (key.equals("checkinTime")) {
                    cell.setCellValue(r.getCheckinTime() != null ? r.getCheckinTime().format(dtf) : "-");
                } else if (key.equals("ticketStatus")) {
                    String statusVal = nvl(r.getTicketStatus());
                    cell.setCellValue(statusVal);
                    if ("CheckedIn".equals(statusVal)) {
                        cell.setCellStyle(checkinStyle);
                    } else if ("Sold".equals(statusVal)) {
                        cell.setCellStyle(pendingStyle);
                    }
                }
            }
        }
    }

    private void writeStatsTable(Sheet sheet, List<AttendeeExportDTO> rows,
                                 StaffEventAttendeeDTO event, int sc,
                                 CellStyle titleStyle, CellStyle eventLabelStyle, CellStyle eventNameStyle,
                                 CellStyle metaLabelStyle, CellStyle metaValueStyle,
                                 CellStyle sectionHeaderStyle,
                                 CellStyle labelStyle, CellStyle valueStyle,
                                 CellStyle labelAltStyle, CellStyle valueAltStyle,
                                 CellStyle breakdownColStyle,
                                 CellStyle breakdownLblStyle, CellStyle breakdownValStyle,
                                 CellStyle breakdownLblAlt, CellStyle breakdownValAlt,
                                 CellStyle rateGoodStyle, CellStyle rateMedStyle, CellStyle rateLowStyle) {

        long totalRegistered = rows.size();
        long totalCheckedIn  = 0;
        long totalSold       = 0;
        for (AttendeeExportDTO r : rows) {
            if ("CheckedIn".equals(r.getTicketStatus())) {
                totalCheckedIn++;
            } else if ("Sold".equals(r.getTicketStatus())) {
                totalSold++;
            }
        }
        long   notCheckedIn = totalRegistered - totalCheckedIn;
        double checkinRate  = totalRegistered > 0 ? (100.0 * totalCheckedIn / totalRegistered) : 0;

        // Tính breakdown theo ticketType bằng vòng lặp
        Map<String, Long> totalByType   = new LinkedHashMap<>();
        Map<String, Long> checkinByType = new LinkedHashMap<>();
        for (AttendeeExportDTO r : rows) {
            String typeName = r.getTicketType() != null ? r.getTicketType() : "Unknown";
            if (totalByType.containsKey(typeName)) {
                totalByType.put(typeName, totalByType.get(typeName) + 1);
            } else {
                totalByType.put(typeName, 1L);
            }
            if ("CheckedIn".equals(r.getTicketStatus())) {
                if (checkinByType.containsKey(typeName)) {
                    checkinByType.put(typeName, checkinByType.get(typeName) + 1);
                } else {
                    checkinByType.put(typeName, 1L);
                }
            }
        }

        int r = 0;

        Row titleRow = getOrCreateRow(sheet, r++);
        titleRow.setHeightInPoints(26);
        setCell(titleRow, sc, "EVENT SUMMARY", titleStyle);

        Row eventRow = getOrCreateRow(sheet, r++);
        eventRow.setHeightInPoints(22);
        setCell(eventRow, sc,     "Event", eventLabelStyle);
        setCell(eventRow, sc + 1, event != null ? event.getTitle() : "", eventNameStyle);

        Row dateRow = getOrCreateRow(sheet, r++);
        dateRow.setHeightInPoints(18);
        setCell(dateRow, sc,     "Export Date", metaLabelStyle);
        setCell(dateRow, sc + 1, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), metaValueStyle);

        r++;

        Row overviewHeader = getOrCreateRow(sheet, r++);
        overviewHeader.setHeightInPoints(22);
        setCell(overviewHeader, sc, "CHECK-IN OVERVIEW", sectionHeaderStyle);

        writeStatRow(sheet, r++, sc, "Total Registered", totalRegistered, labelStyle,    valueStyle);
        writeStatRow(sheet, r++, sc, "Checked In",       totalCheckedIn,  labelAltStyle, valueAltStyle);
        writeStatRow(sheet, r++, sc, "Sold (Pending)",   totalSold,       labelStyle,    valueStyle);
        writeStatRow(sheet, r++, sc, "Not Checked In",   notCheckedIn,    labelAltStyle, valueAltStyle);

        Row rateRow = getOrCreateRow(sheet, r++);
        rateRow.setHeightInPoints(18);
        setCell(rateRow, sc, "Attendance Rate", labelStyle);
        CellStyle rateStyle;
        if (checkinRate >= 80) {
            rateStyle = rateGoodStyle;
        } else if (checkinRate >= 50) {
            rateStyle = rateMedStyle;
        } else {
            rateStyle = rateLowStyle;
        }
        setCell(rateRow, sc + 1, String.format("%.1f%%", checkinRate), rateStyle);

        r++;

        Row breakdownHeader = getOrCreateRow(sheet, r++);
        breakdownHeader.setHeightInPoints(22);
        setCell(breakdownHeader, sc, "TICKET TYPE BREAKDOWN", sectionHeaderStyle);

        Row colHeader = getOrCreateRow(sheet, r++);
        colHeader.setHeightInPoints(18);
        setCell(colHeader, sc,     "Ticket Type", breakdownColStyle);
        setCell(colHeader, sc + 1, "Total",       breakdownColStyle);
        setCell(colHeader, sc + 2, "Checked In",  breakdownColStyle);
        setCell(colHeader, sc + 3, "Rate",        breakdownColStyle);

        int typeIdx = 0;
        for (String typeName : totalByType.keySet()) {
            long   typeTotal   = totalByType.get(typeName);
            long   typeCheckin = checkinByType.containsKey(typeName) ? checkinByType.get(typeName) : 0L;
            double typeRate    = typeTotal > 0 ? (100.0 * typeCheckin / typeTotal) : 0;
            boolean isAlt      = (typeIdx % 2 != 0);

            CellStyle lStyle = isAlt ? breakdownLblAlt : breakdownLblStyle;
            CellStyle vStyle = isAlt ? breakdownValAlt : breakdownValStyle;
            CellStyle rStyle;
            if (typeRate >= 80) {
                rStyle = rateGoodStyle;
            } else if (typeRate >= 50) {
                rStyle = rateMedStyle;
            } else {
                rStyle = rateLowStyle;
            }

            Row dRow = getOrCreateRow(sheet, r++);
            dRow.setHeightInPoints(18);
            setCell(dRow, sc,     typeName,                          lStyle);
            setCell(dRow, sc + 1, typeTotal,                         vStyle);
            setCell(dRow, sc + 2, typeCheckin,                       vStyle);
            setCell(dRow, sc + 3, String.format("%.1f%%", typeRate), rStyle);
            typeIdx++;
        }
    }

    private void writeStatRow(Sheet sheet, int rowIdx, int startCol,
                              String label, Object value,
                              CellStyle labelStyle, CellStyle valueStyle) {
        Row row = getOrCreateRow(sheet, rowIdx);
        row.setHeightInPoints(18);
        setCell(row, startCol,     label, labelStyle);
        setCell(row, startCol + 1, value, valueStyle);
    }

    private Row getOrCreateRow(Sheet sheet, int rowIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row != null) {
            return row;
        }
        return sheet.createRow(rowIdx);
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }

    private void setCell(Row row, int col, Object value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private XSSFColor color(int r, int g, int b) {
        return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, new DefaultIndexedColorMap());
    }

    private void border(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    private void borderColor(CellStyle s) {
        short c = color(226, 232, 240).getIndex();
        s.setTopBorderColor(c);
        s.setBottomBorderColor(c);
        s.setLeftBorderColor(c);
        s.setRightBorderColor(c);
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setBold(true); f.setColor(IndexedColors.WHITE.getIndex()); f.setFontHeightInPoints((short) 10);
        s.setFont(f); s.setFillForegroundColor(color(30, 58, 138)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); return s;
    }

    private CellStyle createDataStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10); s.setFont(f);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createAltRowStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10); s.setFont(f);
        s.setFillForegroundColor(color(241, 245, 249)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createCheckinStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10); f.setColor(color(21, 128, 61)); s.setFont(f);
        s.setFillForegroundColor(color(220, 252, 231)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createPendingStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setFontHeightInPoints((short) 10); f.setColor(color(180, 83, 9)); s.setFont(f);
        s.setFillForegroundColor(color(254, 243, 199)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createStatsTitleStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 13); f.setColor(color(255, 255, 255)); s.setFont(f);
        s.setFillForegroundColor(color(30, 58, 138)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); return s;
    }

    private CellStyle createEventLabelStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10); f.setColor(color(30, 58, 138)); s.setFont(f);
        s.setFillForegroundColor(color(219, 234, 254)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); return s;
    }

    private CellStyle createEventNameStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 11); f.setColor(color(51, 65, 85)); s.setFont(f);
        s.setFillForegroundColor(color(248, 250, 252)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createMetaLabelStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setItalic(true); f.setFontHeightInPoints((short) 9); f.setColor(IndexedColors.GREY_50_PERCENT.getIndex()); s.setFont(f);
        s.setFillForegroundColor(color(248, 250, 252)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        border(s); borderColor(s); return s;
    }

    private CellStyle createMetaValueStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setItalic(true); f.setFontHeightInPoints((short) 9); f.setColor(IndexedColors.GREY_50_PERCENT.getIndex()); s.setFont(f);
        s.setFillForegroundColor(color(248, 250, 252)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        border(s); borderColor(s); return s;
    }

    private CellStyle createSectionHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10); f.setColor(color(255, 255, 255)); s.setFont(f);
        s.setFillForegroundColor(color(71, 85, 105)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); return s;
    }

    private CellStyle createStatLabelStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10); s.setFont(f);
        s.setFillForegroundColor(color(255, 255, 255)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createStatValueStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 11); f.setColor(color(30, 58, 138)); s.setFont(f);
        s.setFillForegroundColor(color(255, 255, 255)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); borderColor(s); return s;
    }

    private CellStyle createStatLabelAltStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10); s.setFont(f);
        s.setFillForegroundColor(color(248, 250, 252)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createStatValueAltStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 11); f.setColor(color(30, 58, 138)); s.setFont(f);
        s.setFillForegroundColor(color(248, 250, 252)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); borderColor(s); return s;
    }

    private CellStyle createBreakdownColStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10); f.setColor(IndexedColors.WHITE.getIndex()); s.setFont(f);
        s.setFillForegroundColor(color(100, 116, 139)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); return s;
    }

    private CellStyle createBreakdownLabelStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10); s.setFont(f);
        s.setFillForegroundColor(color(255, 255, 255)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createBreakdownValStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10); s.setFont(f);
        s.setFillForegroundColor(color(255, 255, 255)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); borderColor(s); return s;
    }

    private CellStyle createBreakdownLabelAltStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10); s.setFont(f);
        s.setFillForegroundColor(color(248, 250, 252)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER); border(s); borderColor(s); return s;
    }

    private CellStyle createBreakdownValAltStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10); s.setFont(f);
        s.setFillForegroundColor(color(248, 250, 252)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); borderColor(s); return s;
    }

    private CellStyle createRateGoodStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10); f.setColor(color(21, 128, 61)); s.setFont(f);
        s.setFillForegroundColor(color(220, 252, 231)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); borderColor(s); return s;
    }

    private CellStyle createRateMedStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10); f.setColor(color(146, 64, 14)); s.setFont(f);
        s.setFillForegroundColor(color(254, 243, 199)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); borderColor(s); return s;
    }

    private CellStyle createRateLowStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10); f.setColor(color(185, 28, 28)); s.setFont(f);
        s.setFillForegroundColor(color(254, 226, 226)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        border(s); borderColor(s); return s;
    }
}