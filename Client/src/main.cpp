//
// Created by spl211 on 05/01/2022.
//

#include "../include/EncoderDecoder.h"
#include "../include/ServerToClient.h"
#include "../include/ClientToServer.h"
#include <stdlib.h>
#include <thread>

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler myConHandler(host, port);

    if (!myConHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    ClientToServer clientToServer(myConHandler);
    ServerToClient serverToClient(myConHandler);

    std::thread th1_ClientToServer(&ClientToServer::run, & clientToServer);
    std::thread th2_ServerToClient(&ServerToClient::run, & serverToClient);

    th1_ClientToServer.join();
    th2_ServerToClient.join();

    return 0;
}