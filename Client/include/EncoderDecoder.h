//
// Created by spl211 on 05/01/2022.
//

#ifndef BOOST_ECHO_CLIENT_CHANNEL_H
#define BOOST_ECHO_CLIENT_CHANNEL_H

#include <iostream>
#include <vector>



class EncoderDecoder {
    //------------------------------------------fields------------------------------------------------------------------



    //------------------------------------------Constructor-------------------------------------------------------------


    //------------------------------------------methods-----------------------------------------------------------------
public:
    static std::vector<std::string> keyboardToVector(const std::string &input, const std::string & splitChar);
    short bytesToShort(char* bytesArr);
    void shortToBytes(short num, char* bytesArr);




};


#endif //BOOST_ECHO_CLIENT_CHANNEL_H
