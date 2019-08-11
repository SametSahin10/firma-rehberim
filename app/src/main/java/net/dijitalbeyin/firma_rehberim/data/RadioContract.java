package net.dijitalbeyin.firma_rehberim.data;

import android.provider.BaseColumns;

public final class RadioContract {
    private RadioContract() {

    }

    public static final class RadioEntry implements BaseColumns {
        public final static String TABLE_NAME = "favourite_radios";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_RADIO_ID = "id";
        public final static String COLUMN_CITY_ID = "city_id";
        public final static String COLUMN_TOWN_ID = "town_id";
        public final static String COLUMN_NEIGHBOURHOOD_ID = "neighbourhood_id";
        public final static String COLUMN_CATEGORY_ID = "category_id";
        public final static String COLUMN_USER_ID = "user_id";
        public final static String COLUMN_RADIO_NAME = "name";
        public final static String COLUMN_RADIO_CATEGORY = "category";
        public final static String COLUMN_RADIO_ICON_URL = "icon_url";
        public final static String COLUMN_RADIO_STREAM_LINK = "stream_link";
        public final static String COLUMN_RADIO_SHAREABLE_LINK = "shareable_link";
        public final static String COLUMN_RADIO_HIT = "hit";
        public final static String COLUMN_NUM_OF_ONLINE_LISTENERS = "num_of_online_listeners";
        public final static String COLUMN_RADIO_IS_BEING_BUFFERED = "is_being_buffered";
        public final static String COLUMN_RADIO_IS_LIKED = "is_Liked";

        //    private int radioId;
        //    private int cityId;
        //    private int townId; //ilceId
        //    private int neighbourhoodId; //mahalleId
        //    private String categoryId;
        //    private int userId;
        //    private String radioName;
        //    private String category;
        //    private String radioIconUrl;
        //    private String streamLink;
        //    private String shareableLink;
        //    private int hit;
        //    private int numOfOnlineListeners;
        //    private boolean isBeingBuffered;
        //    private boolean isLiked;
    }
}
