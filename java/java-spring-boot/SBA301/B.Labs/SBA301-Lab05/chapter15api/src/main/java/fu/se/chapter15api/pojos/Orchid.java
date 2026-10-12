package fu.se.chapter15api.pojos;

import jakarta.persistence.*;

@Entity
@Table(name = "Orchid")
public class Orchid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orchidId;

    @Column(nullable = false)
    private String orchidName;

    private boolean isNatural;

    @Column(length = 1000)
    private String orchidDescription;

    private String orchidCategory;

    private boolean isAttractive;

    private String orchidURL;

    public Orchid() {
    }

    public Integer getOrchidId() {
        return orchidId;
    }

    public void setOrchidId(Integer orchidId) {
        this.orchidId = orchidId;
    }

    public String getOrchidName() {
        return orchidName;
    }

    public void setOrchidName(String orchidName) {
        this.orchidName = orchidName;
    }

    // Jackson maps isNatural()/setNatural(...) to the JSON property "natural"
    public boolean isNatural() {
        return isNatural;
    }

    public void setNatural(boolean natural) {
        this.isNatural = natural;
    }

    public String getOrchidDescription() {
        return orchidDescription;
    }

    public void setOrchidDescription(String orchidDescription) {
        this.orchidDescription = orchidDescription;
    }

    public String getOrchidCategory() {
        return orchidCategory;
    }

    public void setOrchidCategory(String orchidCategory) {
        this.orchidCategory = orchidCategory;
    }

    // Jackson maps isAttractive()/setAttractive(...) to the JSON property "attractive"
    public boolean isAttractive() {
        return isAttractive;
    }

    public void setAttractive(boolean attractive) {
        this.isAttractive = attractive;
    }

    public String getOrchidURL() {
        return orchidURL;
    }

    public void setOrchidURL(String orchidURL) {
        this.orchidURL = orchidURL;
    }
}
