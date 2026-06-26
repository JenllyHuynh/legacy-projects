package vn.edu.fpt.model.dto;

public class CategoryResponseDTO {

    private Integer id;
    private String name;
    private String iconName;
    private int eventCount;

    public CategoryResponseDTO() {
    }

    public CategoryResponseDTO(Integer id, String name, String iconName, int eventCount) {
        this.id = id;
        this.name = name;
        this.iconName = iconName;
        this.eventCount = eventCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }
}