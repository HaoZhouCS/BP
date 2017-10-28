package pers.zhouhao.bp;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lenovo on 2017/10/6.
 */
@Getter
@Setter
public class BPNode {

    private Type type;          //节点类型
    private double y;           //输出预期值，用于输出层节点
    private double x;           //节点的输出值
    private double u;           //节点未经激励函数处理的加权和
    private double d;           //用于BP算法
    private double threadhold;  //阈值
    private List<BPEdge> inputEdges = new LinkedList<BPEdge>();
    private List<BPEdge> outputEdges = new LinkedList<BPEdge>();

    public BPNode(Type _type) {
        type = _type;
    }

    public void addThreadhold(double t) {
        threadhold += t;
    }

    public enum Type {
        INPUT_NODE,
        OUTPUT_NODE,
        HIDDEN_NODE;
    }
}
