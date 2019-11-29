package dev.aqudei.binpacker;

import java.util.ArrayList;
import java.util.List;

public class ContainerData {
    private double containerWidth;
    private double containerLength;
    private double containerHeight;
    private double containerWeight;
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

    public double getContainerLength() {
        return containerLength;
    }

    public void setContainerLength(double containerLength) {
        this.containerLength = containerLength;
    }

    public double getContainerWeight() {
        return containerWeight;
    }

    public void setContainerWeight(double containerWeight) {
        this.containerWeight = containerWeight;
    }
}
