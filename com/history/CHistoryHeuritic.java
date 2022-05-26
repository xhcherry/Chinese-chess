package com.history;

import com.ChessConstant;
import com.chessmove.MoveNode;

public class CHistoryHeuritic {
    public static int[][] cHistory = new int[ChessConstant.chessRoles_eight.length][256];

    //排序最少分数
    public void setCHistoryGOOD(MoveNode moveNode, int depth) {
        if (moveNode != null) {
            cHistory[ChessConstant.chessRoles_eight[moveNode.srcChess]][moveNode.destSite] += 2 << depth;
        }
    }

    public static void main(String[] args) {
        System.out.println(24 >> 2);
    }
}
