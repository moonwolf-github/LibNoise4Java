/*
 * Copyright 2016 Marcin 'MoonWolf' Trendota
 * This file is part of libnoiseforjava.
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

package libnoiseforjava;

import java.io.IOException;

import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Billow;
import libnoiseforjava.module.Perlin;
import libnoiseforjava.module.RidgedMulti;
import libnoiseforjava.module.ScaleBias;
import libnoiseforjava.module.Select;
import libnoiseforjava.module.Turbulence;
import libnoiseforjava.util.ColorCafe;
import libnoiseforjava.util.ImageCafe;
import libnoiseforjava.util.NoiseMap;
import libnoiseforjava.util.NoiseMapBuilderPlane;
import libnoiseforjava.util.RendererImage;
import libnoiseforjava.util.WriterBMP;

public class Tutorials
{
    private NoiseMap heightMap = null;
    private NoiseMapBuilderPlane heightMapBuilder = null;
    private RendererImage renderer = null;
    private ImageCafe image = null;

    public void showSimpleHeightfield(String fileName, int size) throws ExceptionInvalidParam
    {
        Perlin myModule = new Perlin();
        heightMap = new NoiseMap(size, size);
        heightMapBuilder = new NoiseMapBuilderPlane(size, size);
        heightMapBuilder.setSourceModule (myModule);
        heightMapBuilder.setDestNoiseMap (heightMap);
        heightMapBuilder.setDestSize (size, size);
        heightMapBuilder.setBounds (2.0, 6.0, 1.0, 5.0);
        heightMapBuilder.build();
        renderer = new RendererImage();
        image = new ImageCafe(size, size);
        renderer.setSourceNoiseMap (heightMap);
        renderer.setDestImage (image);
        renderer.clearGradient ();
        renderer.addGradientPoint (-1.0000, new ColorCafe(  0,   0, 128, 255)); // deeps
        renderer.addGradientPoint (-0.2500, new ColorCafe(  0,   0, 255, 255)); // shallow
        renderer.addGradientPoint ( 0.0000, new ColorCafe(  0, 128, 255, 255)); // shore
        renderer.addGradientPoint ( 0.0625, new ColorCafe(240, 240,  64, 255)); // sand
        renderer.addGradientPoint ( 0.1250, new ColorCafe( 32, 160,   0, 255)); // grass
        renderer.addGradientPoint ( 0.3750, new ColorCafe(224, 224,   0, 255)); // dirt
        renderer.addGradientPoint ( 0.7500, new ColorCafe(128, 128, 128, 255)); // rock
        renderer.addGradientPoint ( 1.0000, new ColorCafe(255, 255, 255, 255)); // snow
        renderer.enableLight(true);
        renderer.setLightContrast(3.0); // Triple the contrast
        renderer.setLightBrightness(2.0); // Double the brightness
        renderer.render();
        showGeneratedImage(fileName);
    }

    public void showCombined1Heightfield(String fileName, int size) throws ExceptionInvalidParam
    {
        RidgedMulti myModule = new RidgedMulti();
        Billow baseFlatTerrain = new Billow();
        baseFlatTerrain.setFrequency(2.0);
        Perlin terrainType = new Perlin();
        terrainType.setFrequency(0.5);
        terrainType.setPersistence(0.25);
        baseFlatTerrain.setOctaveCount(12);
        myModule.setOctaveCount(12);
        ScaleBias flatTerrain = new ScaleBias(baseFlatTerrain);
        flatTerrain.setScale(0.125);
        heightMap = new NoiseMap(size, size);
        flatTerrain.setBias(-0.75);
        Select terrainSelector = new Select(flatTerrain, myModule, null);
        terrainSelector.setControlModule(terrainType);
        terrainSelector.setBounds(0.0, 1000.0);
        terrainSelector.setEdgeFalloff(0.125);

        Turbulence finalTerrain = new Turbulence(terrainSelector);
        finalTerrain.setFrequency(4.0);
        finalTerrain.setPower(0.125);

        heightMapBuilder = new NoiseMapBuilderPlane(size, size);
        heightMapBuilder.setSourceModule(finalTerrain);
        heightMapBuilder.setDestNoiseMap(heightMap);
        heightMapBuilder.setDestSize(size, size);
        heightMapBuilder.setBounds(9, 9.5, 4, 4.5); // looks like 'z' goes downward
        heightMapBuilder.build();
        renderer = new RendererImage();
        image = new ImageCafe(size, size);
        renderer.setSourceNoiseMap (heightMap);
        renderer.setDestImage(image);
        renderer.clearGradient();
        renderer.addGradientPoint(-1.00, new ColorCafe( 32, 160,   0, 255)); // grass
        renderer.addGradientPoint(-0.25, new ColorCafe(224, 224,   0, 255)); // dirt
        renderer.addGradientPoint( 0.25, new ColorCafe(128, 128, 128, 255)); // rock
        renderer.addGradientPoint( 1.00, new ColorCafe(255, 255, 255, 255)); // snow
        renderer.enableLight(true);
        renderer.setLightContrast(3.0); // Triple the contrast
        renderer.setLightBrightness(2.0); // Double the brightness
        renderer.render();
        showGeneratedImage(fileName);
    }

    public void showGeneratedImage(String fileName)
    {
        WriterBMP writer = new WriterBMP();
        writer.setSourceImage(image);
        writer.setDestFilename(fileName);
        try
        {
            writer.writeDestFile();
        }
        catch (ExceptionInvalidParam exceptionInvalidParam)
        {
            exceptionInvalidParam.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
