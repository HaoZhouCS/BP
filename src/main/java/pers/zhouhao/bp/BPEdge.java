package pers.zhouhao.bp;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lenovo on 2017/10/8.
 */
@Getter
@Setter
public class BPEdge {

    private BPNode nodeU;
    private BPNode nodeV;
    private double w;

    public void addW(double t) {
        w = w + t;
    }
}
