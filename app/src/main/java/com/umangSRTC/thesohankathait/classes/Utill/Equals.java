package com.umangSRTC.thesohankathait.classes.Utill;

import com.umangSRTC.thesohankathait.classes.model.Notices;
import com.umangSRTC.thesohankathait.classes.model.Query_model;

public class Equals {

    public static boolean BothEquals(Notices notices, Notices currentNotice) {

        if (notices.getImageUrl() != null) {

            return notices.getTitle().equals(currentNotice.getTitle()) &&
                    notices.getSender().equals(currentNotice.getSender()) &&
                    notices.getImageUrl().equals(currentNotice.getImageUrl()) &&
                    notices.getDescription().equals(currentNotice.getDescription());
        } else {
            return notices.getTitle().equals(currentNotice.getTitle()) &&
                    notices.getSender().equals(currentNotice.getSender()) &&
                    notices.getDescription().equals(currentNotice.getDescription());

        }
    }

    public static boolean BothEqual(Query_model currentQuery, Query_model old_query_model) {

        return currentQuery.getQuestion().equals(old_query_model.getQuestion())&&
                currentQuery.getAnswer().equals(old_query_model.getAnswer());
    }

}
