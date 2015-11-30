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

//�ļ���ģʽ
#define FILEMODE_MASK           7
#define FILEMODE_READONLY       0x00           /*��ֻ����ʽ��*/
#define FILEMODE_WRITE          0x01           /*��д��ʽ��*/
#define FILEMODE_READWRITE      0x02           /*�Զ�д��ʽ��*/
#define FILEMODE_APPEND         0x03           /*�������ݷ�ʽ��*/
#define FILEMODE_CREATEWRITE    0x04           /*���ļ����������ȴ������Զ�д��ʽ��*/
#define FILEMODE_NOSHAREWRITE   6           /*�����������˹���д�ļ�����С��ΪMp3���ӵģ�2003/7/21*/


/*
jint FileOpen( char *pFileName, jint openMode);
jint  FileClose(jint fd);
//jint FileLength(jint fd);
jint SynClass_FileRead( jint hFile, char *buffer, jint count );
/* �ƶ��ļ�ָ�� 
jint FileSeek( jint fd, jint offset, jint origin );
/* д�ļ� 
jint   FileWrite(jint fd, char *buf, jint count);
*/
#endif
