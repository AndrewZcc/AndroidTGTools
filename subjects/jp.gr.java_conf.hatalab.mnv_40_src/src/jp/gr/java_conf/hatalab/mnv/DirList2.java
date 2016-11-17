package jp.gr.java_conf.hatalab.mnv;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;

public class DirList2 {
	
	private String mBaseDir = "/";
	private String mInitReturn = "";
	private String mPreviousReturn = "";
	private boolean mInitSearch = true;
	private String mFilter  = "(.*\\.txt|.*\\.chi)";
//	private String mFilter  = "(.*\\.txt)";
//	private String mFilter  = ".*";
	
	private boolean mlistFoldersFirstFlag = false;
	private boolean mBeginOfList = false;
	private boolean mEndOfList   = false;
	private boolean mCancelled   = false;
	
	private int mSortDirection = 1; // 1:ascend(����), -1:descend(�~��)
	
	public DirList2(String initFilePath){
		File f = new File(initFilePath);
		if(!f.exists()){
			f = new File("/sdcard");
			if(!f.exists()){
				f = new File("/");
			}			
		}
		
		if(f.isDirectory()){
			mBaseDir = f.getAbsolutePath();//���̃t�@�C�������܂܂��Ԃ͌����𑱂���B
			mPreviousReturn = mBaseDir;
		}else{//�t�@�C���̏ꍇ�͂��̃t�@�C�����܂ރf�B���N�g����baseDir�Ƃ���B
			mBaseDir = f.getParent();
			if(mBaseDir == null)mBaseDir = "/";
			mInitReturn = f.getAbsolutePath();//�ŏ��ɕԋp����t�@�C���p�X�͂���
			mPreviousReturn = mInitReturn;
		}
		

//    	Log.d("DirList", "mInitDir = " + mBaseDir);

	}
//	private static String initDir = "/sdcard";

	public String nextFile(){
		setSortAscend();
		String resultFile;
		mCancelled = false;

//    	Log.d("DirList.nextFile()", "mInitDir = " + mBaseDir);

    	if(mInitSearch){
    		mInitSearch = false;
    		
    		if(!mInitReturn.equals("")){//����
    			mPreviousReturn = mInitReturn;
    			mInitReturn = "";
    			resultFile = mPreviousReturn;

    		}else{
    			String nextFile = mBaseDir;
    			//�f�B���N�g�����܂ߎ��̃t�@�C��������������result���A���Ă���B
    			//�]���āA�t�@�C�����f�B���N�g���������������x��������B
    			while(new File(nextFile).isDirectory()){
    				if(mCancelled)return "";
    				nextFile = searchNextFile(nextFile, "");
    			}
    				
       			if(!nextFile.equals(""))mPreviousReturn = nextFile;
    			resultFile = nextFile;
    			
    		}
    	}else{
    		
    		if( mEndOfList ){//���ɖ����܂ł����Ă��܂����ꍇ
    			resultFile = "";//�O�񌩂���Ȃ������ꍇ�͂����Ƌ��Ԃ��B
    		}else{
    			
    			String nextFile;
    			if(mBeginOfList){//�擪�̏ꍇ��mBaseDir���Z�b�g���Ė������炷�ׂČ����i���������j
    				nextFile = mBaseDir;
//    	   			File f = new File(mPreviousReturn);
//        			nextFile = searchNextFile(f.getParent(),f.getName());
    			}else{
    	   			File f = new File(mPreviousReturn);
        			nextFile = searchNextFile(f.getParent(),f.getName());
    			}
 
    			while(new File(nextFile).isDirectory()){
    				if(mCancelled)return "";
    				nextFile = searchNextFile(nextFile, "");
    			}

    			
    			if(!nextFile.equals(""))mPreviousReturn = nextFile;
    			resultFile = nextFile;
    		}
    	}
    	
    	if(resultFile.equals("")){
    		mEndOfList = true;
    	}else{
    		mBeginOfList = false;
    		mEndOfList = false;    		
    	}
    	return resultFile;
	}

	public String previousFile(){
		setSortDescend();
		String resultFile;
		mCancelled = false;
//    	Log.d("DirList.nextFile()", "mInitDir = " + mBaseDir);

    	if(mInitSearch){
    		mInitSearch = false;
    		
    		if(!mInitReturn.equals("")){//����
    			mPreviousReturn = mInitReturn;
    			mInitReturn = "";
    			resultFile = mPreviousReturn;

    		}else{
  //  			String nextFile = searchNextFile(mBaseDir, "");
    			String nextFile = mBaseDir;
    			//�f�B���N�g�����܂ߎ��̃t�@�C��������������result���A���Ă���B
    			//�]���āA�t�@�C�����f�B���N�g���������������x��������B
    			while(new File(nextFile).isDirectory()){
    				if(mCancelled)return "";
    				nextFile = searchNextFile(nextFile, "");
    			}
    				
       			if(!nextFile.equals(""))mPreviousReturn = nextFile;
    			resultFile = nextFile;
    			
    		}
    	}else{
//    		if(mPreviousReturn.equals("") ){
    		if( mBeginOfList ){//���ɐ擪�܂ł����Ă��܂����ꍇ
    			resultFile = "";//�O�񌩂���Ȃ������ꍇ�͂����Ƌ��Ԃ��B
    		}else{
    			
    			String nextFile;
    			if(mEndOfList){//�����̏ꍇ��mBaseDir���Z�b�g���Ė������炷�ׂČ����i�~�������j
    				nextFile = mBaseDir;
//    	   			File f = new File(mPreviousReturn);
//        			nextFile = searchNextFile(f.getParent(),f.getName());
    			}else{
    	   			File f = new File(mPreviousReturn);
        			nextFile = searchNextFile(f.getParent(),f.getName());
    			}
 
    			while(new File(nextFile).isDirectory()){
    				if(mCancelled)return "";
    				nextFile = searchNextFile(nextFile, "");
    			}

       			if(!nextFile.equals(""))mPreviousReturn = nextFile;//�擪�A���[�̃t�@�C������mPreviousReturn�ɕێ����Ă���

    			resultFile = nextFile;
    		}
    	}
    	
    	if(resultFile.equals("")){
    		mBeginOfList = true;
    	}else{
    		//�t�@�C����������΂���͐擪�ł������ł��Ȃ��B
    		mBeginOfList = false;
    		mEndOfList   = false;
    	}
    	return resultFile;
	}

	//���ɂ�����x�����t�@�C�����擾�������ꍇ�͈ȉ����Ă�
	public void revert(){
		//���񎞂̏��������s�����悤�ɂ���B
		mInitSearch = true;
		mInitReturn = mPreviousReturn; 
	}

	//���݂̌����t�@�C�����Đݒ�
	//�������[�h��������Ȃ������Ƃ��ɁA���������Ƃ���܂Ŗ߂邽�߂Ɏg��
	public void setCurrentFile(String filename){
		//���ݕ\�����̃t�@�C���̎��̃t�@�C�������������悤�ɂ���B
		mPreviousReturn = filename;
   		mBeginOfList = false;
		mEndOfList = false;    		

	}

	
	private String searchNextFile(String currentDir, String fileName){
//		System.out.println("searchNextFile(" + currentDir + " ," + fileName + ")");
		String nextFile = "";

		
		if(!currentDir.startsWith(mBaseDir) ){
//			System.out.println("currentDir:" + currentDir);
//			System.out.println("fileName:" + fileName);
//			System.out.println("mBaseDir:" + mBaseDir );
			return nextFile;// finish!
		}

//		System.out.println("currentDir:" + currentDir);

		ArrayList<File> list = new ArrayList<File>();
		File baseFile = new File(currentDir);
		File[] files = baseFile.listFiles();

		// System.out.println("==== add list ====");
		if(
				!currentDir.equals("/dev") &&
				!currentDir.equals("/proc") && //proc�t�@�C���V�X�e���z����skip
				!currentDir.equals("/sys") &&
				files !=  null // return null if basefile is not a directory
				){ 
			for (File f : files) {
				if(f.getName().matches(mFilter) || f.isDirectory()){
					//if endwith(".txt") or endwith(".chi") or isDirectory
					list.add(f);
					// System.out.println("add list:" + f.getAbsolutePath());
				}
			}
		}
		// System.out.println("==================");


		Collections.sort(list, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				File f1 = (File)o1;
				File f2 = (File)o2;
				String data1 = f1.getName();
				String data2 = f2.getName();
				return data1.compareToIgnoreCase(data2)*mSortDirection;
//				return data1.compareTo(data2);
			}
		});
		
		
		//�����ŕ��ׂ�悤�ɂ���B//�f�B���N�g���͏�
		if(mlistFoldersFirstFlag){
			Collections.sort(list, new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					File f1 = (File)o1;
					File f2 = (File)o2;
					boolean obj1isDir = f1.isDirectory();
					boolean obj2isDir = f2.isDirectory();
					int ret;
					if(obj1isDir == obj2isDir){
						ret = 0;//����dir������file
					}else if(obj1isDir){ //obj2��dir
						ret = -1; //�ŏ��̈������������Ƃ��͕�
					}else{ //obj2��dir
						ret =  1; //�ŏ��̈������傫���Ƃ��͐�
					}
					return ret*mSortDirection;
				}
			});

		}

		/**
		System.out.println("==== list ====");
		for (int i = 0; i < list.size(); i++) {
			File f = list.get(i);
			if( f.isDirectory()){
				System.out.println(f.getAbsolutePath() + "/");
			}else{
				System.out.println(f.getAbsolutePath());
			}
		}
		**/
//		System.out.println("==================");
		//fileName�����鎞�́A�T���Ď��̃t�@�C����������
		int index = list.indexOf(new File(currentDir + "/" + fileName));
		//������Ȃ���ΐ擪�A������Ύ��̃t�@�C����index
		index++; // if i = -1 ,i -> 0, not if i = -1 ; i + 1;(next index) 

		if(index >= list.size() ){
			//It was last index. and search up stair.
			if(currentDir.equals("/")){
				return ""; //�@/�����ׂĒ��׏I�������I���B
			}else{
				File upDir = new File( currentDir );
				return searchNextFile( upDir.getParent() , upDir.getName());
			}
		}
//		System.out.println("+++++++++++++++++++++++++");


		for (int k = index; k < list.size(); k++) {
			File f = list.get(k);
//			System.out.println("Check file:" + f.getAbsolutePath());
			if(f.isDirectory()){
				/**
				String result = searchNextFile( f.getAbsolutePath(), "");
				if(result.equals("")){
					//skip
				}else{
					return result;
				}
				**/
				//�f�B���N�g���ł���������Ԃ��BStack Overflow�΍�
				return f.getAbsolutePath();

			}else{
				return f.getAbsolutePath();
			}
		}



		return nextFile;
	}


	private void setSortAscend(){
		mSortDirection =  1;
	}	
	private void setSortDescend(){
		mSortDirection = -1;
	}

	public void setFoldersFirst(){
		mlistFoldersFirstFlag = true;
	}
	
	public void setCancel(){
		System.out.println("setCancel");

		mCancelled = true;
	}
	public void setFilenameFilter(String filter){
		mFilter = filter;
	}
}
