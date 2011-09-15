#include <iostream>
#include <ctime>
#include <cstdlib>
#include <cstdio>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

#define MAX_BOOK 5
#define MAX_KEY 500
#define INF 10000
#define EPSILON 0.0001

template <class T, class U>
bool compare(const pair<T, U> &a, const pair<T, U> &b) {
	return a.first < b.first;
}

/**
 * Read keypoints and descriptor from input stream 
 * 
 */
inline void readKeyAndDesc(vector<KeyPoint> &trainKeys, Mat &trainDes) {
	// doc du lieu
	int keyNum, octave, classId;
	float x, y, angle, size, response;
	scanf("%d", &keyNum);
	
	for(int i = 0; i < keyNum; ++i) {
		scanf("%f%d%d%f%f%f%f", &angle, &classId, &octave, &x, &y, &response, &size);
		KeyPoint p(x, y, size, angle, response, octave, classId);
		trainKeys.push_back(p);
	}

	int rows, cols, type;
	float *data;
	scanf("%d%d%d", &rows, &cols, &type);
	int matSize = rows*cols;
	
	data = new float[matSize];
	for(int i = 0; i < matSize; ++i) {
		scanf("%f", &data[i]);
	}

	trainDes = Mat(rows, cols, CV_32F, data);
}

/**
 * Read database to memory.
 */
inline void readDatabase(vector<vector<KeyPoint> > &queryKeys, vector<Mat> &queryDes) {
	int querySize;
	scanf("%d", &querySize);
	for(int i = 0; i < querySize; ++i) {
		vector<KeyPoint> qK;
		Mat qD;
		// read a pair of keys and descriptors
		readKeyAndDesc(qK, qD);
		queryKeys.push_back(qK);
		queryDes.push_back(qD);
	}
}

/**
 * Convert data stored in an array into keypoints and descriptor
 */
void readKeyAndDesc(vector<KeyPoint> &trainKeys, Mat &trainDes, float *mdata, int &count) {
	// doc du lieu
	int keyNum, octave, classId;
	float x, y, angle, size, response;
	keyNum = mdata[count++];
	
	for(int i = 0; i < keyNum; ++i) {
		//scanf("%f%d%d%f%f%f%f", &angle, &classId, &octave, &x, &y, &response, &size);
		//ss >> angle >> classId >> octave >> x >> y >> response >> size;
		angle = mdata[count++];
		classId = mdata[count++];
		octave = mdata[count++];
		x = mdata[count++];
		y = mdata[count++];
		response = mdata[count++];
		size = mdata[count++];
		KeyPoint p(x, y, size, angle, response, octave, classId);
		trainKeys.push_back(p);
	}

	int rows, cols, type;
	float *data;
	rows = mdata[count++];
	cols = mdata[count++];
	type = mdata[count++];
	int matSize = rows*cols;
	
	data = new float[matSize];
	for(int i = 0; i < matSize; ++i) {
		data[i] = mdata[count++];
	}

	trainDes = Mat(rows, cols, CV_32F, data);
}

/**
 * Read database from an array
 */
void readDatabase(vector<vector<KeyPoint> > &queryKeys, vector<Mat> &queryDes, float *mdata, int &count) {
	int querySize;
	//scanf("%d", &querySize);
	//ss >> querySize;
	querySize = mdata[count++];
	for(int i = 0; i < querySize; ++i) {
		vector<KeyPoint> qK;
		Mat qD;
		readKeyAndDesc(qK, qD, mdata, count);
		queryKeys.push_back(qK);
		queryDes.push_back(qD);
	}
}

/**
 * Compute average of an array
 * @Return: average of the array
 */
inline float average(const vector<float> &vals, int &count) {
	int size = vals.size();
	count = 0;
	float aver = 0;
	for(int i = 0; i < size; ++i) {
		if(vals[i] < INF) {
			aver += vals[i];
			count++;
		}
	}

	if(count >= 10) return aver/count;
	else return INF;
}

/**
 * Filter the matches: all bad matches are dropped, average of 
 * good matches are computed and divided to the number of good match. 
 * This result is used to rank books in database. Small value
 * indicates good matches
 */
inline float filter(int trainSize, const vector<DMatch> &matches, float threshold = 0.1) {
	int size = matches.size();
	vector<float> vals;
	vals.resize(trainSize);
	for(int i = 0; i < trainSize; ++i) {
		vals[i] = INF;
	}

	for(int i = 0; i < size; ++i) {
		int idx = matches[i].trainIdx;
		float dis = matches[i].distance;
		if(vals[idx] > dis) {
			vals[idx] = dis;
		}
	}

	int count = 0;
	float aver = average(vals, count);
	
	return aver/count;
}

/**
 * Match the query image to images in database. The best matches are returned
 */
inline void match(const vector<KeyPoint> &trainKeys, const Mat &trainDes, const vector<vector<KeyPoint> > &queryKeys, const vector<Mat> &queryDes, vector<pair<float, int> > &result) {
	vector<Mat> trainD;
	trainD.push_back(trainDes);
	// use Flann based matcher to match images
	FlannBasedMatcher fbm;
	// train the query image
	fbm.add(trainD);
	int trainSize = trainKeys.size();

	int size = queryDes.size();
	for(int i = 0; i < size; ++i) {
		// compute match score for each image in the database
		vector<DMatch> matches;
		fbm.match(queryDes[i], matches);
		float m = filter(trainSize, matches);
		pair <float, int> p(m, i);
		result.push_back(p);
	}
	
	// sort books in descending
	std::sort(result.begin(), result.end(), compare<float, int>);
}

/**
 * Get min value of two number
 */
inline int min(int a, int b) {
	return a > b ? b:a;
}

/**
 * Main function: read query image, matching it to database images
 */
int main(int argc, char *argv[]) {
	cerr << "Start " << endl;
	vector<vector<KeyPoint> > queryKeys;
	vector<Mat> queryDes;
	
	// read image from file
	vector<KeyPoint> trainKeys;
	Mat trainDes, img = imread(argv[1], 0);
	
	vector<pair<float, int> > result;
	
	// detect image keypoints
	SurfFeatureDetector sfd1(500);
	SurfDescriptorExtractor sde;
	sfd1.detect(img, trainKeys);
	cerr << "Train keys size " << trainKeys.size() << endl;
	int s = 500;
	// select only the appropriate number of keypoints
	while(trainKeys.size() > 200) {
		cerr << "Train keys size " << trainKeys.size() << endl;
		trainKeys.clear();
		SurfFeatureDetector sfd(s+500);
		s += 500;
		sfd.detect(img, trainKeys);
	}
	
	// compute image descriptor
	sde.compute(img, trainKeys, trainDes);
	
	FILE * pFile;
	long lSize;
	char * buffer;
	size_t sresult;

	pFile = fopen ( "Data.txt" , "rb" );
	if (pFile==NULL) {fputs ("File error",stderr); exit (1);}

	// obtain file size:
	fseek (pFile , 0 , SEEK_END);
	lSize = ftell (pFile);
	rewind (pFile);

	// allocate memory to contain the whole file:
	buffer = (char*) malloc (sizeof(char)*lSize);
	if (buffer == NULL) {fputs ("Memory error",stderr); exit (2);}

	// copy the file into the buffer:
	sresult = fread (buffer,1,lSize,pFile);
	if (sresult != lSize) {fputs ("Reading error",stderr); exit (3);}

	/* the whole file is now loaded in the memory buffer. */

	// terminate
	//fclose (pFile);
	
	int dataSize, count = 0;
	char *endPtr;
	dataSize = strtol(buffer, &endPtr, 10);
	float *mdata = new float[dataSize];
	// read data as an array of float number
	for(int i = 0; i < dataSize; ++i) {
		mdata[i] = strtod(endPtr, &endPtr);
	}

	readDatabase(queryKeys, queryDes, mdata, count);
	fclose(pFile);
	
	match(trainKeys, trainDes, queryKeys, queryDes, result);
	int size = min(result.size(), MAX_BOOK);
	// print out the best result
	printf("%d\n", size);
	for(int i = 0; i < size; ++i) {
		cout << result[i].first << " " << result[i].second << endl;
	}
	trainDes.release();
	trainKeys.clear();

	delete []mdata;
}
