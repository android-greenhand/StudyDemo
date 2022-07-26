//
// Created by 58 on 2022/7/12.
//

#ifndef STUDYDEMO_IHELLOSERVICE_H
#define STUDYDEMO_IHELLOSERVICE_H



/* ²Î¿¼: frameworks\av\include\media\IMediaPlayerService.h */



#include <utils/Errors.h>  // for status_t
#include <utils/KeyedVector.h>
#include <utils/RefBase.h>
#include <utils/String8.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>
#include <system/audio.h>

#define HELLO_SVR_CMD_SAYHELLO     0
#define HELLO_SVR_CMD_SAYHELLO_TO  1


namespace android {

    class IHelloService : public IInterface {
    public:
        DECLARE_META_INTERFACE(HelloService);

        virtual void sayhello(void) = 0;

        virtual int sayhello_to(const char *name) = 0;
    };

    class BnHelloService : public BnInterface<IHelloService> {
    public:
        virtual status_t onTransact(uint32_t code,
                                    const Parcel &data,
                                    Parcel *reply,
                                    uint32_t flags = 0);

        virtual void sayhello(void);

        virtual int sayhello_to(const char *name);

    };
}

#endif //STUDYDEMO_IHELLOSERVICE_H
