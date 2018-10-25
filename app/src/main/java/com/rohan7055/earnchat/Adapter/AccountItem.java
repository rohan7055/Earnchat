package com.rohan7055.earnchat.Adapter;

/**
 * Created by pgupta on 30/5/17.
 */


    public class AccountItem {
        private String name;
        private int icon;

        public AccountItem() {
        }

        public AccountItem(String name, int icon) {
            this.name = name;
            this.icon = icon;

        }

        public String getitem() {
            return name;
        }

        public void setitem(String name) {
            this.name= name;
        }

        public int geticon() {
            return icon;
        }

        public void seticon(int icon) {
            this.icon = icon;
        }


    }