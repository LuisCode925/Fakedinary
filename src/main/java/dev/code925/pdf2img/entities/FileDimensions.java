package dev.code925.pdf2img.entities;

public record FileDimensions (Float width, Float height){
    public Orientation getOrientation(){
        return (this.height > this.width) ? Orientation.VERTICAL : Orientation.HORIZONTAL;
    }
}
