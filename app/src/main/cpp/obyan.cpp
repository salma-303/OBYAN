///Assuming to run on Linux OS

#include <iostream>
#include <chrono>
#include <thread>
#include <cstring>
#include <android/log.h>
#include <variant>
#include <stdint.h>
#include <stdio.h>
#include <jni.h>
//#include <layer.h> //compile,replace with the correct header file name
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <linux/can.h>  // This includes the can_frame struct definition
#include <linux/can/raw.h>
#include <unistd.h>

bool continueReceiving = false;
#define CAN_MAX_DLEN 8
const int frame = 40;
const int data = 9;

uint32_t frames[frame][data] = {

//ID   //Data
        {0x100, 0x4,  0x2,  0x9,  0x4,  0x25, 0x6,  0x22, 0x7},
        {0x100, 0x24, 0x5B, 0x56, 0x14, 0x11, 0x61, 0x34, 0x74},
        {0x101, 0x12, 0x5,  0x77, 0x47, 0x55, 0x6C, 0x66, 0xB7},
        {0x101, 0x9,  0x91, 0x9A, 0x62, 0x91, 0x9,  0xA3, 0x12},
        {0x101, 0x33, 0xA3, 0x2,  0x33, 0x5,  0xB9, 0x11, 0x87},
        {0x101, 0xA,  0x76, 0x89, 0xD2, 0x62, 0x4,  0x32, 0x9},
        {0x101, 0x37, 0x90, 0x10, 0x13, 0x73, 0x19, 0x2C, 0x3},
        {0x100, 0xC,  0x1,  0x9B, 0x56, 0x2,  0x6D, 0x33, 0x45},
        {0x100, 0xB,  0x22, 0x13, 0x69, 0xB2, 0x76, 0x35, 0xA5},
        {0x100, 0x11, 0x6,  0x8B, 0x19, 0x84, 0x60, 0x71, 0x26},

//ID   //Data
        {0x101, 0x4,  0x56, 0x91, 0x62, 0x2,  0x33, 0x5,  0xB9},
        {0x101, 0x41, 0x81, 0x69, 0xB2, 0x62, 0x91, 0x9,  0xA3},
        {0x100, 0x34, 0x19, 0x13, 0xDD, 0xA4, 0x68, 0x22, 0x7},
        {0x101, 0x74, 0x1A, 0x77, 0x47, 0x55, 0x6C, 0x66, 0xB7},
        {0x101, 0xA4, 0x10, 0xB2, 0x76, 0x25, 0x6,  0x22, 0x7},
        {0x100, 0x54, 0x81, 0x62, 0x91, 0x9,  0x13, 0x69, 0xB2},
        {0x101, 0x2,  0x7,  0x11, 0xB4, 0x20, 0x6,  0x22, 0x7},
        {0x100, 0x1,  0x44, 0x33, 0xA3, 0x2,  0x5,  0x77, 0x47},
        {0x101, 0x55, 0x21, 0x61, 0x22, 0x7,  0x10, 0xB2, 0x76},
        {0x100, 0xC,  0xA,  0x6C, 0x19, 0x13, 0x10, 0x13, 0x73},

//ID   //Data
        {0x100, 0xA4, 0x10, 0xB2, 0x76, 0x25, 0x6,  0x22, 0x7},
        {0x101, 0x11, 0x66, 0x9,  0x4,  0x25, 0x6,  0x22, 0x7},
        {0x101, 0x4,  0x56, 0x91, 0x62, 0x2,  0x33, 0x5,  0xB9},
        {0x101, 0x7,  0x68, 0x22, 0x7,  0x19, 0x13, 0xDD, 0x91},
        {0x101, 0xB,  0x1A, 0x77, 0x47, 0x55, 0x6,  0x22, 0x7},
        {0x101, 0x41, 0x81, 0x69, 0xB2, 0x62, 0x91, 0x9,  0xA3},
        {0x100, 0x1,  0x33, 0x13, 0x69, 0xB2, 0x11, 0xB4, 0x20},
        {0x100, 0x21, 0xC,  0x7,  0x10, 0xB2, 0x91, 0x62, 0x2},
        {0x100, 0x11, 0x6,  0x8B, 0x19, 0x84, 0x60, 0x71, 0x26},
        {0x101, 0x55, 0x21, 0x61, 0x22, 0x7,  0x10, 0xB2, 0x76},

//ID   //Data
        {0x100, 0x90, 0x10, 0x13, 0x4,  0x25, 0x6,  0x22, 0x7},
        {0x100, 0x11, 0x55, 0x6C, 0x66, 0xB7, 0xA,  0x76, 0x89},
        {0x100, 0x2C, 0x3,  0x5,  0x77, 0x47, 0x55, 0x24, 0x5B},
        {0x100, 0x14, 0x11, 0x61, 0x30, 0xC5, 0x52, 0xA1, 0x88},
        {0x101, 0x2,  0x33, 0x5,  0x12, 0x7,  0x98, 0x43, 0x2C},
        {0x101, 0x62, 0x4,  0x32, 0x9,  0x21, 0x62, 0x56, 0x55},
        {0x101, 0x22, 0x13, 0x69, 0xB2, 0x9,  0x91, 0x9A, 0x62},
        {0x100, 0x73, 0x19, 0x2C, 0x2,  0x6D, 0x12, 0x5,  0x77},
        {0x101, 0x22, 0x7,  0x10, 0x5B, 0x56, 0x11, 0x61, 0x34},
        {0x101, 0x91, 0x9,  0xA3, 0x19, 0x84, 0x34, 0x19, 0x13}

};






// no need to define it ig (struct can_frame)

// Define the CAN frame struct

struct can_fram {
    uint64_t can_id;   // CAN identifier (11-bit or 29-bit)
    union {
        uint8_t len;   // Data Length Code (1 byte in the data payload)
        uint8_t can_dlc; // Data Length Code (1 byte in the data payload)
    } __attribute__((packed));
    uint8_t __pad;     // Padding byte (not used)
    uint8_t __res0;    // Reserved byte (not used)
    uint8_t len8_dlc;  // Data Length Code (1 byte in the data payload)
    uint32_t data[CAN_MAX_DLEN] __attribute__((aligned(8))); // Data payload (8 bytes) with alignment
};


struct firstFrameData {   //frame_0x100_Data

    //Frame 0x100
    int motorSpeedDec;
    int dcBusCurrentAmps;
    int motorPhaseCurrentAmps;

};


struct secondFrameData {    //frame_0x101_Data

    // Frame 0x101
    double hallInputState;

    double governorGearPositionsDec;
    double motorTempDec;
    double controllerTempDec;

    // individual bits from inputStatus byte
    bool lowSpeed;
    bool highSpeed;
    bool brake;
    bool antiTheft;
    bool parkingGear;
    bool cruiseState;
    bool reversing;
    bool singleSupport;


    // individual bits from faultCode byte
    bool overVoltage;
    bool underVoltage;
    bool overCurrent;
    bool mosFailure;
    bool hallFailure;
    bool controllerOverheated;
    bool stallProtection;
    bool handlebarFailure;

};


// Define the variant type to hold the data for different frame identifiers
using FrameDataVariant = std::variant<firstFrameData, secondFrameData>;


// Global variables to store the latest processed data
firstFrameData cachedDataFrame100;
secondFrameData cachedDataFrame101;


/*
struct can_fram frames[] = {

        {
                .can_id = 0x100,       // 11-bit CAN identifier (0x100)
                .len = 8,             // Data Length Code (8 bytes)
                .__pad = 0x00,         // Padding byte (not used)
                .__res0 = 0x00,        // Reserved byte (not used)
                .len8_dlc = 8,        // Data Length Code (8 bytes)
                // Data payload (8 bytes) in hexadecimal format
                .data = {0x77, 0xA, 0x3, 0x1, 0x5, 0x90, 0x54, 0x81}
        },            {
                .can_id = 0x100,       // 11-bit CAN identifier (0x100)
                .len = 8,             // Data Length Code (8 bytes)
                .__pad = 0x00,         // Padding byte (not used)
                .__res0 = 0x00,        // Reserved byte (not used)
                .len8_dlc = 8,        // Data Length Code (8 bytes)
                // Data payload (8 bytes) in hexadecimal format
                .data = {0x4, 0x1, 0x9, 0x4, 0x25, 0x6, 0x22, 0x7}
        }


        // Add more frames to the array as needed

};
*/



FrameDataVariant processFrame(const struct can_fram &frame) {
    switch (frame.can_id) {

        case 0x100: {

            firstFrameData frame1Data;

            // Extract the data bytes in big-endian format
            uint8_t highByte = frame.data[0];
            uint8_t lowByte = frame.data[1];

            uint8_t dcBusHighByte = frame.data[2];
            uint8_t dcBusLowByte = frame.data[3];

            uint8_t reserved1 = frame.data[4];  //msh mohem
            uint8_t reserved2 = frame.data[5];  //msh mohem

            uint8_t motorPhaseHighByte = frame.data[6];
            uint8_t motorPhaseLowByte = frame.data[7];

            // Concatenate the bytes (Big-endian format)
            uint16_t motorSpeed = (static_cast<uint16_t>(highByte) << 8) | lowByte;
            uint16_t dcBusCurrent = (static_cast<uint16_t>(dcBusHighByte) << 8) | dcBusLowByte;
            uint16_t motorPhaseCurrent =
                    (static_cast<uint16_t>(motorPhaseHighByte) << 8) | motorPhaseLowByte;

            // Convert binary to decimal for motor speed, DC bus current, and motor wire phase current
            //                  &&
            // Store the processed values in the variant

            frame1Data.motorSpeedDec = static_cast<double>(motorSpeed);
            frame1Data.dcBusCurrentAmps = static_cast<double>(dcBusCurrent) * 0.1;
            frame1Data.motorPhaseCurrentAmps = static_cast<double>(motorPhaseCurrent) * 0.1;

            char buffer[100];

            __android_log_print(ANDROID_LOG_DEBUG, "YourTag", "Data to be processed: : lfX",
                                frame1Data.motorSpeedDec);

            // Return the processed data in the variant
            return frame1Data;

            break;
        }


        case 0x101: {

            secondFrameData frame2Data;

            // Extract the data bytes
            uint8_t hallInputState = frame.data[0];     //msh mohem

            uint8_t inputStatus = frame.data[1];

            uint8_t governorGearPositions = frame.data[2];

            uint8_t motorTempHighByte = frame.data[3];
            uint8_t motorTempLowByte = frame.data[4];

            uint8_t controllerTempHighByte = frame.data[5];
            uint8_t controllerTempLowByte = frame.data[6];

            uint8_t faultCode = frame.data[7];

            // Extract individual bits from inputStatus byte && Store the processed values in the variant
            frame2Data.lowSpeed = inputStatus & 0x01;
            frame2Data.highSpeed = inputStatus & 0x02;
            frame2Data.brake = inputStatus & 0x04;
            frame2Data.antiTheft = inputStatus & 0x08;
            frame2Data.parkingGear = inputStatus & 0x10;
            frame2Data.cruiseState = inputStatus & 0x20;
            frame2Data.reversing = inputStatus & 0x40;
            frame2Data.singleSupport = inputStatus & 0x80;

            // Convert DLC3 (Governor gear positions) to decimal
            frame2Data.governorGearPositionsDec = static_cast<double>(governorGearPositions);

            // Concatenate bytes for motor temperature and convert to decimal
            uint16_t motorTemp = (static_cast<uint16_t>(motorTempHighByte) << 8) | motorTempLowByte;
            frame2Data.motorTempDec = static_cast<double>(motorTemp);

            // Concatenate bytes for controller internal temperature and convert to decimal
            uint16_t controllerTemp =
                    (static_cast<uint16_t>(controllerTempHighByte) << 8) | controllerTempLowByte;
            frame2Data.controllerTempDec = static_cast<double>(controllerTemp);

            // Extract individual bits from faultCode byte, Store the processed values in the variant
            frame2Data.overVoltage = faultCode & 0x01;
            frame2Data.underVoltage = faultCode & 0x02;
            frame2Data.overCurrent = faultCode & 0x04;
            frame2Data.mosFailure = faultCode & 0x08;
            frame2Data.hallFailure = faultCode & 0x10;
            frame2Data.controllerOverheated = faultCode & 0x20;
            frame2Data.stallProtection = faultCode & 0x40;
            frame2Data.handlebarFailure = faultCode & 0x80;


            // Return the processed data in the variant
            return frame2Data;

            //break;
        }

        default:
            // Handle unknown identifiers or implement specific actions
            std::cout << "Received frame with unknown identifier: " << frame.can_id << std::endl;

            // Return an empty variant in case of unknown frame type
            return FrameDataVariant{};

            // may perform default actions or handle unrecognized identifiers
            //break;
    }


}


void receiveData() {
    while (continueReceiving) {
        // Loop through the array and process each frame
        for (int i = 0; i < frame; i++) {
            struct can_fram dummy;
            dummy.can_id = frames[i][0];
            int k = 0;
            __android_log_print(ANDROID_LOG_DEBUG, "YourTag", "CAN_ID: %04X", dummy.can_id);
            __android_log_print(ANDROID_LOG_DEBUG, "YourTag",
                                "=====================================");

            for (int m = 1; m < data; m++) {
                dummy.data[k] = frames[i][m];
                k++;
                char buffer[100];
                __android_log_print(ANDROID_LOG_DEBUG, "YourTag", "Data: %02X", dummy.data[k]);
            }

            FrameDataVariant data = processFrame(dummy);

            // Store the processed data in the corresponding global variables
            if (std::holds_alternative<firstFrameData>(data)) {
                cachedDataFrame100 = std::get<firstFrameData>(data);
            } else if (std::holds_alternative<secondFrameData>(data)) {
                cachedDataFrame101 = std::get<secondFrameData>(data);
            }

            std::this_thread::sleep_for(
                    std::chrono::milliseconds(100)); // Delay for 100 milliseconds
        }
    }
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_obyan_MainActivity_getFrame1Data(JNIEnv *env, jclass clazz) {
    jclass cls = env->FindClass(
            "com/example/obyan/FirstFrameData"); // Replace with the correct package and class name if changedddddd
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(III)V");

    jobject obj = env->NewObject(cls, constructor, cachedDataFrame100.motorSpeedDec,
                                 cachedDataFrame100.dcBusCurrentAmps,
                                 cachedDataFrame100.motorPhaseCurrentAmps);
    char buffer[100];
    // Assuming you have a buffer like this in your C/C++ code

// Use __android_log_print to print to logcat
    __android_log_print(ANDROID_LOG_DEBUG, "YourTag", "Motor Speed: : %f",
                        cachedDataFrame100.motorSpeedDec);
    // sprintf(buffer,"Frame 101 Data: %d", cachedDataFrame100.motorSpeedDec);
    return obj;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_obyan_MainActivity_getFrame2Data(JNIEnv *env, jclass clazz) {
    jclass cls = env->FindClass(
            "com/example/obyan/SecondFrameData"); // Replace with the correct package and class name if changed :"(
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(DDDDZZZZZZZZZZZZZZZZ)V");

    jobject obj = env->NewObject(cls, constructor,
                                 cachedDataFrame101.hallInputState,
                                 cachedDataFrame101.governorGearPositionsDec,
                                 cachedDataFrame101.motorTempDec,
                                 cachedDataFrame101.controllerTempDec,
                                 cachedDataFrame101.lowSpeed,
                                 cachedDataFrame101.highSpeed,
                                 cachedDataFrame101.brake,
                                 cachedDataFrame101.antiTheft,
                                 cachedDataFrame101.parkingGear,
                                 cachedDataFrame101.cruiseState,
                                 cachedDataFrame101.reversing,
                                 cachedDataFrame101.singleSupport,
                                 cachedDataFrame101.overVoltage,
                                 cachedDataFrame101.underVoltage,
                                 cachedDataFrame101.overCurrent,
                                 cachedDataFrame101.mosFailure,
                                 cachedDataFrame101.hallFailure,
                                 cachedDataFrame101.controllerOverheated,
                                 cachedDataFrame101.stallProtection,
                                 cachedDataFrame101.handlebarFailure
    );
    return obj;
}


extern "C" JNIEXPORT void JNICALL
Java_com_example_obyan_MainActivity_startReceivingData(JNIEnv *env, jclass clazz) {
    continueReceiving = true;
    receiveData();
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_obyan_MainActivity_stopReceivingData(JNIEnv *env, jclass clazz) {
    continueReceiving = false;
    receiveData();

}
