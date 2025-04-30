package org.engine.utils;

import org.lwjgl.BufferUtils;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

public class TextRenderer {
    private final int BITMAP_W = 512;
    private final int BITMAP_H = 512;
    private final int FONT_HEIGHT = 24;

    private ByteBuffer charData;
    private STBTTAlignedQuad quad = STBTTAlignedQuad.create();
    private ByteBuffer font;
    private int texId;
    private STBTTBakedChar.Buffer cdata;

    public TextRenderer(String fontPath) throws IOException {
        font = BufferUtils.createByteBuffer(Files.readAllBytes(Paths.get(fontPath)).length);
        font.put(Files.readAllBytes(Paths.get(fontPath)));
        font.flip();

        cdata = STBTTBakedChar.malloc(96);
        charData = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        stbtt_BakeFontBitmap(font, FONT_HEIGHT, charData, BITMAP_W, BITMAP_H, 32, cdata);

        texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, charData);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    }

    public void renderText(String text, float x, float y, float scale) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texId);

        glPushMatrix();
        glTranslatef(x, y, 0);
        glScalef(scale, scale, 1.0f);

        glColor3f(1f, 1f, 1f);
        glBegin(GL_QUADS);
        FloatBuffer xb = BufferUtils.createFloatBuffer(1).put(0, 0);
        FloatBuffer yb = BufferUtils.createFloatBuffer(1).put(0, 0);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 32 || c > 126) continue;

            stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, c - 32, xb, yb, quad, true);

            glTexCoord2f(quad.s0(), quad.t0());
            glVertex2f(quad.x0(), quad.y0());

            glTexCoord2f(quad.s1(), quad.t0());
            glVertex2f(quad.x1(), quad.y0());

            glTexCoord2f(quad.s1(), quad.t1());
            glVertex2f(quad.x1(), quad.y1());

            glTexCoord2f(quad.s0(), quad.t1());
            glVertex2f(quad.x0(), quad.y1());
        }

        glEnd();
        glPopMatrix();
    }

    public void cleanup() {
        glDeleteTextures(texId);
        cdata.free();
        quad.free();
    }
}
