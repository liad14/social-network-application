
//
// Created by spl211 on 05/01/2022.
//
#include <string>
#include <vector>
#include <iostream>
#include "../include/EncoderDecoder.h"
#include "../include/ClientToServer.h"
#include <ctime>

ClientToServer::ClientToServer(ConnectionHandler &connectionHandler) : myConHandler(connectionHandler) {
}

void ClientToServer::run() {
    while (!myConHandler.isShouldTerminate()) {
        std::string keyboardAsString = "";
        std::getline(std::cin, keyboardAsString);
        EncoderDecoder *myEncDec = new EncoderDecoder();
        std::vector<std::string> keyboardAsVector = EncoderDecoder::keyboardToVector(keyboardAsString, " ");

        std::string opcodeAsString = keyboardAsVector[0];
        char opCodeToBytes[2];

        bool answer = true;
//        bytes[] opcodeAsByte;
//        opcodeAsByte = new byte[2];
//        std::string opcodeAsString = keyboardAsVector[0];
        if (opcodeAsString == "REGISTER") {
            short opcodeAsShort = 01;//opcodeToShort(opcodeAsString);
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            myConHandler.sendFrameAscii(keyboardAsVector[1], '\0');
            myConHandler.sendFrameAscii(keyboardAsVector[2], '\0');
            myConHandler.sendFrameAscii(keyboardAsVector[3], '\0');
            myConHandler.sendFrameAscii("", ';');
        }
        else if (opcodeAsString == "LOGIN") {
            short opcodeAsShort = 02;//opcodeToShort(opcodeAsString);
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            myConHandler.sendFrameAscii(keyboardAsVector[1], '\0');
            myConHandler.sendFrameAscii(keyboardAsVector[2], '\0');
            myConHandler.sendFrameAscii(keyboardAsVector[3], ';');
        }
        else if (opcodeAsString == "LOGOUT") {
            short opcodeAsShort = 03;//opcodeToShort(opcodeAsString);
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            myConHandler.sendFrameAscii("", ';');
            std::this_thread::sleep_for(std::chrono::milliseconds(400));
            std::this_thread::sleep_for(std::chrono::milliseconds(600));

        }
        else if (opcodeAsString == "FOLLOW") {
            short opcodeAsShort = 04;//opcodeToShort(opcodeAsString);
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            std::string toSend = keyboardAsVector[1] + keyboardAsVector[2];
            myConHandler.sendFrameAscii(toSend, '\0');
            myConHandler.sendFrameAscii("", ';');
        }
        else if (opcodeAsString == "POST") {
            short opcodeAsShort = 05;
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            std::string content = keyboardAsVector[1];
            for (int i = 2; i < keyboardAsVector.size(); i++) { //TODO maybe int i = 2
                content = content + " " + keyboardAsVector[i];
            }
            myConHandler.sendFrameAscii(content, '\0');
            myConHandler.sendFrameAscii("", ';');
        }
        else if (opcodeAsString == "PM") {
            short opcodeAsShort = 06;
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            std::string userName = keyboardAsVector[1];
            myConHandler.sendFrameAscii(userName, '\0');
            std::string content = keyboardAsVector[2];
            for (int i = 3; i < keyboardAsVector.size(); i++) {
                content = content + " " + keyboardAsVector[i];
            }
                myConHandler.sendFrameAscii(content, '\0');

                //-------------getLocalTime----------
                time_t now = time(0);
                tm *localTime = localtime(&now);
                std::string time =
                        std::to_string(localTime->tm_mday) + "-" + std::to_string(localTime->tm_mon + 1) + "-"
                        + std::to_string(localTime->tm_year + 1900) + " " + std::to_string(localTime->tm_hour) + ":" +
                        std::to_string(localTime->tm_min);
                myConHandler.sendFrameAscii(time, '\0');
                myConHandler.sendFrameAscii("", ';');
        }
        else if (opcodeAsString == "LOGSTAT") {
            short opcodeAsShort = 07;//opcodeToShort(opcodeAsString);
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            myConHandler.sendFrameAscii("", ';');
        }
        else if (opcodeAsString == "STAT") {
            short opcodeAsShort = 8;
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            std::string list = keyboardAsVector[1];
            for (int i = 2; i < keyboardAsVector.size(); i++) {
                list = list + "|" + keyboardAsVector[i];
            }
            myConHandler.sendFrameAscii(list, '\0');
            myConHandler.sendFrameAscii("", ';');
        }
        if (opcodeAsString == "BLOCK") {
            short opcodeAsShort = 12;
            myEncDec->shortToBytes(opcodeAsShort, opCodeToBytes);
            myConHandler.sendBytes(opCodeToBytes, 2);
            myConHandler.sendFrameAscii(keyboardAsVector[1], '\0');
            myConHandler.sendFrameAscii("", ';');
        }
        keyboardAsVector.clear();
    }
}