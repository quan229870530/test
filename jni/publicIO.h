#ifndef _SYNCLASS_publicIO_H_
#define _SYNCLASS_publicIO_H_
#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

//#include "SynClassUI.h"
//#include "SynDataSet.h"
//#include "SynDirDataSet.h"

#define MEM_ALLOC(size) malloc(size)
#define MemFree(pPtr)  free(pPtr)
#define MemAlloc(size) malloc(size)
//#define FileSeek(hIn, uOffset,tag)  fseek(hIn, uOffset, tag)






#define LOGI_D(...) __android_log_print(ANDROID_LOG_DEBUG, "JniMsg", __VA_ARGS__)
#define LOGI_I(...) __android_log_print(ANDROID_LOG_INFO, "JniMsg", __VA_ARGS__)
#define LOGI_W(...) __android_log_print(ANDROID_LOG_WARN, "JniMsg", __VA_ARGS__)
#define LOGI_E(...) __android_log_print(ANDROID_LOG_ERROR, "JniMsg", __VA_ARGS__)







#define FS_INVALID_HANDLE 0
#define  FILESEEK_BEGIN SEEK_SET

#define FILE_SUCCESS            0x0F000000
#define FILE_FAIL               0x0F000001
#define ERR_FILEOPEN_MUCH       0x0F000002
#define ERR_FILEOPEN_FAIL       0x0F000003
#define ERR_FILECLOSE_FAIL      0x0F000004

//文件打开模式
#define FILEMODE_MASK           7
#define FILEMODE_READONLY       0x00           /*以只读方式打开*/
#define FILEMODE_WRITE          0x01           /*以写方式打开*/
#define FILEMODE_READWRITE      0x02           /*以读写方式打开*/
#define FILEMODE_APPEND         0x03           /*附加数据方式打开*/
#define FILEMODE_CREATEWRITE    0x04           /*若文件不存在则先创建再以读写方式打开*/
#define FILEMODE_NOSHAREWRITE   6           /*不允许其他人共享写文件，黄小明为Mp3增加的，2003/7/21*/


/*
jint FileOpen( char *pFileName, jint openMode);
jint  FileClose(jint fd);
//jint FileLength(jint fd);
jint SynClass_FileRead( jint hFile, char *buffer, jint count );
/* 移动文件指针 
jint FileSeek( jint fd, jint offset, jint origin );
/* 写文件 
jint   FileWrite(jint fd, char *buf, jint count);
*/
#endif
