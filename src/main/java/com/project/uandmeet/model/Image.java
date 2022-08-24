package com.project.uandmeet.model;

import com.project.uandmeet.dto.ImageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Image{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false)
    private String filename;

    @Column
    private String imageUrl;


    public Image(ImageDto imageDto){
        this.filename = imageDto.getFileName();
        this.imageUrl = imageDto.getImageUrl();
    }


    public Image(String imageUrl, Long id) {
        this.imageUrl = imageUrl;
        this.imageId = id;
    }
}
