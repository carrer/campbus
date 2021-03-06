package com.carr3r.campbus;

/**
 * Created by carrer on 10/23/14.
 * Objeto a ser utilizado pelo LineItemAdapter para gestão de listas (ListViews) com linhas de ônibus
 * baseados no layout descrito em "line_row.xml";
 */
public class LineItem {

    private String title;
    private String number;

    /*
        2 sentidos: sentido de ida: boards[0]; sentido de volta: boards[1];
     */
    private String directions[];
    /*
        2 letreiros: sentido de ida: boards[0]; sentido de volta: boards[1];
     */
    private String boards[];


    public LineItem(String number, String title, String directions[], String boards[])
    {
        super();
        setTitle(title);
        setNumber(number);
        setBoards(boards);
        setDirections(directions);
    }

    public void setTitle(String v) {this.title=v;}
    public String getTitle() { return this.title; }
    public void setNumber(String v) {this.number=v;}
    public String getNumber() { return this.number; }
    public void setBoards(String v[]) {this.boards=v;}
    public String[] getBoards() { return this.boards; }
    public void setDirections(String v[]) {this.directions=v;}
    public String[] getDirections() { return this.directions; }
}
