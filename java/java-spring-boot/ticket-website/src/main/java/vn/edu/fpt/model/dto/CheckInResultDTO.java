package vn.edu.fpt.model.dto;

public class CheckInResultDTO {

    private boolean success;
    private String code;       // SUCCESS | TICKET_NOT_FOUND | ALREADY_CHECKED_IN | SIG_INVALID | QR_INVALID | SERVER_ERROR
    private String message;

    // Thông tin vé — chỉ có khi success = true
    private Integer ticketId;
    private String ticketNumber;
    private String ticketType;
    private String eventName;
    private String checkedInAt;

    public CheckInResultDTO(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    // Getters
    public boolean isSuccess()        { return success; }
    public String getCode()           { return code; }
    public String getMessage()        { return message; }
    public Integer getTicketId()      { return ticketId; }
    public String getTicketNumber()   { return ticketNumber; }
    public String getTicketType()     { return ticketType; }
    public String getEventName()      { return eventName; }
    public String getCheckedInAt()    { return checkedInAt; }

    // Setters
    public void setSuccess(boolean success)          { this.success = success; }
    public void setCode(String code)                 { this.code = code; }
    public void setMessage(String message)           { this.message = message; }
    public void setTicketId(Integer ticketId)        { this.ticketId = ticketId; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public void setTicketType(String ticketType)     { this.ticketType = ticketType; }
    public void setEventName(String eventName)       { this.eventName = eventName; }
    public void setCheckedInAt(String checkedInAt)   { this.checkedInAt = checkedInAt; }
}