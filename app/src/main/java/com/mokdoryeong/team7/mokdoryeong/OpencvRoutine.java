package com.mokdoryeong.team7.mokdoryeong;

import org.opencv.core.Rect;


public class OpencvRoutine {
    public static native int[] nonFrontalFaceDetection(long addrRgba, int x1, int y1, int x2, int y2);
    public static native int[] detectNeckPoints(long addr, int x1, int y1, int x2, int y2);
}
