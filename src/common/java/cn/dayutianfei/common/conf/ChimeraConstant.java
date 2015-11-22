package cn.dayutianfei.common.conf;

import java.io.File;

public class ChimeraConstant {
	public static final String _HOME = "_HOME";
	public static final int _DEPLOY_FETCH_INTERVAL = 200;
	public static final String _SHARD_DELIMETER = "#";
	public static final String _MULL = "MULL";
	public static final String _NULL = "NULL";
	public static final String _ATATALL_NAME = "@@all";
	public static final String _MATCHALL = _ATATALL_NAME + ":0";

	public static final String _STAR = "*";
	public static final String _STATION = "station";
	public static final String _COLON = ":";
	public static final String _INDEX = "index";
	public static final String _ENABLE = "enable";
	public static final String _TMP_SHARD_EXTENSION = "_tmp";
	public static final String _TMP_MERGE_SHARD_EXTENSION = "_merge";
	
	public static final int _MAX_TRANSMIT_UNIT = 2000000;
	public static final int _MAX_ORDER_TOPK = 1000;
	public static final int _MAX_GROUP_TOPK = 2000000;
	public static final int NODE_PORT = 20000;
	public static final String _AND = "AND";
	public static final String _OR = "OR";
	public static final String _NOT = "NOT";
	
	public static final String _TO = "TO";
	
	public static final String _LP = "(";
	public static final String _RP = ")";
	public static final String _SPLIT_COMMA = ",";
	public static final String _SPLIT_HYPHEN = "-";
	public static final String _FORWARD_SLASH = "/";
	public static final String _SPLIT_UNDERLINE = "_";
	public static final String _REGEX_EXP = "/%s/";
	public static final String _STRING_EXP = "\"%s\"";
	public static final String digestFolder = "digest";
	public static final String shardFolder = "shard";
	public static final String storeFileFolder = "storeFile";
	public static final String recycleFolder = "recycleFolder";
	
	public static final String resultSepatator="\t";
	public static final int MAX_LEN=32768;
	public static final String home_path;
	static{
		home_path = new File(System.getProperty("user.dir")).getParent();
	}
}
