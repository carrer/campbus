package com.carr3r;

/**
 * Created by carrer on 10/23/14.
 */
public class LineItem {

    private String title;
    private String number;
    private String boards[];

    public LineItem(String number, String title, String boards[])
    {
        super();
        setTitle(title);
        setNumber(number);
        setBoards(boards);
    }

    public void setTitle(String v) {this.title=v;}
    public String getTitle() { return this.title; }
    public void setNumber(String v) {this.number=v;}
    public String getNumber() { return this.number; }
    public void setBoards(String v[]) {this.boards=v;}
    public String[] getBoards() { return this.boards; }
}
