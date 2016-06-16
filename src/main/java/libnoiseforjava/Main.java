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

import libnoiseforjava.exception.ExceptionInvalidParam;

public class Main
{
    public static void main(String[] params)
    {
        Tutorials tuts = new Tutorials();
        try
        {
            tuts.showSimpleHeightfield("tutorial1.bmp", 256);
            tuts.showCombined1Heightfield("tutorial2.bmp", 256);
        }
        catch (ExceptionInvalidParam exceptionInvalidParam)
        {
            exceptionInvalidParam.printStackTrace();
        }
    }
}
