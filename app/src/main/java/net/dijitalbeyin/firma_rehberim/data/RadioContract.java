package net.dijitalbeyin.firma_rehberim.data;

import android.provider.BaseColumns;

public final class RadioContract {

    public static final class RadioEntry implements BaseColumns {
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_NEIGHBOURHOOD_ID = "neighbourhood_id";
        public static final String COLUMN_NUM_OF_ONLINE_LISTENERS = "num_of_online_listeners";
        public static final String COLUMN_RADIO_CATEGORY = "category";
        public static final String COLUMN_RADIO_HIT = "hit";
        public static final String COLUMN_RADIO_ICON_URL = "icon_url";
        public static final String COLUMN_RADIO_ID = "id";
        public static final String COLUMN_RADIO_IS_BEING_BUFFERED = "is_being_buffered";
        public static final String COLUMN_RADIO_IS_LIKED = "is_Liked";
        public static final String COLUMN_RADIO_NAME = "name";
        public static final String COLUMN_RADIO_SHAREABLE_LINK = "shareable_link";
        public static final String COLUMN_RADIO_STREAM_LINK = "stream_link";
        public static final String COLUMN_TOWN_ID = "town_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String TABLE_NAME = "favourite_radios";
        public static final String _ID = "_id";
    }

    private RadioContract() {
    }
}
