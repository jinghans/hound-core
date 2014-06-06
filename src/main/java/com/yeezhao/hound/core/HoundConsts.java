package com.yeezhao.hound.core;

public class HoundConsts {
	
	public static final String ORG = "yeezhao";
	public static final String APP = "hound";
	
	public static enum DATA_TYPE {
		GL(5),			//google scholar
		LN(4),			//linkedin
		GZ(6);			//广州人才中心数据
		
		DATA_TYPE(int type){
			this.type = type;
		}
		int type;
		
		public int getTypeValue(){
			return type;
		}
		
		public static DATA_TYPE fromType(int type){
			if(type == 5){
				return GL;
			} else if(type == 4){
				return LN;
			} else if(type == 6){
				return GZ;
			}
			return null;
		}
	}
	
	public static final String JOB_TYPE_SCHOLAR_PAGE = "SCHOLAR_PAGE";
	public static final String JOB_TYPE_HOME_PAGE = "SCHOLAR_HOMEPAGE";
	public static final String JOB_TYPE_PDF_PAGE = "SCHOLAR_PDF";
	public static final String JOB_TYPE_SCHOLAR_SYNC = "SCHOLAR_SYNC";
	public static final String JOB_TYPE_SCHOLAR_LOCATION = "SCHOLAR_LOCATION";
	public static final String JOB_TYPE_SCHOLAR_FUSION = "SCHOLAR_FUSION";

	public static final String JOB_SCHOLAR_PARAM_STARTROW = "start_row";
	public static final String JOB_SCHOLAR_PARAM_ENDROW = "end_row";
	
	public static final String JOB_PDF_PARAM_AUTHORS = "authors";
	public static final String JOB_PDF_PARAM_SCHOLAR_NAME = "scholar_name";
	public static final String JOB_PARAM_LOCATION = "location";
	
	public static final String JOB_PARAM_ROWKEY = "rowkey";
	public static final String JOB_PARAM_SCHOLAR_ID = "scholar_id";
	public static final String JOB_PARAM_URL = "url";
	public static final String JOB_PARAM_DATA_TYPE = "datatype";

    public static final String FILE_BUFFALO_JOB = "yz-hound-serv-scholar-job.xml";
    
    
    public static final String PARAM_ES_HOST = "es.hosts";
    public static final String ES_CLUSTER_NAME = "amkt_es_cluster";
    public static final String INDEX_PEOPLE = "hound-index";
    public static final String INDEX_PAPER = "hound-paper";
    public static final String INDEX_TYPE_PAPER_EN = "hound-paper-eng-type";
    public static final String INDEX_TYPE_SCHOLAR_EN = "hound-eng-type";
    public static final String INDEX_TYPE_SCHOLAR_CH = "hound-ch-type";

    // constants for parsers
    public static final String ORGLL = "<ORGANIZATION>";
    public static final String ORGRL = "</ORGANIZATION>";
    public static final String ORGANIZATION = "organization"; // 所在单位, 和HBase中的qualifier对应
    public static final int ORGLLEN = ORGLL.length();
    public static final String LOCLL = "<LOCATION>";
    public static final String LOCRL = "</LOCATION>";
    public static final String LOCATION = "org_address"; // 所在地, 和HBase中的qualifier对应

    public static final int LOCLLLEN = LOCLL.length();
    public static final String EMPTY = "";
    public static final String POSITION = "position"; // 职位, 和HBase中的qualifier对应

    public static final String DEPARTMENT = "work_department"; // 部门, 和HBase中的qualifier对应
}
