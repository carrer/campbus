package com.carr3r;

/**
 * Created by carrer on 10/23/14.
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
