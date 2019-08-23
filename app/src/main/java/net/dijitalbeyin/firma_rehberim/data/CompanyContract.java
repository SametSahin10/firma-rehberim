package net.dijitalbeyin.firma_rehberim.data;

import android.provider.BaseColumns;

public class CompanyContract {
    private CompanyContract(){

    }

    public static final class CompanyEntry implements BaseColumns {
        public final static String TABLE_NAME = "companies";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_WEBPAGE_LINK = "webpage_link";
        public final static String COLUMN_COMPANY_NAME = "company_name";
        public final static String COLUMN_AUTHORITATIVE_NAME = "authoritative_name";
        public final static String COLUMN_AUTHORITATIVE_WEBPAGE_LINK = "authoritative_webpage_link";
        public final static String COLUMN_CALL_STATUS = "call_status";
        public final static String COLUMN_DATE_INFO = "date_info";
        public final static int CALL_STATUS_INCOMING = 1;
        public final static int CALL_STATUS_OUTGOING = 2;
        public final static int CALL_STATUS_MISSED = 3;
    }
}
