package net.dijitalbeyin.firma_rehberim.data;

import android.provider.BaseColumns;

public final class RadioContract {
    private RadioContract() {

    }

    public static final class RadioEntry implements BaseColumns {
        public final static String TABLE_NAME = "favourite_radios";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_RADIO_ID = "id";
        public final static String COLUMN_RADIO_NAME = "name";
        public final static String COLUMN_RADIO_CATEGORY = "category";
        public final static String COLUMN_RADIO_ICON_URL = "icon_url";
        public final static String COLUMN_RADIO_STREAM_LINK = "stream_link";
        public final static String COLUMN_RADIO_SHAREABLE_LINK = "shareable_link";
        public final static String COLUMN_RADIO_HIT = "hit";
        public final static String COLUMN_NUM_OF_ONLINE_LISTENERS = "num_of_online_listeners";
        public final static String COLUMN_RADIO_IS_BEING_BUFFERED = "is_being_buffered";
        public final static String COLUMN_RADIO_IS_LIKED = "is_Liked";
    }
}
