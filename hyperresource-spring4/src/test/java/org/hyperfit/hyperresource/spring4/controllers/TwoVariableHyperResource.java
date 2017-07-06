package org.hyperfit.hyperresource.spring4.controllers;

import org.hyperfit.hyperresource.HyperResource;

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

    public String getTwo() {return  two;}

    public String toString() {
        return "This is the TwoVariableHyperResource " + one + two;
    }

}