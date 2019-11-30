package dev.aqudei.binpacker;

import java.util.ArrayList;
import java.util.List;

public class ContainerData {
    private int containerWidth;
    private int containerLength;
    private int containerHeight;
    private List<Part> parts;

    public ContainerData() {
        parts = new ArrayList<Part>();
    }

    public double getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }

    public double getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(int containerHeight) {
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

    public void setContainerLength(int containerLength) {
        this.containerLength = containerLength;
    }
}
