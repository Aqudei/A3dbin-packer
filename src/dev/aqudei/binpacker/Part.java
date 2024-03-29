package dev.aqudei.binpacker;

import java.util.Objects;

public class Part {
    private int partQuantity;
    private int length;
    private int width;
    private int height;
    private String partName;

    public int getPartQuantity() {
        return partQuantity;
    }

    public void setPartQuantity(int partQuantity) {
        this.partQuantity = partQuantity;
    }

    public double getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "" + partQuantity +
                "_" + length +
                "_" + width +
                "_" + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return partQuantity == part.partQuantity &&
                Double.compare(part.length, length) == 0 &&
                Double.compare(part.width, width) == 0 &&
                Double.compare(part.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(partQuantity, length, width, height);
    }


    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
}
