package pers.zhouhao.random;

/**
 * Created by lenovo on 2017/10/8.
 */
public class NormalDistribution {

    public static double next(double expectance, double variance) {
        double f = 0;
        double c0 = 2.515517, c1 = 0.802853, c2 = 0.010328;
        double d1 = 1.432788, d2 = 0.189269, d3 = 0.001308;
        double w;
        double r = Math.random();
        if (r <= 0.5) w = r;
        else w = 1 - r;
        if ((r - 0.5) > 0) f = 1;
        else if ((r - 0.5) < 0) f = -1;
        double y = Math.sqrt((-2) * Math.log(w));
        double x = f * (y - (c0 + c1 * y + c2 * y * y) / (1 + d1 * y + d2 * y * y + d3 * y * y * y));
        double z = expectance + x * Math.sqrt(variance);
        return z;
    }

}
