package com.github.golimpio.atm.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Amount {
    private long value;

    public Amount() {}

    public Amount(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
