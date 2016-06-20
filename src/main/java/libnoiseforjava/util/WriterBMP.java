/*
 * Copyright (C) 2003-2005 Jason Bevins (original libnoise code)
 * Copyright  2016 Marcin 'MoonWolf' Trendota (java port of libnoise)
 *
 * This file is part of libnoiseforjava.
 *
 * libnoiseforjava is a Java port of the C++ library libnoise, which may be found at
 * http://libnoise.sourceforge.net/.  libnoise was developed by Jason Bevins, who may be
 * contacted at jlbezigvins@gmzigail.com (for great email, take off every 'zig').
 * Porting to Java was done by Thomas Hodge, who may be contacted at
 * libnoisezagforjava@gzagmail.com (remove every 'zag').
 * This particular class was ported by Marcin 'MoonWolf' Trendota who may be contacted at
 * marctoein@tretoendtoeota.com (remove every 'toe').
 *
 * libnoiseforjava is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * libnoiseforjava is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * libnoiseforjava.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package libnoiseforjava.util;

/// Windows bitmap image writer class.
///
/// This class creates a file in Windows bitmap (*.bmp) format given the
/// contents of an image object.
///
/// <b>Writing the image</b>
///
/// To write the image to a file, perform the following steps:
/// - Pass the filename to the SetDestFilename() method.
/// - Pass an Image object to the SetSourceImage() method.
/// - Call the WriteDestFile() method.
///
/// The SetDestFilename() and SetSourceImage() methods must be called
/// before calling the WriteDestFile() method.


import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import libnoiseforjava.exception.ExceptionInvalidParam;

public class WriterBMP
{
    // Bitmap header size.
    final int BMP_HEADER_SIZE = 54;

    /// Returns the name of the file to write.
    ///
    /// @returns The name of the file to write.
    public String getDestFilename()
    {
        return destFilename;
    }

    /// Sets the name of the file to write.
    ///
    /// @param filename The name of the file to write.
    ///
    /// Call this method before calling the WriteDestFile() method.
    public void setDestFilename(String filename)
    {
        destFilename = filename;
    }

    /// Sets the image object that is written to the file.
    ///
    /// @param sourceImage The image object to write.
    ///
    /// This object only stores a pointer to an image object, so make sure
    /// this object exists before calling the WriteDestFile() method.
    public void setSourceImage(ImageCafe sourceImage)
    {
        this.sourceImage = sourceImage;
    }

    /// Writes the contents of the image object to the file.
    ///
    /// @pre SetDestFilename() has been previously called.
    /// @pre SetSourceImage() has been previously called.
    ///
    /// @throw noise::ExceptionInvalidParam See the preconditions.
    /// @throw noise::ExceptionOutOfMemory Out of memory.
    /// @throw noise::ExceptionUnknown An unknown exception occurred.
    /// Possibly the file could not be written.
    ///
    /// This method encodes the contents of the image and writes it to a
    /// file.  Before calling this method, call the SetSourceImage()
    /// method to specify the image, then call the SetDestFilename()
    /// method to specify the name of the file to write.
    public void writeDestFile() throws ExceptionInvalidParam, IOException
    {
        if (sourceImage == null)
        {
            throw new ExceptionInvalidParam("Invalid parameter in WriterBMP");
        }

        int width  = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        // The width of one line in the file must be aligned on a 4-byte boundary.
        int lineSize = calcWidthByteCount(width);
        int destSize = lineSize * height;

        // Open the destination file.
        DataOutputStream os = new DataOutputStream(new FileOutputStream(destFilename));

        // Build the header.
        os.writeBytes("BM");
        os.writeInt(UnpackLittle32(destSize + BMP_HEADER_SIZE));
        os.writeBytes("\0\0\0\0");
        os.writeInt(UnpackLittle32(BMP_HEADER_SIZE));
        os.writeInt(UnpackLittle32(40));   // Palette offset
        os.writeInt(UnpackLittle32(width));
        os.writeInt(UnpackLittle32(height));
        os.writeShort(UnpackLittle16(1));   // Planes per pixel
        os.writeShort(UnpackLittle16(24));   // Bits per plane
        os.writeBytes("\0\0\0\0"); // Compression (0 = none)
        os.writeInt(UnpackLittle32(destSize));
        os.writeInt(UnpackLittle32(2834)); // X pixels per meter
        os.writeInt(UnpackLittle32(2834)); // Y pixels per meter
        os.writeBytes("\0\0\0\0");
        os.writeBytes("\0\0\0\0");

        // Build and write each horizontal line to the file.
        for (int y = 0; y <  height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                ColorCafe color = sourceImage.getValue(x, y);
                os.writeByte(color.getBlue());
                os.writeByte(color.getGreen());
                os.writeByte(color.getRed());
            }
            int x = width;
            while (x % 4 != 0)
            {
                os.writeByte(0);
                x++;
            }
        }

        os.close();
    }

    /// Calculates the width of one horizontal line in the file, in bytes.
    ///
    /// @param width The width of the image, in points.
    ///
    /// @returns The width of one horizontal line in the file.
    ///
    /// Windows bitmap files require that the width of one horizontal line
    /// must be aligned to a 32-bit boundary.
    protected int calcWidthByteCount(int width)
    {
        return ((width * 3) + 3) & ~0x03;
    }

    /// Name of the file to write.
    protected String destFilename;

    /// Image object that will be written to the file.
    protected ImageCafe sourceImage;

    private int UnpackLittle32(int integer)
    {
        return ((integer & 0x000000ff) << 24) | ((integer & 0x0000ff00) << 8) | ((integer & 0x00ff0000) >> 8) | ((integer & 0xff000000) >> 24);
    }

    private int UnpackLittle16(int integer)
    {
        return ((integer & 0x00ff) << 8) | ((integer & 0xff00) >> 8);
    }
}
