#include "com_mokdoryeong_team7_mokdoryeong_OpencvRoutine.h"
JNIEXPORT jintArray JNICALL Java_com_mokdoryeong_team7_mokdoryeong_OpencvRoutine_nonFrontalFaceDetection(JNIEnv* env, jclass jcls, jlong addrRgba,
        jint x1, jint y1, jint x2, jint y2){

    Mat& frame = *(Mat*) addrRgba;

    int frameCols = frame.cols;
    int frameRows = frame.rows;

    int faceX1 = (int)x1 / 3;
    int faceY1 = (int)y1 / 3;
    int faceX2 = (int)x2 / 3;
    int faceY2 = (int)y2 / 3;

    try {
        resize(frame, frame, Size(frameCols / 3, frameRows / 3), 0, 0, CV_INTER_NN);
    }catch(Exception e){

        faceX1 *= 3; faceY1 *= 3; faceX2 *= 3; faceY2 *= 3;
        jintArray facePoint = env->NewIntArray(4);
        jint points[4] = {(jint)faceX1, (jint)faceY1, (jint)faceX2, (jint)faceY2};
        env->SetIntArrayRegion(facePoint, 0, 4, points);
        return facePoint;

    }

    detectNonfrontalFace(frame, faceX1, faceY1, faceX2, faceY2);

    //Returns image matrix original size
    transpose(frame, frame);
    flip(frame, frame, 0);
    resize(frame, frame, Size(frameCols, frameRows), 0, 0, CV_INTER_NN);

    //Transfer ROI points to java
    faceX1 *= 3; faceY1 *= 3; faceX2 *= 3; faceY2 *= 3;
    jintArray facePoint = env->NewIntArray(4);
    jint points[4] = {(jint)faceX1, (jint)faceY1, (jint)faceX2, (jint)faceY2};
    env->SetIntArrayRegion(facePoint, 0, 4, points);
    return facePoint;

}

void detectNonfrontalFace(Mat& frame, int& x1, int& y1, int& x2, int& y2){

    transpose(frame, frame);
    flip(frame, frame, 1);

    String face_cascade_name = "/storage/emulated/0/data/lbpcascade_profileface.xml";

    CascadeClassifier face_cascade;

    if( !face_cascade.load( face_cascade_name ) ){ printf("--(!)Error loading\n"); return; };

    std::vector<Rect> faces;
    Rect elect;
    bool checkExistance = false;

    Mat frame_gray;
    cvtColor( frame, frame_gray, CV_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );

    //-- Detect faces
    face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(30, 30) );

    for( size_t i = 0; i < faces.size(); i++ )
    {
        Point center( faces[i].x + faces[i].width * 0.5, faces[i].y + faces[i].height * 0.5 );

        int currentX1 = faces[i].x;
        int currentY1 = faces[i].y;
        int currentX2 = faces[i].x + faces[i].width;
        int currentY2 = faces[i].y + faces[i].height;

        if( (x1 > currentX1 - 40 && x1 < currentX1 + 40 && y1 > currentY1 - 40 && y1 < currentY1 + 40) ||
                (checkExistance == false && i == faces.size() - 1)){

            elect = faces[i];

            x1 = elect.x;               y1 = elect.y;
            x2 = elect.x + elect.width; y2 = elect.y + elect.height;

            checkExistance = true;
            break;
        }

    }

    if(checkExistance == false)
        x1 = y1 = x2 = y2 = 0;

}

JNIEXPORT jintArray JNICALL Java_com_mokdoryeong_team7_mokdoryeong_OpencvRoutine_detectNeckPoints
        (JNIEnv* env, jclass jcls, jlong addr, jint faceStartPointX, jint faceStartPointY, jint neckStartPointX, jint neckStartPointY){

    Mat& frame = *(Mat*) addr;

    int imgCols = frame.cols;
    int imgRows = frame.rows;

    cvtColor(frame, frame, CV_RGBA2BGR);
    cvtColor(frame, frame, CV_BGR2YCrCb);
    inRange(frame, Scalar(0, 133, 77), Scalar(255, 173, 127), frame);

    auto structuringElement = getStructuringElement(MORPH_RECT, Size(3, 11), Point(1, 5));
    morphologyEx( frame, frame, MORPH_CLOSE, structuringElement );

    int roiHeight = (int)(neckStartPointY - faceStartPointY);

    if(neckStartPointY + roiHeight > imgRows)
        roiHeight = imgRows - neckStartPointY;

    Mat neckROI = frame(Rect(neckStartPointX, neckStartPointY, imgCols - neckStartPointX, roiHeight));

    vector<vector<Point>> contours;
    findContours(neckROI, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);

    vector<Point> neckPoints;
    auto maxSize = 0;

    for(auto iter = contours.begin(); iter != contours.end(); ++iter) {
        if(iter->size() > maxSize) {
            maxSize = iter->size();
            neckPoints = *iter;
        }
    }

    auto upper = 10000;
    auto lower = 0;
    vector<Point> rightPoints;

    //Get upper point
    for(Point p : neckPoints)
        if(p.y < upper)
            upper = p.y;

    //Get lower point on right side
    for(Point p1 : neckPoints)
        for(Point p2 : neckPoints)
            if(p1.y == p2.y && p1.x < p2.x)
                rightPoints.emplace_back(p2);

    for(Point p : rightPoints)
        if(p.y > lower)
            lower = p.y;

    int interval = (lower - upper) / 7;
    int standard[8];

    vector<vector<Point>> bucket{};

    for(int i = 0; i < 8; ++i){
        vector<cv::Point> init;
        bucket.emplace_back(init);
    }

    for(int i = 0; i < 8; ++i) {
        standard[i] = upper + interval * i;
        __android_log_print(ANDROID_LOG_DEBUG, "OpenCV", "%d", standard[i]);
    }

    for(Point p : neckPoints){
        for(int i = 0; i < 8; ++i)
            if(standard[i] == p.y) {
                bucket[i].emplace_back(p);
            }
    }

    int maxX = 0;
    int minX = 5000;

    Point maxPoint{0, 0};
    Point minPoint{0, 0};

    for(int i = 0; i < 8; ++i) {
        if(bucket[i].size() > 2) {
            for (Point p : bucket[i])
                if(p.x > maxX){
                    maxX = p.x;
                    maxPoint = p;
                }
            for(Point p : bucket[i])
                if(p.x < minX){
                    minX = p.x;
                    minPoint = p;
                }
            __android_log_print(ANDROID_LOG_DEBUG, "OpenCV", "%d | %d", minX, maxX);
            bucket[i].clear();
            bucket[i].emplace_back(maxPoint);
            bucket[i].emplace_back(minPoint);
            maxX = 0; minX = 5000;
        }
    }

    //Print points
    for(int i = 0; i < 8; ++i)
        circle(frame, Point(neckStartPointX + (bucket[i][0].x + bucket[i][1].x) / 2, neckStartPointY + bucket[i][0].y), 5, Scalar(255, 0, 0),
                   CV_FILLED);


    jintArray intArray = env->NewIntArray(16);
    jint *ptrArray = env->GetIntArrayElements(intArray, nullptr);

    for(int i = 0; i < 8; ++i)
        for(int j = 0; j < 2; ++j) {
            if(j == 0)
                ptrArray[i * 2 + j] = bucket[i][j].x;
            else {
                ptrArray[i * 2 + j] = neckStartPointY + bucket[i][j].y;
                ptrArray[i * 2 + j - 1] = neckStartPointX + (ptrArray[i * 2 + j - 1] + bucket[i][j].x) / 2;
            }
        }

    env->ReleaseIntArrayElements(intArray, ptrArray, 0);
    return intArray;

}


