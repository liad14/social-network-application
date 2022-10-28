//
// Created by spl211 on 05/01/2022.
//

#ifndef BOOST_ECHO_CLIENT_SERVERTOCLIENT_H
#define BOOST_ECHO_CLIENT_SERVERTOCLIENT_H
#include "../include/ConnectionHandler.h"

class ServerToClient {
    //------------------------------------------fields-----------------------------------------------------------
private:
    ConnectionHandler &myConHandler;

    //-------------------------------------------methods---------------------------------------------------------------
public:
    void run();

//---------------------------------------Constructor---------------------------------------------------------------
ServerToClient(ConnectionHandler &connectionHandler);
};


#endif //BOOST_ECHO_CLIENT_SERVERTOCLIENT_H
