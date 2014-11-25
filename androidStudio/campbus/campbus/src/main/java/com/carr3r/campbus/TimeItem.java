package com.carr3r.campbus;

/**
 * Created by carrer on 10/23/14.
 * Objeto a ser utilizado pelo TimeItemAdapter para gestão de listas (GridViews) de horários
 */
public class TimeItem {

    private boolean accessibility;
    private String title;

    public TimeItem(String number)
    {
        super();
        setTitle(number);
    }

    public void setTitle(String v) {
        if (v.contains("*")) {
            this.title = v.substring(0, v.length() - 1);
            this.accessibility = true;
        } else
        {
            this.title = v;
            this.accessibility = false;
        }
    }
    public String getTitle() { return this.title; }
}
