package pers.zhouhao.bp;

import lombok.Getter;
import pers.zhouhao.random.NormalDistribution;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static pers.zhouhao.bp.BPNode.Type.HIDDEN_NODE;
import static pers.zhouhao.bp.BPNode.Type.INPUT_NODE;
import static pers.zhouhao.bp.BPNode.Type.OUTPUT_NODE;

/**
 * Created by lenovo on 2017/10/5.
 */
@Getter
public class BackPropagation {

    private final double c;

    private final int inputNodes = 2;
    private final int outputNodes = 4;

    private int LayerNum = 3;
    private int LayerNodes = 4;

    private final int maxLayerNum = 10;
    private final int minLayerNum = 2;
    private final int maxLayerNodes = 10;
    private final int minLayerNodes = 1;

    private List<BPNode> inputLayer = new LinkedList<BPNode>();
    private List<BPNode> outputLayer = new LinkedList<BPNode>();

    public BackPropagation(double _c) {

        c = _c;
        initNetwork();
    }

    public BackPropagation(int _LayerNum, int _LayerNodes, double _c) {

        c = _c;

        if(_LayerNum > maxLayerNum || _LayerNum < minLayerNum) {
            System.out.println("Layer number exceeds the limits");
        } else if(_LayerNodes > maxLayerNodes || _LayerNodes < minLayerNodes) {
            System.out.println("Layer nodes exceed the limits");
        } else {
            LayerNum = _LayerNum;
            LayerNodes = _LayerNodes;
            initNetwork();
        }

    }

    /**
     * init NN according the number of layers
     * and nodes in each hidden layer
     */
    private void initNetwork() {

        for(int i = 0;i < inputNodes;i ++) {
            BPNode node = new BPNode(INPUT_NODE);
            inputLayer.add(node);
        }

        for(int i = 0;i < outputNodes;i ++) {
            BPNode node = new BPNode(OUTPUT_NODE);
            outputLayer.add(node);
        }

        List<BPNode> lastLayer = inputLayer;
        for(int i = 1;i < LayerNum;i ++) {

            List<BPNode> thisLayer = new LinkedList<BPNode>();

            //judge whether this layer is output layer
            if(i < LayerNum - 1) {
                for (int j = 0; j < LayerNodes; j++) {
                    BPNode node = new BPNode(HIDDEN_NODE);
                    thisLayer.add(node);
                }
            } else {
                thisLayer = outputLayer;
            }

            for(BPNode u: lastLayer) {
                for(BPNode v: thisLayer) {
                    BPEdge e = new BPEdge();
                    e.setNodeU(u);
                    e.setNodeV(v);
                    e.setW(NormalDistribution.next(0, 1));          //set the initial weight as randomized value
                    u.getOutputEdges().add(e);
                    v.getInputEdges().add(e);
                    v.setThreadhold(NormalDistribution.next(0, 1));
                }
            }

            lastLayer = thisLayer;
        }
    }

    private double ThreadholdFunc(BPNode node) {

        List<BPEdge> inputEdges = node.getInputEdges();

        double U = 0;
        for(BPEdge bpEdge: inputEdges) {
            U += bpEdge.getW() * bpEdge.getNodeU().getX();
        }
        U -= node.getThreadhold();
        node.setU(U);
        node.setX(ActivationFunc(U));
        return ActivationFunc(U);
    }

    private double ActivationFunc(double U) {
        return sigmod(U);
        //return ReLu(U);
        //return tanh(U);
    }

    private double ActivationFunc_derivate(double U) {
        return sigmod_derivate(sigmod(U));
        //return ReLu_derivate(U);
        //return tanh_derivate(U);
    }


    private double ReLu(double U) {
        if(U <= 0) return 0.01*U;
        else return U;
    }

    private double ReLu_derivate(double U) {
        if(U <= 0) return 0.01;
        else return 1;
    }

    private double tanh_old(double U) {
        return (Math.exp(U) - Math.exp(-U))/(Math.exp(U) + Math.exp(-U));
    }

    private double tanh(double U) {
        return (tanh_old(U) + 1) / 2;
    }

    private double tanh_derivate(double U) {
        return (1 - tanh_old(U) * tanh_old(U))/2;
    }

    private double sigmod(double U) {
        double res = 1 / (1 + Math.exp(-U));
//        if(res > 0.999) return 0.999;
//        else if(res < 0.0001) return 0.0001;
//        else return res;
        return res;
        //return 1 / (1 + Math.exp(-U));
    }

    private double sigmod_derivate(double sigmod_U) {
        return (sigmod_U - sigmod_U * sigmod_U) * Math.exp(2);
    }

    public double getError() {
        return outputLayer.stream()
                .map(node -> Math.pow(node.getX() - node.getY(), 2) / 2)
                .reduce(0.0, (a, b) -> a + b);
    }

    public void study(double x, double y, int type) {

        inputLayer.get(0).setX(x);
        inputLayer.get(1).setX(y);

        outputLayer.get(0).setY(type == 1? 1 : 0);
        outputLayer.get(1).setY(type == 2? 1 : 0);
        outputLayer.get(2).setY(type == 3? 1 : 0);
        outputLayer.get(3).setY(type == 4? 1 : 0);

        forward();
        backward();
    }

    public int predict(double x, double y) {

        inputLayer.get(0).setX(x);
        inputLayer.get(1).setX(y);

        forward();

        double max = -100;
        int pred = -1;
        for(int i = 0;i < outputNodes;i ++) {
            if(max < outputLayer.get(i).getX()) {
                max = outputLayer.get(i).getX();
                pred = i + 1;
            }
        }
        return pred;
    }

    private void forward() {

        List<BPNode> lastLayer = inputLayer;
        for(int i = 1;i < LayerNum;i ++) {
            List<BPNode> thisLayer = lastLayer.get(0).getOutputEdges()
                                        .stream()
                                        .map(BPEdge::getNodeV)
                                        .collect(Collectors.toList());
            thisLayer.stream()
                    .forEach(node -> ThreadholdFunc(node));
            lastLayer = thisLayer;
        }
    }

    private void backward() {

        outputLayer.stream()
                .forEach(node ->
                        node.setD(
                                (node.getX() - node.getY())
                                        * ActivationFunc_derivate(node.getU())));

        List<BPNode> lastLayer = outputLayer;
        for(int i = LayerNum - 2;i > 0;i --) {
            List<BPNode> thisLayer = lastLayer.get(0).getInputEdges()
                    .stream()
                    .map(BPEdge::getNodeU)
                    .collect(Collectors.toList());
            thisLayer.stream()
                    .forEach(node -> {
                        double sum = node.getOutputEdges()
                                .stream()
                                .map(e -> e.getW() * e.getNodeV().getD())
                                .reduce(0.0, (a, b) -> a + b);
                        node.setD(sum * ActivationFunc_derivate(node.getU()));
                    });
            lastLayer = thisLayer;
        }

        List<BPNode> layer = inputLayer;
        for(int i = 1;i < LayerNum;i ++) {

            layer.stream()
                    .map(BPNode::getOutputEdges)
                    .forEach(edges ->
                            edges.stream().forEach(
                                    e -> e.addW(
                                            - c * e.getNodeV().getD()
                                                    * e.getNodeU().getX())));
            layer = layer.get(0).getOutputEdges()
                    .stream()
                    .map(BPEdge::getNodeV)
                    .collect(Collectors.toList());

            layer.stream()
                    .forEach(node -> node.addThreadhold(c * node.getD()));

        }
    }
}
