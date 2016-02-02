package com.nlp.gestalt;

public class GestaltConstants
{
	// Shared data folder root
	public static final String SHARED_DATA_FOLDER_ROOT = "/shr/data/ws";
	// Corpus file names
	public static final String LEGACY_CORPUS_MECAB_PR_TEXT_FILE_NAME = SHARED_DATA_FOLDER_ROOT + "/corpus_mecab_pr.txt";
	public static final String CORPUS_MECAB_PR_TEXT_1_0_FILE_NAME = SHARED_DATA_FOLDER_ROOT + "/corpus_mecab_pr.v1.txt";
	// PGraph file names
	public static final String LEGACY_CWGRAPH_TEXT_FILE_NAME = SHARED_DATA_FOLDER_ROOT + "/CWgraph.txt";
	public static final String CWGRAPH_TEXT_1_0_FILE_NAME = SHARED_DATA_FOLDER_ROOT + "/CWgraph.v1.txt";
	// Gestalt file names
	public static final String LEGACY_P_GESTALT_MAX_FILE_NAME_SUFFIX = ".PgstaltMAX";
	public static final String LEGACY_P_GESTALT_MAX_FILE_NAME = SHARED_DATA_FOLDER_ROOT + "/corpus_mecab_pr.txt" + LEGACY_P_GESTALT_MAX_FILE_NAME_SUFFIX;
	public static final String P_GESTALT_MAX_V1_0_FILE_NAME_SUFFIX = ".PgstaltMAX.v1";
	public static final String P_GESTALT_MAX_V1_0_FILE_NAME = SHARED_DATA_FOLDER_ROOT + "/corpus_mecab_pr.txt" + P_GESTALT_MAX_V1_0_FILE_NAME_SUFFIX;
	// SGraph file names
	public static final String LEGCAY_SGRAPH_FILE_NAME_SUFFIX = ".sgraph";
	public static final String SGRAPH_V1_0_FILE_NAME_SUFFIX = ".sv1";
}
