
#include "iconv.h"
#include "publicIO.h"
///#include <android/secretfile.h>

static int __M11CopyAllFile(const char *ImageFileName);
static int __M11CreateDir(const char *FileName);
static int __StrRepace(unsigned char *Str, unsigned char src, unsigned char dst);
static int __M11CopyFile(const char *SrcFileName, const char *DstFileName);
static int __M11CopyImgFile(FILE *hSrcFile, long long StartPos, unsigned long Length, const char *DstFileName);
static int __Gbk2Utf8(char *DstBuf, char *SrcBuf);

#define LOGI_D(...) __android_log_print(ANDROID_LOG_DEBUG, "JniMsg", __VA_ARGS__)
#define LOGI_I(...) __android_log_print(ANDROID_LOG_INFO, "JniMsg", __VA_ARGS__)
#define LOGI_W(...) __android_log_print(ANDROID_LOG_WARN, "JniMsg", __VA_ARGS__)
#define LOGI_E(...) __android_log_print(ANDROID_LOG_ERROR, "JniMsg", __VA_ARGS__)

jint M11IsExistSystemFile(void);
jint M11IsExistDataFile(void);
jint M11CopyDataFile(void);
jint M11CopySystemFile(void);
jint M11CopyInternalDataFile(void);
static int __CheckWritedFile(FILE *hSrcFile, long long nStartPos, unsigned long nDataLen, const char *pDstFilePath);

#define N7PLUS	1
//#define N787	1 // ODM
//#define N787S	1 // ODM
//#define N797	1 // ODM

#define gMethodNum  4
#define gClassName "com/android/UpdateTopSystem/DataEng"

#ifdef N7PLUS

#define SRC_SYS_FILE_NAME		"/mnt/external_sd/update.zip"
#define DST_SYS_FILE_NAME		"" /// no use

#define SRC_DATA_FILE_NAME		"/mnt/external_sd/D.img"
#define DST_DATA_FILE_PATH		"/mnt/sdcard"

#define SRC_INTERNAL_DATA_FILE_NAME		"/mnt/internal_sd/D.img"

#elif N787

#define SRC_SYS_FILE_NAME		"/storage/sdcard1/update.zip"
#define DST_SYS_FILE_NAME		"" /// no use

#define SRC_DATA_FILE_NAME		"/storage/sdcard1/D.img"
#define DST_DATA_FILE_PATH		"/storage/sdcard0"

#elif N787S

#define SRC_SYS_FILE_NAME		"/storage/sdcard1/update.zip"
#define DST_SYS_FILE_NAME		"" /// no use

#define SRC_DATA_FILE_NAME		"/storage/sdcard1/D.img"
#define DST_DATA_FILE_PATH		"/storage/sdcard0"

#elif N797

#define SRC_SYS_FILE_NAME		"/storage/sdcard1/update.zip"
#define DST_SYS_FILE_NAME		"" /// no use

#define SRC_DATA_FILE_NAME		"/storage/sdcard1/D.img"
#define DST_DATA_FILE_PATH		"/storage/sdcard0"

#else

#define SRC_SYS_FILE_NAME		"/mnt/sdcard/extsd/update.zip"
#define DST_SYS_FILE_NAME		"/cache/update.zip"

#define SRC_DATA_FILE_NAME		"/mnt/sdcard/extsd/D.img"
#define DST_DATA_FILE_PATH		"/mnt/sdcard"

#endif

#define TEST_DATA_READ	"/mnt/sdcard/eng.900"
#define TEST_DATA_WRITE	"/mnt/sdcard/wtdata"

#define READ_BUFFER_LEN 	(128 * 1024)

JavaVM* jvm = 0;

static JNINativeMethod gMethods[gMethodNum] = {
/* name, signature, funcPtr */
	{ "JniM11IsExistSystemFile", "()I", (jint *)M11IsExistSystemFile },
	{ "JniM11IsExistDataFile", "()I", (jint *)M11IsExistDataFile },
	{ "JniM11CopyDataFile", "()I", (jint *)M11CopyDataFile },
	{ "JniM11CopyInternalDataFile", "()I", (jint *)M11CopyInternalDataFile },
	{ "JniM11CopySystemFile", "()I", (jint *)M11CopySystemFile },
};


jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv *env;
	jclass cls;
	
	LOGI_D("JNI_OnLoad 1");
	
	jvm = vm;
	
	if ( (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) ) 
		return JNI_ERR;
	
	cls = (*env)->FindClass(env, gClassName);
	(*env)->RegisterNatives(env, cls, gMethods, gMethodNum);
	
	LOGI_D("JNI_OnLoad OK");
	
	return JNI_VERSION_1_4;
}

/*
int TempTestFunc(void)
{
	FILE *fp = NULL;
	char buf[1024] = {0};
	int cnt = 0;
	
	LOGI_D("TempTestFunc!");
	
	do{
		fp = MJFileOpen(TEST_DATA_READ, "rb");
		//fp = open(fileName, "rb");
		if(fp == NULL){
			LOGI_D("fopen %s failed!", TEST_DATA_READ);
			break;
		}
		
		if((cnt = MJFileRead((char*)buf, 1, 1024, fp)) != 1024){
		//if(fread((char*)&Eng900,1, sizeof(ENG900_INFO), fp) != sizeof(ENG900_INFO)){
			LOGI_D("fread ENG900_INFO failed!, only read %d bytes", cnt);
			break;
		}
		MJFileClose(fp);
		fp = NULL;
		
		fp = fopen(TEST_DATA_WRITE, "wb");
		if(fp == NULL){
			LOGI_D("fopen %s failed!", TEST_DATA_WRITE);
			break;
		}
		if((cnt = fwrite((char*)buf, 1, 1024, fp)) != 1024){
				LOGI_D("fwrite ENG900_INFO failed!, only write %d bytes", cnt);
				break;
		}

	}while(0);
	
	if(fp != NULL){
		fclose(fp);	
		fp = NULL;
	}
	
	return 0;	
}
*/

//�ж�ϵͳ�����ļ��Ƿ����
int M11IsExistSystemFile(void)
{
	int		ret;
	int	*hFile;

	ret		= 0;
	hFile = FS_INVALID_HANDLE;
	
	LOGI_D("M11IsExistSystemFile");

	do
	{
		hFile = fopen(SRC_SYS_FILE_NAME, "rb");
		
		LOGI_D("open SRC_SYS_FILE(%s) return %d", SRC_SYS_FILE_NAME, hFile);
		
		if(hFile == FS_INVALID_HANDLE)
			break;

		ret = 1;
	}while(0);

	if(hFile != FS_INVALID_HANDLE)
		fclose(hFile);

	// only for test
	///TempTestFunc();

	return ret;
}

//ÅÐ¶Ï×ÊÁÏÊý¾ÝÊÇ·ñ´æÔÚ
int M11IsExistDataFile(void)
{
	int		ret;
	int	*hFile;

	ret		= 0;
	hFile = FS_INVALID_HANDLE;

	LOGI_D("M11IsExistDataFile");

	do
	{
		hFile = fopen(SRC_DATA_FILE_NAME, "rb");
		
		LOGI_D("open SRC_DATA_FILE(%s) return %d", SRC_DATA_FILE_NAME, hFile);
		
		if(hFile == FS_INVALID_HANDLE)
			break;

		ret = 1;
	}while(0);

	if(hFile != FS_INVALID_HANDLE)
		fclose(hFile);

	return ret;			
}

//copyϵͳ�����ļ�
int M11CopySystemFile(void)
{
	return __M11CopyFile(SRC_SYS_FILE_NAME, DST_SYS_FILE_NAME);
}



typedef struct tagFileHeadInfo
{
	char FileTag[4];
	int	 FileNum;
	char Reserve[16];
}FileHeadInfo;

typedef struct tagFileListInfo
{
	char FileName[124];
	unsigned long StartAddr;
}FileListInfo;

typedef struct tagFileListInfo3
{
	char FileName[124];
	int *StartAddr;
}FileListInfo3;


//copy�����ļ�
int M11CopyDataFile(void)
{
	LOGI_D("M11CopyDataFile");
	return __M11CopyAllFile(SRC_DATA_FILE_NAME);
}

//copy�����ļ�
int M11CopyInternalDataFile(void)
{
	LOGI_D("M11CopyInternalDataFile");
	return __M11CopyAllFile(SRC_INTERNAL_DATA_FILE_NAME);
}

static int __M11CopyAllFile(const char *ImageFileName)
{
	int 	ret;
	int		Count;
	int		ReadLen;
	int		FileNum;
	char	*ReadBuf;
	FILE *hReadFile;
	FileListInfo	ListInfo1;
	FileListInfo	ListInfo2;
	FileHeadInfo	HeadInfo;
	char 			*PrevPath;
	char 			*CurrName;
	char			*TempBuf;
	long offset = 0;

	ret = 0;
	hReadFile = NULL;
	ReadBuf = NULL;
	TempBuf  = NULL;

	LOGI_D("__M11CopyAllFile");

	do
	{
		if( ImageFileName == NULL )
		{
			LOGI_D("ImageFileName = NULL");
			break;
		}

		TempBuf = (char*)malloc(1024*3);
		if( TempBuf == NULL )
		{
			LOGI_D("TempBuf = NULL");
			break;
		}

		memset(TempBuf, 0, 1024*3);
		PrevPath = TempBuf + 1024;
		CurrName = TempBuf + 2048;
		/*
		fseek(logfp, 0, SEEK_END);
		fwrite("ImageFileName", 1, strlen("ImageFileName"), logfp);
		fwrite("\r\n",1,2,logfp);
		*/
		//sprintf
		//LOGI_E("ImageFileName= %s",ImageFileName);

		hReadFile = fopen(ImageFileName, "rb");
		if(hReadFile == NULL)
		{
			LOGI_D("hReadFile = NULL");
			break;
		}

		ReadLen = fread(&HeadInfo, 1, sizeof(HeadInfo), hReadFile);
		if(ReadLen != sizeof(HeadInfo))
		{
			LOGI_D("ReadLen != sizeof(HeadInfo)");
			break;
		}
		//LOGI_E("ReadLen = fread(&HeadInfo, 1, sizeof(HeadInfo), hReadFile)");
		if(strcmp(HeadInfo.FileTag, "img") != 0 || HeadInfo.FileNum <= 0)
		{
			LOGI_D("strcmp(HeadInfo.FileTag, 'img') != 0 || HeadInfo.FileNum <= 0");
			break;
		}

		ReadBuf = (char*) malloc( (HeadInfo.FileNum + 1) * sizeof(FileListInfo) );
		if(ReadBuf == NULL)
		{
			LOGI_D("ReadBuf = NULL");
			break;
		}
		//LOGI_E("ReadBuf = (char*) malloc( (HeadInfo.FileNum + 1) * sizeof(FileListInfo) )");
		//fseek(hReadFile, sizeof(HeadInfo), FILESEEK_BEGIN);
		fseek(hReadFile, sizeof(HeadInfo), FILESEEK_BEGIN);
		ReadLen = fread(ReadBuf, 1, (HeadInfo.FileNum + 1) * sizeof(FileListInfo), hReadFile);
		if(ReadLen != (HeadInfo.FileNum + 1) * sizeof(FileListInfo))
		{
			LOGI_D("ReadLen != (HeadInfo.FileNum + 1) * sizeof(FileListInfo)");
			break;
		}
		//LOGI_E("ReadLen = fread(ReadBuf, 1, (HeadInfo.FileNum + 1) * sizeof(FileListInfo), hReadFile)");
		Count = 0;
		while( Count < HeadInfo.FileNum )
		{
			char	*str;
			int		Temp;

			memcpy( &ListInfo1, ReadBuf + Count * sizeof(FileListInfo), sizeof(FileListInfo) );
			memcpy( &ListInfo2, ReadBuf + (Count+1) * sizeof(FileListInfo), sizeof(FileListInfo) );
			LOGI_E("count = %d", Count);

			long long startAddr1 = ListInfo1.StartAddr;
			long long startAddr2 = ListInfo2.StartAddr;
			LOGI_E("ListInfo1.StartAddr = %lld", startAddr1);
            LOGI_E("ListInfo2.StartAddr = %lld", startAddr2);

			strcpy(TempBuf, ListInfo1.FileName);

			//LOGI_E("TempBuf %s",TempBuf);	
			__StrRepace(TempBuf, '\\', '/');
			//LOGI_E("TempBuf %s",TempBuf);
			str = strrchr(TempBuf, '/');
			if(str == NULL) break;
			*str = 0;
			//LOGI_E("__StrRepace(TempBuf, '\\', '/')");

			if(strcmp(TempBuf, PrevPath) != 0)
			{

				Temp = __M11CreateDir(TempBuf);
				if(Temp == 0)
					break;
				strcpy(PrevPath, TempBuf);
			}

			*str = '/';

			//�õ�����UTF8·��
			//__Gbk2Utf8(TempBuf, ListInfo1.FileName);
			//strcpy(CurrName, DST_DATA_FILE_PATH, TempBuf);
			strcpy(CurrName, DST_DATA_FILE_PATH);
			strcat(CurrName,TempBuf);

			memset(TempBuf,0,1024);
			//�õ�����UTF8·��
			__Gbk2Utf8(TempBuf, CurrName);
			//LOGI_E("CurrName  %s",TempBuf);
			/*
			fseek(logfp, 0, SEEK_END);
			fwrite("copy�����е�", 1, strlen("copy�����е�"), logfp);
			fwrite("\r\n",1,2,logfp);
			*/
			//copy¾µÏñÖÐµÄÎÄ¼þµ½¶ÔÓ¦Ä¿Â¼ÏÂ
			Temp = __M11CopyImgFile(hReadFile, startAddr1, (startAddr2 - startAddr1), TempBuf);
			if(Temp == 0)
			{
				LOGI_D("CopyImgFile failed!");
				break;
			}

#if (defined N787) || (defined N787S) || (defined N797)

			// do not check data!

#else
			// У��д�������
			if(__CheckWritedFile(hReadFile, startAddr1, (startAddr2 - startAddr1), TempBuf) == 0)
			{
				LOGI_D("CheckWritedFile failed!");
				// У��δͨ�������¿���һ������
				if(__M11CopyImgFile(hReadFile, startAddr1, (startAddr2 - startAddr1), TempBuf) == 0)
				{
					// ���������˳�
					LOGI_D("redo CopyImgFile failed!");
					break;
				}
				// �ٴ�У��
				if(__CheckWritedFile(hReadFile, startAddr1, (startAddr2 - startAddr1), TempBuf) == 0)
				{
					// ÈÔÎ´Ð£ÑéÍ¨¹ý£¬ÍË³ö
					LOGI_D("redo CheckWritedFile failed!");
					break;
				}
			}
#endif
			Count++;
		}

		if( Count != HeadInfo.FileNum )
		{
			LOGI_D("Count != HeadInfo.FileNum");
			break;
		}

		ret = 1;
	}while(0);

	if(ReadBuf != NULL)
		free(ReadBuf);

	if(TempBuf != NULL)
		free(TempBuf);

	if(hReadFile != NULL)
		fclose(hReadFile);

	return ret;
}
/*
static int __M11CreateDir(const char *FileName)
{
	int  ret;
	char *pStart;
	char *pEnd;

	char *TempBuf;
	char *CurrPath;
	char *Utf8Name;
	char*TempName;
	int len;
	ret = 0;
	TempBuf = NULL;
	TempName=NULL;
	len=0;
	do
	{
		if(FileName == NULL)
			break;
		len=strlen(FileName);
		TempName= (char*)malloc(len+1);
		if(TempName == NULL)
			break;
		memset(TempName,0,len+1);

		TempBuf = (char*)malloc(3096);
		if(TempBuf == NULL)
			break;

		memset(TempBuf, 0, 3096);
		CurrPath = TempBuf+1024;
		Utf8Name = TempBuf+2048;

		//strcpy(TempName, FileName);
		//__Gbk2Utf8(CurrPath, TempName);
		strcpy(Utf8Name, DST_DATA_FILE_PATH);
		pStart = FileName;


		while(1)
		{	
			pEnd 	= strrchr(pStart, '/');
			pStart 	= strchr(pStart, '/');

			if(pStart==NULL)
				break;
			LOGI_E("FileName %s",FileName);
			LOGI_E("Utf8Name %s",Utf8Name);
			memset(TempName,0,len+1);

			if(pStart == pEnd)
			{
				LOGI_E("pStart == pEnd");
				memcpy( TempName, pStart, (int)(FileName+strlen(FileName)) - (int)pStart );
				LOGI_E("TempName22 %s",TempName);
				TempName[(int)(FileName+strlen(FileName))   - (int)pStart] = 0;
				LOGI_E("TempName22 %s",TempName);
				//break;
			}
			else if(pStart != pEnd)
			{
				LOGI_E("pStart != pEnd");
				memcpy( TempName, pStart, (int)pEnd - (int)pStart );
				LOGI_E("TempName %s",TempName);
				TempName[(int)pEnd - (int)pStart] = 0;
				LOGI_E("TempName %s",TempName);
			}

			strcat(Utf8Name, TempName);
			LOGI_E("Utf8Name %s",Utf8Name);

			//memset(CurrPath,0,2048);
			__Gbk2Utf8(CurrPath, Utf8Name);


			LOGI_E("CurrPath %s",CurrPath);
			LOGI_E("Utf8Name %s",Utf8Name);
			ret=	mkdir(CurrPath, 0x00777);
			LOGI_E("mkdir ret  %d",ret);
			
			//if( mkdir(CurrPath, 0x00700) == -1 )
		//	{
			//LOGI_E("mkdir(CurrPath, 0x00700) == -1");
			//break;
		//	}
			
			pStart = pStart+ 1;

		}

		ret = 1;
	}while(0);

	if(TempBuf != NULL)
		free(TempBuf);
	if(TempName != NULL)
		free(TempName);

	return ret;
}
*/

static int __M11CreateDir(const char *FileName)
{
	int  ret;
	char *pStart;
	char *pEnd;

	char *TempBuf;
	char *CurrPath;
	char *Utf8Name;
	char*TempName;
	int len;
	ret = 0;
	TempBuf = NULL;
	TempName=NULL;
	len=0;
	do
	{
		if(FileName == NULL)
			break;
		len=strlen(FileName);
		TempName= (char*)malloc(len+1);
		if(TempName == NULL)
			break;
		memset(TempName,0,len+1);

		TempBuf = (char*)malloc(3096);
		if(TempBuf == NULL)
			break;

		memset(TempBuf, 0, 3096);
		CurrPath = TempBuf+1024;
		Utf8Name = TempBuf+2048;

		//strcpy(TempName, FileName);
		//__Gbk2Utf8(CurrPath, TempName);
		strcpy(Utf8Name, DST_DATA_FILE_PATH);
		pStart = FileName;


		while(1)
		{	


			//LOGI_E("mkdir(CurrPath, 0x00700) == -2");
			pStart 	= strchr(pStart, '/');
			
				
			if(pStart==NULL)
				break;

			pEnd 	= strchr(pStart+1, '/');

			
			//LOGI_E("FileName %s",FileName);
			//LOGI_E("Utf8Name %s",Utf8Name);
			memset(TempName,0,len+1);

			if((pStart == pEnd)||(pEnd==NULL))
			{
				//LOGI_E("pStart == pEnd");
				memcpy( TempName, pStart, (int)(FileName+strlen(FileName)) - (int)pStart );
				//LOGI_E("TempName22 %s",TempName);
				TempName[(int)(FileName+strlen(FileName))   - (int)pStart] = 0;
				//LOGI_E("TempName22 %s",TempName);
				//break;
			}
			else if(pStart != pEnd)
			{
				//LOGI_E("pStart != pEnd");
				memcpy( TempName, pStart, (int)pEnd - (int)pStart );
				//LOGI_E("TempName %s",TempName);
				TempName[(int)pEnd - (int)pStart] = 0;
				//LOGI_E("TempName %s",TempName);
			}
		

			strcat(Utf8Name, TempName);
		//	LOGI_E("Utf8Name %s",Utf8Name);

			//memset(CurrPath,0,2048);
			__Gbk2Utf8(CurrPath, Utf8Name);


		//	LOGI_E("CurrPath %s",CurrPath);
			//LOGI_E("Utf8Name %s",Utf8Name);
			ret=	mkdir(CurrPath, 0x00777);
			//LOGI_E("mkdir ret  %d",ret);
			/*
			if( mkdir(CurrPath, 0x00700) == -1 )
			{
			LOGI_E("mkdir(CurrPath, 0x00700) == -1");
			break;
			}
			*/
			pStart = pStart+ 1;
			//LOGI_E("mkdir(CurrPath, 0x00700) == -1");

		}

		ret = 1;
	}while(0);

	if(TempBuf != NULL)
		free(TempBuf);
	if(TempName != NULL)
		free(TempName);

	return ret;
}




static int __StrRepace(unsigned char *str, unsigned char src, unsigned char dst)
{
	int ret = 0;

	if(str != NULL)
	{
		while(*str != 0)
		{
			if(*str == src)
				*str = dst;
			if( (*str)&0x80 )
				str += 2;
			else
				str++;
		}
	}

	return ret;
}

static int __M11CopyFile(const char *SrcFileName, const char *DstFileName)
{
	int		ret;
	long	FileLen;
	int		ReadLen;
	int		WriteLen;
	char	*ReadBuf;
	int	*hReadFile;
	int	*hWriteFile;

	ret		= 0;
	hReadFile = FS_INVALID_HANDLE;
	hWriteFile = FS_INVALID_HANDLE;
	ReadBuf = NULL;

	do
	{
		if( SrcFileName == NULL || DstFileName == NULL )
			break;

		ReadBuf = (char*) malloc( READ_BUFFER_LEN );
		if(ReadBuf == NULL)
			break;

		hReadFile = fopen(SrcFileName, "rb");
		if(hReadFile == FS_INVALID_HANDLE)
			break;

		hWriteFile = fopen(DstFileName, "wb");
		if(hWriteFile == FS_INVALID_HANDLE)
			break;

		fseek(hReadFile, 0, SEEK_END);
		FileLen = ftell(hReadFile);
		if(FileLen == 0)
			break;
		fseek(hReadFile, 0, SEEK_SET);

		ReadLen = 0;
		WriteLen = 0;

		while( FileLen > 0 )
		{
			ReadLen = fread(ReadBuf, 1, READ_BUFFER_LEN, hReadFile);
			if(ReadLen == 0)
				break;

			WriteLen = fwrite(ReadBuf, 1, ReadLen, hWriteFile);
			if(WriteLen != ReadLen)
				break;
			FileLen -= WriteLen;
		}

		if( FileLen > 0 )
			break;

		ret = 1;
	}while(0);

	if(ReadBuf != NULL)
		free(ReadBuf);

	if(hReadFile != FS_INVALID_HANDLE)
		fclose(hReadFile);

	if(hWriteFile != FS_INVALID_HANDLE)
		fclose(hWriteFile);

	return ret;
}

int fseek_64(FILE *stream, int64_t offset, int origin) {
    setbuf(stream, NULL); //清空buffer
    int fd = fileno(stream);
    if (lseek64(fd, offset, origin) == -1) {
    	LOGI_E("fseek_64 error");
    	return errno;
    }
    return 0;
}

static int __M11CopyImgFile(FILE *hSrcFile, long long StartPos, unsigned long Length, const char *DstFileName)
{
	int		ret;
	int		ReadLen;
	int		WriteLen;
	char	*ReadBuf;
	FILE	*hDstFile;

	ret		= 0;
	ReadBuf = NULL;
	hDstFile = FS_INVALID_HANDLE;

	do
	{
		if( hSrcFile == NULL|| DstFileName == NULL )
			break;

		ReadBuf = (char*) malloc( READ_BUFFER_LEN );
		if(ReadBuf == NULL)
			break;
		//LOGI_E("ReadBuf = (char*) malloc( READ_BUFFER_LEN )");
		/*
		fseek(logfp, 0, SEEK_END);
		fwrite("ReadBuf = (char*) malloc( READ_BUFFER_LEN )", 1, strlen("ReadBuf = (char*) malloc( READ_BUFFER_LEN )"), logfp);
		fwrite("\r\n",1,2,logfp);
		fwrite(DstFileName, 1, strlen(DstFileName), logfp);
		fwrite("\r\n",1,2,logfp);
		*/
		hDstFile = fopen(DstFileName, "wb");
		if(hDstFile == NULL)
		{
			LOGI_E("hDstFile == NULL");
			break;
		}
		/*
		fseek(logfp, 0, SEEK_END);
		fwrite(" fopen(DstFileName", 1, strlen(" fopen(DstFileName"), logfp);
		fwrite("\r\n",1,2,logfp);
		*/
	//	LOGI_E("__M11CopyImgFile hDstFile = fopen(DstFileName");
		ReadLen = 0;
		WriteLen = 0;
		LOGI_E("StartPos ld = %lld", StartPos);
		LOGI_E("Length ld = %ld", Length);

		if(0 == fseek_64(hSrcFile, StartPos, FILESEEK_BEGIN)) {
			//LOGI_E("fseek success !!!");
		} else {
			LOGI_E("fseek unsuccess !!!");
		}
		//LOGI_E("write start");
	//	LOGI_E("11write start length= %d",Length);

		while( Length > 0 )
		{
			//FileSeek(hSrcFile, sizeof(HeadInfo), FILESEEK_BEGIN);
			memset(ReadBuf,0,READ_BUFFER_LEN);
			fseek(hSrcFile, 0, SEEK_CUR);
			if(Length>READ_BUFFER_LEN)
			{
				ReadLen = fread(ReadBuf, 1, READ_BUFFER_LEN, hSrcFile);
			}
			else {
				ReadLen = fread(ReadBuf, 1, Length, hSrcFile);
			}
			if(ReadLen == 0)
			{
				LOGI_E("WriteLen =0");
				break;
			}
			//LOGI_E("ReadLen = %d",ReadLen);
			WriteLen = fwrite(ReadBuf, 1, ReadLen, hDstFile);
			if(WriteLen != ReadLen)
			{
				LOGI_E("WriteLen = %d",WriteLen);
				break;
			}
			
			Length -= WriteLen;
			//LOGI_E("write start length= %d",Length);
		}

		if( Length > 0 ){
			break;
		}

		LOGI_E("write success");
		ret = 1;
	}while(0);

	if(ReadBuf != NULL)
		free(ReadBuf);

	if(hDstFile != NULL)
		fclose(hDstFile);

	return ret;
}


// by jinzhoucheng
static int __CheckWritedFile(FILE *hSrcFile, long long nStartPos, unsigned long nDataLen, const char *pDstFilePath)
{
	int		i=0, nRet=0;
	int		nReadLen=0, nTotalLen=nDataLen;
	char	*pSrcBuf=NULL, *pDstBuf=NULL;
	FILE	*hDstFile = NULL;

	do
	{
		nRet = 0;

		if(hSrcFile == NULL || pDstFilePath == NULL){
			LOGI_E("__CheckWritedFile: hSrcFile=0x%x, pDstFilePath=0x%x", hSrcFile, pDstFilePath);
			break;
		}

		hDstFile = fopen(pDstFilePath, "rb");
		if(hDstFile == NULL){
			LOGI_E("__CheckWritedFile: hDstFile = null");
			break;
		}

		LOGI_E("nStartPos lld = %lld", nStartPos);

		//fseek(hSrcFile, nStartPos, FILESEEK_BEGIN);

		if(0 == fseek_64(hSrcFile, nStartPos, FILESEEK_BEGIN)) {
			//LOGI_E("check file fseek success !!!");
		} else {
			LOGI_E("check file fseek unsuccess !!!");
		}

		fseek(hDstFile, 0, FILESEEK_BEGIN);

		pSrcBuf = malloc(READ_BUFFER_LEN);
		if(pSrcBuf == NULL){
			LOGI_E("__CheckWritedFile: pSrcBuf = null");
			break;
		}
		pDstBuf = malloc(READ_BUFFER_LEN);
		if(pDstBuf == NULL){
			LOGI_E("__CheckWritedFile: pDstBuf = null");
			break;
		}

		while(nTotalLen > 0)
		{
			if(nTotalLen >= READ_BUFFER_LEN){
				nReadLen = READ_BUFFER_LEN;
				nTotalLen -= READ_BUFFER_LEN;
			}
			else{
				nReadLen = nTotalLen;
				nTotalLen -= nReadLen;
			}
			memset(pSrcBuf, 0, READ_BUFFER_LEN);
			memset(pDstBuf, 0, READ_BUFFER_LEN);

			if(fread(pSrcBuf, 1, nReadLen, hSrcFile) != nReadLen){
				LOGI_E("__CheckWritedFile: fread(pSrcBuf, 1, nReadLen, hSrcFile) != nReadLen");
				goto ERROR;
			}
			if(fread(pDstBuf, 1, nReadLen, hDstFile) != nReadLen){
				LOGI_E("__CheckWritedFile: fread(pDstBuf, 1, nReadLen, hDstFile) != nReadLen");
				goto ERROR;
			}
			if(memcmp(pSrcBuf, pDstBuf, nReadLen) != 0){
				LOGI_E("__CheckWritedFile: memcmp(pSrcBuf, pDstBuf, nReadLen) != 0");
				goto ERROR;
			}
		}

		nRet = 1;
	}while(0);

ERROR:
	if(hDstFile){
		fclose(hDstFile);
	}
	if(pSrcBuf){
		free(pSrcBuf);
	}
	if(pDstBuf){
		free(pDstBuf);
	}
	return nRet;
}

static int __Gbk2Utf8(char *DstBuf, char *SrcBuf)
{
	int ret;
	int gbklen;
	char *outbuf;
	char *inbuf; 
	ret = 0;
	gbklen=0;
	iconv_t cd;
	do{
		if(SrcBuf==NULL) break;
		gbklen=strlen(SrcBuf);
		cd = iconv_open( "UTF-8", "GBk");
		inbuf= SrcBuf;
		outbuf=DstBuf;
		size_t outlen = gbklen*4;
		iconv(cd, &inbuf, (size_t *)&gbklen, &outbuf,&outlen);
		iconv_close(cd);
		LOGI_E("DstBuf is %s", DstBuf);
	}while(0);


	return ret;	
}

