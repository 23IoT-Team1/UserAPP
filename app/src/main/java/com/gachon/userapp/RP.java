package com.gachon.userapp;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RP {

    private static final double INFINITY = Double.POSITIVE_INFINITY;    // for dijkstra
    private static final double STAIR = 100.0;   // 계단,엘베끼리의 거리 가중치
    private ArrayList<Integer> pathIndex = new ArrayList<>();
    private ArrayList<Point> pathPoint = new ArrayList<>();
    private ArrayList<String> pathDirection = new ArrayList<>();
    private ArrayList<Point> pathOfFloor4 = new ArrayList<>();
    private ArrayList<Point> pathOfFloor5 = new ArrayList<>();


    public RP() {}

//    // 가장 가까운 화장실/계단/엘베 찾아줄 때 쓰려고 데려온 함수
//    public void findShortestPathToAllExits(int firstIndexOfExitNode) {
//
//        int shortestExit = -1;
//
//        double min = Double.POSITIVE_INFINITY;
//        for (int i = firstIndexOfExitNode; i < node.size(); i++) {
//            double result = findPath.dijkstra(matrix.get(1), node.size(), i);
//            if (result < min) {
//                min = result;
//                shortestExit = i;
//            }
//        }
//
//        // 가장 짧은 exit을 endNode로 하는 다익스트라를 다시 호출해서 저장되게 하기 (비효율적이지만 돌아는 감)
//        findPath.dijkstra(matrix.get(1), node.size(), shortestExit);
//        pathIndex = findPath.getPathIndex();
//    }

    public double dijkstra(int startNode, int endNode) {    // rpList의 index 값으로 돌리기

        int numNodes = rpList.size();   // 95
        boolean[] visited = new boolean[numNodes];
        double[] distance = new double[numNodes];
        int[] previous = new int[numNodes];
        Arrays.fill(distance, INFINITY);
        Arrays.fill(previous, -1);
        distance[startNode] = 0;

        System.out.println("startNode: " + startNode + "  endNode: " + endNode);

        if (startNode == endNode) { // 예외 처리
            pathIndex.add(startNode);
            return 0;
        }

        for (int i = 0; i < numNodes - 1; i++) {
            int minDistanceNode = getMinDistanceNode(distance, visited);
            visited[minDistanceNode] = true;

            for (int j = 0; j < numNodes; j++) {
                String jRp = rpList.get(j).getRp();
                for (String str : rpList.get(minDistanceNode).getConnect().keySet()) {
                    if (jRp.equals(str)) {    // minDistanceNode랑 연결되어있는 rp라면
                        if (!visited[j] && minDistanceNode != j && distance[minDistanceNode] != INFINITY &&
                                distance[minDistanceNode] + rpList.get(minDistanceNode).getConnect().get(jRp) < distance[j]) {
                            distance[j] = distance[minDistanceNode] + rpList.get(minDistanceNode).getConnect().get(jRp);
                            previous[j] = minDistanceNode;
                        }
                    }
                }
            }

        }
        logShortestPath(startNode, endNode, previous);

        return distance[endNode];
    }

    private static int getMinDistanceNode(double[] distance, boolean[] visited) {
        double minDistance = INFINITY;
        int minDistanceNode = -1;
        int numNodes = distance.length;

        for (int i = 0; i < numNodes; i++) {
            if (!visited[i] && distance[i] <= minDistance) {
                minDistance = distance[i];
                minDistanceNode = i;
            }
        }

        return minDistanceNode;
    }

    public void logShortestPath(int startNode, int endNode, int[] previous) {

        if (pathIndex.size() > 0) { pathIndex.clear(); }    // 초기화
        
        if (previous[endNode] == -1) {
            Log.d("dijkstra", "No path found.");
        } else {
            int node = endNode;

            while (node != startNode) {
                node = previous[node];
                pathIndex.add(0, node);
            }
            pathIndex.add(endNode);
        }
    }

    // set direction list to guide
    public void setDirectionList() {

        // path의 좌표 arraylist
        for (int i : pathIndex) {
            pathPoint.add(new Point(rpList.get(i).getX(), rpList.get(i).getY()));
        }

        // path의 방향 arraylist를 pathPoint를 기반으로 생성
        //// "way" / "left" / "right"/ "elevator" / "destination"
        //// "endOfFloor" 추가. 엘베로 층 바뀌기 전 마지막 노드

        if (pathPoint.size() > 2) { // 일반적인 경우

            // 1. 출발지 노드
            if (isElevatorNode(0) && isElevatorNode(1)) { 
                pathDirection.add("endOfFloor"); // 나도 엘베 다음도 엘베면 난 그 층의 마지막 노드
            }
            else { pathDirection.add("way"); }  

            // 2. 중간 노드
            for (int i = 1; i < pathPoint.size() - 1; i++) {
                Point previous = pathPoint.get(i - 1);
                Point now = pathPoint.get(i);
                Point next = pathPoint.get(i + 1);

                if (isElevatorNode(i) && isElevatorNode(i+1)) { 
                        pathDirection.add("endOfFloor");    // 나&다음이 엘베면 난 그 층의 마지막 노드
                }
                else if (isElevatorNode(i) && isElevatorNode(i-1)) {
                    pathDirection.add("elevator");  // 나&이전이 엘베면 난 다음 층의 첫 노드
                }
                else {  // 나머지는 way 혹은 방향
                    // calculate ccw
                    int ccw = ((now.x - previous.x) * (next.y - previous.y)) - ((next.x - previous.x) * (now.y - previous.y));
                    System.out.println(i + "(ccw): "+ ccw);

                    if (ccw > 200) { pathDirection.add("right"); }
                    else if (ccw < -200) { pathDirection.add("left"); }
                    else { pathDirection.add("way"); }  // ccw == 0
                    // ccw는 원래 0을 기준으로 나누지만 거의 직선이면 방향 지시를 안하기 위해 보정값으로 200을 넣음
                }
            }

            // 3. 목적지 노드
            if (isElevatorNode(pathIndex.size()-1) && isElevatorNode(pathIndex.size()-2)) {
                pathDirection.add("elevator"); // 나&이전이 엘베면 난 엘베이자 마지막 노드
            }
            else { pathDirection.add("destination"); }

        }
        else {  // 엄청 가까운 거리여서 path가 2개 이하일 때 처리
            switch (pathPoint.size()) {
                case 0:
                    Log.d("path", "path not found");
                    break;
                case 1:
                    pathDirection.add("destination");
                    break;
                case 2:
                    if (isElevatorNode(0) && isElevatorNode(1)) {
                        pathDirection.add("endOfFloor"); // 둘밖에 없는데 둘다 엘베인 어이 없는 길찾기 상황
                        pathDirection.add("elevator");  // 테스트하다 이 예외를 발견한 게 더 웃김
                    }
                    else {
                        pathDirection.add("way");
                        pathDirection.add("destination");
                    }
                    break;
            }
        }

        // 테스트
        System.out.println("---pathDirection---");
        for (String str : pathDirection) {
            System.out.println(str);
        }
        System.out.println("-------------------");
    }

    // divide pathPoint array by each floor. (to draw separately)
    public void setPathOnEachFloor() {

        boolean isDivided = false;

        for (int i = 0; i < pathDirection.size(); i++) {
            if (pathDirection.get(i).equals("endOfFloor")) {    // divide based on "endOfFloor"
                isDivided = true;
                if (pathIndex.get(i) < 49) {    // 출발지가 4층, 목적지가 5층
                    for (int j = 0; j <= i; j++) {
                        pathOfFloor4.add(pathPoint.get(j));  // 4층
                    }
                    for (int k = i + 1; k < pathDirection.size(); k++) {
                        pathOfFloor5.add(pathPoint.get(k)); // 5층
                    }
                }
                else {  // 출발지가 5층, 목적지가 4층
                    for (int j = 0; j <= i; j++) {
                        pathOfFloor5.add(pathPoint.get(j));  // 5층
                    }
                    for (int k = i; k < pathDirection.size(); k++) {
                        pathOfFloor4.add(pathPoint.get(k)); // 4층
                    }
                }
            }
        }

        if (!isDivided) {   // 출발지와 목적지의 층이 같을 때
            if (pathIndex.get(0) < 49) {    // 4층
                for (Point tmp : pathPoint) {
                    pathOfFloor4.add(tmp);
                }
            }
            else {  // 5층
                for (Point tmp : pathPoint) {
                    pathOfFloor5.add(tmp);
                }
            }
        }
    }

    public boolean isElevatorNode(int index) {
        int[] elevatorIndexList = {11, 17, 20, 31, 42, 43, 48,
                57, 66, 69, 80, 88, 89, 94};    // 계단/엘베 파악용으로 쓸 index array

        boolean isElev = false;
        for (int i : elevatorIndexList) {
            if (pathIndex.get(index) == i) {
                isElev = true;
            }
        }

        return isElev;
    }

    public void clearArrayLists() {
        pathIndex.clear();
        pathPoint.clear();
        pathDirection.clear();
        pathOfFloor4.clear();
        pathOfFloor5.clear();
    }



    public static ArrayList<ReferencePointDTO> getRpList() {
        return rpList;
    }
    public ArrayList<Integer> getPathIndex() { return pathIndex; }
    public ArrayList<String> getPathDirection() {
        return pathDirection;
    }
    public ArrayList<Point> getPathOfFloor4() {
        return pathOfFloor4;
    }
    public ArrayList<Point> getPathOfFloor5() {
        return pathOfFloor5;
    }

    public int rpToIndex(String rp) {
        int index = 0;

        for (int i = 0; i < rpList.size(); i++) {
            if (rp.equals(rpList.get(i).getRp())) {
                index = i;
            }
        }
        return index;
    }

    // rp data (size: 95)
    // 4층 4_1 ~ 4_49 (index 0 ~ 48)
    // 5층 5_1 ~ 5_46 (index 49 ~ 94)
    private static final ArrayList<ReferencePointDTO> rpList = new ArrayList(){{

        add(new ReferencePointDTO("4_1", "4층 아르테크네", 260,161,
                new HashMap<String,Double>(){{
                    put("4_2", 96.0);
                    put("4_43", 60.0);
                }}));
        add(new ReferencePointDTO("4_2", "425호 앞", 260,257,
                new HashMap<String,Double>(){{
                    put("4_1", 96.0);
                    put("4_3", 69.0);
                }}));
        add(new ReferencePointDTO("4_3", "424호 앞", 260,326,
                new HashMap<String,Double>(){{
                    put("4_2", 69.0);
                    put("4_4", 64.0);
                }}));
        add(new ReferencePointDTO("4_4", "423호 앞", 260,390,
                new HashMap<String,Double>(){{
                    put("4_3", 64.0);
                    put("4_5", 74.0);
                }}));
        add(new ReferencePointDTO("4_5", "422호 앞", 260,464,
                new HashMap<String,Double>(){{
                    put("4_4", 74.0);
                    put("4_6", 69.0);
                }}));
        add(new ReferencePointDTO("4_6", "421호 앞", 260,533,
                new HashMap<String,Double>(){{
                    put("4_5", 69.0);
                    put("4_7", 66.0);
                }}));
        add(new ReferencePointDTO("4_7", "420호 앞", 260,599,
                new HashMap<String,Double>(){{
                    put("4_6", 66.0);
                    put("4_8", 66.0);
                }}));
        add(new ReferencePointDTO("4_8", "419호 앞", 260,665,
                new HashMap<String,Double>(){{
                    put("4_7", 66.0);
                    put("4_9", 53.0);
                }}));
        add(new ReferencePointDTO("4_9", "418호 앞", 260,718,
                new HashMap<String,Double>(){{
                    put("4_8", 53.0);
                    put("4_10", 90.0);
                    put("4_44", 70.0);
                }}));
        add(new ReferencePointDTO("4_10", "417호 앞", 260,808,
                new HashMap<String,Double>(){{
                    put("4_9", 90.0);
                    put("4_11", 68.0);
                }}));
        add(new ReferencePointDTO("4_11", "416호 앞", 260,876,
                new HashMap<String,Double>(){{
                    put("4_10", 68.0);
                    put("4_12", 49.0);
                }}));
        add(new ReferencePointDTO("4_12", "416호 옆 계단", 260,925,
                new HashMap<String,Double>(){{
                    put("4_11", 49.0);
                    put("4_13", 150.0);
                    put("5_9", STAIR);
                }}));
        add(new ReferencePointDTO("4_13", "415호 앞", 260,1075,
                new HashMap<String,Double>(){{
                    put("4_12", 150.0);
                    put("4_14", 210.0);
                }}));
        add(new ReferencePointDTO("4_14", "414호 앞", 260,1285,
                new HashMap<String,Double>(){{
                    put("4_13", 210.0);
                    put("4_15", 205.0);
                }}));
        add(new ReferencePointDTO("4_15", "413호 앞", 260,1490,
                new HashMap<String,Double>(){{
                    put("4_14", 205.0);
                    put("4_16", 126.0);
                }}));
        add(new ReferencePointDTO("4_16", "412호 앞", 260,1616,
                new HashMap<String,Double>(){{
                    put("4_15", 126.0);
                    put("4_17", 105.0);
                    put("4_46", 63.0);
                }}));
        add(new ReferencePointDTO("4_17", "412호 사물함 앞", 260,1721,
                new HashMap<String,Double>(){{
                    put("4_16", 105.0);
                    put("4_18", 124.0);
                }}));
        add(new ReferencePointDTO("4_18", "411호 앞 (계단 앞)", 260,1845,
                new HashMap<String,Double>(){{
                    put("4_17", 124.0);
                    put("4_19", 84.0);
                    put("5_18", STAIR);
                }}));
        add(new ReferencePointDTO("4_19", "410호 앞", 344,1845,
                new HashMap<String,Double>(){{
                    put("4_18", 84.0);
                    put("4_20", 98.0);
                }}));
        add(new ReferencePointDTO("4_20", "409호 앞", 442,1845,
                new HashMap<String,Double>(){{
                    put("4_19", 98.0);
                    put("4_21", 193.0);
                    put("4_49", 124.0);
                }}));
        add(new ReferencePointDTO("4_21", "408호 앞 (계단 앞)", 635,1845,
                new HashMap<String,Double>(){{
                    put("4_20", 193.0);
                    put("4_22", 161.0);
                    put("5_21", STAIR);
                }}));
        add(new ReferencePointDTO("4_22", "407호 앞", 796,1845,
                new HashMap<String,Double>(){{
                    put("4_21", 161.0);
                    put("4_23", 158.0);
                    put("4_24", 88.32326986700618);
                }}));
        add(new ReferencePointDTO("4_23", "407A호 앞", 954,1845,
                new HashMap<String,Double>(){{
                    put("4_22", 158.0);
                }}));
        add(new ReferencePointDTO("4_24", "406호 앞", 772,1760,
                new HashMap<String,Double>(){{
                    put("4_22", 88.32326986700618);
                    put("4_25", 87.09190547921202);
                }}));
        add(new ReferencePointDTO("4_25", "405호 앞", 749,1676,
                new HashMap<String,Double>(){{
                    put("4_24", 87.09190547921202);
                    put("4_26", 62.36184731067546);
                }}));
        add(new ReferencePointDTO("4_26", "405호 앞", 732,1616,
                new HashMap<String,Double>(){{
                    put("4_25", 62.36184731067546);
                    put("4_27", 64.5600495662759);
                    put("4_48", 124.0);
                }}));
        add(new ReferencePointDTO("4_27", "405호 앞", 714,1554,
                new HashMap<String,Double>(){{
                    put("4_26", 64.5600495662759);
                    put("4_28", 139.2838827718412);
                }}));
        add(new ReferencePointDTO("4_28", "404호 앞", 676,1420,
                new HashMap<String,Double>(){{
                    put("4_27", 139.2838827718412);
                    put("4_29", 205.7668583615933);
                }}));
        add(new ReferencePointDTO("4_29", "403호 앞", 620,1222,
                new HashMap<String,Double>(){{
                    put("4_28", 205.7668583615933);
                    put("4_30", 188.04786624686813);
                }}));
        add(new ReferencePointDTO("4_30", "402호 앞", 569,1041,
                new HashMap<String,Double>(){{
                    put("4_29", 188.04786624686813);
                    put("4_31", 91.48223871331527);
                }}));
        add(new ReferencePointDTO("4_31", "401호 앞", 544,953,
                new HashMap<String,Double>(){{
                    put("4_30", 91.48223871331527);
                    put("4_32", 64.28841264178172);
                }}));
        add(new ReferencePointDTO("4_32", "401호 옆 계단", 527,891,
                new HashMap<String,Double>(){{
                    put("4_31", 64.28841264178172);
                    put("4_33", 62.36184731067546);
                    put("5_32", STAIR);
                }}));
        add(new ReferencePointDTO("4_33", "435호 앞", 510,831,
                new HashMap<String,Double>(){{
                    put("4_32", 62.36184731067546);
                    put("4_34", 59.20304046246274);
                }}));
        add(new ReferencePointDTO("4_34", "434호 앞", 494,774,
                new HashMap<String,Double>(){{
                    put("4_33", 59.20304046246274);
                    put("4_35", 58.240879114244144);
                }}));
        add(new ReferencePointDTO("4_35", "433호 앞", 478,718,
                new HashMap<String,Double>(){{
                    put("4_34", 58.240879114244144);
                    put("4_36", 90.52071586106685);
                    put("4_44", 148.0);
                }}));
        add(new ReferencePointDTO("4_36", "432호 앞", 453,631,
                new HashMap<String,Double>(){{
                    put("4_35", 90.52071586106685);
                    put("4_37", 66.48308055437865);
                }}));
        add(new ReferencePointDTO("4_37", "431호 앞", 435,567,
                new HashMap<String,Double>(){{
                    put("4_36", 66.48308055437865);
                    put("4_38", 70.60453243241541);
                }}));
        add(new ReferencePointDTO("4_38", "430호 앞", 416,499,
                new HashMap<String,Double>(){{
                    put("4_37", 70.60453243241541);
                    put("4_39", 67.446274915669);
                }}));
        add(new ReferencePointDTO("4_39", "429호 앞", 398,434,
                new HashMap<String,Double>(){{
                    put("4_38", 67.446274915669);
                    put("4_40", 67.72001181334805);
                }}));
        add(new ReferencePointDTO("4_40", "428호 앞", 379,369,
                new HashMap<String,Double>(){{
                    put("4_39", 67.72001181334805);
                    put("4_41", 73.76313442364011);
                }}));
        add(new ReferencePointDTO("4_41", "427호 앞", 359,298,
                new HashMap<String,Double>(){{
                    put("4_40", 73.76313442364011);
                    put("4_42", 65.52098900352466);
                }}));
        add(new ReferencePointDTO("4_42", "426호 앞", 341,235,
                new HashMap<String,Double>(){{
                    put("4_41", 65.52098900352466);
                    put("4_43", 76.92203845452875);
                }}));
        add(new ReferencePointDTO("4_43", "복정 방향 엘리베이터 앞", 320,161,
                new HashMap<String,Double>(){{
                    put("4_42", 76.92203845452875);
                    put("4_1", 60.0);
                    put("5_40", STAIR);
                }}));
        add(new ReferencePointDTO("4_44", "중간 엘리베이터 앞", 330,718,
                new HashMap<String,Double>(){{
                    put("4_9", 70.0);
                    put("4_35", 148.0);
                    put("4_45", 92.0);
                    put("5_41", STAIR);
                }}));
        add(new ReferencePointDTO("4_45", "4층 테라스", 330,810,
                new HashMap<String,Double>(){{
                    put("4_44", 92.0);
                }}));
        add(new ReferencePointDTO("4_46", "운동장 방향 엘리베이터 옆 복도", 323,1616,
                new HashMap<String,Double>(){{
                    put("4_16", 63.0);
                    put("4_47", 119.0);
                }}));
        add(new ReferencePointDTO("4_47", "운동장 방향 엘리베이터 옆 복도", 442,1616,
                new HashMap<String,Double>(){{
                    put("4_46", 119.0);
                    put("4_48", 166.0);
                    put("4_49", 105.0);
                }}));
        add(new ReferencePointDTO("4_48", "운동장 방향 엘리베이터 옆 복도", 608,1616,
                new HashMap<String,Double>(){{
                    put("4_47", 166.0);
                    put("4_26", 124.0);
                }}));
        add(new ReferencePointDTO("4_49", "운동장 방향 엘리베이터 앞", 442,1721,
                new HashMap<String,Double>(){{
                    put("4_47", 105.0);
                    put("4_20", 124.0);
                    put("5_46", STAIR);
                }}));
        // 5층 시작
        add(new ReferencePointDTO("5_1", "5층 아르테크네", 260,161,
                new HashMap<String,Double>(){{
                    put("5_2", 96.0);
                    put("5_40", 60.0);
                }}));
        add(new ReferencePointDTO("5_2", "525호 앞", 260,257,
                new HashMap<String,Double>(){{
                    put("5_1", 96.0);
                    put("5_3", 86.0);
                }}));
        add(new ReferencePointDTO("5_3", "524호 앞", 260,343,
                new HashMap<String,Double>(){{
                    put("5_2", 86.0);
                    put("5_4", 101.0);
                }}));
        add(new ReferencePointDTO("5_4", "523호 앞", 260,444,
                new HashMap<String,Double>(){{
                    put("5_3", 101.0);
                    put("5_5", 95.0);
                }}));
        add(new ReferencePointDTO("5_5", "522호 앞", 260,539,
                new HashMap<String,Double>(){{
                    put("5_4", 95.0);
                    put("5_6", 102.0);
                }}));
        add(new ReferencePointDTO("5_6", "521호 앞", 260,641,
                new HashMap<String,Double>(){{
                    put("5_5", 102.0);
                    put("5_7", 87.0);
                }}));
        add(new ReferencePointDTO("5_7", "520호 앞", 260,728,
                new HashMap<String,Double>(){{
                    put("5_6", 87.0);
                    put("5_8", 125.0);
                    put("5_41", 88.0);
                }}));
        add(new ReferencePointDTO("5_8", "519호 앞", 260,853,
                new HashMap<String,Double>(){{
                    put("5_7", 125.0);
                    put("5_9", 80.0);
                }}));
        add(new ReferencePointDTO("5_9", "519호 옆 계단", 260,933,
                new HashMap<String,Double>(){{
                    put("5_8", 80.0);
                    put("5_10", 80.0);
                    put("4_12", STAIR);
                }}));
        add(new ReferencePointDTO("5_10", "518호 앞", 260,1013,
                new HashMap<String,Double>(){{
                    put("5_9", 80.0);
                    put("5_11", 115.0);
                }}));
        add(new ReferencePointDTO("5_11", "517호 앞", 260,1128,
                new HashMap<String,Double>(){{
                    put("5_10", 115.0);
                    put("5_12", 78.0);
                }}));
        add(new ReferencePointDTO("5_12", "516호 앞", 260,1206,
                new HashMap<String,Double>(){{
                    put("5_11", 78.0);
                    put("5_13", 62.0);
                }}));
        add(new ReferencePointDTO("5_13", "515호 앞", 260,1268,
                new HashMap<String,Double>(){{
                    put("5_12", 62.0);
                    put("5_14", 93.0);
                }}));
        add(new ReferencePointDTO("5_14", "514호 앞", 260,1361,
                new HashMap<String,Double>(){{
                    put("5_13", 93.0);
                    put("5_15", 129.0);
                    put("5_42", 233.69210512980536);
                }}));
        add(new ReferencePointDTO("5_15", "513호 앞", 260,1490,
                new HashMap<String,Double>(){{
                    put("5_14", 129.0);
                    put("5_16", 126.0);
                }}));
        add(new ReferencePointDTO("5_16", "512호 앞", 260,1616,
                new HashMap<String,Double>(){{
                    put("5_15", 126.0);
                    put("5_17", 105.0);
                    put("5_43", 63.0);
                }}));
        add(new ReferencePointDTO("5_17", "512호 앞", 260,1721,
                new HashMap<String,Double>(){{
                    put("5_16", 105.0);
                    put("5_18", 124.0);
                }}));
        add(new ReferencePointDTO("5_18", "511호 앞 (계단 앞)", 260,1845,
                new HashMap<String,Double>(){{
                    put("5_17", 124.0);
                    put("5_19", 84.0);
                    put("4_18", STAIR);
                }}));
        add(new ReferencePointDTO("5_19", "510호 앞", 344,1845,
                new HashMap<String,Double>(){{
                    put("5_18", 84.0);
                    put("5_20", 98.0);
                }}));
        add(new ReferencePointDTO("5_20", "509호 앞", 442,1845,
                new HashMap<String,Double>(){{
                    put("5_19", 98.0);
                    put("5_21", 193.0);
                    put("5_46", 119.0);
                }}));
        add(new ReferencePointDTO("5_21", "508호 앞 (계단 앞)", 635,1845,
                new HashMap<String,Double>(){{
                    put("5_20", 193.0);
                    put("5_22", 161.0);
                    put("4_21", STAIR);
                }}));
        add(new ReferencePointDTO("5_22", "507호 앞", 796,1845,
                new HashMap<String,Double>(){{
                    put("5_21", 161.0);
                    put("5_23", 158.0);
                    put("5_24", 88.32326986700618);
                }}));
        add(new ReferencePointDTO("5_23", "507A호 앞", 954,1845,
                new HashMap<String,Double>(){{
                    put("5_22", 158.0);
                }}));
        add(new ReferencePointDTO("5_24", "506호 앞", 772,1760,
                new HashMap<String,Double>(){{
                    put("5_22", 88.32326986700618);
                    put("5_25", 87.09190547921202);
                }}));
        add(new ReferencePointDTO("5_25", "505호 앞", 749,1676,
                new HashMap<String,Double>(){{
                    put("5_24", 87.09190547921202);
                    put("5_26", 62.36184731067546);
                }}));
        add(new ReferencePointDTO("5_26", "505호 앞", 732,1616,
                new HashMap<String,Double>(){{
                    put("5_25", 62.36184731067546);
                    put("5_27", 64.5600495662759);
                    put("5_45", 124.0);
                }}));
        add(new ReferencePointDTO("5_27", "505호 앞", 714,1554,
                new HashMap<String,Double>(){{
                    put("5_26", 64.5600495662759);
                    put("5_28", 139.2838827718412);
                }}));
        add(new ReferencePointDTO("5_28", "504호 앞", 676,1420,
                new HashMap<String,Double>(){{
                    put("5_27", 139.2838827718412);
                    put("5_29", 264.0075756488817);
                }}));
        add(new ReferencePointDTO("5_29", "503호 앞", 604,1166,
                new HashMap<String,Double>(){{
                    put("5_28", 264.0075756488817);
                    put("5_30", 100.68763578513501);
                    put("5_42", 161.74362429474616);
                }}));
        add(new ReferencePointDTO("5_30", "502호 앞", 577,1069,
                new HashMap<String,Double>(){{
                    put("5_29", 100.68763578513501);
                    put("5_31", 120.60265337047937);
                }}));
        add(new ReferencePointDTO("5_31", "501호 앞", 544,953,
                new HashMap<String,Double>(){{
                    put("5_30", 120.60265337047937);
                    put("5_32", 64.28841264178172);
                }}));
        add(new ReferencePointDTO("5_32", "501호 옆 계단", 527,891,
                new HashMap<String,Double>(){{
                    put("5_31", 64.28841264178172);
                    put("5_33", 83.24061508662703);
                    put("4_32", STAIR);
                }}));
        add(new ReferencePointDTO("5_33", "532호 앞", 504,811,
                new HashMap<String,Double>(){{
                    put("5_32", 83.24061508662703);
                    put("5_34", 86.40023148117139);
                }}));
        add(new ReferencePointDTO("5_34", "531호 앞", 480,728,
                new HashMap<String,Double>(){{
                    put("5_33", 86.40023148117139);
                    put("5_35", 118.40608092492548);
                    put("5_41", 132.0);
                }}));
        add(new ReferencePointDTO("5_35", "530호 앞", 448,614,
                new HashMap<String,Double>(){{
                    put("5_34", 118.40608092492548);
                    put("5_36", 119.36917525056458);
                }}));
        add(new ReferencePointDTO("5_36", "529호 앞", 416,499,
                new HashMap<String,Double>(){{
                    put("5_35", 119.36917525056458);
                    put("5_37", 91.48223871331527);
                }}));
        add(new ReferencePointDTO("5_37", "528호 앞", 391,411,
                new HashMap<String,Double>(){{
                    put("5_36", 91.48223871331527);
                    put("5_38", 117.44360348695028);
                }}));
        add(new ReferencePointDTO("5_38", "527호 앞", 359,298,
                new HashMap<String,Double>(){{
                    put("5_37", 117.44360348695028);
                    put("5_39", 65.52098900352466);
                }}));
        add(new ReferencePointDTO("5_39", "526호 앞", 341,235,
                new HashMap<String,Double>(){{
                    put("5_38", 65.52098900352466);
                    put("5_40", 76.92203845452875);
                }}));
        add(new ReferencePointDTO("5_40", "복정 방향 엘리베이터 앞", 320,161,
                new HashMap<String,Double>(){{
                    put("5_39", 76.92203845452875);
                    put("5_1", 60.0);
                    put("4_43", STAIR);
                }}));
        add(new ReferencePointDTO("5_41", "중간 엘리베이터 앞", 348,728,
                new HashMap<String,Double>(){{
                    put("5_7", 88.0);
                    put("5_34", 132.0);
                    put("4_44", STAIR);
                }}));
        add(new ReferencePointDTO("5_42", "G-CUBE", 464,1247,
                new HashMap<String,Double>(){{
                    put("5_14", 233.69210512980536);
                    put("5_29", 161.74362429474616);
                }}));
        add(new ReferencePointDTO("5_43", "운동장 방향 엘리베이터 옆 복도", 323,1616,
                new HashMap<String,Double>(){{
                    put("5_16", 63.0);
                    put("5_44", 119.0);
                }}));
        add(new ReferencePointDTO("5_44", "운동장 방향 엘리베이터 옆 복도", 442,1616,
                new HashMap<String,Double>(){{
                    put("5_43", 119.0);
                    put("5_45", 166.0);
                    put("5_46", 110.0);
                }}));
        add(new ReferencePointDTO("5_45", "운동장 방향 엘리베이터 옆 복도", 608,1616,
                new HashMap<String,Double>(){{
                    put("5_44", 166.0);
                    put("5_26", 124.0);
                }}));
        add(new ReferencePointDTO("5_46", "운동장 방향 엘리베이터 앞", 442,1726,
                new HashMap<String,Double>(){{
                    put("5_44", 110.0);
                    put("5_20", 119.0);
                    put("4_49", STAIR);
                }}));
        
    }};

//    public void calculateWeight() //weight 값 넣을 때 사용했음
//    {
//        for (int i = 0; i < rpList.size(); i++) {
//            int x1 = rpList.get(i).getX();
//            int y1 = rpList.get(i).getY();
//            int x2;
//            int y2;
//            double weight;
//            System.out.println("connected with " + rpList.get(i).getRp());
//            for(String connectedRP : rpList.get(i).getConnect().keySet()){ //저장된 key값 확인
//                for(int j = 0; j < rpList.size(); j++) {
//                    if (rpList.get(j).getRp().equals(connectedRP)) {
//                        x2 = rpList.get(j).getX();
//                        y2 = rpList.get(j).getY();
//                        weight = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
////                        if (weight != rpList.get(i).getConnect().get(connectedRP)){ // 값이 잘 들어갔는지 확인할 때
////                            System.out.println(rpList.get(j).getRp() + " : " + weight);
////                        }
//                        System.out.println(rpList.get(j).getRp() + " : " + weight);   // 값 계산할 때
//                    }
//                }
//            }
//            System.out.println("---------------");
//        }
//    }
    
}
