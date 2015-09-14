package com.bodybuilding.hyper.resource;

public class TwoVariableHyperResource implements HyperResource {

    private String one;
    String two;
    
    public TwoVariableHyperResource(String one, String two) {
        this.one = one;
        this.two = two;
    }

    public String getOne() {
        return one;
    }
    
    public String toString() {
        return "This is the TwoVariableHyperResource " + one + two;
    }

}