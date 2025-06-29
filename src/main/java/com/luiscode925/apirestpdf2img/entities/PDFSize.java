package com.luiscode925.apirestpdf2img.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PDFSize {

    private Float width;

    private Float height;

    private Orientation orientation;

    public PDFSize(Float width, Float height) {
        this.width = width;
        this.height = height;
        this.orientation =  (this.height > this.width) ? Orientation.VERTICAL : Orientation.HORIZONTAL;
    }

}
