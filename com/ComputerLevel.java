package com;

public enum ComputerLevel {
    //����
    greenHand(6, 4),
    //����
    introduction(7, 8),
    //ҵ��
    amateur(8, 16),
    //רҵ
    career(9, 32),
    //��ʦ
    master(15, 64),
    //�޵�
    invincible(32, 60 * 60);
    public final int depth;
    public final long time;

    ComputerLevel(int depth, long time) {
        //�ȼ�
        this.depth = depth;
        //��ɵȴ�ʱ��
        this.time = time * 1000;
    }
}