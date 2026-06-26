package vn.edu.fpt.model.dto;

public class DistributionDTO {

    private String ticketTypeName;

    private Long count;

    private double percent;

    private String color;

    private double dash;

    private double offset;

    public DistributionDTO(String ticketTypeName, Long count) {
        this.ticketTypeName = ticketTypeName;
        this.count = count;
    }

    public DistributionDTO(String ticketTypeName, double offset, double dash, String color, double percent, Long count) {
        this.ticketTypeName = ticketTypeName;
        this.offset = offset;
        this.dash = dash;
        this.color = color;
        this.percent = percent;
        this.count = count;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public void setTicketTypeName(String ticketTypeName) {
        this.ticketTypeName = ticketTypeName;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public double getDash() {
        return dash;
    }

    public void setDash(double dash) {
        this.dash = dash;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
