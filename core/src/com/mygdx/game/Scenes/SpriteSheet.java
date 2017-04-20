package com.mygdx.game.Scenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class SpriteSheet {
    private String path;
    private int width;
    private int height;
    private int[] pixels;

    public SpriteSheet(String path){
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(e);
        }

        if (image == null){
            return;
        }

        this.path = path;
        this.height = image.getHeight();
        this.width = image.getWidth();
        pixels = image.getRGB(0,0,width,height,null,0,width);

        for (int i = 0; i < pixels.length; i++){
            //0xRRGGBB
            //255/4 = 63.75 =~ 64 : divide by 4 because 4 monochromatic colours
            pixels[i] = (pixels[i]&0xff)/(64);
        }
    }

}
