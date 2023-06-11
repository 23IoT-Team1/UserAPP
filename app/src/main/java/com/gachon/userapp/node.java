package com.gachon.userapp;

import java.util.ArrayList;

//전달하는 노드의 json 형식
class node {
    ArrayList<WifiDTO> aps;

    public node(ArrayList arrayList) {
        this.aps = arrayList;
    }
}
