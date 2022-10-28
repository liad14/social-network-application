//
// Created by spl211 on 05/01/2022.
//

#include "string"
#include "vector"


#include "../include/EncoderDecoder.h"
std::vector<std::string> EncoderDecoder::keyboardToVector(const std::string &input, const std::string & splitChar) { // TODO call this function from conHandler

    std::vector<std::string> words;
    int first = 0;
    int last;
    //td::string = "liad is doing homework"
    while ((last = input.find(splitChar, first)) != std::string::npos) {
        words.push_back(input.substr(first, last - first)); // {liad, is, doing, homework}
        first = last + 1;
    }
    words.push_back(input.substr(first, last - first));
    return words;
}

short EncoderDecoder::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void EncoderDecoder::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}