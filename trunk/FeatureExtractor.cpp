#include <iostream>
#include <fstream>
#include <opencv2/opencv.hpp>
using namespace std;
using namespace cv;

/**
 * Write key to output stream
 */
void writeKey(ostream &fout, const KeyPoint &key) {
	fout << key.angle << endl;
	fout << key.class_id << endl;
	fout << key.octave << endl;
	fout << key.pt.x << endl;
	fout << key.pt.y << endl;
	fout << key.response << endl;
	fout << key.size << endl;
}

/**
 * Write multiple keys to output stream
 */
void writeKeys(ostream &fout, const vector<KeyPoint> &keys) {
	int size = keys.size();
	fout << size << endl;
	for(int i = 0; i < size; ++i) {
		writeKey(fout, keys[i]);
	}
}

/**
 * Write descriptor to output stream
 */
void writeDes(ostream &fout, const Mat &des) {
	fout << des.rows << endl;
	fout << des.cols << endl;
	if(des.rows == 0 || des.cols == 0) {
		return;
	}
	
	int size = des.rows * des.cols;
	MatConstIterator_<float> it = des.begin<float>();
	
	while(it != des.end<float>()) {
		fout << *it << endl;
		++it;
	}
}

/**
 * Get a copy of a Mat
 * @return: copy of the mat specified in parametter
 */
Mat copyMat(const Mat &m) {
	float *s = new float[m.rows*m.cols];
	MatConstIterator_<float> it = m.begin<float>();
	int i = 0;
	while(it != m.end<float>()) {
		s[i] = *it;
		i++;
		it++;
	}

	return Mat(m.rows, m.cols, CV_32F, s);
}

/**
 * Main function: extract keypoints and descriptors from multiple images
 */
int main(int argc, char *argv[]) {
	string s;
	SurfDescriptorExtractor sde;
	int count = 0;
	while(getline(cin, s)) {
		cout << s << endl;
		Mat img = imread(s, 0);

		vector<KeyPoint> keys;
		Mat des;

		int level = 500;
		SurfFeatureDetector sfd(level);
		sfd.detect(img, keys);
		// extract only the appropriate number of keypoints
		while(keys.size() > 160) {
			keys.clear();
			level += 500;	// increase threshold to reduce number of detected keypoints
			SurfFeatureDetector sfd1(level);
			sfd1.detect(img, keys);
			level++;
		}
		
		// compute descriptor from keypoint and image
		sde.compute(img, keys, des);
		cout << "Ok " << endl;

		stringstream ss;
		ss << s << ".txt";
		ofstream fout(ss.str().c_str());
		/*
		fout << count << endl; // bookId
		fout << "Title " << s << endl; // title
		fout << "Author " << count << endl; // author
		fout << "Info " << count << endl; // info
		fout << "Tags " << count << endl; // tags
		fout << 0 << endl; // rating
		fout << 0 << endl; // rateCount
		fout << s << endl;	// path to image
		fout << 15000 << endl; // price
		*/
		fout << s << endl;
		writeKeys(fout, keys);
		cout << "Keys num " << keys.size() << endl;
		writeDes(fout, des);
		fout.close();
		count++;
	}
	
	return 0;
}