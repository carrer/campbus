package com.carr3r;

/**
 * Created by carrer on 10/23/14.
 * Objeto a ser utilizado pelo StreetItemAdapter para gestão de listas (ListViews) com localidades
 * baseado no layout descrito em "street_row.xml";
 */
public class StreetItem {

        private String title;

        public StreetItem(String title)
        {
            super();
            setTitle(title);
        }

        public void setTitle(String v) {this.title=v;}
        public String getTitle() { return this.title; }
}
