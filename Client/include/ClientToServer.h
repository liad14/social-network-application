//
// Created by spl211 on 05/01/2022.
//

#ifndef BOOST_ECHO_CLIENT_CLIENTTOSERVER_H
#define BOOST_ECHO_CLIEionHandler.h"
#include "ConnectionHandler.h"
#include "thread"
class ClientToServer {

    //---------------------------------------------fields--------------------------------------------------------
private:
    bool isLogin;
    ConnectionHandler &myConHandler;

public:
    //---------------------------------------------Constructor-----------------------------------------------------
    ClientToServer(ConnectionHandler &connectionHandler);

    //---------------------------------------------methods---------------------------------------------------------
    void run();

private:
    short opcodeToShort(std::string opcode) {};
    std::string getlocalTime() {};

};


#endif //BOOST_ECHO_CLIENT_CLIENTTOSERVER_H
