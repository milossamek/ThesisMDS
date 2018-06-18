package com.MDS.ThesisMDS.backend.implementation.spaceFillingAlg;

import com.MDS.ThesisMDS.backend.implementation.interfaces.ILinearOrdering;
import com.MDS.ThesisMDS.backend.implementation.objects.LinearPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/* Converted from C to Java

   copyright Ken Musgrave */
/* June 1986 */

/*
 * space-filling peano curve algorithm.
 * fills n-space with a 1-D peano curve.
 *
 * the algorithm is utterly incomprehensible,
 * so expect a paucity of sensible comments in this code.
 * it consists largely of bit-wise logical operations,
 * and is therefore quite impenetrable.
 * but it works.
 */


public class PeanCurve implements ILinearOrdering {
    private static final int FB_SIZE = 1024;
    private static final int MAX_DIMENSION = 5;
    private static final int MAX_PRECISION = 11;
    private static int n;
    private byte[] rho, sigma, tau, tilde_sigma, tilde_tau, omega, alpha;
    private byte[] bitmask; //filled with bitmasks
    private int[] J; //principal positions
    private int dimensions;
    private int precision;
    private byte bytemask;
    private int[] coord, last_coord;
    private ArrayList<Point> calculatedPoints;


    public PeanCurve(int dimensions) {
        this.dimensions = dimensions;
        this.rho = new byte[MAX_PRECISION];
        this.sigma = new byte[MAX_PRECISION];
        this.tau = new byte[MAX_PRECISION];
        this.tilde_sigma = new byte[MAX_PRECISION];
        this.tilde_tau = new byte[MAX_PRECISION];
        this.omega = new byte[MAX_PRECISION];
        this.alpha = new byte[MAX_PRECISION];
        this.J = new int[MAX_PRECISION];
        this.bitmask = new byte[MAX_PRECISION];
        this.coord = new int[MAX_DIMENSION];
        this.last_coord = new int[MAX_DIMENSION];

        for (int i = 0; i < dimensions; i++) {
            bitmask[i] = (byte) (1 << (dimensions - i - 1));
            bytemask |= 1 << i;
            last_coord[i] = 0;
        }
        this.n = 0;
        this.calculatedPoints = new ArrayList<>();
    }

    private ArrayList<LinearPoint> processPeano(Integer width, Integer height, Integer bits, Integer margin) {
        if (bits < 0 || bits > 64) {
            return null;
        }
        int n = 1 << bits;
        int cellSize = (width - 2 * margin) / (n);
        ArrayList<LinearPoint> returnArray = new ArrayList<>();

        returnArray.add(new LinearPoint(new Point(0, 0), new Point(margin + cellSize / 2, margin + cellSize / 2)));
        for (int i = 1; i < calculatedPoints.size(); i++) {
            Point point = calculatedPoints.get(i);
            int x = (int) Math.round((double) point.y / (n - 1) * (width - 2 * margin - cellSize) + margin) + cellSize / 2;
            int y = (int) Math.round((double) point.x / (n - 1) * (height - 2 * margin - cellSize) + margin) + cellSize / 2;
            returnArray.add(new LinearPoint(new Point((int) point.x, (int) point.y), new Point(x, y)));
        }

        return returnArray;
    }

    public void calculateIt(int precision) throws Exception {
        this.n = 0;
        this.calculatedPoints.clear();
        this.precision = precision;
        if (precision > MAX_PRECISION) throw new Exception("Fak");
        if (dimensions > MAX_DIMENSION) throw new Exception("Fak");

        recurse(coord, last_coord, (int) Math.pow(2.0, (double) precision), dimensions);
    }

    private void recurse(int[] coord, int[] last_coord, int iterations, int level) {
        if (level > 0)
            for (int i = 0; i < iterations; i++)
                recurse(coord, last_coord, iterations, level - 1);
        else {
            /* get x,y coord of position n on peano curve */
            peano(coord, n++);
            calculatedPoints.add(new Point(coord[0], coord[1]));
            System.out.println(coord[0] + ";" + coord[1]);
            for (int i = 0; i < precision; i++)
                last_coord[i] = coord[i];
        }
    }

    private void build_rho(int point) {
        int mask = bytemask;
        for (int i = 0; i < precision; i++) {
            rho[precision - i - 1] = (byte) ((point & mask) >> (i * dimensions));
            mask <<= dimensions;
        }
    }

    private int principal_pos(byte a_byte) {
        int nth_bit;

        nth_bit = a_byte & 0x01;
        for (int i = 1; i < dimensions; i++) {
            if (((a_byte & bitmask[dimensions - i - 1]) >> i) != nth_bit)
                return (dimensions - i);
        }

        return (dimensions);

    }


    private void build_sigma() {
        int bit;

        for (int i = 0; i < precision; i++) {
            sigma[i] |= rho[i] & bitmask[0];
            for (bit = 1; bit < dimensions; bit++) {
                sigma[i] |= (rho[i] & bitmask[bit])
                        ^ ((rho[i] & bitmask[bit - 1]) >> 1);
            }
        }

    }


    private void build_tau() {
        int parity, bit;
        byte temp_byte;

        for (int i = 0; i < precision; i++) {
            parity = 0;
            /* complement nth bit */
            if ((sigma[i] & bitmask[dimensions - 1]) != 0)
                tau[i] = (byte) (sigma[i] - 1);    /* nth bit was 1 */
            else
                tau[i] = (byte) (sigma[i] + 1);    /* nth bit was 0 */

            for (int j = 0; j < dimensions; j++)    /* find parity */
                if ((tau[i] & bitmask[j]) != 0)
                    parity++;

            if (ODD(parity)) {    /* complement principal bit */
                bit = J[i] - 1;    /* get index of principal bit */
                /* get bit in question */
                temp_byte = (byte) (tau[i] & bitmask[bit]);
                tau[i] |= bitmask[bit];    /* set the bit to 1 */
                tau[i] &= ~temp_byte;    /* assign complement */
            }
        }

    }


    private void build_tilde_sigma() {
        int shift = 0;

        tilde_sigma[0] = sigma[0];
        for (int i = 1; i < precision; i++) {
            shift += J[i - 1] - 1;
            shift %= dimensions;
            tilde_sigma[i] = (byte) RT_CSHFT(sigma[i], shift, dimensions, bytemask);
        }

    }


    private void build_tilde_tau() {
        int shift = 0;

        tilde_tau[0] = tau[0];
        for (int i = 1; i < precision; i++) {
            shift += J[i - 1] - 1;
            shift %= dimensions;
            tilde_tau[i] = (byte) RT_CSHFT(tau[i], shift, dimensions, bytemask);
        }

    }

    private void build_omega() {
        omega[0] = 0;
        for (int i = 1; i < precision; i++)
            omega[i] = (byte) (omega[i - 1] ^ tilde_tau[i - 1]);

    }

    private void build_alpha() {
        int i;

        for (i = 0; i < precision; i++)
            alpha[i] = (byte) (omega[i] ^ tilde_sigma[i]);

    }

    private void zero(int[] array) {
        for (int i = 0; i < precision; i++)
            array[i] = 0;

    }


    void v_convert(byte[] alph, int[] coord) {
        int i, j, bit, a_bitmask = 1;

        for (i = 0; i < dimensions; i++) {
            coord[i] = 0;
            bit = precision;
            for (j = 0; j < precision; j++)    /* extract each bit of coord* i */
                coord[i] |= ((alph[j] & a_bitmask) << --bit) >> i;
            a_bitmask <<= 1;
        }

    }

    void peano(int[] coord, int point) {
        int i;

        zero(sigma);
        zero(tilde_sigma);
        zero(tilde_tau);

        build_rho(point);
        for (i = 0; i < precision; i++)
            J[i] = principal_pos(rho[i]);
        build_sigma();
        build_tau();
        build_tilde_sigma();
        build_tilde_tau();
        build_omega();
        build_alpha();

        v_convert(alpha, coord);

    }

    private void zero(byte[] array) {
        Arrays.fill(array, (byte) 0);
    }


    private Boolean ODD(int x) {
        return (((x) & 0x1) != 0);
    }

    private int RT_CSHFT(byte bajt, int shift, int dimensions, byte bytemask) {
        return ((((bajt) >> (shift)) | ((bajt) << (dimensions - (shift)))) & bytemask);
    }

    @Override
    public ArrayList<LinearPoint> calculatePoints(Integer width, Integer height, Integer bits, Integer margin) {
        try {
            calculateIt(bits);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("calculated");
        return processPeano(width, height, bits, margin);
    }
}
