//
// Created by 58 on 2022/7/12.
//

/* 参考: frameworks\av\media\mediaserver\Main_mediaserver.cpp */

#define LOG_TAG "HelloService"
//#define LOG_NDEBUG 0

#include <fcntl.h>
#include <sys/prctl.h>
#include <sys/wait.h>
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#include <cutils/properties.h>
#include <utils/Log.h>


using namespace android;

void main(void)
{
    /* addService */

    /* while(1){ read data, 解析数据, 调用服务函数 } */

    /* 打开驱动, mmap */
    sp<ProcessState> proc(ProcessState::self());

    /* 获得BpServiceManager */
    sp<IServiceManager> sm = defaultServiceManager();

    sm->addService(String16("hello"), new BnHelloService());

    /* 循环体 */
    ProcessState::self()->startThreadPool();
    IPCThreadState::self()->joinThreadPool();
}
