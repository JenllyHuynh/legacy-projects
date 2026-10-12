package org.example.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "orchids")
// Tạo getter và setter, Tạo constructor default và có tham số.



// Tạo getter và setter, Tạo constructor default và có tham số.
@Data
@NoArgsConstructor
@AllArgsConstructor

public class orchid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="orchid_id")
    private int id;
    @Column(name = "orchid_name")
    private String name;
 @Column(name = "is_natural", columnDefinition = "bit default 0")
 private boolean isNatural;
    @Column(name = "orchid_description")
    private String description;
@Column (name = "category_id")
    private String categoryId;
@Column( name = "is_attractive",columnDefinition = "bit default 0")
    private boolean isAttractive;
@Column (name ="orchid_url")
    private String url;


}
