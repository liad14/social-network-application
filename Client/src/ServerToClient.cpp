//
// Created by spl211 on 05/01/2022.
//



#include "../include/ServerToClient.h"
#include "../include/EncoderDecoder.h"
#include <string>
#include <vector>
#include <iostream>

ServerToClient::ServerToClient(ConnectionHandler &connectionHandler) : myConHandler(connectionHandler){}


void ServerToClient::run() {

    while (!myConHandler.isShouldTerminate()) {
        EncoderDecoder *myEncDec = new EncoderDecoder;
        std::string output;
        char opcodeAsString[2];
        myConHandler.getBytes(opcodeAsString, 2);
        short opcodeAsShort = myEncDec->bytesToShort(opcodeAsString);

        switch(opcodeAsShort) {

            case 9: {
                output = "NOTIFICATION";
                std::string sender;
                myConHandler.getFrameAscii(sender, '\0');
                std::string Pm_Public = sender.substr(0, 1);
                output = output + (Pm_Public == "0" ? " PM " : " Public ");
                output = output + sender.substr(1, sender.length() - 2);
                std::string content;
                myConHandler.getFrameAscii(content, '\0');
                output = output + " " + content.substr(0, content.length() - 1);
                std::cout<<output<<std::endl;
                break;
            }

            case 10: {
                output = "ACK";
                char ackToOut[2];
                myConHandler.getBytes(ackToOut, 2);
                short ackAsBytes = myEncDec->bytesToShort(ackToOut);
                output = output + " " + std::to_string(ackAsBytes);

                if (ackAsBytes == 3) {
                    myConHandler.setShouldTerminate(true);
                }

                std::string additionalMsg;
                myConHandler.getFrameAscii(additionalMsg, ';');
                    if (additionalMsg.length() > 0)
                        output = output + " " + additionalMsg.substr(0, additionalMsg.size() - 1);
                std::cout<<output<<std::endl;
                break;
                }
               // break;
            case 11: {
                output = "ERROR";
                char ackToError[2];
                myConHandler.getBytes(ackToError, 2);
                short errorAsBytes = myEncDec->bytesToShort(ackToError);
                output = output + " " + std::to_string(errorAsBytes);
                std::string takeLastDelimeter;
                myConHandler.getFrameAscii(takeLastDelimeter, ';');
                std::cout<<output<<std::endl;
                break;
            }
        }
        delete(myEncDec);
    }
}
