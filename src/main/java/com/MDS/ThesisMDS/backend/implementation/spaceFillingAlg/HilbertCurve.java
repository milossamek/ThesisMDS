package com.MDS.ThesisMDS.backend.implementation.spaceFillingAlg;


import com.MDS.ThesisMDS.backend.implementation.interfaces.ILinearOrdering;
import com.MDS.ThesisMDS.backend.implementation.objects.LinearPoint;

import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;


// C. "Programming the Hilbert curve" by John Skilling (from the AIP Conf. Proc. 707, 381 (2004)) - rewrited
public class HilbertCurve implements ILinearOrdering {

    private final int dimensions = 2; //working in 2D
    private int length;
    private int bits;

    public HilbertCurve() {
    }

    public HilbertCurve(int bits) {
        this.bits = bits;
        this.length = 2 * bits;
    }

    private ArrayList<LinearPoint> calculateHilbert(Integer width, Integer height, Integer bits, Integer margin) {
        if (bits < 0 || bits > 64) {
            return null;
        }
        HilbertCurve curve = new HilbertCurve(bits);
        int n = 1 << bits;
        int cellSize = (width - 2 * margin) / (n);
        ArrayList<LinearPoint> returnArray = new ArrayList<>();

        returnArray.add(new LinearPoint(new Point(0, 0), new Point(margin + cellSize / 2, margin + cellSize / 2)));
        for (long i = 0; i < n * n; i++) {
            long[] point = curve.point(BigInteger.valueOf(i));
            System.out.println(point[0] + ";" + point[1]);
            int x = (int) Math.round((double) point[0] / (n - 1) * (width - 2 * margin - cellSize) + margin) + cellSize / 2;
            int y = (int) Math.round((double) point[1] / (n - 1) * (height - 2 * margin - cellSize) + margin) + cellSize / 2;
            returnArray.add(new LinearPoint(new Point((int) point[0], (int) point[1]), new Point(x, y)));
        }

        return returnArray;
    }

    private long[] point(BigInteger index) {
        return transposedIndexToPoint(bits, transpose(index));
    }

    private long[] transposedIndexToPoint(int bits, long[] x) {
        final long N = 2L << (bits - 1);
        // Note that x is mutated by this method (as a performance improvement
        // to avoid allocation)
        int n = x.length; // number of dimensions
        long p, q, t;
        int i;
        // Gray decode by H ^ (H/2)
        t = x[n - 1] >> 1;
        // Corrected error in Skilling's paper on the following line. The
        // appendix had i >= 0 leading to negative array index.
        for (i = n - 1; i > 0; i--) {
            x[i] ^= x[i - 1];
        }
        x[0] ^= t;
        // Undo excess work
        for (q = 2; q != N; q <<= 1) {
            p = q - 1;
            for (i = n - 1; i >= 0; i--)
                if ((x[i] & q) != 0L)
                    x[0] ^= p; // invert
                else {
                    t = (x[0] ^ x[i]) & p;
                    x[0] ^= t;
                    x[i] ^= t;
                }
        } // exchange
        return x;
    }

    private long[] transpose(BigInteger index) {
        long[] x = new long[dimensions];
        byte[] b = index.toByteArray();
        for (int idx = 0; idx < 8 * b.length; idx++) {
            if ((b[b.length - 1 - idx / 8] & (1L << (idx % 8))) != 0) {
                int dim = (length - idx - 1) % dimensions;
                int shift = (idx / dimensions) % bits;
                x[dim] |= 1L << shift;
            }
        }
        return x;
    }


    @Override
    public ArrayList<LinearPoint> calculatePoints(Integer width, Integer height, Integer bits, Integer margin) {
        return calculateHilbert(width, height, bits, margin);
    }
}

