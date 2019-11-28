package dev.aqudei.binpacker;

import java.util.ArrayList;
import java.util.List;

public class ContainerData {
    private double containerWidth;
    private double containerHeight;
    private List<Part> parts;

    public ContainerData() {
        parts = new ArrayList<Part>();
    }

    public double getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(double containerWidth) {
        this.containerWidth = containerWidth;
    }

    public double getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(double containerHeight) {
        this.containerHeight = containerHeight;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
}
