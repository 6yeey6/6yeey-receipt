package com.ibg.receipt.util.execltoimage;

import org.apache.poi.ss.usermodel.Cell;

import java.awt.*;

public class UserCell implements Comparable<UserCell> {
    private Cell cell;
    private int row;
    private int col;
    private boolean show;
    private String text = "";
    private Color color = null;

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int compareTo(UserCell uc) {
        try {
            double meDouble = Double.parseDouble(this.getText().replace("%", ""));
            double heDouble = Double.parseDouble(uc.getText().replace("%", ""));
            if (meDouble < heDouble) {
                return -1;
            } else if (meDouble > heDouble) {
                return 1;
            }
        } catch (Exception ignored) {
        }

        return 0;
    }
}
