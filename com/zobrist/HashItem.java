package com.zobrist;

import com.chessmove.MoveNode;

public class HashItem {
    //У���
    public long checkSum;
    //hash����  �ϱ߽�  �±߽�  ��ȷֵ
    public int entry_type;
    //ֵ
    public int value;
    //����
    public int depth;

    public MoveNode moveNode;
    //�Ƿ��������
    public boolean isExists = true;

    public HashItem() {
    }

}
